package com.example.fit.data.repository

import android.content.Context
import com.example.fit.data.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

interface FitRepository {
    val configured: Boolean
    val user: Flow<String?>
    suspend fun login(email: String, password: String)
    suspend fun register(name: String, email: String, password: String)
    suspend fun reset(email: String)
    fun logout()
    fun profile(uid: String): Flow<Profile?>
    fun workouts(uid: String): Flow<List<Completed>>
    fun weights(uid: String): Flow<List<Weight>>
    suspend fun saveProfile(uid: String, profile: Profile)
    suspend fun complete(uid: String, record: Completed)
    suspend fun addWeight(uid: String, id: String, kg: Double)
}
class FirebaseRepository(context: Context): FitRepository {
    override val configured = FirebaseApp.getApps(context).isNotEmpty() || FirebaseApp.initializeApp(context) != null
    private val auth get() = FirebaseAuth.getInstance()
    private val db get() = FirebaseFirestore.getInstance()
    private fun ref(uid: String) = db.collection("users").document(uid)
    override val user: Flow<String?> = if (!configured) flowOf(null) else callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }
    override suspend fun login(email: String, password: String) { auth.signInWithEmailAndPassword(email, password).await(); ensureProfile() }
    private suspend fun ensureProfile(name: String = "") {
        val u = auth.currentUser ?: error("Тіркелгіге кіріңіз")
        if (!ref(u.uid).get().await().exists()) ref(u.uid).set(mapOf("name" to name.ifBlank { u.email?.substringBefore('@') ?: "Спортшы" }, "email" to (u.email ?: ""), "age" to 0, "height" to 0.0, "weight" to 0.0, "goal" to StoredIds.DEFAULT_GOAL, "createdAt" to FieldValue.serverTimestamp())).await()
    }
    override suspend fun register(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
        ensureProfile(name)
    }
    override suspend fun reset(email: String) { auth.sendPasswordResetEmail(email).await() }
    override fun logout() { if (configured) auth.signOut() }
    override fun profile(uid: String): Flow<Profile?> = callbackFlow {
        val listener = ref(uid).addSnapshotListener { s, e ->
            if (e != null) close(e) else trySend(s?.takeIf { it.exists() }?.let { Profile(it.getString("name") ?: "", it.getString("email") ?: "", (it.getLong("age") ?: 0).toInt(), it.getDouble("height") ?: 0.0, it.getDouble("weight") ?: 0.0, it.getString("goal")?.let { goal -> StoredIds.goal(goal) ?: goal } ?: StoredIds.DEFAULT_GOAL, it.getTimestamp("createdAt")?.toDate()?.time ?: 0) })
        }; awaitClose { listener.remove() }
    }
    override fun workouts(uid: String): Flow<List<Completed>> = callbackFlow {
        val listener = ref(uid).collection("completedWorkouts").addSnapshotListener { s, e ->
            if (e != null) close(e) else trySend(s?.documents.orEmpty().map { Completed(it.id, it.getString("workoutId") ?: "", it.getString("title") ?: "", it.getString("category")?.let { category -> StoredIds.category(category) ?: category } ?: "", it.getDouble("durationMinutes") ?: 0.0, it.getTimestamp("completedAt")?.toDate()?.time ?: 0) }.sortedByDescending { it.completedAt })
        }; awaitClose { listener.remove() }
    }
    override fun weights(uid: String): Flow<List<Weight>> = callbackFlow {
        val listener = ref(uid).collection("weightHistory").addSnapshotListener { s, e ->
            if (e != null) close(e) else trySend(s?.documents.orEmpty().map { Weight(it.id, it.getDouble("weight") ?: 0.0, it.getTimestamp("recordedAt")?.toDate()?.time ?: 0) }.sortedBy { it.recordedAt })
        }; awaitClose { listener.remove() }
    }
    override suspend fun saveProfile(uid: String, profile: Profile) {
                val batch = db.batch().update(ref(uid), mapOf("name" to profile.name, "age" to profile.age, "height" to profile.height, "weight" to profile.weight, "goal" to StoredIds.requireGoal(profile.goal)))
        val old = ref(uid).get().await().getDouble("weight")
        if (profile.weight > 0 && old != profile.weight) batch.set(ref(uid).collection("weightHistory").document(), mapOf("weight" to profile.weight, "recordedAt" to FieldValue.serverTimestamp()))
        batch.commit().await()
    }
    override suspend fun complete(uid: String, record: Completed) {
        val doc = ref(uid).collection("completedWorkouts").document(record.id)
        db.runTransaction { tx -> if (!tx.get(doc).exists()) tx.set(doc, mapOf("workoutId" to record.workoutId, "title" to record.title, "category" to StoredIds.requireCategory(record.category), "durationMinutes" to record.durationMinutes, "completedAt" to FieldValue.serverTimestamp())); Unit }.await()
    }
    override suspend fun addWeight(uid: String, id: String, kg: Double) {
        db.runTransaction { tx ->
            val profileRef = ref(uid)
            val storedGoal = tx.get(profileRef).getString("goal") ?: StoredIds.DEFAULT_GOAL
            val goal = StoredIds.requireGoal(storedGoal)
            tx.update(profileRef, mapOf("weight" to kg, "goal" to goal))
            tx.set(profileRef.collection("weightHistory").document(id), mapOf("weight" to kg, "recordedAt" to FieldValue.serverTimestamp()))
            Unit
        }.await()
    }
}
fun friendlyError(t: Throwable): String = when (t) {
    is TimeoutCancellationException -> "Сервер жауап бермеді. Интернетті тексеріп, қайталап көріңіз."
    is com.google.firebase.FirebaseNetworkException -> "Қызметпен байланыс жоқ. Интернетті тексеріңіз."
    is FirebaseAuthException -> when(t.errorCode) {
        "ERROR_EMAIL_ALREADY_IN_USE" -> "Бұл электрондық пошта мекенжайы бұрын тіркелген."
        "ERROR_INVALID_EMAIL" -> "Электрондық пошта мекенжайын тексеріңіз."
        "ERROR_WEAK_PASSWORD" -> "Кемінде 6 таңбадан тұратын құпиясөз қолданыңыз."
        "ERROR_TOO_MANY_REQUESTS" -> "Әрекет саны тым көп. Кейінірек қайталап көріңіз."
        else -> "Кіру мүмкін болмады. Электрондық пошта мен құпиясөзді тексеріңіз."
    }
    else -> "Деректерді алу немесе сақтау мүмкін болмады. Байланысты және қызмет баптауларын тексеріңіз."
}



package com.example.fit.ui.viewmodel

import androidx.lifecycle.*
import com.example.fit.data.model.*
import com.example.fit.data.repository.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID

data class FitState(val loading: Boolean = true, val uid: String? = null, val profile: Profile? = null, val workouts: List<Completed> = emptyList(), val weights: List<Weight> = emptyList(), val busy: Boolean = false, val message: String? = null)
class FitViewModel(private val repo: FitRepository, private val saved: SavedStateHandle): ViewModel() {
    private val mutable = MutableStateFlow(FitState())
    val state = mutable.asStateFlow()
    val configured get() = repo.configured
    private var listeners: Job? = null
    init { viewModelScope.launch { repo.user.collect { uid ->
        listeners?.cancel()
        mutable.value = FitState(loading=false, uid=uid, busy=mutable.value.busy)
        if (uid != null) listeners = launch { supervisorScope {
            launch { repo.profile(uid).catch { error(it) }.collect { p -> mutable.update { it.copy(profile=p) } } }
            launch { repo.workouts(uid).catch { error(it) }.collect { r -> mutable.update { it.copy(workouts=r) } } }
            launch { repo.weights(uid).catch { error(it) }.collect { r -> mutable.update { it.copy(weights=r) } } }
        } }
    } } }
    private fun error(t: Throwable) { mutable.update { it.copy(message=friendlyError(t)) } }
    fun message(value: String?) { mutable.update { it.copy(message=value) } }
    fun run(action: suspend () -> Unit, success: () -> Unit = {}) {
        if (mutable.value.busy) return
        mutable.update { it.copy(busy=true, message=null) }
        viewModelScope.launch {
            try { withTimeout(25000) { action() }; success() }
            catch (t: Exception) { if (t is CancellationException && t !is TimeoutCancellationException) throw t; error(t) }
            finally { mutable.update { it.copy(busy=false) } }
        }
    }
    fun login(email: String, password: String) = run({ repo.login(email.trim(),password) })
    fun register(name: String, email: String, password: String) = run({ repo.register(name.trim(), email.trim(),password) })
    fun reset(email: String) = run({ repo.reset(email.trim()) }, { message("Қалпына келтіру хаты жіберілді. Электрондық поштаңызды тексеріңіз.") })
    fun logout() { repo.logout(); clearSession() }
    fun save(profile: Profile) { val uid=state.value.uid ?: return; run({ repo.saveProfile(uid, profile) }, { message("Профиль сақталды") }) }
    fun weight(kg: Double) { val uid=state.value.uid ?: return; val id=UUID.randomUUID().toString(); run({ repo.addWeight(uid,id,kg) }, { message("Салмақ сақталды") }) }
    val workoutId = saved.getStateFlow<String?>("workout",null)
    val setIndex = saved.getStateFlow("set",0)
    val exerciseIndex = saved.getStateFlow("exercise",0)
    fun start(w: Workout) { if (workoutId.value == w.id) return; saved["workout"]=w.id; saved["set"]=0; saved["exercise"]=0; saved["started"]=System.currentTimeMillis(); saved["session"]=UUID.randomUUID().toString() }
    fun setDone(w: Workout) { val e=w.exercises.getOrNull(exerciseIndex.value) ?: return; if (setIndex.value < e.sets) saved["set"]=setIndex.value+1 }
    fun next(w: Workout) { if (setIndex.value >= w.exercises[exerciseIndex.value].sets && exerciseIndex.value < w.exercises.lastIndex) { saved["exercise"]=exerciseIndex.value+1; saved["set"]=0 } }
    fun clearSession() { saved["workout"]=null; saved["set"]=0; saved["exercise"]=0; saved.remove<String>("session"); saved.remove<Long>("started") }
    fun finish(w: Workout, done: () -> Unit) {
        val uid=state.value.uid ?: return
        if(workoutId.value != w.id) return
        val id=saved.get<String>("session") ?: return
        val now=System.currentTimeMillis()
        val record=Completed(id,w.id,w.title,w.category,((now-(saved.get<Long>("started") ?: now))/60000.0).coerceAtLeast(0.01),now)
        run({ repo.complete(uid,record) }, { clearSession(); done() })
    }
}



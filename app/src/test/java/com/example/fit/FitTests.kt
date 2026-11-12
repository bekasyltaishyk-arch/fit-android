package com.example.fit

import androidx.lifecycle.SavedStateHandle
import com.example.fit.data.model.*
import com.example.fit.data.local.WorkoutCatalog
import com.example.fit.data.repository.FitRepository
import com.example.fit.ui.viewmodel.FitViewModel
import com.example.fit.utils.Validation
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import java.time.*

@OptIn(ExperimentalCoroutinesApi::class)
class FitTests {
    private val dispatcher=StandardTestDispatcher()
    @Before fun setup() {Dispatchers.setMain(dispatcher)}
    @After fun teardown() {Dispatchers.resetMain()}
    @Test fun validatesEmail() { assertTrue(Validation.email("fit@example.com"));assertFalse(Validation.email("fit@"));assertFalse(Validation.email("a b@example.com")) }
    @Test fun validatesPassword() {assertFalse(Validation.password("12345"));assertTrue(Validation.password("123456"))}
    @Test fun confirmsPassword() {assertTrue(Validation.matches("abcdef","abcdef"));assertFalse(Validation.matches("abcdef","abcdeg"))}
    @Test fun validatesWeight() {assertEquals(72.5,Validation.weight("72,5") ?: 0.0,0.001);assertNull(Validation.weight("NaN"));assertNull(Validation.weight("0"))}
    @Test fun catalogHasCompleteWorkouts() {assertTrue(WorkoutCatalog.all.size>=12);assertEquals(WorkoutCatalog.all.size,WorkoutCatalog.all.map{it.id}.distinct().size);WorkoutCatalog.all.forEach{w->assertTrue(w.exercises.isNotEmpty());w.exercises.forEach{assertTrue(it.sets>0);assertTrue(it.instruction.isNotBlank())}}}
    @Test fun calculatesCalendarStats() {
        val day=LocalDate.of(2026,10,21);val zone=ZoneId.of("Asia/Qyzylorda")
        fun r(date: LocalDate)=Completed(date.toString(),"full","Full","Всё тело",12.5,date.atTime(12,0).atZone(zone).toInstant().toEpochMilli())
        val stats=statistics(listOf(r(day),r(day.minusDays(2)),r(day.minusMonths(1))),day,zone)
        assertEquals(3,stats.total);assertEquals(37.5,stats.minutes,0.001);assertEquals(2,stats.week);assertEquals(2,stats.month);assertEquals(1,stats.days.last())
    }
    @Test fun emptyStats() {val s=statistics(emptyList());assertEquals(0,s.total);assertEquals(List(7){0},s.days)}
    @Test fun sessionRequiresAllSetsAndRestoresState() = runTest(dispatcher) {
        val saved=SavedStateHandle();val vm=FitViewModel(FakeRepository(),saved);runCurrent();val w=WorkoutCatalog.all.first();vm.start(w)
        vm.next(w);assertEquals(0,vm.exerciseIndex.value)
        repeat(w.exercises.first().sets+2){vm.setDone(w)};assertEquals(w.exercises.first().sets,vm.setIndex.value)
        vm.next(w);assertEquals(1,vm.exerciseIndex.value);assertEquals(0,vm.setIndex.value)
        val restored=FitViewModel(FakeRepository(),saved);assertEquals(w.id,restored.workoutId.value);assertEquals(1,restored.exerciseIndex.value)
    }
    @Test fun repositoryErrorIsVisibleAndBusyClears() = runTest(dispatcher) {
        val repo=FakeRepository();repo.fail=true;val vm=FitViewModel(repo,SavedStateHandle());runCurrent();vm.login("test@example.com","123456");advanceUntilIdle();assertFalse(vm.state.value.busy);assertNotNull(vm.state.value.message)
    }
    @Test fun repeatedFinishUsesSingleRecord() = runTest(dispatcher) {
        val repo=FakeRepository();val vm=FitViewModel(repo,SavedStateHandle());runCurrent();val w=WorkoutCatalog.all.first();vm.start(w)
        var finished=0;vm.finish(w){finished++};vm.finish(w){finished++};advanceUntilIdle();assertEquals(1,repo.records.size);assertEquals(1,finished);assertNull(vm.workoutId.value)
    }
    @Test fun abandonedSessionCannotBeSaved() = runTest(dispatcher) {
        val repo=FakeRepository();val vm=FitViewModel(repo,SavedStateHandle());runCurrent();val w=WorkoutCatalog.all.first();vm.start(w);vm.clearSession();vm.finish(w){};advanceUntilIdle();assertTrue(repo.records.isEmpty())
    }
    @Test fun logoutClearsUserAndSession() = runTest(dispatcher) {
        val repo=FakeRepository();val vm=FitViewModel(repo,SavedStateHandle());runCurrent();vm.start(WorkoutCatalog.all.first());vm.logout();runCurrent();assertNull(vm.state.value.uid);assertNull(vm.state.value.profile);assertNull(vm.workoutId.value)
    }
    @Test fun profileUpdatesThroughRepository() = runTest(dispatcher) {
        val repo=FakeRepository();val vm=FitViewModel(repo,SavedStateHandle());runCurrent();vm.save(Profile(name="Алексей",age=25));advanceUntilIdle();assertEquals("Алексей",repo.p.value?.name)
    }
}
class FakeRepository: FitRepository {
    var fail=false;val records=mutableMapOf<String,Completed>();val p=MutableStateFlow<Profile?>(Profile(name="Тест"))
    override val configured=true
    override val user=MutableStateFlow<String?>("test")
    override suspend fun login(email:String,password:String) {if(fail) error("offline")}
    override suspend fun register(name:String,email:String,password:String) {if(fail) error("offline")}
    override suspend fun reset(email:String) {if(fail) error("offline")}
    override fun logout() {user.value=null}
    override fun profile(uid:String): Flow<Profile?> = p
    override fun workouts(uid:String): Flow<List<Completed>> = flowOf(emptyList())
    override fun weights(uid:String): Flow<List<Weight>> = flowOf(emptyList())
    override suspend fun saveProfile(uid:String,profile:Profile) {if(fail) error("offline");p.value=profile}
    override suspend fun complete(uid:String,record:Completed) {if(fail) error("offline");records.putIfAbsent(record.id,record)}
    override suspend fun addWeight(uid:String,id:String,kg:Double) {if(fail) error("offline")}
}

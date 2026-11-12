package com.example.fit

import androidx.compose.material3.Surface
import androidx.compose.ui.test.*
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fit.data.local.WorkoutCatalog
import com.example.fit.data.model.*
import com.example.fit.data.repository.FitRepository
import com.example.fit.ui.components.*
import com.example.fit.ui.screens.*
import com.example.fit.ui.theme.FitTheme
import com.example.fit.ui.viewmodel.*
import kotlinx.coroutines.flow.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith

/** Device tests only. No real Firebase requests or user data are used. */
@RunWith(AndroidJUnit4::class)
class PremiumScreensTest {
    @get:Rule val compose=createComposeRule()
    private val store=ViewModelStore()
    private fun vm(): FitViewModel = ViewModelProvider(store,viewModelFactory {initializer {FitViewModel(ScreenRepository(),SavedStateHandle())}})[FitViewModel::class.java]
    @After fun clear() {compose.runOnIdle {store.clear()}}
    @Test fun authenticationFormCanSwitchToRegistrationAndValidate() {
        val model=vm()
        compose.setContent {FitTheme {Surface {AuthScreen(model,FitState(loading=false),{})}}}
        compose.onNodeWithText("Тіркелгі жасау").performScrollTo().performClick()
        compose.onNodeWithText("Құпиясөзді қайталаңыз").performScrollTo().assertExists()
        compose.onNodeWithText("Тіркелу").performScrollTo().performClick()
        compose.runOnIdle {assertEquals("Атыңызды 2–60 таңбамен енгізіңіз",model.state.value.message)}
    }
    @Test fun loadingScreenHasBrandAndIndicator() {
        compose.setContent {FitTheme {Surface {FitLoadingState()}}}
        compose.onNodeWithText("FIT").assertIsDisplayed()
        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)).assertExists()
    }
    @Test fun catalogSearchOpensCorrectWorkout() {
        var opened=""
        compose.setContent {FitTheme {Surface {Catalog {opened=it}}}}
        compose.onNode(hasSetTextAction()).performTextInput("Мықты арқа")
        compose.onAllNodesWithText("Мықты арқа").onLast().performScrollTo().performClick()
        compose.runOnIdle {assertEquals("back-strength",opened)}
    }
    @Test fun catalogCategoryFilterUsesStableIdWithKazakhLabel() {
        compose.setContent {FitTheme {Surface {Catalog {}}}}
        compose.onAllNodesWithText("Кеуде").onFirst().performClick()
        compose.onNodeWithText("2 жаттығу").assertExists()
    }
    @Test fun homeShowsEmptyStateWithoutInventedHistory() {
        compose.setContent {FitTheme {Surface {Home(FitState(loading=false,profile=Profile(name="Айдана")),{}, {})}}}
        compose.onNodeWithText("Сәлем, Айдана!").assertExists()
        compose.onNodeWithText("Алғашқы қадамың").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("0").assertExists()
    }
    @Test fun guestCannotStartWorkout() {
        compose.setContent {FitTheme {Surface {Detail(WorkoutCatalog.all.first(),false) {error("Guest must not start")}}}}
        compose.onNodeWithText("Жаттығуды бастау").performScrollTo().assertIsNotEnabled()
    }
    @Test fun detailsStartActionKeepsCallback() {
        var started=false
        compose.setContent {FitTheme {Surface {Detail(WorkoutCatalog.all.first(),true) {started=true}}}}
        compose.onNodeWithText("Жаттығуды бастау").performScrollTo().performClick()
        compose.runOnIdle {assertTrue(started)}
    }
    @Test fun settingsChangesWeightUnit() {
        var selected=false
        compose.setContent {FitTheme {Surface {SettingsScreen(false,false,{selected=it},{})}}}
        compose.onNodeWithText("Фунт").performScrollTo().performClick()
        compose.runOnIdle {assertTrue(selected)}
    }
    @Test fun completionScreenNavigatesToProgress() {
        var opened=false
        compose.setContent {FitTheme {Surface {SuccessScreen({opened=true},{})}}}
        compose.onNodeWithText("Жетістіктерді көру").performScrollTo().performClick()
        compose.runOnIdle {assertTrue(opened)}
    }
    @Test fun progressShowsActualEmptyWeightState() {
        val model=vm()
        compose.setContent {FitTheme {Surface {Progress(FitState(loading=false,uid="test"),model,false)}}}
        compose.onNodeWithText("Салмағыңызды алғаш рет жазыңыз — осы жерде график пайда болады.").performScrollTo().assertIsDisplayed()
    }
    @Test fun profileHasStableGoalLabelsAndSaveAction() {
        val model=vm()
        compose.setContent {FitTheme {Surface {ProfileScreen(FitState(loading=false,uid="test",profile=Profile(name="Айдана",email="test@example.com",age=25,height=170.0,weight=65.0)),model,{},false)}}}
        compose.onNodeWithText("Бұлшықет массасын арттыру").performScrollTo().performClick()
        compose.onNodeWithText("Сақтау").performScrollTo().assertIsEnabled()
    }
    @Test fun activeSessionStillCompletesSetsBeforeNextExercise() {
        val model=vm();val w=WorkoutCatalog.all.first();model.start(w)
        compose.setContent {FitTheme {Surface {Active(w,model,FitState(loading=false,uid="test"),{}, {})}}}
        compose.onNodeWithText("Келесі жаттығу").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Тәсіл орындалды").performScrollTo().performClick()
        compose.runOnIdle {assertEquals(1,model.setIndex.value);assertEquals(0,model.exerciseIndex.value)}
    }
}
private class ScreenRepository: FitRepository {
    override val configured=true
    override val user=MutableStateFlow<String?>("test")
    override suspend fun login(email:String,password:String) {}
    override suspend fun register(name:String,email:String,password:String) {}
    override suspend fun reset(email:String) {}
    override fun logout() {user.value=null}
    override fun profile(uid:String): Flow<Profile?> = flowOf(Profile(name="Айдана"))
    override fun workouts(uid:String): Flow<List<Completed>> = flowOf(emptyList())
    override fun weights(uid:String): Flow<List<Weight>> = flowOf(emptyList())
    override suspend fun saveProfile(uid:String,profile:Profile) {}
    override suspend fun complete(uid:String,record:Completed) {}
    override suspend fun addWeight(uid:String,id:String,kg:Double) {}
}


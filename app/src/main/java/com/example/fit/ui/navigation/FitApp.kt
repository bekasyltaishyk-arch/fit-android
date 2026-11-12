package com.example.fit.ui.navigation

import android.content.Context
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.dp
import com.example.fit.ui.components.*
import com.example.fit.ui.theme.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.fit.R
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.*
import com.example.fit.data.local.WorkoutCatalog
import com.example.fit.ui.screens.*
import com.example.fit.ui.viewmodel.FitViewModel

@Composable fun FitApp(vm: FitViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val nav=rememberNavController(); val entry by nav.currentBackStackEntryAsState(); val route=entry?.destination?.route
    val snackbar=remember {SnackbarHostState()}
    val context=LocalContext.current
    val prefs=remember {context.getSharedPreferences("fit_settings",Context.MODE_PRIVATE)}
    var pounds by remember {mutableStateOf(prefs.getBoolean("pounds",false))}
    var logout by remember {mutableStateOf(false)}
    val activeId by vm.workoutId.collectAsStateWithLifecycle()
    LaunchedEffect(state.uid,state.loading) {
                if(!state.loading) {
            val current = nav.currentDestination?.route
            val protectedHistory = runCatching { nav.getBackStackEntry("home") }.isSuccess
            if(state.uid==null && (protectedHistory || current !in listOf("auth", "catalog", "detail/{id}"))) {
                nav.navigate("auth") {popUpTo(nav.graph.id) {inclusive=true};launchSingleTop=true}
            } else if(state.uid!=null && current in listOf(null,"auth","splash")) {
                nav.navigate("home") {popUpTo(nav.graph.id) {inclusive=true};launchSingleTop=true}
                if(activeId!=null) nav.navigate("active/$activeId") {launchSingleTop=true}
            }
        }
    }
    LaunchedEffect(state.message) {state.message?.let {snackbar.showSnackbar(it);vm.message(null)}}
    if(logout) AlertDialog(onDismissRequest={logout=false},title={Text(context.getString(R.string.fit_text_073))},confirmButton={TextButton({logout=false;vm.logout()}) {Text(context.getString(R.string.fit_text_041))}},dismissButton={TextButton({logout=false}) {Text(context.getString(R.string.fit_text_074))}})
    val tabs=listOf("home","catalog","progress","profile")
    val labels=listOf(context.getString(R.string.fit_text_075),context.getString(R.string.fit_text_031),context.getString(R.string.fit_text_054),context.getString(R.string.fit_text_064))
    val icons=listOf(Icons.Default.Home,Icons.Default.FitnessCenter,Icons.Default.BarChart,Icons.Default.Person)
    Scaffold(snackbarHost={SnackbarHost(snackbar)},bottomBar={if(state.uid!=null && route in tabs) NavigationBar(containerColor=FitSurface,tonalElevation=0.dp) {tabs.forEachIndexed {i,r -> NavigationBarItem(colors=NavigationBarItemDefaults.colors(selectedIconColor=FitBackground,selectedTextColor=Lime,indicatorColor=Lime,unselectedIconColor=FitMuted,unselectedTextColor=FitMuted),selected=route==r,onClick={nav.navigate(r) {popUpTo("home") {saveState=true};launchSingleTop=true;restoreState=true}},icon={Icon(icons[i],labels[i])},label={Text(labels[i])})}}},topBar={if(route!=null && (route !in tabs || state.uid==null) && route!="auth" && route!="splash" && route?.startsWith("active")==false) TextButton({nav.popBackStack()}) {Text(context.getString(R.string.fit_text_076))}}) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            NavHost(nav,startDestination="splash",enterTransition={fadeIn(tween(180))},exitTransition={fadeOut(tween(120))},popEnterTransition={fadeIn(tween(180))},popExitTransition={fadeOut(tween(120))}) {
                composable("splash") { FitLoadingState() }
                composable("auth") { AuthScreen(vm,state) {nav.navigate("catalog") {launchSingleTop=true}} }
                composable("home") {if(state.uid!=null) Home(state,{nav.navigate("detail/$it") {launchSingleTop=true}},{nav.navigate("progress") {launchSingleTop=true}})}
                composable("catalog") {Catalog {nav.navigate("detail/$it") {launchSingleTop=true}}}
                composable("detail/{id}") { back -> WorkoutCatalog.find(back.arguments?.getString("id"))?.let {w -> Detail(w,state.uid!=null) {vm.start(w);nav.navigate("active/${w.id}") {launchSingleTop=true}}} }
                composable("active/{id}") {back -> WorkoutCatalog.find(back.arguments?.getString("id"))?.let {w -> if(state.uid!=null) Active(w,vm,state,{nav.navigate("success") {popUpTo("home");launchSingleTop=true}},{nav.popBackStack()})} }
                composable("success") {SuccessScreen(
                    onProgress={nav.navigate("progress") {popUpTo("home");launchSingleTop=true}},
                    onHome={nav.navigate("home") {popUpTo("home") {inclusive=true}}}
                )}
                composable("progress") {if(state.uid!=null) Progress(state,vm,pounds)}
                composable("profile") {if(state.uid!=null) ProfileScreen(state,vm,{nav.navigate("settings") {launchSingleTop=true}},pounds)}
                composable("settings") {if(state.uid!=null) SettingsScreen(
                    pounds=pounds,busy=state.busy,
                    onUnit={value -> pounds=value;prefs.edit().putBoolean("pounds",value).apply()},
                    onLogout={logout=true}
                )}

            }
            if(state.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
        }
    }
}



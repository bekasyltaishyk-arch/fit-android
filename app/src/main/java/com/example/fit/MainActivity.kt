package com.example.fit
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fit.data.repository.FirebaseRepository
import com.example.fit.ui.viewmodel.FitViewModel
import com.example.fit.ui.theme.FitTheme
import com.example.fit.ui.navigation.FitApp
class MainActivity: ComponentActivity() {
    override fun attachBaseContext(newBase: android.content.Context) {
        val configuration = android.content.res.Configuration(newBase.resources.configuration)
        val locale = java.util.Locale.forLanguageTag("kk-KZ")
        configuration.setLocales(android.os.LocaleList(locale))
        super.attachBaseContext(newBase.createConfigurationContext(configuration))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle=androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT), navigationBarStyle=androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        val repository=FirebaseRepository(applicationContext)
        setContent { FitTheme { val vm: FitViewModel=viewModel(factory=viewModelFactory { initializer { FitViewModel(repository,createSavedStateHandle()) } }); FitApp(vm) } }
    }
}



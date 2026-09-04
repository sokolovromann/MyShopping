package ru.sokolovromann.myshopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.sokolovromann.myshopping.core.navigation.Screen
import ru.sokolovromann.myshopping.core.ui.theme.MyShoppingTheme
import androidx.compose.runtime.collectAsState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val mainState = mainViewModel.mainState
        installSplashScreen().apply {
            setKeepOnScreenCondition { mainState.value.isWaiting }
        }

        setContent {
            MyShoppingTheme(
                themeType = mainState.collectAsState().value.themeType,
                fontSize = mainState.collectAsState().value.themeFontSize
            ) {
                val navController = rememberNavController()
                LaunchedEffect(Unit) {
                    mainViewModel.navigationActions.collect { action ->
                        when (action) {
                            is NavigationAction.NavigateTo -> navController.navigate(action.screen)
                            NavigationAction.NavigateBack -> navController.popBackStack()
                            NavigationAction.Finish -> finish()
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Screen.Purchases
                ) {
                    composable<Screen.Purchases> { TextScreen("Purchases Screen") }
                    composable<Screen.Archive> { TextScreen("Archive Screen") }
                    composable<Screen.Trash> { TextScreen("Trash Screen") }
                    composable<Screen.Dictionary> { TextScreen("Dictionary Screen") }
                    composable<Screen.Settings> { TextScreen("Settings Screen") }
                    composable<Screen.About> { TextScreen("About Screen") }
                }
            }
        }
    }

    @Composable
    private fun TextScreen(text: String) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = { Text(text) }
        )
    }
}
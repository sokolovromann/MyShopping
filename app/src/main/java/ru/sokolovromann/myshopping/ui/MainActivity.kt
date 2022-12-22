package ru.sokolovromann.myshopping.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.sokolovromann.myshopping.ui.compose.event.MainScreenEvent
import ru.sokolovromann.myshopping.ui.theme.MyShoppingTheme
import ru.sokolovromann.myshopping.ui.viewmodel.MainViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.MainEvent

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val shoppingUid = intent.extras?.getString(UiRouteKey.ShoppingUid.key)
        val event = MainEvent.OnCreate(shoppingUid)
        viewModel.onEvent(event)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.loadingState.value
            }
        }

        setContent {
            MyShoppingTheme(
                darkTheme = viewModel.nightThemeState.value,
                content = { MainContent() }
            )
        }
    }

    @Composable
    private fun MainContent() {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = UiRoute.Purchases.graph,
            builder = {
                purchasesGraph(navController)
                archiveGraph(navController)
                trashGraph(navController)
                productsGraph(navController)
                autocompletesGraph(navController)
                settingsGraph(navController)
            }
        )

        LaunchedEffect(Unit) {
            viewModel.screenEventFlow.collect {
                when (it) {
                    MainScreenEvent.GetDefaultPreferences -> addDefaultPreferences()

                    is MainScreenEvent.ShowProducts -> navController.navigate(
                        route = UiRoute.Products.productsScreen(it.uid)
                    )

                    MainScreenEvent.GetScreenSize -> migrateFromAppVersion14()
                }
            }
        }
    }

    private fun addDefaultPreferences() {
        val event = MainEvent.AddDefaultPreferences(
            screenWidth = resources.configuration.screenWidthDp,
            screenHeight = resources.configuration.screenHeightDp
        )
        viewModel.onEvent(event)
    }

    private fun migrateFromAppVersion14() {
        val event = MainEvent.MigrateFromAppVersion14(
            screenWidth = resources.configuration.screenWidthDp,
            screenHeight = resources.configuration.screenHeightDp
        )
        viewModel.onEvent(event)
    }
}
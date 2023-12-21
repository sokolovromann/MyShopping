package ru.sokolovromann.myshopping.ui

import android.content.Intent
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

        val mainState = viewModel.mainState
        viewModel.onEvent(MainEvent.OnCreate)

        installSplashScreen().apply {
            setKeepOnScreenCondition { mainState.waiting }
        }

        setContent {
            MyShoppingTheme(
                darkTheme = mainState.nightTheme,
                content = { MainContent() }
            )
        }
    }

    override fun onStart() {
        super.onStart()

        val shoppingUid = intent.extras?.getString(UiRouteKey.ShoppingUid.key)
        val event = MainEvent.OnStart(shoppingUid)
        viewModel.onEvent(event)
    }

    override fun onStop() {
        intent = getIntentWithoutExtras()

        viewModel.onEvent(MainEvent.OnStop)
        super.onStop()
    }

    private fun getIntentWithoutExtras(): Intent {
        return Intent().apply {
            val args = intent.extras
            if (args?.containsKey(UiRouteKey.ShoppingUid.key) == true) {
                intent.removeExtra(UiRouteKey.ShoppingUid.key)
            }
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

        viewModel.mainState.shoppingUid?.let {
            navController.navigate(route = UiRoute.Products.productsScreen(it))
        }

        LaunchedEffect(Unit) {
            viewModel.screenEventFlow.collect {
                when (it) {
                    MainScreenEvent.GetDefaultDeviceConfig -> addDefaultDeviceConfig()

                    MainScreenEvent.GetScreenSize -> migrateFromCodeVersion14()
                }
            }
        }
    }

    private fun addDefaultDeviceConfig() {
        val event = MainEvent.AddDefaultDeviceConfig(
            screenWidth = resources.configuration.screenWidthDp,
            screenHeight = resources.configuration.screenHeightDp
        )
        viewModel.onEvent(event)
    }

    private fun migrateFromCodeVersion14() {
        val event = MainEvent.MigrateFromCodeVersion14(
            screenWidth = resources.configuration.screenWidthDp,
            screenHeight = resources.configuration.screenHeightDp
        )
        viewModel.onEvent(event)
    }
}
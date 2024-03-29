package ru.sokolovromann.myshopping.ui.activity

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
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.archiveGraph
import ru.sokolovromann.myshopping.ui.autocompletesGraph
import ru.sokolovromann.myshopping.ui.compose.event.MainScreenEvent
import ru.sokolovromann.myshopping.ui.productsGraph
import ru.sokolovromann.myshopping.ui.purchasesGraph
import ru.sokolovromann.myshopping.ui.settingsGraph
import ru.sokolovromann.myshopping.ui.theme.MyShoppingTheme
import ru.sokolovromann.myshopping.ui.trashGraph
import ru.sokolovromann.myshopping.ui.viewmodel.MainViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.MainEvent

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val event = MainEvent.OnCreate(
            screenWidth = resources.configuration.screenWidthDp,
            screenHeight = resources.configuration.screenHeightDp
        )
        viewModel.onEvent(event)

        val mainState = viewModel.mainState

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

        val event = MainEvent.OnSaveIntent(
            action = intent.action,
            uid = intent.extras?.getString(UiRouteKey.ShoppingUid.key)
        )
        viewModel.onEvent(event)
    }

    override fun onStop() {
        intent = Intent().apply {
            val args = intent.extras
            if (args?.containsKey(UiRouteKey.ShoppingUid.key) == true) {
                intent.removeExtra(UiRouteKey.ShoppingUid.key)
            }
        }

        val event = MainEvent.OnSaveIntent(
            action = null,
            uid = null
        )
        viewModel.onEvent(event)
        super.onStop()
    }

    @Composable
    private fun MainContent() {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = UiRoute.Purchases.graph,
            builder = {
                purchasesGraph(navController) { finish() }
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
                    MainScreenEvent.OnFinishApp -> finish()
                }
            }
        }
    }
}
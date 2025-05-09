package ru.sokolovromann.myshopping.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.sokolovromann.myshopping.data.model.AfterAddShopping
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.aboutGraph
import ru.sokolovromann.myshopping.ui.archiveGraph
import ru.sokolovromann.myshopping.ui.autocompletesGraph
import ru.sokolovromann.myshopping.ui.compose.event.MainScreenEvent
import ru.sokolovromann.myshopping.ui.productsGraph
import ru.sokolovromann.myshopping.ui.purchasesGraph
import ru.sokolovromann.myshopping.ui.settingsGraph
import ru.sokolovromann.myshopping.ui.theme.MyShoppingTheme
import ru.sokolovromann.myshopping.ui.theme.createTypography
import ru.sokolovromann.myshopping.ui.trashGraph
import ru.sokolovromann.myshopping.ui.viewmodel.MainViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.MainEvent

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onCreateEvent = MainEvent.OnCreate(
            screenWidth = resources.configuration.screenWidthDp,
            screenHeight = resources.configuration.screenHeightDp
        )
        viewModel.onEvent(onCreateEvent)

        val mainState = viewModel.mainState

        installSplashScreen().apply {
            setKeepOnScreenCondition { mainState.waiting }
        }

        setContent {
            MyShoppingTheme(
                darkTheme = mainState.nightTheme.isAppNightTheme(),
                typography = createTypography(mainState.fontSizeOffset)
            ) {
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
                        aboutGraph(navController)
                    }
                )

                viewModel.mainState.shoppingUid?.let { uid ->
                    when (viewModel.mainState.afterAddShopping) {
                        AfterAddShopping.OPEN_PRODUCTS_SCREEN -> {
                            navController.navigate(route = UiRoute.Products.productsScreen(uid))
                        }
                        AfterAddShopping.OPEN_EDIT_SHOPPING_NAME_SCREEN -> {
                            navController.navigate(route = UiRoute.Products.editShoppingListNameFromPurchasesScreen(uid))
                        }
                        AfterAddShopping.OPEN_ADD_PRODUCT_SCREEN -> {
                            navController.navigate(route = UiRoute.Products.addProductScreen(uid, "true"))
                        }
                        null -> {
                            navController.navigate(route = UiRoute.Products.productsScreen(uid))
                        }
                    }

                    val event = MainEvent.OnSaveIntent(action = null, uid = null)
                    viewModel.onEvent(event)
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

        val onSaveIntentEvent = MainEvent.OnSaveIntent(
            action = intent?.action,
            uid = intent?.extras?.getString(UiRouteKey.ShoppingUid.key)
        )
        viewModel.onEvent(onSaveIntentEvent).let {
            intent?.apply {
                action = null
                putExtras(Bundle())
            }
        }
    }
}
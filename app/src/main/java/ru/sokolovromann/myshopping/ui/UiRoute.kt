package ru.sokolovromann.myshopping.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ru.sokolovromann.myshopping.ui.compose.PurchasesScreen

sealed class UiRoute(val graph: String) {

    object Purchases : UiRoute(graph = "Purchases") {
        const val purchasesScreen = "purchases"
    }

    object Archive : UiRoute(graph = "Archive") {
        const val archiveScreen = "archive"
    }

    object Trash : UiRoute(graph = "Trash") {
        const val trashScreen = "trash"
    }

    object Products : UiRoute(graph = "Products") {
        fun productsScreen(shoppingUid: String): String {
            return "products/$shoppingUid"
        }
    }

    object Autocompletes : UiRoute(graph = "Autocompletes") {
        const val autocompletesScreen = "autocompletes"
    }

    object Settings : UiRoute(graph = "Settings") {
        const val settingsScreen = "settings"
    }
}

enum class UiRouteKey(val key: String, val placeholder: String) {
    ShoppingUid(
        key = "shopping-uid",
        placeholder = "{shopping-uid}"
    )
}

@ExperimentalFoundationApi
fun NavGraphBuilder.purchasesGraph(navController: NavController) {
    navigation(
        startDestination = UiRoute.Purchases.purchasesScreen,
        route = UiRoute.Purchases.graph
    ) {
        composable(route = UiRoute.Purchases.purchasesScreen) {
            PurchasesScreen(navController)
        }
    }
}

fun NavController.navigateWithDrawerOption(route: String) {
    navigate(route = route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
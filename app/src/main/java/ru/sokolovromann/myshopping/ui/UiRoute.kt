package ru.sokolovromann.myshopping.ui

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import ru.sokolovromann.myshopping.ui.compose.*

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

        fun addProductScreen(shoppingUid: String): String {
            return "add-product/$shoppingUid"
        }

        fun editProductScreen(productUid: String): String {
            return "edit-product/$productUid"
        }

        fun editShoppingListNameScreen(shoppingUid: String): String {
            return "edit-shopping-list-name/$shoppingUid"
        }

        fun editShoppingListReminderScreen(shoppingUid: String): String {
            return "edit-shopping-list-reminder/$shoppingUid"
        }

        fun copyProductToShoppingList(productUid: String): String {
            return "copy-product-to-shopping-list/$productUid"
        }

        fun moveProductToShoppingList(productUid: String): String {
            return "move-product-to-shopping-list/$productUid"
        }

        fun calculateChange(shoppingUid: String): String {
            return "calculate-change/$shoppingUid"
        }
    }

    object Autocompletes : UiRoute(graph = "Autocompletes") {
        const val autocompletesScreen = "autocompletes"
        const val addAutocompletesScreen = "add-autocomplete"

        fun editAutocompleteScreen(uid: String): String {
            return "edit-autocomplete/$uid"
        }
    }

    object Settings : UiRoute(graph = "Settings") {
        const val settingsScreen = "settings"
        const val editCurrencySymbolScreen = "edit-currency-symbol"
        const val editTaxRateScreen = "edit-tax-rate"
    }
}

enum class UiRouteKey(val key: String, val placeholder: String) {
    ShoppingUid(
        key = "shopping-uid",
        placeholder = "{shopping-uid}"
    ),
    ProductUid(
        key = "product-uid",
        placeholder = "{product-uid}"
    ),
    AutocompleteUid(
        key = "autocomplete-uid",
        placeholder = "{autocomplete-uid}"
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

@ExperimentalFoundationApi
fun NavGraphBuilder.archiveGraph(navController: NavController) {
    navigation(
        startDestination = UiRoute.Archive.archiveScreen,
        route = UiRoute.Archive.graph
    ) {
        composable(route = UiRoute.Archive.archiveScreen) {
            ArchiveScreen(navController)
        }
    }
}

@ExperimentalFoundationApi
fun NavGraphBuilder.trashGraph(navController: NavController) {
    navigation(
        startDestination = UiRoute.Trash.trashScreen,
        route = UiRoute.Trash.graph
    ) {
        composable(route = UiRoute.Trash.trashScreen) {
            TrashScreen(navController)
        }
    }
}

@ExperimentalFoundationApi
fun NavGraphBuilder.productsGraph(navController: NavController) {
    navigation(
        startDestination = UiRoute.Products.productsScreen(UiRouteKey.ShoppingUid.placeholder),
        route = UiRoute.Products.graph
    ) {
        composable(route = UiRoute.Products.productsScreen(UiRouteKey.ShoppingUid.placeholder)) {
            ProductsScreen(navController)
        }
        composable(route = UiRoute.Products.addProductScreen(UiRouteKey.ShoppingUid.placeholder)) {
            AddEditProductScreen(navController)
        }
        composable(route = UiRoute.Products.editProductScreen(UiRouteKey.ProductUid.placeholder)) {
            AddEditProductScreen(navController)
        }
        dialog(route = UiRoute.Products.editShoppingListNameScreen(UiRouteKey.ShoppingUid.placeholder)) {
            EditShoppingListNameScreen(navController)
        }
        dialog(route = UiRoute.Products.calculateChange(UiRouteKey.ShoppingUid.placeholder)) {
            CalculateChangeScreen(navController)
        }
        dialog(route = UiRoute.Products.editShoppingListReminderScreen(UiRouteKey.ShoppingUid.placeholder)) {
            EditReminderScreen(navController)
        }
    }
}

@ExperimentalFoundationApi
fun NavGraphBuilder.autocompletesGraph(navController: NavController) {
    navigation(
        startDestination = UiRoute.Autocompletes.autocompletesScreen,
        route = UiRoute.Autocompletes.graph
    ) {
        composable(route = UiRoute.Autocompletes.autocompletesScreen) {
            AutocompletesScreen(navController)
        }
        dialog(route = UiRoute.Autocompletes.addAutocompletesScreen) {
            AddEditAutocompleteScreen(navController)
        }
        dialog(route = UiRoute.Autocompletes.editAutocompleteScreen(UiRouteKey.AutocompleteUid.placeholder)) {
            AddEditAutocompleteScreen(navController)
        }
    }
}

fun NavGraphBuilder.settingsGraph(navController: NavController) {
    navigation(
        startDestination = UiRoute.Settings.settingsScreen,
        route = UiRoute.Settings.graph
    ) {
        composable(route = UiRoute.Settings.settingsScreen) {
            SettingsScreen(navController)
        }
        dialog(route = UiRoute.Settings.editCurrencySymbolScreen) {
            EditCurrencySymbolScreen(navController)
        }
        dialog(route = UiRoute.Settings.editTaxRateScreen) {
            EditTaxRateScreen(navController)
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

fun NavController.chooseNavigate(intent: Intent, title: String? = null) {
    context.startActivity(Intent.createChooser(intent, title))
}
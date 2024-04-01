package ru.sokolovromann.myshopping.ui

import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
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

        fun editProductScreen(shoppingUid: String, productUid: String): String {
            return "edit-product/$productUid?${UiRouteKey.ShoppingUid.key}=$shoppingUid"
        }

        fun editShoppingListNameScreen(shoppingUid: String): String {
            return "edit-shopping-list-name/$shoppingUid"
        }

        fun editShoppingListReminderScreen(shoppingUid: String): String {
            return "edit-shopping-list-reminder/$shoppingUid"
        }

        fun editShoppingListTotalScreen(shoppingUid: String): String {
            return "edit-shopping-list-total/$shoppingUid"
        }

        fun copyProductsToShoppingList(productUids: String): String {
            return "copy-products-to-shopping-list/$productUids"
        }

        fun moveProductsToShoppingList(productUids: String): String {
            return "move-products-to-shopping-list/$productUids"
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
        const val fontSizesScreen = "font-sizes-screen"
        const val editCurrencySymbolScreen = "edit-currency-symbol"
        const val editTaxRateScreen = "edit-tax-rate"
        const val backupScreen = "backup-screen"
        const val maxAutocompletesScreen = "max-autocompletes-screen"
    }

    fun toDrawerScreen(): DrawerScreen = when (this) {
        Purchases -> DrawerScreen.PURCHASES
        Archive -> DrawerScreen.ARCHIVE
        Trash -> DrawerScreen.TRASH
        Autocompletes -> DrawerScreen.AUTOCOMPLETES
        Settings -> DrawerScreen.SETTINGS
        Products -> DrawerScreen.PURCHASES
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
    ),
    IsCopy(
        key = "is-copy",
        placeholder = "{is-copy}"
    )
}

enum class DrawerScreen {

    PURCHASES, ARCHIVE, TRASH, AUTOCOMPLETES, SETTINGS;

    fun getScreen(): String = when (this) {
        PURCHASES -> UiRoute.Purchases.purchasesScreen
        ARCHIVE -> UiRoute.Archive.archiveScreen
        TRASH -> UiRoute.Trash.trashScreen
        AUTOCOMPLETES -> UiRoute.Autocompletes.autocompletesScreen
        SETTINGS -> UiRoute.Settings.settingsScreen
    }

    fun toUiRoute(): UiRoute = when (this) {
        PURCHASES -> UiRoute.Purchases
        ARCHIVE -> UiRoute.Archive
        TRASH -> UiRoute.Trash
        AUTOCOMPLETES -> UiRoute.Autocompletes
        SETTINGS -> UiRoute.Settings
    }
}

fun NavGraphBuilder.purchasesGraph(
    navController: NavController,
    onFinishApp: () -> Unit
) {
    navigation(
        startDestination = UiRoute.Purchases.purchasesScreen,
        route = UiRoute.Purchases.graph
    ) {
        composable(route = UiRoute.Purchases.purchasesScreen) {
            PurchasesScreen(navController, onFinishApp)
        }
    }
}

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
        composable(
            route = UiRoute.Products.editProductScreen(UiRouteKey.ShoppingUid.placeholder, UiRouteKey.ProductUid.placeholder),
            arguments = listOf(navArgument(UiRouteKey.ShoppingUid.key) { defaultValue = "default" })
        ) {
            AddEditProductScreen(navController)
        }
        composable(
            route = UiRoute.Products.copyProductsToShoppingList(UiRouteKey.ProductUid.placeholder),
            arguments = listOf(navArgument(UiRouteKey.IsCopy.key) { defaultValue = true })
        ) {
            CopyMoveProductsScreen(navController)
        }
        composable(
            route = UiRoute.Products.moveProductsToShoppingList(UiRouteKey.ProductUid.placeholder),
            arguments = listOf(navArgument(UiRouteKey.IsCopy.key) { defaultValue = false })
        ) {
            CopyMoveProductsScreen(navController)
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
        dialog(route = UiRoute.Products.editShoppingListTotalScreen(UiRouteKey.ShoppingUid.placeholder)) {
            EditShoppingListTotalScreen(navController)
        }
    }
}

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
        dialog(route = UiRoute.Settings.fontSizesScreen) {
            FontSizeScreen(navController)
        }
        dialog(route = UiRoute.Settings.editCurrencySymbolScreen) {
            EditCurrencySymbolScreen(navController)
        }
        dialog(route = UiRoute.Settings.editTaxRateScreen) {
            EditTaxRateScreen(navController)
        }
        dialog(route = UiRoute.Settings.backupScreen) {
            BackupScreen(navController)
        }
        dialog(route = UiRoute.Settings.maxAutocompletesScreen) {
            MaxAutocompletesScreen(navController)
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

fun NavController.navigate(intent: Intent) {
    context.startActivity(intent)
}

fun NavController.chooseNavigate(intent: Intent, title: String? = null) {
    context.startActivity(Intent.createChooser(intent, title))
}
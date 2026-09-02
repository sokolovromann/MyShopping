package ru.sokolovromann.myshopping.core.data.old

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.data.R
import ru.sokolovromann.myshopping.core.data.datasource.LocalDataStoreScheme
import ru.sokolovromann.myshopping.core.data.datasource.LocalEnvironment
import ru.sokolovromann.myshopping.core.data.di.Api15DataStore
import ru.sokolovromann.myshopping.core.data.old.datasource.Api15LocalDataStoreScheme
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.AfterAddingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterAddingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterArchivingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterEditingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByCartCheckbox
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductCheckbox
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductEnter
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductItem
import ru.sokolovromann.myshopping.core.domain.model.CartsProductsDisplayMode
import ru.sokolovromann.myshopping.core.domain.model.CheckboxColor
import ru.sokolovromann.myshopping.core.domain.model.DeletionCartFromTrash
import ru.sokolovromann.myshopping.core.domain.model.DisplaySuggestionDetails
import ru.sokolovromann.myshopping.core.domain.model.DisplaySuggestionNames
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.GroupProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.KeyboardDisplayDelay
import ru.sokolovromann.myshopping.core.domain.model.LockProductField
import ru.sokolovromann.myshopping.core.domain.model.MoneyFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.ProductsAddingMode
import ru.sokolovromann.myshopping.core.domain.model.ProductsTotalCalculatingMode
import ru.sokolovromann.myshopping.core.domain.model.ProductsView
import ru.sokolovromann.myshopping.core.domain.model.StrikethroughCompletedProducts
import ru.sokolovromann.myshopping.core.domain.model.SuggestionAddingMode
import ru.sokolovromann.myshopping.core.domain.model.SwipeCartActionName
import ru.sokolovromann.myshopping.core.domain.model.SwipeProductActionName
import ru.sokolovromann.myshopping.core.domain.model.Theme
import kotlin.text.orEmpty

class Api15DataStoreMigrationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @Api15DataStore private val api15DataStore: DataStore<Preferences>,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getGeneralPreferences(): Preferences = withContext(ioDispatcher) {
        val api15Preferences = api15DataStore.data.first()
        val theme = when (api15Preferences[Api15LocalDataStoreScheme.User.appNightTheme]) {
            false -> Theme.Light
            else -> Theme.Dark
        }
        val fontSize = when (api15Preferences[Api15LocalDataStoreScheme.User.appFontSize]) {
            "SMALL" -> FontSize.Small
            "LARGE" -> FontSize.Large
            "HUGE" -> FontSize.ExtraLarge
            "HUGE_2" -> FontSize.Huge
            "HUGE_3" -> FontSize.ExtraHuge
            else -> FontSize.Medium
        }
        val dateTimeFormattingMode = context.resources.getString(R.string.data_default_date_time_formatting_mode)
        val is24hourTimeFormat = context.resources.getString(R.string.data_default_is_24_hour_time_format)
        val moneyFormattingMode = when (api15Preferences[Api15LocalDataStoreScheme.User.minMoneyFractionDigits]) {
            0 -> MoneyFormattingMode.Advanced
            else -> MoneyFormattingMode.Simple
        }
        val currency = api15Preferences[Api15LocalDataStoreScheme.User.currency]
        val displayCurrencyToLeft = when (api15Preferences[Api15LocalDataStoreScheme.User.displayCurrencyToLeft]) {
            true -> "Left"
            else -> "Right"
        }
        preferencesOf(
            LocalDataStoreScheme.General.THEME to theme.toString(),
            LocalDataStoreScheme.General.FONT_SIZE to fontSize.toString(),
            LocalDataStoreScheme.General.DATE_TIME_FORMATTING_MODE to dateTimeFormattingMode,
            LocalDataStoreScheme.General.IS_24_HOUR_TIME_FORMAT to is24hourTimeFormat,
            LocalDataStoreScheme.General.MONEY_FORMATTING_MODE to moneyFormattingMode.toString(),
            LocalDataStoreScheme.General.CURRENCY to currency.orEmpty(),
            LocalDataStoreScheme.General.CURRENCY_DISPLAY_SIDE to displayCurrencyToLeft,
            LocalDataStoreScheme.General.KEYBOARD_DISPLAY_DELAY to KeyboardDisplayDelay.Ms50.toString()
        )
    }

    suspend fun getCartsPreferences(): Preferences = withContext(ioDispatcher) {
        val api15Preferences = api15DataStore.data.first()
        val view = when (api15Preferences[Api15LocalDataStoreScheme.User.shoppingsMultiColumns]) {
            true -> "Grid"
            else -> "List"
        }
        val displayMode = when (api15Preferences[Api15LocalDataStoreScheme.User.displayShoppingsProducts]) {
            "COLUMNS" -> CartsProductsDisplayMode.ProductsVertically
            "HIDE", "HIDE_IF_HAS_TITLE" -> CartsProductsDisplayMode.HideProducts
            else -> CartsProductsDisplayMode.ProductsHorizontally
        }
        val sort = if (api15Preferences[Api15LocalDataStoreScheme.User.shoppingsSortFormatted] == true) {
            when (api15Preferences[Api15LocalDataStoreScheme.User.shoppingsSortBy]) {
                "CREATED" -> "ByCreated"
                "LAST_MODIFIED" -> "ByLastModified"
                "NAME" -> "ByName"
                "TOTAL" -> "ByTotal"
                else -> "DoNotSort"
            }
        } else "DoNotSort"
        val sortByAscending = api15Preferences[Api15LocalDataStoreScheme.User.shoppingsSortAscending] ?: true
        val groupByStatus = when (api15Preferences[Api15LocalDataStoreScheme.User.displayCompleted]) {
            "FIRST" -> "CompletedFirst"
            "HIDE" -> "HideCompleted"
            "NO_SPLIT" -> "DoNotGroup"
            else -> "ActiveFirst"
        }
        val displayEmpty = api15Preferences[Api15LocalDataStoreScheme.User.displayEmptyShoppings] ?: true
        val calculateTotal = if (api15Preferences[Api15LocalDataStoreScheme.User.displayMoney] == true) {
            when (api15Preferences[Api15LocalDataStoreScheme.User.displayTotal]) {
                "ALL" -> "AllProducts"
                "COMPLETED" -> "CompletedProducts"
                "ACTIVE" -> "ActiveProducts"
                else -> "DoNotCalculate"
            }
        } else "DoNotCalculate"
        val totalCalculatingMode = when (api15Preferences[Api15LocalDataStoreScheme.User.displayLongTotal]) {
            true -> ProductsTotalCalculatingMode.Long
            else -> ProductsTotalCalculatingMode.Short
        }
        val afterAdding = when (api15Preferences[Api15LocalDataStoreScheme.User.afterAddShopping]) {
            "OPEN_EDIT_SHOPPING_NAME_SCREEN" -> AfterAddingCart.OpenAddNameScreen
            "OPEN_ADD_PRODUCT_SCREEN" -> AfterAddingCart.OpenAddProductScreen
            else -> AfterAddingCart.OpenProductsScreen
        }
        val afterCompleting = when (api15Preferences[Api15LocalDataStoreScheme.User.afterShoppingCompleted]) {
            "ARCHIVE" -> AfterCompletingCart.ArchiveCart
            "DELETE" -> AfterCompletingCart.DeleteCart
            "DELETE_PRODUCTS" -> AfterCompletingCart.DeleteProducts
            "DELETE_LIST_AND_PRODUCTS" -> AfterCompletingCart.DeleteCartAndProducts
            else -> AfterCompletingCart.DoNothing
        }
        val afterArchiving = when (api15Preferences[Api15LocalDataStoreScheme.User.archiveAsCompleted]) {
            true -> AfterArchivingCart.ChangeCartStatusToCompleted
            else -> AfterArchivingCart.DoNothing
        }
        val checkboxColor = when (api15Preferences[Api15LocalDataStoreScheme.User.coloredCheckbox]) {
            true -> CheckboxColor.RedOrGreen
            else -> CheckboxColor.Gray
        }
        fun swipeActionName(name: String?) = when (name) {
            "ARCHIVE" -> SwipeCartActionName.ArchiveOrUnarchiveCart
            "DELETE" -> SwipeCartActionName.DeleteOrRestoreCart
            "COMPLETE" -> SwipeCartActionName.ChangeCartStatusToCompletedOrActive
            "DELETE_PRODUCTS" -> SwipeCartActionName.DeleteProducts
            else -> SwipeCartActionName.Off
        }.toString()
        val deletionFromTrash = when (api15Preferences[Api15LocalDataStoreScheme.User.automaticallyEmptyTrash]) {
            true -> DeletionCartFromTrash.After7Days
            else -> DeletionCartFromTrash.DoNotDelete
        }
        preferencesOf(
            LocalDataStoreScheme.Carts.VIEW to view,
            LocalDataStoreScheme.Carts.PRODUCTS_DISPLAY_MODE to displayMode.toString(),
            LocalDataStoreScheme.Carts.SORT to sort,
            LocalDataStoreScheme.Carts.SORT_BY_ASCENDING to sortByAscending.toString(),
            LocalDataStoreScheme.Carts.GROUP_BY_STATUS to groupByStatus,
            LocalDataStoreScheme.Carts.DISPLAY_EMPTY to displayEmpty.toString(),
            LocalDataStoreScheme.Carts.CALCULATE_PRODUCTS_TOTAL to calculateTotal,
            LocalDataStoreScheme.Carts.PRODUCTS_TOTAL_CALCULATING_MODE to totalCalculatingMode.toString(),
            LocalDataStoreScheme.Carts.AFTER_ADDING to afterAdding.toString(),
            LocalDataStoreScheme.Carts.AFTER_COMPLETING to afterCompleting.toString(),
            LocalDataStoreScheme.Carts.AFTER_ARCHIVING to afterArchiving.toString(),
            LocalDataStoreScheme.Carts.AFTER_TAPPING_BY_CHECKBOX to AfterTappingByCartCheckbox.DoNothing.toString(),
            LocalDataStoreScheme.Carts.CHECKBOX_COLOR to checkboxColor.toString(),
            LocalDataStoreScheme.Carts.SWIPE_LEFT to
                    swipeActionName(api15Preferences[Api15LocalDataStoreScheme.User.swipeShoppingLeft]),
            LocalDataStoreScheme.Carts.SWIPE_RIGHT to
                    swipeActionName(api15Preferences[Api15LocalDataStoreScheme.User.swipeShoppingRight]),
            LocalDataStoreScheme.Carts.DELETION_FROM_TRASH to deletionFromTrash.toString()
        )
    }

    suspend fun getProductsPreferences(): Preferences = withContext(ioDispatcher) {
        val api15Preferences = api15DataStore.data.first()
        val view = when (api15Preferences[Api15LocalDataStoreScheme.User.productsMultiColumns]) {
            true -> ProductsView.Grid
            else -> ProductsView.List
        }
        val groupByStatus = when (api15Preferences[Api15LocalDataStoreScheme.User.displayCompleted]) {
            "FIRST" -> GroupProductsByStatus.CompletedFirst
            "HIDE" -> GroupProductsByStatus.HideCompleted
            "NO_SPLIT" -> GroupProductsByStatus.DoNotGroup
            else -> GroupProductsByStatus.ActiveFirst
        }
        val addingMode = when (api15Preferences[Api15LocalDataStoreScheme.User.displayListOfAutocompletes]) {
            true -> ProductsAddingMode.Advanced
            else -> ProductsAddingMode.Simple
        }
        val calculateTotal = if (api15Preferences[Api15LocalDataStoreScheme.User.displayMoney] == true) {
            when (api15Preferences[Api15LocalDataStoreScheme.User.displayTotal]) {
                "ALL" -> "AllProducts"
                "COMPLETED" -> "CompletedProducts"
                "ACTIVE" -> "ActiveProducts"
                else -> "DoNotCalculate"
            }
        } else "DoNotCalculate"
        val totalCalculatingMode = when (api15Preferences[Api15LocalDataStoreScheme.User.displayLongTotal]) {
            true -> ProductsTotalCalculatingMode.Long
            else -> ProductsTotalCalculatingMode.Short
        }
        val strikethroughCompleted = when (api15Preferences[Api15LocalDataStoreScheme.User.strikethroughCompletedProducts]) {
            true -> StrikethroughCompletedProducts.On
            else -> StrikethroughCompletedProducts.Off
        }
        val afterCompleting = when (api15Preferences[Api15LocalDataStoreScheme.User.afterProductCompleted]) {
            "EDIT" -> AfterCompletingProduct.EditProduct
            "DELETE" -> AfterCompletingProduct.DeleteProduct
            else -> AfterCompletingProduct.DoNothing
        }
        val afterTappingByCheckbox = when (api15Preferences[Api15LocalDataStoreScheme.User.completedWithCheckbox]) {
            true -> AfterTappingByProductCheckbox.ChangeProductStatus
            else -> AfterTappingByProductCheckbox.DoNothing
        }
        val checkboxColor = when (api15Preferences[Api15LocalDataStoreScheme.User.coloredCheckbox]) {
            true -> CheckboxColor.RedOrGreen
            else -> CheckboxColor.Gray
        }
        val afterTappingByItem = when (api15Preferences[Api15LocalDataStoreScheme.User.completedWithCheckbox]) {
            true -> AfterTappingByProductItem.DoNothing
            else -> AfterTappingByProductItem.ChangeProductStatus
        }
        fun swipeActionName(name: String?) = when (name) {
            "EDIT" -> SwipeProductActionName.EditProduct
            "DELETE" -> SwipeProductActionName.DeleteProduct
            "COMPLETE" -> SwipeProductActionName.ChangeProductStatusToCompletedOrActive
            else -> SwipeProductActionName.Off
        }.toString()
        preferencesOf(
            LocalDataStoreScheme.Products.VIEW to view.toString(),
            LocalDataStoreScheme.Products.SORT to "DoNotSort",
            LocalDataStoreScheme.Products.SORT_BY_ASCENDING to "false",
            LocalDataStoreScheme.Products.GROUP_BY_STATUS to groupByStatus.toString(),
            LocalDataStoreScheme.Products.ADDING_MODE to addingMode.toString(),
            LocalDataStoreScheme.Products.CALCULATE_PRODUCTS_TOTAL to calculateTotal,
            LocalDataStoreScheme.Products.PRODUCTS_TOTAL_CALCULATING_MODE to totalCalculatingMode.toString(),
            LocalDataStoreScheme.Products.STRIKETHROUGH_COMPLETED to strikethroughCompleted.toString(),
            LocalDataStoreScheme.Products.AFTER_COMPLETING to afterCompleting.toString(),
            LocalDataStoreScheme.Products.AFTER_TAPPING_BY_CHECKBOX to afterTappingByCheckbox.toString(),
            LocalDataStoreScheme.Products.CHECKBOX_COLOR to checkboxColor.toString(),
            LocalDataStoreScheme.Products.AFTER_TAPPING_BY_ITEM to afterTappingByItem.toString(),
            LocalDataStoreScheme.Products.SWIPE_LEFT to
                    swipeActionName(api15Preferences[Api15LocalDataStoreScheme.User.swipeProductLeft]),
            LocalDataStoreScheme.Products.SWIPE_RIGHT to
                    swipeActionName(api15Preferences[Api15LocalDataStoreScheme.User.swipeProductRight])
        )
    }

    suspend fun getProductsWidgetPreferences(): Preferences = withContext(ioDispatcher) {
        val api15Preferences = api15DataStore.data.first()
        val theme = when (api15Preferences[Api15LocalDataStoreScheme.User.widgetNightTheme]) {
            true -> Theme.Dark
            else -> Theme.Light
        }
        val fontSize = when (api15Preferences[Api15LocalDataStoreScheme.User.widgetFontSize]) {
            "SMALL" -> FontSize.Small
            "LARGE" -> FontSize.Large
            "HUGE" -> FontSize.ExtraLarge
            "HUGE_2" -> FontSize.Huge
            "HUGE_3" -> FontSize.ExtraHuge
            else -> FontSize.Medium
        }
        val groupByStatus = when (api15Preferences[Api15LocalDataStoreScheme.User.widgetDisplayCompleted]) {
            "FIRST" -> GroupProductsByStatus.CompletedFirst
            "HIDE" -> GroupProductsByStatus.HideCompleted
            "NO_SPLIT" -> GroupProductsByStatus.DoNotGroup
            else -> GroupProductsByStatus.ActiveFirst
        }
        preferencesOf(
            LocalDataStoreScheme.ProductsWidget.THEME to theme.toString(),
            LocalDataStoreScheme.ProductsWidget.FONT_SIZE to fontSize.toString(),
            LocalDataStoreScheme.ProductsWidget.SORT to "DoNotSort",
            LocalDataStoreScheme.ProductsWidget.SORT_BY_ASCENDING to "false",
            LocalDataStoreScheme.ProductsWidget.GROUP_BY_STATUS to groupByStatus.toString()
        )
    }

    suspend fun getAddEditProductPreferences(): Preferences = withContext(ioDispatcher) {
        val api15Preferences = api15DataStore.data.first()
        val lockField = when (api15Preferences[Api15LocalDataStoreScheme.User.lockProductElement]) {
            "QUANTITY" -> LockProductField.Quantity
            "PRICE" -> LockProductField.UnitPrice
            else -> LockProductField.Cost
        }
        val afterTappingByEnter = when (api15Preferences[Api15LocalDataStoreScheme.User.enterToSaveProduct]) {
            true -> AfterTappingByProductEnter.SaveProduct
            else ->  AfterTappingByProductEnter.GoToNextField
        }
        val afterAdding = when (api15Preferences[Api15LocalDataStoreScheme.User.afterSaveProduct]) {
            "CLOSE_SCREEN" -> AfterAddingProduct.CloseScreen
            "OPEN_NEW_SCREEN" -> AfterAddingProduct.OpenNewScreen
            else -> AfterAddingProduct.DoNothing
        }
        val taxRate = api15Preferences[Api15LocalDataStoreScheme.User.taxRate]
        preferencesOf(
            LocalDataStoreScheme.AddEditProduct.LOCK_FIELD to lockField.toString(),
            LocalDataStoreScheme.AddEditProduct.AFTER_TAPPING_BY_ENTER to afterTappingByEnter.toString(),
            LocalDataStoreScheme.AddEditProduct.AFTER_ADDING to afterAdding.toString(),
            LocalDataStoreScheme.AddEditProduct.AFTER_EDITING to AfterEditingProduct.CloseScreen.toString(),
            LocalDataStoreScheme.AddEditProduct.TAX to taxRate?.toString().orEmpty()
        )
    }

    suspend fun getSuggestionsPreferences(): Preferences = withContext(ioDispatcher) {
        val api15Preferences = api15DataStore.data.first()
        val addingMode = when (api15Preferences[Api15LocalDataStoreScheme.User.saveProductToAutocompletes]) {
            true -> SuggestionAddingMode.All
            else -> SuggestionAddingMode.Off
        }
        val displayNames = when (api15Preferences[Api15LocalDataStoreScheme.User.maxAutocompletesNames]) {
            0 -> DisplaySuggestionNames.DoNotDisplay
            in 1..5 -> DisplaySuggestionNames.Low
            else -> DisplaySuggestionNames.Medium
        }
        preferencesOf(
            LocalDataStoreScheme.Suggestions.VIEW to "List",
            LocalDataStoreScheme.Suggestions.FIELDS_DISPLAY_MODE to "All",
            LocalDataStoreScheme.Suggestions.SORT to "ByName",
            LocalDataStoreScheme.Suggestions.SORT_BY_ASCENDING to "true",
            LocalDataStoreScheme.Suggestions.ADDING_MODE to addingMode.toString(),
            LocalDataStoreScheme.Suggestions.DISPLAY_NAMES to displayNames.toString(),
            LocalDataStoreScheme.Suggestions.DISPLAY_DETAILS to DisplaySuggestionDetails.Medium.toString()
        )
    }

    suspend fun getBackupPreferences(): Preferences = withContext(ioDispatcher) {
        preferencesOf(
            LocalDataStoreScheme.Backup.DIRECTORY to LocalEnvironment.ROOT_DIRECTORY
        )
    }

    suspend fun getUserConfig(): Preferences = withContext(ioDispatcher) {
        val api15Preferences = api15DataStore.data.first()
        val codeVersion = api15Preferences[Api15LocalDataStoreScheme.Build.userCodeVersion]
        preferencesOf(
            LocalDataStoreScheme.User.API to codeVersion?.toString().orEmpty()
        )
    }

    suspend fun exists(): Boolean = withContext(ioDispatcher) {
        api15DataStore.data.first().asMap().isNotEmpty()
    }
}
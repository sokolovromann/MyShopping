package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.getDisplayDateAndTime
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.theme.AppTypography
import java.util.*
import javax.inject.Inject

class ViewModelMapping @Inject constructor() {

    fun toShoppingListItem(
        shoppingList: ShoppingList,
        preferences: ShoppingListPreferences
    ): ShoppingListItem {
        val reminderText: UiText = if (shoppingList.reminder == null) {
            UiText.Nothing
        } else {
            Calendar.getInstance()
                .apply { timeInMillis = shoppingList.reminder }
                .getDisplayDateAndTime()
        }

        val products = if (shoppingList.productsEmpty) {
            val pair = Pair(
                IconData(),
                toBody(
                    text = toResourcesUiText(R.string.purchases_productsNotFound),
                    fontSize = preferences.fontSize
                )
            )
            listOf(pair)
        } else {
            shoppingList.products.map { product ->
                toIconTextBody(product, preferences)
            }
        }

        val totalText: UiText = if (preferences.displayMoney) {
            toShoppingListsDisplayTotalText(
                shoppingList.calculateTotal(),
                preferences.displayTotal
            )
        } else {
            UiText.Nothing
        }

        return ShoppingListItem(
            uid = shoppingList.uid,
            title = toTitle(
                text = toUiTextOrNothing(shoppingList.name),
                fontSize = preferences.fontSize
            ),
            productsBody = products,
            totalBody = toBody(
                text = totalText,
                fontSize = preferences.fontSize
            ),
            reminderBody = toBody(
                text = reminderText,
                fontSize = preferences.fontSize
            )
        )
    }

    fun toProductItem(
        product: Product,
        preferences: ProductPreferences
    ): ProductItem {
        val displayQuantity = product.quantity.isNotEmpty()
        val displayPrice = product.price.isNotEmpty() && preferences.displayMoney

        val bodyText = if (displayPrice) {
            if (displayQuantity) {
                "${product.quantity} • ${product.calculateTotal()}"
            } else {
                "${product.calculateTotal()}"
            }
        } else {
            if (displayQuantity) "${product.quantity}" else ""
        }

        return ProductItem(
            uid = product.productUid,
            title = toTitle(
                text = toUiTextOrNothing(product.name),
                fontSize = preferences.fontSize
            ),
            body = toBody(
                text = toUiTextOrNothing(bodyText),
                fontSize = preferences.fontSize
            ),
            completed = toCheckbox(product.completed)
        )
    }

    fun toAutocompleteItem(
        autocomplete: Autocomplete,
        preferences: AutocompletePreferences
    ): AutocompleteItem {
        return AutocompleteItem(
            uid = autocomplete.uid,
            title = toTitle(
                text = toUiTextOrNothing(autocomplete.name),
                fontSize = preferences.fontSize,
            )
        )
    }

    fun toAutocompleteItem(
        text: String,
        preferences: ProductPreferences
    ): TextData {
        return toBody(
            text = toUiTextOrNothing(text),
            fontSize = preferences.fontSize,
            appColor = AppColor.OnBackground
        )
    }

    fun toQuantityItem(
        quantity: Quantity,
        preferences: ProductPreferences
    ): QuantityItem {
        return QuantityItem(
            quantity = quantity,
            text = toBody(
                text = toUiTextOrNothing(quantity.toString()),
                fontSize = preferences.fontSize,
                appColor = AppColor.OnBackground
            )
        )
    }

    fun toQuantitySymbolItem(
        quantity: Quantity,
        preferences: ProductPreferences
    ): QuantityItem {
        return QuantityItem(
            quantity = quantity.copy(value = 0f),
            text = toBody(
                text = toUiTextOrNothing(quantity.symbol),
                fontSize = preferences.fontSize,
                appColor = AppColor.OnBackground
            )
        )
    }

    fun toMoneyItem(
        money: Money,
        preferences: ProductPreferences
    ): MoneyItem {
        return MoneyItem(
            money = money,
            text = toBody(
                text = toUiTextOrNothing(money.toString()),
                fontSize = preferences.fontSize,
                appColor = AppColor.OnBackground
            )
        )
    }

    fun toDiscountItem(
        discount: Discount,
        preferences: ProductPreferences
    ): DiscountItem {
        return DiscountItem(
            discount = discount,
            text = toBody(
                text = toUiTextOrNothing(discount.toString()),
                fontSize = preferences.fontSize,
                appColor = AppColor.OnBackground
            )
        )
    }

    fun toSettingsHeader(
        @StringRes header: Int,
        preferences: SettingsPreferences
    ) : TextData {
        return TextData(
            text = toResourcesUiText(header),
            style = AppTypography.Subtitle2.textStyle,
            color = ColorData(appColor = AppColor.OnSurface),
            fontSize = toTextUnit(preferences.fontSize, FontSizeType.Body),
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }

    fun toGeneralSettingsItems(settings: Settings): List<SettingsItem> {
        val values = settings.settingsValues
        val preferences = settings.preferences

        return listOf(
            SettingsItem(
                uid = SettingsUid.NightTheme,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_nightTheme),
                    fontSize = preferences.fontSize
                ),
                checked = toOnSurfaceSwitch(values.nightTheme)
            ),
            SettingsItem(
                uid = SettingsUid.FontSize,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_fontSize),
                    fontSize =  preferences.fontSize
                ),
                body = toBody(
                    text = toFontSizeText(values.fontSize),
                    fontSize =  preferences.fontSize
                )
            ),
            SettingsItem(
                uid = SettingsUid.FirstLetterUppercase,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_firstLetterUppercaseTitle),
                    fontSize =  preferences.fontSize
                ),
                body = toBody(
                    text = toResourcesUiText(R.string.settings_firstLetterUppercaseBody),
                    fontSize =  preferences.fontSize
                ),
                checked = toOnSurfaceSwitch(values.firstLetterUppercase)
            ),
        )
    }

    fun toMoneySettingsItems(settings: Settings): List<SettingsItem> {
        val values = settings.settingsValues
        val preferences = settings.preferences

        return listOf(
            SettingsItem(
                uid = SettingsUid.DisplayMoney,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_displayMoney),
                    fontSize = preferences.fontSize
                ),
                checked = toOnSurfaceSwitch(values.displayMoney)
            ),
            SettingsItem(
                uid = SettingsUid.Currency,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_currencySymbol),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toUiTextOrNothing(values.currency.symbol),
                    fontSize = preferences.fontSize
                )
            ),
            SettingsItem(
                uid = SettingsUid.DisplayCurrencyToLeft,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_displayCurrencySymbolToLeft),
                    fontSize = preferences.fontSize
                ),
                checked = toOnSurfaceSwitch(values.currency.displayToLeft)
            ),
            SettingsItem(
                uid = SettingsUid.TaxRate,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_taxRate),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toUiText(values.taxRate.toString()),
                    fontSize = preferences.fontSize
                )
            )
        )
    }

    fun toPurchasesSettingsItems(settings: Settings): List<SettingsItem> {
        val values = settings.settingsValues
        val preferences = settings.preferences

        return listOf(
            SettingsItem(
                uid = SettingsUid.ShoppingsMultiColumns,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_shoppingsMultiColumns),
                    fontSize = preferences.fontSize
                ),
                checked = toOnSurfaceSwitch(values.shoppingsMultiColumns)
            ),
            SettingsItem(
                uid = SettingsUid.ProductsMultiColumns,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_productsMultiColumns),
                    fontSize = preferences.fontSize
                ),
                checked = toOnSurfaceSwitch(values.productsMultiColumns)
            ),
            SettingsItem(
                uid = SettingsUid.DisplayAutocomplete,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_displayAutocomplete),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toDisplayAutocompleteText(values.productsDisplayAutocomplete),
                    fontSize = preferences.fontSize
                )
            ),
            SettingsItem(
                uid = SettingsUid.EditCompleted,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_editCompletedProductTitle),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toResourcesUiText(R.string.settings_editCompletedProductBody),
                    fontSize = preferences.fontSize
                ),
                checked = toOnSurfaceSwitch(values.productsEditCompleted)
            ),
            SettingsItem(
                uid = SettingsUid.AddProduct,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_addLastProductTitle),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toResourcesUiText(R.string.settings_addLastProductBody),
                    fontSize = preferences.fontSize
                ),
                checked = toOnSurfaceSwitch(values.productsAddLastProduct)
            )
        )
    }

    fun toAboutSettingsItems(settings: Settings): List<SettingsItem> {
        val values = settings.settingsValues
        val preferences = settings.preferences

        return listOf(
            SettingsItem(
                uid = SettingsUid.NoUId,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_developer),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toUiText(values.developerName),
                    fontSize = preferences.fontSize
                )
            ),
            SettingsItem(
                uid = SettingsUid.Email,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_emailTitle),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toResourcesUiText(R.string.settings_emailBody),
                    fontSize = preferences.fontSize
                )
            ),
            SettingsItem(
                uid = SettingsUid.NoUId,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_appVersion),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toUiText(values.appVersion),
                    fontSize = preferences.fontSize
                )
            ),
            SettingsItem(
                uid = SettingsUid.Github,
                title = toTitle(
                    text = toResourcesUiText(R.string.settings_githubTitle),
                    fontSize = preferences.fontSize
                ),
                body = toBody(
                    text = toResourcesUiText(R.string.settings_githubBody),
                    fontSize = preferences.fontSize
                )
            )
        )
    }

    fun toFontSizeText(fontSize: FontSize): UiText {
        return when (fontSize) {
            FontSize.TINY -> toResourcesUiText(R.string.settings_tinyFontSize)
            FontSize.SMALL -> toResourcesUiText(R.string.settings_smallFontSize)
            FontSize.MEDIUM -> toResourcesUiText(R.string.settings_mediumFontSize)
            FontSize.LARGE -> toResourcesUiText(R.string.settings_largeFontSize)
            FontSize.HUGE -> toResourcesUiText(R.string.settings_hugeFontSize)
        }
    }

    fun toDisplayAutocompleteText(displayAutocomplete: DisplayAutocomplete): UiText {
        return when (displayAutocomplete) {
            DisplayAutocomplete.ALL -> toResourcesUiText(R.string.settings_displayAllAutocomplete)
            DisplayAutocomplete.NAME -> toResourcesUiText(R.string.settings_displayNameAutocomplete)
            DisplayAutocomplete.HIDE -> toResourcesUiText(R.string.settings_hideAutocomplete)
        }
    }

    fun toPurchasesItemMenu(fontSize: FontSize): PurchasesItemMenu {
        return PurchasesItemMenu(
            moveToArchiveBody = toBody(
                text = UiText.FromResources(R.string.purchases_moveShoppingListToArchive),
                fontSize = fontSize
            ),
            moveToTrashBody = toBody(
                text = UiText.FromResources(R.string.purchases_moveShoppingListToTrash),
                fontSize = fontSize
            )
        )
    }

    fun toArchiveItemMenu(fontSize: FontSize): ArchiveItemMenu {
        return ArchiveItemMenu(
            moveToPurchasesBody = toBody(
                text = UiText.FromResources(R.string.archive_moveShoppingListToPurchases),
                fontSize = fontSize
            ),
            moveToTrashBody = toBody(
                text = UiText.FromResources(R.string.archive_moveShoppingListToTrash),
                fontSize = fontSize
            )
        )
    }

    fun toTrashItemMenu(fontSize: FontSize): TrashItemMenu {
        return TrashItemMenu(
            moveToPurchasesBody = toBody(
                text = UiText.FromResources(R.string.trash_moveShoppingListToPurchases),
                fontSize = fontSize
            ),
            moveToArchiveBody = toBody(
                text = UiText.FromResources(R.string.trash_moveShoppingListToArchive),
                fontSize = fontSize
            ),
            deleteBody = toBody(
                text = UiText.FromResources(R.string.trash_deleteShoppingList),
                fontSize = fontSize
            )
        )
    }

    fun toProductsItemMenu(fontSize: FontSize): ProductsItemMenu {
        return ProductsItemMenu(
            editBody = toBody(
                text = UiText.FromResources(R.string.products_editProduct),
                fontSize = fontSize
            ),
            deleteBody = toBody(
                text = UiText.FromResources(R.string.products_deleteProduct),
                fontSize = fontSize
            ),
            copyToShoppingListBody = toBody(
                text = UiText.FromResources(R.string.products_copyProductToShoppingList),
                fontSize = fontSize
            ),
            moveToShoppingListBody = toBody(
                text = UiText.FromResources(R.string.products_moveProductToShoppingList),
                fontSize = fontSize
            )
        )
    }

    fun toProductsMenu(fontSize: FontSize): ProductsMenu {
        return ProductsMenu(
            editNameBody = toBody(
                text = UiText.FromResources(R.string.products_editShoppingListName),
                fontSize = fontSize
            ),
            editReminderBody = toBody(
                text = UiText.FromResources(R.string.products_editShoppingListReminder),
                fontSize = fontSize
            ),
            calculateChangeBody = toBody(
                text = UiText.FromResources(R.string.products_calculateChange),
                fontSize = fontSize
            ),
            deleteProductsBody = toBody(
                text = UiText.FromResources(R.string.products_deleteProducts),
                fontSize = fontSize
            ),
            shareBody = toBody(
                text = UiText.FromResources(R.string.products_shareProducts),
                fontSize = fontSize
            )
        )
    }

    fun toAutocompleteItemMenu(fontSize: FontSize): AutocompleteItemMenu {
        return AutocompleteItemMenu(
            editBody = toBody(
                text = UiText.FromResources(R.string.autocompletes_editAutocomplete),
                fontSize = fontSize
            ),
            deleteBody = toBody(
                text = UiText.FromResources(R.string.autocompletes_deleteAutocomplete),
                fontSize = fontSize
            )
        )
    }

    fun toIconTextBody(product: Product, preferences: ShoppingListPreferences): Pair<IconData, TextData> {
        val icon = IconData(
            icon = if (product.completed) {
                UiIcon.FromResources(R.drawable.ic_all_check_box)
            } else {
                UiIcon.FromResources(R.drawable.ic_all_check_box_outline)
            },
            size = toDp(preferences.fontSize, FontSizeType.Body)
        )

        val displayQuantity = product.quantity.isNotEmpty()
        val displayPrice = product.price.isNotEmpty() && preferences.displayMoney

        val productBody = if (displayPrice) {
            if (displayQuantity) {
                " • ${product.quantity} • ${product.calculateTotal()}"
            } else {
                " • ${product.calculateTotal()}"
            }
        } else {
            if (displayQuantity) " • ${product.quantity}" else ""
        }

        val shortText = preferences.multiColumns &&
                preferences.screenSize == ScreenSize.SMARTPHONE

        val uiText: UiText = if (shortText) {
            UiText.FromString(product.name)
        } else {
            val str = "${product.name}$productBody"
            UiText.FromString(str)
        }

        val text = toBody(
            text = uiText,
            fontSize = preferences.fontSize
        )

        return Pair(icon, text)
    }

    fun toShoppingListsSortBody(sortBy: SortBy, fontSize: FontSize): TextData {
        val text: UiText = when (sortBy) {
            SortBy.CREATED -> UiText.FromResources(R.string.sortShoppingLists_byCreated)
            SortBy.LAST_MODIFIED -> UiText.FromResources(R.string.sortShoppingLists_byLastModified)
            SortBy.NAME -> UiText.FromResources(R.string.sortShoppingLists_byName)
            SortBy.TOTAL -> UiText.FromResources(R.string.sortShoppingLists_byTotal)
        }
        return toBody(
            text = text,
            fontSize = fontSize,
            appColor = AppColor.OnBackground
        )
    }

    fun toShoppingListsLocationBody(
        archived: Boolean,
        deleted: Boolean,
        fontSize: FontSize
    ): TextData {
        val text: UiText = if (archived && !deleted) {
            UiText.FromResources(R.string.shoppingListsLocation_archive)
        } else if (!archived && deleted) {
            UiText.FromResources(R.string.shoppingListsLocation_trash)
        } else {
            UiText.FromResources(R.string.shoppingListsLocation_purchases)
        }
        return toBody(
            text = text,
            fontSize = fontSize,
            appColor = AppColor.OnBackground
        )
    }

    fun toAutocompletesSortBody(sortBy: SortBy, fontSize: FontSize): TextData {
        val text: UiText = when (sortBy) {
            SortBy.CREATED -> UiText.FromResources(R.string.sortAutocompletes_byCreated)
            SortBy.NAME -> UiText.FromResources(R.string.sortAutocompletes_byName)
            else -> UiText.Nothing
        }
        return toBody(
            text = text,
            fontSize = fontSize,
            appColor = AppColor.OnBackground
        )
    }

    fun toShoppingListsTotalTitle(
        total: Money,
        displayTotal: DisplayTotal,
        fontSize: FontSize
    ): TextData {
        return toTitle(
            text = toShoppingListsDisplayTotalText(total, displayTotal),
            fontSize = fontSize,
            appColor = AppColor.OnBackground
        )
    }

    fun toProductsTotalTitle(
        total: Money,
        displayTotal: DisplayTotal,
        fontSize: FontSize
    ): TextData {
        return toTitle(
            text = toProductsDisplayTotalText(total, displayTotal),
            fontSize = fontSize,
            appColor = AppColor.OnBackground
        )
    }

    fun toDiscountAsPercentBody(asPercent: Boolean, fontSize: FontSize): TextData {
        val text: UiText = if (asPercent) {
            UiText.FromResources(R.string.addEditProduct_discountAsPercents)
        } else {
            UiText.FromResources(R.string.addEditProduct_discountAsMoney)
        }
        return toBody(
            text = text,
            fontSize = fontSize,
            appColor = AppColor.OnBackground
        )
    }

    fun toSortAscendingIconBody(ascending: Boolean): IconData {
        val icon = if (ascending) {
            UiIcon.FromVector(Icons.Default.KeyboardArrowUp)
        } else {
            UiIcon.FromVector(Icons.Default.KeyboardArrowDown)
        }
        return IconData(icon = icon)
    }

    fun toDisplayCompletedIconBody(): IconData {
        return IconData(
            icon = UiIcon.FromResources(R.drawable.ic_all_display_completed),
        )
    }

    fun toMenuIconBody(): IconData {
        return IconData(
            icon = toUiIcon(Icons.Default.MoreVert),
        )
    }

    fun toShoppingListsSortMenu(sortBy: SortBy, fontSize: FontSize): ShoppingListsSortMenu {
        return ShoppingListsSortMenu(
            title = toTitle(
                text = UiText.FromResources(R.string.sortShoppingLists_title),
                fontSize = fontSize
            ),
            byCreatedBody = toBody(
                text = UiText.FromResources(R.string.sortShoppingLists_byCreated),
                fontSize = fontSize
            ),
            byCreatedSelected = toRadioButton(
                selected = sortBy == SortBy.CREATED
            ),
            byLastModifiedBody = toBody(
                text = UiText.FromResources(R.string.sortShoppingLists_byLastModified),
                fontSize = fontSize
            ),
            byLastModifiedSelected = toRadioButton(
                selected = sortBy == SortBy.LAST_MODIFIED
            ),
            byNameBody = toBody(
                text = UiText.FromResources(R.string.sortShoppingLists_byName),
                fontSize = fontSize
            ),
            byNameSelected = toRadioButton(
                selected = sortBy == SortBy.NAME
            ),
            byTotalBody = toBody(
                text = UiText.FromResources(R.string.sortShoppingLists_byTotal),
                fontSize = fontSize
            ),
            byTotalSelected = toRadioButton(
                selected = sortBy == SortBy.TOTAL
            )
        )
    }

    fun toShoppingListsLocationMenu(
        archived: Boolean,
        deleted: Boolean,
        fontSize: FontSize
    ): ShoppingListsLocationMenu {
        return ShoppingListsLocationMenu(
            title = toTitle(
                text = UiText.FromResources(R.string.shoppingListsLocation_title),
                fontSize = fontSize
            ),
            purchasesBody = toBody(
                text = UiText.FromResources(R.string.shoppingListsLocation_purchases),
                fontSize = fontSize
            ),
            purchasesSelected = toRadioButton(
                selected = !archived && !deleted
            ),
            archiveBody = toBody(
                text = UiText.FromResources(R.string.shoppingListsLocation_archive),
                fontSize = fontSize
            ),
            archiveSelected = toRadioButton(
                selected = archived && !deleted
            ),
            trashBody = toBody(
                text = UiText.FromResources(R.string.shoppingListsLocation_trash),
                fontSize = fontSize
            ),
            trashSelected = toRadioButton(
                selected = !archived && deleted
            ),
        )
    }

    fun toProductsSortMenu(sortBy: SortBy, fontSize: FontSize): ProductsSortMenu {
        return ProductsSortMenu(
            title = toTitle(
                text = UiText.FromResources(R.string.sortProducts_title),
                fontSize = fontSize
            ),
            byCreatedBody = toBody(
                text = UiText.FromResources(R.string.sortProducts_byCreated),
                fontSize = fontSize
            ),
            byCreatedSelected = toRadioButton(
                selected = sortBy == SortBy.CREATED
            ),
            byLastModifiedBody = toBody(
                text = UiText.FromResources(R.string.sortProducts_byLastModified),
                fontSize = fontSize
            ),
            byLastModifiedSelected = toRadioButton(
                selected = sortBy == SortBy.LAST_MODIFIED
            ),
            byNameBody = toBody(
                text = UiText.FromResources(R.string.sortProducts_byName),
                fontSize = fontSize
            ),
            byNameSelected = toRadioButton(
                selected = sortBy == SortBy.NAME
            ),
            byTotalBody = toBody(
                text = UiText.FromResources(R.string.sortProducts_byTotal),
                fontSize = fontSize
            ),
            byTotalSelected = toRadioButton(
                selected = sortBy == SortBy.TOTAL
            )
        )
    }

    fun toAutocompletesSortMenu(sortBy: SortBy, fontSize: FontSize): AutocompletesSortMenu {
        return AutocompletesSortMenu(
            title = toTitle(
                text = UiText.FromResources(R.string.sortAutocompletes_title),
                fontSize = fontSize
            ),
            byCreatedBody = toBody(
                text = UiText.FromResources(R.string.sortAutocompletes_byCreated),
                fontSize = fontSize
            ),
            byCreatedSelected = toRadioButton(
                selected = sortBy == SortBy.CREATED
            ),
            byNameBody = toBody(
                text = UiText.FromResources(R.string.sortAutocompletes_byName),
                fontSize = fontSize
            ),
            byNameSelected = toRadioButton(
                selected = sortBy == SortBy.NAME
            )
        )
    }

    fun toDiscountAsPercentMenuMenu(asPercent: Boolean, fontSize: FontSize): DiscountAsPercentMenu {
        return DiscountAsPercentMenu(
            asPercentBody = toBody(
                text = UiText.FromResources(R.string.addEditProduct_discountAsPercents),
                fontSize = fontSize
            ),
            asPercentSelected = toRadioButton(
                selected = asPercent
            ),
            asMoneyBody = toBody(
                text = UiText.FromResources(R.string.addEditProduct_discountAsMoney),
                fontSize = fontSize
            ),
            asMoneySelected = toRadioButton(
                selected = !asPercent
            ),
        )
    }

    fun toShoppingListsCompletedMenu(
        displayCompleted: DisplayCompleted,
        fontSize: FontSize
    ): ShoppingListsCompletedMenu {
        return ShoppingListsCompletedMenu(
            title = toTitle(
                text = UiText.FromResources(R.string.displayShoppingListsCompleted_title),
                fontSize = fontSize
            ),
            firstBody = toBody(
                text = UiText.FromResources(R.string.displayShoppingListsCompleted_firstCompleted),
                fontSize = fontSize
            ),
            firstSelected = toRadioButton(
                selected = displayCompleted == DisplayCompleted.FIRST
            ),
            lastBody = toBody(
                text = UiText.FromResources(R.string.displayShoppingListsCompleted_lastCompleted),
                fontSize = fontSize
            ),
            lastSelected = toRadioButton(
                selected = displayCompleted == DisplayCompleted.LAST
            ),
            hideBody = toBody(
                text = UiText.FromResources(R.string.displayShoppingListsCompleted_hideCompleted),
                fontSize = fontSize
            ),
            hideSelected = toRadioButton(
                selected = displayCompleted == DisplayCompleted.HIDE
            )
        )
    }

    fun toProductsCompletedMenu(
        displayCompleted: DisplayCompleted,
        fontSize: FontSize
    ): ProductsCompletedMenu {
        return ProductsCompletedMenu(
            title = toTitle(
                text = UiText.FromResources(R.string.displayProductsCompleted_title),
                fontSize = fontSize
            ),
            firstBody = toBody(
                text = UiText.FromResources(R.string.displayProductsCompleted_firstCompleted),
                fontSize = fontSize
            ),
            firstSelected = toRadioButton(
                selected = displayCompleted == DisplayCompleted.FIRST
            ),
            lastBody = toBody(
                text = UiText.FromResources(R.string.displayProductsCompleted_lastCompleted),
                fontSize = fontSize
            ),
            lastSelected = toRadioButton(
                selected = displayCompleted == DisplayCompleted.LAST
            ),
            hideBody = toBody(
                text = UiText.FromResources(R.string.displayShoppingListsCompleted_hideCompleted),
                fontSize = fontSize
            ),
            hideSelected = toRadioButton(
                selected = displayCompleted == DisplayCompleted.HIDE
            )
        )
    }

    fun toShoppingListsTotalMenu(
        displayTotal: DisplayTotal,
        fontSize: FontSize
    ): ShoppingListsTotalMenu {
        return ShoppingListsTotalMenu(
            title = toTitle(
                text = UiText.FromResources(R.string.displayShoppingListsTotal_title),
                fontSize = fontSize
            ),
            allBody = toBody(
                text = UiText.FromResources(R.string.displayShoppingListsTotal_allTotal),
                fontSize = fontSize
            ),
            allSelected = toRadioButton(
                selected = displayTotal == DisplayTotal.ALL
            ),
            completedBody = toBody(
                text = UiText.FromResources(R.string.displayShoppingListsTotal_completedTotal),
                fontSize = fontSize
            ),
            completedSelected = toRadioButton(
                selected = displayTotal == DisplayTotal.COMPLETED
            ),
            activeBody = toBody(
                text = UiText.FromResources(R.string.displayShoppingListsTotal_activeTotal),
                fontSize = fontSize
            ),
            activeSelected = toRadioButton(
                selected = displayTotal == DisplayTotal.ACTIVE
            )
        )
    }

    fun toProductsTotalMenu(
        displayTotal: DisplayTotal,
        fontSize: FontSize
    ): ProductsTotalMenu {
        return ProductsTotalMenu(
            title = toTitle(
                text = UiText.FromResources(R.string.displayProductsTotal_title),
                fontSize = fontSize
            ),
            allBody = toBody(
                text = UiText.FromResources(R.string.displayProductsTotal_allTotal),
                fontSize = fontSize
            ),
            allSelected = toRadioButton(
                selected = displayTotal == DisplayTotal.ALL
            ),
            completedBody = toBody(
                text = UiText.FromResources(R.string.displayProductsTotal_completedTotal),
                fontSize = fontSize
            ),
            completedSelected = toRadioButton(
                selected = displayTotal == DisplayTotal.COMPLETED
            ),
            activeBody = toBody(
                text = UiText.FromResources(R.string.displayProductsTotal_activeTotal),
                fontSize = fontSize
            ),
            activeSelected = toRadioButton(
                selected = displayTotal == DisplayTotal.ACTIVE
            )
        )
    }

    fun toFontSizeMenu(fontSize: FontSize): FontSizeMenu {
        return FontSizeMenu(
            tinyBody = toBody(
                text = toResourcesUiText(R.string.settings_tinyFontSize),
                fontSize = fontSize
            ),
            tinySelected = toRadioButton(selected = fontSize == FontSize.TINY),
            smallBody = toBody(
                text = toResourcesUiText(R.string.settings_smallFontSize),
                fontSize = fontSize
            ),
            smallSelected = toRadioButton(selected = fontSize == FontSize.SMALL),
            mediumBody = toBody(
                text = toResourcesUiText(R.string.settings_mediumFontSize),
                fontSize = fontSize
            ),
            mediumSelected = toRadioButton(selected = fontSize == FontSize.MEDIUM),
            largeBody = toBody(
                text = toResourcesUiText(R.string.settings_largeFontSize),
                fontSize = fontSize
            ),
            largeSelected = toRadioButton(selected = fontSize == FontSize.LARGE),
            hugeBody = toBody(
                text = toResourcesUiText(R.string.settings_hugeFontSize),
                fontSize = fontSize
            ),
            hugeSelected = toRadioButton(selected = fontSize == FontSize.HUGE)
        )
    }

    fun toDisplayAutocompleteMenu(
        displayAutocomplete: DisplayAutocomplete,
        fontSize: FontSize
    ): DisplayAutocompleteMenu {
        return DisplayAutocompleteMenu(
            allBody = toBody(
                text = toResourcesUiText(R.string.settings_displayAllAutocomplete),
                fontSize = fontSize
            ),
            allSelected = toRadioButton(selected = displayAutocomplete == DisplayAutocomplete.ALL),
            nameBody = toBody(
                text = toResourcesUiText(R.string.settings_displayNameAutocomplete),
                fontSize = fontSize
            ),
            nameSelected = toRadioButton(selected = displayAutocomplete == DisplayAutocomplete.NAME),
            hideBody = toBody(
                text = toResourcesUiText(R.string.settings_hideAutocomplete),
                fontSize = fontSize
            ),
            hideSelected = toRadioButton(selected = displayAutocomplete == DisplayAutocomplete.HIDE)
        )
    }

    fun toTextFieldValue(value: Long): TextFieldValue {
        val text = value.toString()
        return TextFieldValue(
            text = text,
            selection = TextRange(text.length),
            composition = TextRange(text.length)
        )
    }

    fun toTextFieldValue(value: Int): TextFieldValue {
        val text = value.toString()
        return TextFieldValue(
            text = text,
            selection = TextRange(text.length),
            composition = TextRange(text.length)
        )
    }

    fun toTextFieldValue(value: Float): TextFieldValue {
        val text = value.toString()
        return TextFieldValue(
            text = text,
            selection = TextRange(text.length),
            composition = TextRange(text.length)
        )
    }

    fun toTextFieldValue(value: String): TextFieldValue {
        return TextFieldValue(
            text = value,
            selection = TextRange(value.length),
            composition = TextRange(value.length)
        )
    }

    fun toLong(textFieldValue: TextFieldValue): Long? {
        return textFieldValue.text.toLongOrNull()
    }

    fun toLong(uiText: UiText): Long? {
        return if (uiText is UiText.FromLong) uiText.value else null
    }

    fun toInt(textFieldValue: TextFieldValue): Int? {
        return textFieldValue.text.toIntOrNull()
    }

    fun toInt(uiText: UiText): Int? {
        return if (uiText is UiText.FromInt) uiText.value else null
    }

    fun toFloat(textFieldValue: TextFieldValue): Float? {
        return textFieldValue.text.toFloatOrNull()
    }

    fun toFloat(uiText: UiText): Float? {
        return if (uiText is UiText.FromFloat) uiText.value else null
    }

    fun toString(textFieldValue: TextFieldValue): String {
        return textFieldValue.text
    }

    fun toString(uiText: UiText): String {
        return if (uiText is UiText.FromString) uiText.value else ""
    }

    fun toTextUnit(fontSize: FontSize, type: FontSizeType): TextUnit {
        return when (type) {
            FontSizeType.Header -> when (fontSize) {
                FontSize.TINY -> 18.sp
                FontSize.SMALL -> 22.sp
                FontSize.MEDIUM -> 24.sp
                FontSize.LARGE -> 28.sp
                FontSize.HUGE -> 32.sp
            }

            FontSizeType.Title -> when (fontSize) {
                FontSize.TINY -> 14.sp
                FontSize.SMALL -> 16.sp
                FontSize.MEDIUM -> 18.sp
                FontSize.LARGE -> 20.sp
                FontSize.HUGE -> 22.sp
            }

            FontSizeType.Body -> when (fontSize) {
                FontSize.TINY -> 12.sp
                FontSize.SMALL -> 14.sp
                FontSize.MEDIUM -> 16.sp
                FontSize.LARGE -> 18.sp
                FontSize.HUGE -> 20.sp
            }
        }
    }

    fun toDp(fontSize: FontSize, type: FontSizeType): Dp {
        return when (type) {
            FontSizeType.Header -> when (fontSize) {
                FontSize.TINY -> 18.dp
                FontSize.SMALL -> 22.dp
                FontSize.MEDIUM -> 24.dp
                FontSize.LARGE -> 28.dp
                FontSize.HUGE -> 32.dp
            }

            FontSizeType.Title -> when (fontSize) {
                FontSize.TINY -> 14.dp
                FontSize.SMALL -> 16.dp
                FontSize.MEDIUM -> 18.dp
                FontSize.LARGE -> 20.dp
                FontSize.HUGE -> 22.dp
            }

            FontSizeType.Body -> when (fontSize) {
                FontSize.TINY -> 12.dp
                FontSize.SMALL -> 14.dp
                FontSize.MEDIUM -> 16.dp
                FontSize.LARGE -> 18.dp
                FontSize.HUGE -> 20.dp
            }
        }
    }

    fun toUiText(value: Long): UiText {
        return UiText.FromLong(value)
    }

    fun toUiText(value: Int): UiText {
        return UiText.FromInt(value)
    }

    fun toUiText(value: Float): UiText {
        return UiText.FromFloat(value)
    }

    fun toUiText(value: String): UiText {
        return UiText.FromString(value)
    }

    fun toUiText(value: String, firstLetterUppercase: Boolean): UiText {
        val formatValue = value.formatFirst(firstLetterUppercase)
        return UiText.FromString(formatValue)
    }

    fun toUiTextOrNothing(value: String): UiText {
        return if (value.isEmpty()) {
             UiText.Nothing
        } else {
            UiText.FromString(value)
        }
    }

    fun toUiTextOrNothing(value: String, firstLetterUppercase: Boolean): UiText {
        if (value.isEmpty()) {
            return UiText.Nothing
        }

        val formatValue = value.formatFirst(firstLetterUppercase)
        return UiText.FromString(formatValue)
    }

    fun toResourcesUiText(@StringRes id: Int): UiText {
        return UiText.FromResources(id)
    }

    fun toResourcesUiText(@StringRes id: Int, vararg args: Any): UiText {
        return UiText.FromResourcesWithArgs(id, args)
    }

    fun toUiIcon(imageVector: ImageVector): UiIcon {
        return UiIcon.FromVector(imageVector)
    }

    fun toUiIcon(@DrawableRes id: Int): UiIcon {
        return UiIcon.FromResources(id)
    }

    fun toRadioButton(
        selected: Boolean,
        appColor: AppColor = AppColor.OnSurface
    ) = RadioButtonData(
        selected = selected,
        selectedColor = ColorData(appColor = appColor),
        unselectedColor = ColorData(appColor = appColor)
    )

    fun toCheckbox(
        checked: Boolean,
        checkedAppColor: AppColor = AppColor.OnSurface,
        checkmarkAppColor: AppColor = AppColor.Surface
    ) = CheckboxData(
        checked = checked,
        checkedColor = ColorData(appColor = checkedAppColor, alpha = 0.7f),
        uncheckedColor = ColorData(appColor = checkedAppColor),
        checkmarkColor = ColorData(appColor = checkmarkAppColor)
    )

    fun toOnSurfaceSwitch(checked: Boolean) = SwitchData(
        checked = checked,
        checkedThumbColor = ColorData(appColor = AppColor.Secondary),
        checkedTrackColor = ColorData(appColor = AppColor.OnSurface),
        uncheckedThumbColor = ColorData(appColor = AppColor.Surface),
        uncheckedTrackColor = ColorData(appColor = AppColor.OnSurface)
    )

    fun toOnBackgroundSwitch(checked: Boolean) = SwitchData(
        checked = checked,
        checkedThumbColor = ColorData(appColor = AppColor.Secondary),
        checkedTrackColor = ColorData(appColor = AppColor.OnBackground),
        uncheckedThumbColor = ColorData(appColor = AppColor.Background),
        uncheckedTrackColor = ColorData(appColor = AppColor.OnBackground)
    )

    fun toOnTopAppBarHeader(text: UiText, fontSize: FontSize) = TextData(
        text = text,
        style = AppTypography.H5.textStyle,
        color = ColorData(light = AppColor.OnPrimary, dark = AppColor.OnSurface),
        fontSize = toTextUnit(fontSize, FontSizeType.Header),
        fontWeight = FontWeight.Normal,
        overflow = TextOverflow.Ellipsis,
        maxLines = 2
    )

    fun toOnNavigationDrawerHeader(
        text: UiText = UiText.FromResources(R.string.route_header),
        fontSize: FontSize = FontSize.MEDIUM
    ) = TextData(
        text = text,
        style = AppTypography.H5.textStyle,
        color = ColorData(appColor = AppColor.OnBackground),
        fontSize = toTextUnit(fontSize, FontSizeType.Header)
    )

    fun toOnDialogHeader(
        text: UiText,
        fontSize: FontSize,
        appColor: AppColor = AppColor.OnSurface
    ) = TextData(
        text = text,
        style = AppTypography.H5.textStyle,
        color = ColorData(appColor = appColor),
        fontSize = toTextUnit(fontSize, FontSizeType.Header)
    )

    fun toTitle(
        text: UiText,
        fontSize: FontSize,
        appColor: AppColor = AppColor.OnSurface
    ) = TextData(
        text = text,
        style = AppTypography.Subtitle1.textStyle,
        color = ColorData(appColor = appColor),
        fontSize = toTextUnit(fontSize, FontSizeType.Title),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
    )

    fun toBody(
        text: UiText,
        fontSize: FontSize,
        appColor: AppColor = AppColor.OnSurface
    ) = TextData(
        text = text,
        style = AppTypography.Body1.textStyle,
        color = ColorData(appColor = appColor, alpha = 0.7f),
        fontSize = toTextUnit(fontSize, FontSizeType.Body),
        overflow = TextOverflow.Ellipsis,
        maxLines = 2
    )

    fun toOnSurfaceIcon(icon: UiIcon, size: Dp = Dp.Unspecified) = IconData(
        icon = icon,
        size = size,
        tint = ColorData(
            appColor = AppColor.OnSurface,
            alpha = 0.7f
        )
    )

    fun toOnBackgroundIcon(icon: UiIcon, size: Dp = Dp.Unspecified) = IconData(
        icon = icon,
        size = size,
        tint = ColorData(
            appColor = AppColor.OnBackground,
            alpha = 0.7f
        )
    )

    fun toOnTopAppBar(icon: UiIcon, size: Dp = Dp.Unspecified) = IconData(
        icon = icon,
        size = size,
        tint = ColorData(
            light = AppColor.OnPrimary,
            lightAlpha = 0.7f,
            dark = AppColor.OnSurface,
            darkAlpha = 0.7f
        )
    )

    fun toOnBottomAppBar(icon: UiIcon, size: Dp = Dp.Unspecified) = IconData(
        icon = icon,
        size = size,
        tint = ColorData(
            appColor = AppColor.OnBackground,
            alpha = 0.7f
        )
    )

    fun toOnFloatingActionButton(icon: UiIcon, size: Dp = Dp.Unspecified) = IconData(
        icon = icon,
        size = size,
        tint = ColorData(appColor = AppColor.OnSecondary)
    )

    fun toRouteItem(route: UiRoute, text: UiText, icon: UiIcon, checked: Boolean) = RouteItemData(
        route = route,
        name = toBody(
            text = text,
            fontSize = FontSize.MEDIUM,
            appColor = if (checked) AppColor.Secondary else AppColor.OnSurface
        ),
        icon = IconData(
            icon = icon,
            tint = if (checked) {
                ColorData(appColor = AppColor.Secondary)
            } else {
                ColorData(
                    appColor = AppColor.OnSurface,
                    alpha = 0.7f
                )
            }
        ),
        checked = checked
    )

    fun toNavigationDrawerItems(checked: UiRoute): List<RouteItemData> = listOf(
        toRouteItem(
            route = UiRoute.Purchases,
            text = toResourcesUiText(R.string.route_purchasesName),
            icon = toUiIcon(R.drawable.ic_all_purchases),
            checked = checked == UiRoute.Purchases
        ),
        toRouteItem(
            route = UiRoute.Archive,
            text = toResourcesUiText(R.string.route_archiveName),
            icon = toUiIcon(R.drawable.ic_all_archive),
            checked = checked == UiRoute.Archive
        ),
        toRouteItem(
            route = UiRoute.Trash,
            text = toResourcesUiText(R.string.route_trashName),
            icon = toUiIcon(Icons.Default.Delete),
            checked = checked == UiRoute.Trash
        ),
        toRouteItem(
            route = UiRoute.Autocompletes,
            text = toResourcesUiText(R.string.route_autocompletesName),
            icon = toUiIcon(Icons.Default.List),
            checked = checked == UiRoute.Autocompletes
        ),
        toRouteItem(
            route = UiRoute.Settings,
            text = toResourcesUiText(R.string.route_settingsName),
            icon = toUiIcon(Icons.Default.Settings),
            checked = checked == UiRoute.Settings
        )
    )

    fun toShoppingListsDisplayTotalText(total: Money, displayTotal: DisplayTotal): UiText {
        return when (displayTotal) {
            DisplayTotal.ALL -> UiText.FromResourcesWithArgs(
                R.string.shoppingListsTotal_text_allTotal,
                total.toString()
            )
            DisplayTotal.COMPLETED -> UiText.FromResourcesWithArgs(
                R.string.shoppingListsTotal_text_completedTotal,
                total.toString()
            )
            DisplayTotal.ACTIVE -> UiText.FromResourcesWithArgs(
                R.string.shoppingListsTotal_text_activeTotal,
                total.toString()
            )
        }
    }

    fun toProductsDisplayTotalText(total: Money, displayTotal: DisplayTotal): UiText {
        return when (displayTotal) {
            DisplayTotal.ALL -> UiText.FromResourcesWithArgs(
                R.string.productsTotal_productsAllTotal,
                total.toString()
            )
            DisplayTotal.COMPLETED -> UiText.FromResourcesWithArgs(
                R.string.productsTotal_productsCompletedTotal,
                total.toString()
            )
            DisplayTotal.ACTIVE -> UiText.FromResourcesWithArgs(
                R.string.productsTotal_productsActiveTotal,
                total.toString()
            )
        }
    }

    fun toShoppingsMultiColumns(screenWidth: Int): Boolean {
        return screenWidth >= 600
    }

    fun toProductsMultiColumns(screenWidth: Int): Boolean {
        return screenWidth >= 720
    }

    fun toScreenSize(screenWidth: Int): ScreenSize {
        return if (screenWidth >= 720) {
            ScreenSize.TABLET
        } else {
            ScreenSize.SMARTPHONE
        }
    }
}
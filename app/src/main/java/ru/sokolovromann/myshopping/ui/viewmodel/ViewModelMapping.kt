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
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.theme.AppTypography
import javax.inject.Inject

class ViewModelMapping @Inject constructor() {

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
                    text = toResourcesUiText(R.string.settings_header_fontSize),
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
                    text = toResourcesUiText(R.string.settings_header_displayAutocomplete),
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
            FontSize.TINY -> toResourcesUiText(R.string.settings_action_selectTinyFontSize)
            FontSize.SMALL -> toResourcesUiText(R.string.settings_action_selectSmallFontSize)
            FontSize.MEDIUM -> toResourcesUiText(R.string.settings_action_selectMediumFontSize)
            FontSize.LARGE -> toResourcesUiText(R.string.settings_action_selectLargeFontSize)
            FontSize.HUGE -> toResourcesUiText(R.string.settings_action_selectHugeFontSize)
        }
    }

    fun toDisplayAutocompleteText(displayAutocomplete: DisplayAutocomplete): UiText {
        return when (displayAutocomplete) {
            DisplayAutocomplete.ALL -> toResourcesUiText(R.string.settings_action_displayAllAutocomplete)
            DisplayAutocomplete.NAME -> toResourcesUiText(R.string.settings_action_displayNameAutocomplete)
            DisplayAutocomplete.HIDE -> toResourcesUiText(R.string.settings_action_selectHideAutocomplete)
        }
    }

    fun toProductsItemMenu(fontSize: FontSize): ProductsItemMenu {
        return ProductsItemMenu(
            editBody = toBody(
                text = UiText.FromResources(R.string.products_action_editProduct),
                fontSize = fontSize
            ),
            deleteBody = toBody(
                text = UiText.FromResources(R.string.products_action_deleteProduct),
                fontSize = fontSize
            ),
            copyToShoppingListBody = toBody(
                text = UiText.FromResources(R.string.products_action_copyProductToShoppingList),
                fontSize = fontSize
            ),
            moveToShoppingListBody = toBody(
                text = UiText.FromResources(R.string.products_action_moveProductToShoppingList),
                fontSize = fontSize
            )
        )
    }

    fun toProductsMenu(fontSize: FontSize): ProductsMenu {
        return ProductsMenu(
            editNameBody = toBody(
                text = UiText.FromResources(R.string.products_action_editShoppingListName),
                fontSize = fontSize
            ),
            editReminderBody = toBody(
                text = UiText.FromResources(R.string.products_action_editShoppingListReminder),
                fontSize = fontSize
            ),
            calculateChangeBody = toBody(
                text = UiText.FromResources(R.string.products_action_calculateChange),
                fontSize = fontSize
            ),
            deleteProductsBody = toBody(
                text = UiText.FromResources(R.string.products_action_deleteProducts),
                fontSize = fontSize
            ),
            shareBody = toBody(
                text = UiText.FromResources(R.string.products_action_shareProducts),
                fontSize = fontSize
            )
        )
    }

    fun toShoppingListsSortBody(sortBy: SortBy, fontSize: FontSize): TextData {
        val text: UiText = when (sortBy) {
            SortBy.CREATED -> UiText.FromResources(R.string.shoppingLists_action_sortByCreated)
            SortBy.LAST_MODIFIED -> UiText.FromResources(R.string.shoppingLists_action_sortByLastModified)
            SortBy.NAME -> UiText.FromResources(R.string.shoppingLists_action_sortByName)
            SortBy.TOTAL -> UiText.FromResources(R.string.shoppingLists_action_sortByTotal)
        }
        return toBody(
            text = text,
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
            UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsPercents)
        } else {
            UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsMoney)
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

    fun toProductsSortMenu(sortBy: SortBy, fontSize: FontSize): ProductsSortMenu {
        return ProductsSortMenu(
            title = toTitle(
                text = UiText.FromResources(R.string.products_header_sort),
                fontSize = fontSize
            ),
            byCreatedBody = toBody(
                text = UiText.FromResources(R.string.products_action_sortByCreated),
                fontSize = fontSize
            ),
            byCreatedSelected = toRadioButton(
                selected = sortBy == SortBy.CREATED
            ),
            byLastModifiedBody = toBody(
                text = UiText.FromResources(R.string.products_action_sortByLastModified),
                fontSize = fontSize
            ),
            byLastModifiedSelected = toRadioButton(
                selected = sortBy == SortBy.LAST_MODIFIED
            ),
            byNameBody = toBody(
                text = UiText.FromResources(R.string.products_action_sortByName),
                fontSize = fontSize
            ),
            byNameSelected = toRadioButton(
                selected = sortBy == SortBy.NAME
            ),
            byTotalBody = toBody(
                text = UiText.FromResources(R.string.products_action_sortByTotal),
                fontSize = fontSize
            ),
            byTotalSelected = toRadioButton(
                selected = sortBy == SortBy.TOTAL
            )
        )
    }

    fun toDiscountAsPercentMenuMenu(asPercent: Boolean, fontSize: FontSize): DiscountAsPercentMenu {
        return DiscountAsPercentMenu(
            asPercentBody = toBody(
                text = UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsPercents),
                fontSize = fontSize
            ),
            asPercentSelected = toRadioButton(
                selected = asPercent
            ),
            asMoneyBody = toBody(
                text = UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsMoney),
                fontSize = fontSize
            ),
            asMoneySelected = toRadioButton(
                selected = !asPercent
            ),
        )
    }

    fun toProductsCompletedMenu(
        displayCompleted: DisplayCompleted,
        fontSize: FontSize
    ): ProductsCompletedMenu {
        return ProductsCompletedMenu(
            title = toTitle(
                text = UiText.FromResources(R.string.products_header_displayCompleted),
                fontSize = fontSize
            ),
            firstBody = toBody(
                text = UiText.FromResources(R.string.products_action_displayCompletedFirst),
                fontSize = fontSize
            ),
            firstSelected = toRadioButton(
                selected = displayCompleted == DisplayCompleted.FIRST
            ),
            lastBody = toBody(
                text = UiText.FromResources(R.string.products_action_displayCompletedLast),
                fontSize = fontSize
            ),
            lastSelected = toRadioButton(
                selected = displayCompleted == DisplayCompleted.LAST
            ),
            hideBody = toBody(
                text = UiText.FromResources(R.string.shoppingLists_action_displayCompletedHide),
                fontSize = fontSize
            ),
            hideSelected = toRadioButton(
                selected = displayCompleted == DisplayCompleted.HIDE
            )
        )
    }

    fun toProductsTotalMenu(
        displayTotal: DisplayTotal,
        fontSize: FontSize
    ): ProductsTotalMenu {
        return ProductsTotalMenu(
            title = toTitle(
                text = UiText.FromResources(R.string.products_header_displayTotal),
                fontSize = fontSize
            ),
            allBody = toBody(
                text = UiText.FromResources(R.string.products_action_displayAllTotal),
                fontSize = fontSize
            ),
            allSelected = toRadioButton(
                selected = displayTotal == DisplayTotal.ALL
            ),
            completedBody = toBody(
                text = UiText.FromResources(R.string.products_action_displayCompletedTotal),
                fontSize = fontSize
            ),
            completedSelected = toRadioButton(
                selected = displayTotal == DisplayTotal.COMPLETED
            ),
            activeBody = toBody(
                text = UiText.FromResources(R.string.products_action_displayActiveTotal),
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
                text = toResourcesUiText(R.string.settings_action_selectTinyFontSize),
                fontSize = fontSize
            ),
            tinySelected = toRadioButton(selected = fontSize == FontSize.TINY),
            smallBody = toBody(
                text = toResourcesUiText(R.string.settings_action_selectSmallFontSize),
                fontSize = fontSize
            ),
            smallSelected = toRadioButton(selected = fontSize == FontSize.SMALL),
            mediumBody = toBody(
                text = toResourcesUiText(R.string.settings_action_selectMediumFontSize),
                fontSize = fontSize
            ),
            mediumSelected = toRadioButton(selected = fontSize == FontSize.MEDIUM),
            largeBody = toBody(
                text = toResourcesUiText(R.string.settings_action_selectLargeFontSize),
                fontSize = fontSize
            ),
            largeSelected = toRadioButton(selected = fontSize == FontSize.LARGE),
            hugeBody = toBody(
                text = toResourcesUiText(R.string.settings_action_selectHugeFontSize),
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
                text = toResourcesUiText(R.string.settings_action_displayAllAutocomplete),
                fontSize = fontSize
            ),
            allSelected = toRadioButton(selected = displayAutocomplete == DisplayAutocomplete.ALL),
            nameBody = toBody(
                text = toResourcesUiText(R.string.settings_action_displayNameAutocomplete),
                fontSize = fontSize
            ),
            nameSelected = toRadioButton(selected = displayAutocomplete == DisplayAutocomplete.NAME),
            hideBody = toBody(
                text = toResourcesUiText(R.string.settings_action_selectHideAutocomplete),
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

    fun toProductsDisplayTotalText(total: Money, displayTotal: DisplayTotal): UiText {
        return when (displayTotal) {
            DisplayTotal.ALL -> UiText.FromResourcesWithArgs(
                R.string.products_text_allTotal,
                total.toString()
            )
            DisplayTotal.COMPLETED -> UiText.FromResourcesWithArgs(
                R.string.products_text_completedTotal,
                total.toString()
            )
            DisplayTotal.ACTIVE -> UiText.FromResourcesWithArgs(
                R.string.products_text_activeTotal,
                total.toString()
            )
        }
    }
}
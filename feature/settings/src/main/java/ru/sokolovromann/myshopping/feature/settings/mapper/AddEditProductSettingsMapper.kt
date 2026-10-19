package ru.sokolovromann.myshopping.feature.settings.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.AddEditProductPreferences
import ru.sokolovromann.myshopping.core.domain.model.AfterAddingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterEditingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductEnter
import ru.sokolovromann.myshopping.core.domain.model.MoneyFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.Tax
import ru.sokolovromann.myshopping.core.domain.utils.MoneyUtils
import ru.sokolovromann.myshopping.core.ui.extension.toUiText
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.model.AddEditProductSettingsItems
import ru.sokolovromann.myshopping.feature.settings.model.SettingsItem
import java.math.BigDecimal

class AddEditProductSettingsMapper @Inject constructor() {

    fun toItems(
        preferences: AddEditProductPreferences,
        formattingMode: MoneyFormattingMode
    ): AddEditProductSettingsItems {
        val afterTappingEnter = SettingsItem(
            selected = preferences.afterTappingByEnter,
            text = when (preferences.afterTappingByEnter) {
                AfterTappingByProductEnter.SaveProduct ->
                    UiText.FromResources(R.string.settings_text_after_tapping_enter_save)
                AfterTappingByProductEnter.GoToNextField ->
                    UiText.FromResources(R.string.settings_text_after_tapping_enter_next_field)
            }
        )
        val afterAdding = SettingsItem(
            selected = preferences.afterAdding,
            text = when (preferences.afterAdding) {
                AfterAddingProduct.CloseScreen ->
                    UiText.FromResources(R.string.settings_text_after_adding_product_close)
                AfterAddingProduct.OpenNewScreen ->
                    UiText.FromResources(R.string.settings_text_after_adding_product_open)
                AfterAddingProduct.DoNothing ->
                    UiText.FromResources(R.string.settings_text_after_adding_product_do_nothing)
            }
        )
        val afterEditing = SettingsItem(
            selected = preferences.afterEditing,
            text = when (preferences.afterEditing) {
                AfterEditingProduct.CloseScreen ->
                    UiText.FromResources(R.string.settings_text_after_editing_product_close)
                AfterEditingProduct.OpenNextProductScreen ->
                    UiText.FromResources(R.string.settings_text_after_editing_product_next)
                AfterEditingProduct.DoNothing ->
                    UiText.FromResources(R.string.settings_text_after_editing_product_do_nothing)
            }
        )
        val tax = SettingsItem(
            selected = MoneyUtils.formatToPlainString(
                bigDecimal = preferences.tax?.value ?: BigDecimal.ZERO,
                formattingMode = formattingMode
            ),
            text = MoneyUtils.format(
                tax = preferences.tax ?: Tax(BigDecimal.ZERO),
                formattingMode = formattingMode
            ).toUiText()
        )
        return AddEditProductSettingsItems(
            afterTappingEnter = afterTappingEnter,
            afterAdding = afterAdding,
            afterEditing = afterEditing,
            tax = tax
        )
    }
}
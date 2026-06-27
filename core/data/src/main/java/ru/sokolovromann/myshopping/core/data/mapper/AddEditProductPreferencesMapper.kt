package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.datasource.AddEditProductPreferencesScheme
import ru.sokolovromann.myshopping.core.domain.model.AddEditProductPreferences
import ru.sokolovromann.myshopping.core.domain.model.AfterAddingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterEditingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductEnter
import ru.sokolovromann.myshopping.core.domain.model.LockProductField
import ru.sokolovromann.myshopping.core.domain.model.Tax
import ru.sokolovromann.myshopping.core.domain.utils.EnumUtils
import java.math.BigDecimal

@Singleton
class AddEditProductPreferencesMapper @Inject constructor() : DataStoreMapper<AddEditProductPreferences>() {

    override fun toModel(preferences: Preferences) = AddEditProductPreferences(
        EnumUtils.valueOfOrDefault(
            preferences[AddEditProductPreferencesScheme.LOCK_FIELD_KEY],
            LockProductField.Cost
        ),
        EnumUtils.valueOfOrDefault(
            preferences[AddEditProductPreferencesScheme.AFTER_TAPPING_BY_ENTER_KEY],
            AfterTappingByProductEnter.SaveProduct
        ),
        EnumUtils.valueOfOrDefault(
            preferences[AddEditProductPreferencesScheme.AFTER_ADDING_KEY],
            AfterAddingProduct.CloseScreen
        ),
        EnumUtils.valueOfOrDefault(
            preferences[AddEditProductPreferencesScheme.AFTER_EDITING_KEY],
            AfterEditingProduct.CloseScreen
        ),
        toTax(preferences[AddEditProductPreferencesScheme.TAX_KEY]),
    )

    override fun toPreferences(model: AddEditProductPreferences) = preferencesOf(
        AddEditProductPreferencesScheme.LOCK_FIELD_KEY
                to model.lockField.toString(),
        AddEditProductPreferencesScheme.AFTER_TAPPING_BY_ENTER_KEY
                to model.afterTappingByEnter.toString(),
        AddEditProductPreferencesScheme.AFTER_ADDING_KEY
                to model.afterAdding.toString(),
        AddEditProductPreferencesScheme.AFTER_EDITING_KEY
                to model.afterEditing.toString(),
        AddEditProductPreferencesScheme.TAX_KEY
                to model.tax?.value?.toPlainString().orEmpty()
    )

    private fun toTax(tax: String?): Tax? = tax?.let { Tax(BigDecimal(it)) }
}
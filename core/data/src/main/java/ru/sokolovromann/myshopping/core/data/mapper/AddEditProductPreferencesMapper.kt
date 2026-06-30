package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.datasource.LocalDataStoreScheme
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
            preferences[LocalDataStoreScheme.AddEditProduct.LOCK_FIELD],
            LockProductField.Cost
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.AddEditProduct.AFTER_TAPPING_BY_ENTER],
            AfterTappingByProductEnter.SaveProduct
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.AddEditProduct.AFTER_ADDING],
            AfterAddingProduct.CloseScreen
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.AddEditProduct.AFTER_EDITING],
            AfterEditingProduct.CloseScreen
        ),
        toTax(preferences[LocalDataStoreScheme.AddEditProduct.TAX]),
    )

    override fun toPreferences(model: AddEditProductPreferences) = preferencesOf(
        LocalDataStoreScheme.AddEditProduct.LOCK_FIELD
                to model.lockField.toString(),
        LocalDataStoreScheme.AddEditProduct.AFTER_TAPPING_BY_ENTER
                to model.afterTappingByEnter.toString(),
        LocalDataStoreScheme.AddEditProduct.AFTER_ADDING
                to model.afterAdding.toString(),
        LocalDataStoreScheme.AddEditProduct.AFTER_EDITING
                to model.afterEditing.toString(),
        LocalDataStoreScheme.AddEditProduct.TAX
                to model.tax?.value?.toPlainString().orEmpty()
    )

    private fun toTax(tax: String?): Tax? = tax?.let { Tax(BigDecimal(it)) }
}
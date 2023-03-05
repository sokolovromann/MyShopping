package ru.sokolovromann.myshopping.data.local.resources

import android.content.res.Resources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.local.entity.CurrencyResourcesEntity
import javax.inject.Inject

class MainResources @Inject constructor(
    private val resources: Resources
) {

    fun getCurrencyResources(): Flow<CurrencyResourcesEntity> {
        val entity = CurrencyResourcesEntity(
            defaultCurrency = resources.getString(R.string.data_text_defaultCurrency),
            displayDefaultCurrencyToLeft = resources.getBoolean(R.bool.data_value_displayDefaultCurrencyToLeft)
        )
        return flowOf(entity)
    }
}
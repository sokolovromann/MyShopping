package ru.sokolovromann.myshopping.data.local.resources

import android.content.res.Resources
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.local.entity.CurrencyResourcesEntity
import javax.inject.Inject

class MainResources @Inject constructor(
    private val resources: Resources
) {

    fun getCurrencyResources(): CurrencyResourcesEntity {
        return CurrencyResourcesEntity(
            defaultCurrency = resources.getString(R.string.data_defaultCurrency),
            defaultCurrencyDisplayToLeft = resources.getBoolean(R.bool.data_defaultCurrencyDisplayToLeft)
        )
    }
}
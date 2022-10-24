package ru.sokolovromann.myshopping.data.local.resources

import android.content.res.Resources
import ru.sokolovromann.myshopping.R
import javax.inject.Inject

class MainResources @Inject constructor(
    private val resources: Resources
) {

    fun getDefaultCurrency(): String {
        return resources.getString(R.string.data_defaultCurrency)
    }

    fun getDefaultCurrencyDisplayToLeft(): Boolean {
        return resources.getBoolean(R.bool.data_defaultCurrencyDisplayToLeft)
    }
}
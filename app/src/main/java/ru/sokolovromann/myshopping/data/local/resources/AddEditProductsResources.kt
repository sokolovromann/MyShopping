package ru.sokolovromann.myshopping.data.local.resources

import android.content.res.Resources
import ru.sokolovromann.myshopping.R
import javax.inject.Inject

class AddEditProductsResources @Inject constructor(
    private val resources: Resources
) {

    fun getDefaultAutocompleteNames(search: String): List<String> {
        return resources.getStringArray(R.array.data_defaultAutocompleteNames)
            .filter { it.contains(search) }
    }
}
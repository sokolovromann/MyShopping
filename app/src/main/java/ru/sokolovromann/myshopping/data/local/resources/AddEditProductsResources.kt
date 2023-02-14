package ru.sokolovromann.myshopping.data.local.resources

import android.content.res.Resources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.sokolovromann.myshopping.R
import javax.inject.Inject

class AddEditProductsResources @Inject constructor(
    private val resources: Resources
) {

    fun getDefaultAutocompleteNames(search: String): Flow<List<String>> {
        val names = resources.getStringArray(R.array.data_defaultAutocompleteNames)
            .filter { it.lowercase().contains(search.lowercase()) }
        return flowOf(names)
    }
}
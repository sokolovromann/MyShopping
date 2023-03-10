package ru.sokolovromann.myshopping.data.local.resources

import android.content.res.Resources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.sokolovromann.myshopping.R
import javax.inject.Inject

class AutocompletesResources @Inject constructor(
    private val resources: Resources
) {

    fun getDefaultAutocompleteNames(): Flow<List<String>> {
        val names = resources.getStringArray(R.array.data_text_defaultAutocompleteNames).toList()
        return flowOf(names)
    }
}
package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.ui.model.mapper.UiAutocompletesMapper
import ru.sokolovromann.myshopping.ui.viewmodel.AutocompletesViewModel
import ru.sokolovromann.myshopping.utils.UID

class AutocompletesState {

    private var autocompletesData: AutocompletesViewModel.AutocompletesData? by mutableStateOf(null)

    var autocompletes: List<AutocompleteItem> by mutableStateOf(listOf())
        private set

    var selectedUids: List<UID>? by mutableStateOf(null)
        private set

    var multiColumns: Boolean by mutableStateOf(false)
        private set

    var deviceSize: DeviceSize by mutableStateOf(DeviceSize.DefaultValue)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(data: AutocompletesViewModel.AutocompletesData) {
        autocompletesData = data

        autocompletes = UiAutocompletesMapper.toAutocompleteItems(data.suggestionsWithDetails, data.currency)
        selectedUids = null
        deviceSize = DeviceSize.Medium
        multiColumns = deviceSize == DeviceSize.Large
        waiting = false
    }

    fun onAllAutocompletesSelected(selected: Boolean) {
        selectedUids = if (selected) {
            autocompletesData?.suggestionsWithDetails?.map { it.suggestion.uid }
        } else { null }
    }

    fun onAutocompleteSelected(selected: Boolean, uid: UID) {
        val uids = (selectedUids?.toMutableList() ?: mutableListOf()).apply {
            if (selected) add(uid) else remove(uid)
        }
        selectedUids = if (uids.isEmpty()) null else uids
    }

    fun onWaiting() {
        waiting = true
    }

    fun isNotFound(): Boolean {
        return autocompletesData?.suggestionsWithDetails.isNullOrEmpty()
    }
}
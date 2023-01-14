package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.AutocompletesRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.AutocompletesScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.viewmodel.event.AutocompletesEvent
import javax.inject.Inject

@HiltViewModel
class AutocompletesViewModel @Inject constructor(
    private val repository: AutocompletesRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers
) : ViewModel(), ViewModelEvent<AutocompletesEvent> {

    val autocompletesState: AutocompletesState = AutocompletesState()

    private val _floatingActionButtonState: MutableState<FloatingActionButtonData> = mutableStateOf(
        FloatingActionButtonData()
    )
    val floatingActionButtonState: State<FloatingActionButtonData> = _floatingActionButtonState

    private val _topBarState: MutableState<TopBarData> = mutableStateOf(TopBarData())
    val topBarState: State<TopBarData> = _topBarState

    private val _screenEventFlow: MutableSharedFlow<AutocompletesScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<AutocompletesScreenEvent> = _screenEventFlow

    init {
        showFloatingActionButton()
        showTopBar()

        getAutocompletes()
    }

    override fun onEvent(event: AutocompletesEvent) {
        when (event) {
            AutocompletesEvent.AddAutocomplete -> addAutocomplete()

            is AutocompletesEvent.EditAutocomplete -> editAutocomplete(event)

            is AutocompletesEvent.DeleteAutocomplete -> deleteAutocomplete(event)

            AutocompletesEvent.SelectAutocompletesSort -> selectAutocompletesSort()

            is AutocompletesEvent.SelectNavigationItem -> selectNavigationItem(event)

            AutocompletesEvent.SortAutocompletesByCreated -> sortAutocompletesByCreated()

            AutocompletesEvent.SortAutocompletesByName -> sortAutocompletesByName()

            AutocompletesEvent.InvertAutocompletesSort -> invertAutocompletesSort()

            AutocompletesEvent.ShowBackScreen -> showBackScreen()

            AutocompletesEvent.ShowNavigationDrawer -> showNavigationDrawer()

            is AutocompletesEvent.ShowAutocompleteMenu -> showAutocompleteMenu(event)

            AutocompletesEvent.HideNavigationDrawer -> hideNavigationDrawer()

            AutocompletesEvent.HideAutocompleteMenu -> hideAutocompleteMenu()

            AutocompletesEvent.HideAutocompletesSort -> hideAutocompletesSort()
        }
    }

    private fun getAutocompletes() = viewModelScope.launch(dispatchers.io) {
        showAutocompletesLoading()
        repository.getAutocompletes().collect {
            showAutocompletes(it)
        }
    }

    private fun addAutocomplete() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.AddAutocomplete)
    }

    private fun editAutocomplete(
        event: AutocompletesEvent.EditAutocomplete
    ) = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.EditAutocomplete(event.uid))
        hideAutocompleteMenu()
    }

    private fun deleteAutocomplete(
        event: AutocompletesEvent.DeleteAutocomplete
    ) = viewModelScope.launch(dispatchers.io) {
        repository.deleteAutocomplete(event.uid)

        withContext(dispatchers.main) {
            hideAutocompleteMenu()
        }
    }

    private fun selectAutocompletesSort() {
        autocompletesState.showSort()
    }

    private fun selectNavigationItem(
        event: AutocompletesEvent.SelectNavigationItem
    ) = viewModelScope.launch(dispatchers.main) {
        when (event.route) {
            UiRoute.Purchases -> _screenEventFlow.emit(AutocompletesScreenEvent.ShowPurchases)
            UiRoute.Archive -> _screenEventFlow.emit(AutocompletesScreenEvent.ShowArchive)
            UiRoute.Trash -> _screenEventFlow.emit(AutocompletesScreenEvent.ShowTrash)
            UiRoute.Settings -> _screenEventFlow.emit(AutocompletesScreenEvent.ShowSettings)
            else -> return@launch
        }
    }

    private fun sortAutocompletesByCreated() = viewModelScope.launch(dispatchers.io) {
        repository.sortAutocompletesByCreated()

        withContext(dispatchers.main) {
            hideAutocompletesSort()
        }
    }

    private fun sortAutocompletesByName() = viewModelScope.launch(dispatchers.io) {
        repository.sortAutocompletesByName()

        withContext(dispatchers.main) {
            hideAutocompletesSort()
        }
    }

    private fun invertAutocompletesSort() = viewModelScope.launch(dispatchers.io) {
        repository.invertAutocompleteSort()
    }

    private suspend fun showAutocompletesLoading() = withContext(dispatchers.main) {
        autocompletesState.showLoading()
    }

    private suspend fun showAutocompletes(autocompletes: Autocompletes) = withContext(dispatchers.main) {
        if (autocompletes.autocompletes.isEmpty()) {
            autocompletesState.showNotFound(autocompletes.preferences)
        } else {
            autocompletesState.showAutocompletes(autocompletes)
        }
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.ShowBackScreen)
    }

    private fun showNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.ShowNavigationDrawer)
    }

    private fun showAutocompleteMenu(event: AutocompletesEvent.ShowAutocompleteMenu) {
        autocompletesState.showAutocompleteMenu(event.uid)
    }

    private fun showTopBar() {
        val data = TopBarData(
            title = mapping.toOnTopAppBarHeader(
                text = mapping.toResourcesUiText(R.string.autocompletes_header_autocompletes),
                fontSize = FontSize.MEDIUM
            ),
            navigationIcon = mapping.toOnTopAppBar(
                icon = UiIcon.FromVector(Icons.Default.Menu)
            )
        )
        _topBarState.value = data
    }

    private fun showFloatingActionButton() {
        val data = FloatingActionButtonData(
            icon = IconData(
                icon = UiIcon.FromVector(Icons.Default.Add),
                tint = ColorData(appColor = AppColor.OnSecondary)
            )
        )
        _floatingActionButtonState.value = data
    }

    private fun hideNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AutocompletesScreenEvent.HideNavigationDrawer)
    }

    private fun hideAutocompleteMenu() {
        autocompletesState.hideAutocompleteMenu()
    }

    private fun hideAutocompletesSort() {
        autocompletesState.hideSort()
    }
}
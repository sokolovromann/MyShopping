package ru.sokolovromann.myshopping.feature.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.MainDispatcher
import ru.sokolovromann.myshopping.core.domain.model.Dictionary
import ru.sokolovromann.myshopping.core.domain.model.FabricDirectory
import ru.sokolovromann.myshopping.core.domain.model.FabricValue
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsView
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.usecase.DeleteFabricsUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.DeleteSuggestionsUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.ObserveDictionaryUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.ObserveGeneralPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.ObserveSuggestionsPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.utils.MoneyUtils
import ru.sokolovromann.myshopping.core.domain.utils.QuantityUtils
import ru.sokolovromann.myshopping.core.navigation.Navigator
import ru.sokolovromann.myshopping.core.navigation.Screen
import ru.sokolovromann.myshopping.core.ui.extension.toUiText
import ru.sokolovromann.myshopping.core.ui.model.NavigationItem
import ru.sokolovromann.myshopping.core.ui.model.UiText

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val observeDictionaryUseCase: ObserveDictionaryUseCase,
    private val observeSuggestionsPreferencesUseCase: ObserveSuggestionsPreferencesUseCase,
    private val observeGeneralPreferencesUseCase: ObserveGeneralPreferencesUseCase,
    private val deleteSuggestionsUseCase: DeleteSuggestionsUseCase,
    private val deleteFabricsUseCase: DeleteFabricsUseCase,
    private val navigator: Navigator,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _dictionaryState: MutableStateFlow<DictionaryState> = MutableStateFlow(DictionaryState())
    val dictionaryState: StateFlow<DictionaryState> = _dictionaryState

    init { onInit() }

    fun onNavigationItemSelected(navigationItem: NavigationItem) = when (navigationItem) {
        NavigationItem.Purchases -> navigator.navigateTo(Screen.Purchases)
        NavigationItem.Archive -> navigator.navigateTo(Screen.Archive)
        NavigationItem.Trash -> navigator.navigateTo(Screen.Trash)
        NavigationItem.Dictionary -> {}
        NavigationItem.Settings -> navigator.navigateTo(Screen.Settings)
        NavigationItem.About -> navigator.navigateTo(Screen.About)
    }

    fun onBackClick() {
        val suggestionsState = _dictionaryState.value.suggestionsState
        if (suggestionsState.isSelectMode) {
            val newState = _dictionaryState.value.copy(
                suggestionsState = suggestionsState.copy(selected = emptySet())
            )
            _dictionaryState.tryEmit(newState)
        } else {
            navigator.navigateBack()
        }
    }

    fun onSuggestionItemClick(uid: UID) {
        val suggestionsState = _dictionaryState.value.suggestionsState
        if (suggestionsState.isSelectMode) {
            val selected = suggestionsState.selected.toMutableSet()
                .apply { if (contains(uid)) remove(uid) else add(uid) }
            val newState = _dictionaryState.value.copy(
                suggestionsState = suggestionsState.copy(selected = selected)
            )
            _dictionaryState.tryEmit(newState)
        }
    }

    fun onSuggestionItemLongClick(uid: UID) {
        val suggestionsState = _dictionaryState.value.suggestionsState
        if (!suggestionsState.isSelectMode) {
            val selected = setOf(uid)
            val newState = _dictionaryState.value.copy(
                suggestionsState = suggestionsState.copy(selected = selected)
            )
            _dictionaryState.tryEmit(newState)
        }
    }

    fun onAddSuggestionClick() {
        navigator.navigateTo(Screen.AddEditSuggestion())
    }

    fun onEditSuggestionClick() {
        val suggestionsState = _dictionaryState.value.suggestionsState
        val uid = suggestionsState.selected.first()

        val newState = _dictionaryState.value.copy(
            suggestionsState = suggestionsState.copy(selected = emptySet())
        )
        _dictionaryState.tryEmit(newState)

        navigator.navigateTo(Screen.AddEditSuggestion(uid.value))
    }

    fun onDeleteSuggestionsClick() = viewModelScope.launch {
        val suggestionsState = _dictionaryState.value.suggestionsState
        val uids = suggestionsState.selected

        val newState = _dictionaryState.value.copy(
            suggestionsState = suggestionsState.copy(selected = emptySet())
        )
        _dictionaryState.tryEmit(newState)

        deleteSuggestionsUseCase.invoke(uids)
        uids.forEach {
            val directory = FabricDirectory(it)
            deleteFabricsUseCase.invoke(directory)
        }
    }

    fun onSelectAllSuggestionsClick() {
        val suggestionsState = _dictionaryState.value.suggestionsState

        val selected = suggestionsState.items.map { it.uid }.toSet()
        val newState = _dictionaryState.value.copy(
            suggestionsState = suggestionsState.copy(selected = selected)
        )
        _dictionaryState.tryEmit(newState)
    }

    private fun onInit() = viewModelScope.launch {
        combine(
            flow = observeDictionaryUseCase(),
            flow2 = observeSuggestionsPreferencesUseCase(),
            flow3 = observeGeneralPreferencesUseCase(),
            transform = { dictionary, suggestionsPreferences, generalPreferences ->
                withContext(mainDispatcher) {
                    DictionaryState(
                        isLoading = false,
                        suggestionsState = SuggestionsState(
                            items = createSuggestionItems(dictionary, generalPreferences),
                            cells = createGridCells(suggestionsPreferences.view),
                            selected = emptySet()
                        )
                    )
                }
            }
        ).collect { state ->
            withContext(mainDispatcher) {
                _dictionaryState.tryEmit(state)
            }
        }
    }

    private fun createSuggestionItems(
        dictionary: Dictionary,
        generalPreferences: GeneralPreferences
    ) = dictionary.supports.map { support ->
        val prefix = ": "
        val fabrics = support.filteredFabricsByType
        val quantities: UiText? = if (fabrics.quantities.isNotEmpty()) {
            UiText.FromResourcesWithArgs(
                R.string.dictionary_item_quantities,
                fabrics.quantities.joinToString(prefix = prefix) { fabric ->
                    val data = (fabric.value as FabricValue.QuantityType).data
                    QuantityUtils.format(data)
                }
            )
        } else null
        val unitPrice: UiText? = if (fabrics.unitPrices.isNotEmpty()) {
            UiText.FromResourcesWithArgs(
                R.string.dictionary_item_unit_price,
                fabrics.unitPrices.joinToString(prefix = prefix) { fabric ->
                    val data = (fabric.value as FabricValue.UnitPriceType).data
                    MoneyUtils.format(
                        bigDecimal = data,
                        currency = generalPreferences.currency,
                        formattingMode = generalPreferences.moneyFormattingMode
                    )
                }
            )
        } else null
        val discounts: UiText? = if (fabrics.discounts.isNotEmpty()) {
            UiText.FromResourcesWithArgs(
                R.string.dictionary_item_discounts,
                fabrics.discounts.joinToString(prefix = prefix) { fabric ->
                    val data = (fabric.value as FabricValue.DiscountType).data
                    MoneyUtils.format(
                        discount = data,
                        currency = generalPreferences.currency,
                        formattingMode = generalPreferences.moneyFormattingMode
                    )
                }
            )
        } else null
        val taxes: UiText? = if (fabrics.taxes.isNotEmpty()) {
            UiText.FromResourcesWithArgs(
                R.string.dictionary_item_taxes,
                fabrics.taxes.joinToString(prefix = prefix) { fabric ->
                    val data = (fabric.value as FabricValue.TaxType).data
                    MoneyUtils.format(
                        bigDecimal = data.value,
                        currency = generalPreferences.currency,
                        formattingMode = generalPreferences.moneyFormattingMode
                    )
                }
            )
        } else null
        val costs: UiText? = if (fabrics.costs.isNotEmpty()) {
            UiText.FromResourcesWithArgs(
                R.string.dictionary_item_costs,
                fabrics.costs.joinToString(prefix = prefix) { fabric ->
                    val data = (fabric.value as FabricValue.CostType).data
                    MoneyUtils.format(
                        bigDecimal = data,
                        currency = generalPreferences.currency,
                        formattingMode = generalPreferences.moneyFormattingMode
                    )
                }
            )
        } else null
        val manufacturers: UiText? = if (fabrics.manufacturers.isNotEmpty()) {
            UiText.FromResourcesWithArgs(
                R.string.dictionary_item_manufacturers,
                fabrics.manufacturers.joinToString(prefix = prefix) { fabric ->
                    (fabric.value as FabricValue.ManufacturerType).data
                }
            )
        } else null
        val brands: UiText? = if (fabrics.brands.isNotEmpty()) {
            UiText.FromResourcesWithArgs(
                R.string.dictionary_item_brands,
                fabrics.brands.joinToString(prefix = prefix) { fabric ->
                    (fabric.value as FabricValue.BrandType).data
                }
            )
        } else null
        val sizes: UiText? = if (fabrics.sizes.isNotEmpty()) {
            UiText.FromResourcesWithArgs(
                R.string.dictionary_item_sizes,
                fabrics.sizes.joinToString(prefix = prefix) { fabric ->
                    (fabric.value as FabricValue.SizeType).data
                }
            )
        } else null
        val colors: UiText? = if (fabrics.colors.isNotEmpty()) {
            UiText.FromResourcesWithArgs(
                R.string.dictionary_item_colors,
                fabrics.colors.joinToString(prefix = prefix) { fabric ->
                    (fabric.value as FabricValue.ColorType).data
                }
            )
        } else null
        SuggestionItem(
            uid = support.uid,
            name = support.name.toUiText(),
            quantities = quantities,
            unitPrices = unitPrice,
            discounts = discounts,
            taxes = taxes,
            costs = costs,
            manufacturers = manufacturers,
            brands = brands,
            sizes = sizes,
            colors = colors
        )
    }

    private fun createGridCells(view: SuggestionsView) = when (view) {
        is SuggestionsView.Grid -> 2
        is SuggestionsView.List -> 1
    }
}
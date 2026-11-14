package ru.sokolovromann.myshopping.feature.carts.viewmodel

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
import ru.sokolovromann.myshopping.core.domain.model.CartDirectory
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.usecase.ObserveCartsPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.ObserveGeneralPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.ObservePurchasesUseCase
import ru.sokolovromann.myshopping.core.navigation.Navigator
import ru.sokolovromann.myshopping.core.navigation.Screen
import ru.sokolovromann.myshopping.core.ui.model.NavigationItem
import ru.sokolovromann.myshopping.feature.carts.mapper.PurchasesMapper
import ru.sokolovromann.myshopping.feature.carts.model.PurchasesState

@HiltViewModel
class PurchasesViewModel @Inject constructor(
    private val observePurchasesUseCase: ObservePurchasesUseCase,
    private val observeGeneralPreferencesUseCase: ObserveGeneralPreferencesUseCase,
    private val observeCartsPreferencesUseCase: ObserveCartsPreferencesUseCase,
    private val purchasesMapper: PurchasesMapper,
    private val navigator: Navigator,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _purchasesState: MutableStateFlow<PurchasesState> = MutableStateFlow(PurchasesState())
    val purchasesState: StateFlow<PurchasesState> = _purchasesState

    init { onInit() }

    fun onNavigationItemSelected(navigationItem: NavigationItem) = when (navigationItem) {
        NavigationItem.Purchases -> {}
        NavigationItem.Archive -> navigator.navigateTo(Screen.Archive)
        NavigationItem.Trash -> navigator.navigateTo(Screen.Trash)
        NavigationItem.Suggestions -> navigator.navigateTo(Screen.Suggestions)
        NavigationItem.Settings -> navigator.navigateTo(Screen.Settings)
        NavigationItem.About -> navigator.navigateTo(Screen.About)
    }

    fun onBackClick() {
        val oldState = _purchasesState.value
        if (oldState.itemsSelected.isEmpty()) {
            navigator.finish()
        } else {
            val newState = oldState.copy(itemsSelected = emptySet())
            _purchasesState.tryEmit(newState)
        }
    }

    fun onCartItemClick(uid: UID) {
        val oldState = _purchasesState.value
        if (oldState.itemsSelected.isNotEmpty()) {
            val newItemSelected = oldState.itemsSelected.toMutableSet().apply {
                if (contains(uid)) remove(uid) else add(uid)
            }
            val newState = oldState.copy(itemsSelected = newItemSelected)
            _purchasesState.tryEmit(newState)
        }
    }

    fun onCartItemLongClick(uid: UID) {
        val oldState = _purchasesState.value
        if (oldState.itemsSelected.isEmpty()) {
            val itemSelected = setOf(uid)
            val newState = oldState.copy(itemsSelected = itemSelected)
            _purchasesState.tryEmit(newState)
        } else { onCartItemClick(uid) }
    }

    private fun onInit() = viewModelScope.launch {
        combine(
            flow = observePurchasesUseCase(CartDirectory.Current),
            flow2 = observeCartsPreferencesUseCase(),
            flow3 = observeGeneralPreferencesUseCase(),
            transform = { purchases, cartsPreferences, generalPreferences ->
                withContext(mainDispatcher) {
                    purchasesMapper.createPurchasesState(
                        purchases,
                        cartsPreferences,
                        generalPreferences
                    )
                }
            }
        ).collect { state ->
            withContext(mainDispatcher) {
                _purchasesState.tryEmit(state)
            }
        }
    }
}
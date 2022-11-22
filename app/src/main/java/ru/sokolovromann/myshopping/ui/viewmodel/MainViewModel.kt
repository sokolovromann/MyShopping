package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.MainRepository
import ru.sokolovromann.myshopping.data.repository.model.AppOpenedAction
import ru.sokolovromann.myshopping.data.repository.model.Currency
import ru.sokolovromann.myshopping.data.repository.model.ScreenSize
import ru.sokolovromann.myshopping.notification.purchases.PurchasesNotificationManager
import ru.sokolovromann.myshopping.ui.compose.event.MainScreenEvent
import ru.sokolovromann.myshopping.ui.viewmodel.event.MainEvent
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers,
    private val notificationManager: PurchasesNotificationManager
) : ViewModel(), ViewModelEvent<MainEvent> {

    private val _nightThemeState: MutableState<Boolean> = mutableStateOf(false)
    val nightThemeState: State<Boolean> = _nightThemeState

    private val _loadingState: MutableState<Boolean> = mutableStateOf(true)
    val loadingState: State<Boolean> = _loadingState

    private val _screenEventFlow: MutableSharedFlow<MainScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<MainScreenEvent> = _screenEventFlow

    override fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnCreate -> onCreate(event)

            is MainEvent.AddDefaultPreferences -> addDefaultPreferences(event)
        }
    }

    private fun onCreate(event: MainEvent.OnCreate) = viewModelScope.launch(dispatchers.io) {
        showLoading()

        repository.getMainPreferences().collect {
            applyNightTheme(it.nightTheme)
            onOpened(it.appOpenedAction, event)
        }
    }

    private suspend fun onOpened(
        firstOpenedAction: AppOpenedAction,
        event: MainEvent.OnCreate
    ) = withContext(dispatchers.main) {
        when (firstOpenedAction) {
            AppOpenedAction.NOTHING -> if (event.shoppingUid == null) {
                showPurchases()
            } else {
                showProducts(event.shoppingUid)
            }

            AppOpenedAction.ADD_DEFAULT_DATA -> getDefaultPreferences()

            else -> {}
        }
    }

    private fun getDefaultPreferences() = viewModelScope.launch(dispatchers.io) {
        _screenEventFlow.emit(MainScreenEvent.GetDefaultPreferences)
    }

    private fun addDefaultPreferences(
        event: MainEvent.AddDefaultPreferences
    ) = viewModelScope.launch(dispatchers.io) {
        val defaultCurrency = repository.getDefaultCurrency().firstOrNull() ?: Currency()
        repository.addCurrency(defaultCurrency)

        val shoppingsMultiColumns = event.screenWidth >= 600
        repository.addShoppingListsMultiColumns(shoppingsMultiColumns)

        val productsMultiColumns = event.screenWidth >= 720
        repository.addProductsMultiColumns(productsMultiColumns)

        val screenSize: ScreenSize = if (event.screenWidth >= 720) {
            ScreenSize.TABLET
        } else {
            ScreenSize.SMARTPHONE
        }
        repository.addScreenSize(screenSize)

        repository.addAppOpenedAction(AppOpenedAction.NOTHING)

        notificationManager.createNotificationChannel()
    }

    private suspend fun applyNightTheme(nightTheme: Boolean) = withContext(dispatchers.main) {
        _nightThemeState.value = nightTheme
    }

    private suspend fun showLoading() = withContext(dispatchers.main) {
        _loadingState.value = true
    }

    private suspend fun showPurchases() = withContext(dispatchers.main) {
        hideLoading()
    }

    private fun showProducts(shoppingUid: String) = viewModelScope.launch(dispatchers.main) {
        hideLoading()

        val event = MainScreenEvent.ShowProducts(shoppingUid)
        _screenEventFlow.emit(event)
    }

    private fun hideLoading() {
        _loadingState.value = false
    }
}
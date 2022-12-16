package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.CalculateChangeRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.CalculateChangeScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.TextData
import ru.sokolovromann.myshopping.ui.compose.state.TextFieldState
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.viewmodel.event.CalculateChangeEvent
import javax.inject.Inject

@HiltViewModel
class CalculateChangeViewModel @Inject constructor(
    private val repository: CalculateChangeRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<CalculateChangeEvent> {

    private val calculateChangeState: MutableState<CalculateChange> = mutableStateOf(CalculateChange())

    private val _headerState: MutableState<TextData> = mutableStateOf(TextData())
    val headerState: State<TextData> = _headerState

    val userMoneyState: TextFieldState = TextFieldState()

    private val _totalState: MutableState<TextData> = mutableStateOf(TextData())
    val totalState: State<TextData> = _totalState

    private val _changeState: MutableState<TextData> = mutableStateOf(TextData())
    val changeState: State<TextData> = _changeState

    private val _backState: MutableState<TextData> = mutableStateOf(TextData())
    val backState: State<TextData> = _backState

    private val _keyboardFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val keyboardFlow: SharedFlow<Boolean> = _keyboardFlow

    private val _screenEventFlow: MutableSharedFlow<CalculateChangeScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<CalculateChangeScreenEvent> = _screenEventFlow

    private val uid: String? = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key)

    init {
        showBackButton()
        getCalculateChange()
    }

    override fun onEvent(event: CalculateChangeEvent) {
        when (event) {
            CalculateChangeEvent.ShowBackScreen -> showBackScreen()

            is CalculateChangeEvent.UserMoneyChanged -> userMoneyChange(event)
        }
    }

    private fun getCalculateChange() = viewModelScope.launch(dispatchers.io) {
        repository.getCalculateChange(uid).firstOrNull()?.let {
            showCalculateChange(it)
        }
    }

    private suspend fun showCalculateChange(
        calculateChange: CalculateChange
    ) = withContext(dispatchers.main) {
        calculateChangeState.value = calculateChange

        val value = calculateChange.calculateTotal()
        val preferences = calculateChange.preferences

        userMoneyState.showTextField(
            text = TextFieldValue(text = ""),
            label = mapping.toBody(
                text = mapping.toResourcesUiText(R.string.calculateChange_userMoneyLabel),
                fontSize = preferences.fontSize
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                capitalization = KeyboardCapitalization.None
            )
        )

        _totalState.value = mapping.toBody(
            text = UiText.FromResourcesWithArgs(R.string.calculateChange_total, value.toString()),
            fontSize = preferences.fontSize
        )

        _changeState.value = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.calculateChange_noChange),
            fontSize = preferences.fontSize
        )

        showHeader(preferences)
        showKeyboard()
    }

    private fun userMoneyChange(event: CalculateChangeEvent.UserMoneyChanged) {
        userMoneyState.changeText(event.value)
        calculateChange()
    }

    private fun calculateChange() {
        val userMoney = mapping.toFloat(userMoneyState.currentData.text) ?: 0f
        val change: Float = userMoney - calculateChangeState.value.calculateTotal().value
        val changeMoney = Money(
            value = change,
            currency = calculateChangeState.value.calculateTotal().currency
        )

        if (changeMoney.isEmpty()) {
            showNoChange()
        } else {
            showChange(changeMoney.toString())
        }
    }

    private fun showNoChange() {
        _changeState.value = _changeState.value.copy(
            text = mapping.toResourcesUiText(R.string.calculateChange_noChange)
        )
    }

    private fun showChange(changeMoney: String) {
        _changeState.value = _changeState.value.copy(
            text = UiText.FromResourcesWithArgs(R.string.calculateChange_change, changeMoney)
        )
    }

    private fun showHeader(preferences: ProductPreferences) {
        _headerState.value = mapping.toOnDialogHeader(
            text = mapping.toResourcesUiText(R.string.calculateChange_header),
            fontSize = preferences.fontSize
        )
    }

    private fun showBackButton() {
        _backState.value = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.calculateChange_back),
            fontSize = FontSize.MEDIUM
        )
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(CalculateChangeScreenEvent.ShowBackScreen)
        hideKeyboard()
    }

    private fun showKeyboard() = viewModelScope.launch(dispatchers.main) {
        _keyboardFlow.emit(true)
    }

    private fun hideKeyboard() = viewModelScope.launch(dispatchers.main) {
        _keyboardFlow.emit(false)
    }
}
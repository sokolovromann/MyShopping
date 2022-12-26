package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
import ru.sokolovromann.myshopping.data.repository.EditReminderRepository
import ru.sokolovromann.myshopping.data.repository.model.EditReminder
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.ProductPreferences
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.ui.*
import ru.sokolovromann.myshopping.ui.compose.event.EditReminderScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.TextData
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.utils.getDefaultReminder
import ru.sokolovromann.myshopping.ui.utils.getDisplayDate
import ru.sokolovromann.myshopping.ui.utils.getDisplayTime
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditReminderEvent
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class EditReminderViewModel @Inject constructor(
    private val repository: EditReminderRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle,
    private val alarmManager: PurchasesAlarmManager
) : ViewModel(), ViewModelEvent<EditReminderEvent> {

    private val editReminderState: MutableState<EditReminder> = mutableStateOf(EditReminder())

    private val _headerState: MutableState<TextData> = mutableStateOf(TextData())
    val headerState: State<TextData> = _headerState

    private val _dateState: MutableState<TextData> = mutableStateOf(TextData())
    val dateState: State<TextData> = _dateState

    private val _timeState: MutableState<TextData> = mutableStateOf(TextData())
    val timeState: State<TextData> = _timeState

    private val _deleteState: MutableState<TextData?> = mutableStateOf(TextData())
    val deleteState: State<TextData?> = _deleteState

    private val _cancelState: MutableState<TextData> = mutableStateOf(TextData())
    val cancelState: State<TextData> = _cancelState

    private val _saveState: MutableState<TextData> = mutableStateOf(TextData())
    val saveState: State<TextData> = _saveState

    private val _reminderState: MutableState<Calendar> = mutableStateOf(
        Calendar.getInstance().getDefaultReminder()
    )
    val reminderState: State<Calendar> = _reminderState

    private val _dateDialogState: MutableState<Boolean> = mutableStateOf(false)
    val dateDialogState: State<Boolean> = _dateDialogState

    private val _timeDialogState: MutableState<Boolean> = mutableStateOf(false)
    val timeDialogState: State<Boolean> = _timeDialogState

    private val _screenEventFlow: MutableSharedFlow<EditReminderScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<EditReminderScreenEvent> = _screenEventFlow

    private val uid: String? = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key)

    init {
        showDateButton()
        showTimeButton()
        showCancelButton()
        showSaveButton()
        getReminder()
    }

    override fun onEvent(event: EditReminderEvent) {
        when (event) {
            EditReminderEvent.SaveReminder -> saveReminder()

            EditReminderEvent.CancelSavingReminder -> cancelSavingReminder()

            EditReminderEvent.CancelSelectingReminderDate -> cancelSelectingReminderDate()

            EditReminderEvent.CancelSelectingReminderTime -> cancelSelectingReminderTime()

            EditReminderEvent.DeleteReminder -> deleteReminder()

            EditReminderEvent.SelectReminderDate -> selectReminderDate()

            EditReminderEvent.SelectReminderTime -> selectReminderTime()

            is EditReminderEvent.ReminderDateChanged -> reminderDateChanged(event)

            is EditReminderEvent.ReminderTimeChanged -> reminderTimeChanged(event)
        }
    }

    private fun getReminder() = viewModelScope.launch(dispatchers.io) {
        repository.getEditReminder(uid).firstOrNull()?.let {
            showReminder(it)
        }
    }

    private fun saveReminder() = viewModelScope.launch(dispatchers.io) {
        repository.saveReminder(
            uid = uid ?: "",
            reminder = _reminderState.value.timeInMillis,
            lastModified = System.currentTimeMillis()
        )

        withContext(dispatchers.main) {
            alarmManager.createReminder(
                uid = uid ?: "",
                reminder = _reminderState.value.timeInMillis
            )
        }

        showBackScreen()
    }

    private fun cancelSavingReminder() = viewModelScope.launch(dispatchers.main) {
        showBackScreen()
    }

    private fun cancelSelectingReminderDate() {
        _dateDialogState.value = false
    }

    private fun cancelSelectingReminderTime() {
        _timeDialogState.value = false
    }

    private fun selectReminderDate() {
        _dateDialogState.value = true
    }

    private fun selectReminderTime() {
        _timeDialogState.value = true
    }

    private fun deleteReminder() = viewModelScope.launch(dispatchers.io) {
        repository.deleteReminder(
            uid = uid ?: "",
            lastModified = System.currentTimeMillis()
        )

        withContext(dispatchers.main) {
            alarmManager.deleteReminder(uid ?: "")
        }

        showBackScreen()
    }

    private fun reminderDateChanged(event: EditReminderEvent.ReminderDateChanged) {
        _reminderState.value = _reminderState.value.apply {
            set(Calendar.YEAR, event.year)
            set(Calendar.MONTH, event.month)
            set(Calendar.DAY_OF_MONTH, event.dayOfMonth)
        }

        _dateState.value = _dateState.value.copy(
            text = _reminderState.value.getDisplayDate()
        )
    }

    private fun reminderTimeChanged(event: EditReminderEvent.ReminderTimeChanged) {
        _reminderState.value = _reminderState.value.apply {
            set(Calendar.HOUR_OF_DAY, event.hourOfDay)
            set(Calendar.MINUTE, event.minute)
        }

        _timeState.value = _timeState.value.copy(
            text = _reminderState.value.getDisplayTime()
        )
    }

    private suspend fun showReminder(editReminder: EditReminder) = withContext(dispatchers.main) {
        editReminderState.value = editReminder

        val value = editReminder.shoppingList ?: ShoppingList()
        val preferences = editReminder.preferences

        showHeader(
            isAdd = value.reminder == null,
            preferences = preferences
        )

        if (value.reminder == null) {
            hideDeleteButton()
        } else {
            _reminderState.value = Calendar.getInstance().apply {
                timeInMillis = value.reminder
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            showDeleteButton()
        }
    }

    private fun showHeader(isAdd: Boolean, preferences: ProductPreferences) {
        val text: UiText = if (isAdd) {
            mapping.toResourcesUiText(R.string.editReminder_header_addReminder)
        } else {
            mapping.toResourcesUiText(R.string.editReminder_header_editReminder)
        }

        _headerState.value = mapping.toOnDialogHeader(
            text = text,
            fontSize = preferences.fontSize
        )
    }

    private fun showDateButton() {
        _dateState.value = mapping.toBody(
            text = _reminderState.value.getDisplayDate(),
            fontSize = FontSize.MEDIUM
        )
    }

    private fun showTimeButton() {
        _timeState.value = mapping.toBody(
            text = _reminderState.value.getDisplayTime(),
            fontSize = FontSize.MEDIUM
        )
    }

    private fun showDeleteButton() {
        _deleteState.value = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.editReminder_action_deleteReminder),
            fontSize = FontSize.MEDIUM
        )
    }

    private fun showCancelButton() {
        _cancelState.value = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.editReminder_action_cancelSavingReminder),
            fontSize = FontSize.MEDIUM
        )
    }

    private fun showSaveButton() {
        _saveState.value = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.editReminder_action_saveReminder),
            fontSize = FontSize.MEDIUM
        )
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(EditReminderScreenEvent.ShowBackScreen)
    }

    private fun hideDeleteButton() {
        _deleteState.value = null
    }
}
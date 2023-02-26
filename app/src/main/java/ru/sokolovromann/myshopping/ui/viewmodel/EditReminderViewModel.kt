package ru.sokolovromann.myshopping.ui.viewmodel

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
import ru.sokolovromann.myshopping.data.repository.EditReminderRepository
import ru.sokolovromann.myshopping.data.repository.model.EditReminder
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.ui.*
import ru.sokolovromann.myshopping.ui.compose.event.EditReminderScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.EditReminderState
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditReminderEvent
import javax.inject.Inject

@HiltViewModel
class EditReminderViewModel @Inject constructor(
    private val repository: EditReminderRepository,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle,
    private val alarmManager: PurchasesAlarmManager
) : ViewModel(), ViewModelEvent<EditReminderEvent> {

    val editReminderState: EditReminderState = EditReminderState()

    private val _screenEventFlow: MutableSharedFlow<EditReminderScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<EditReminderScreenEvent> = _screenEventFlow

    init {
        getEditReminder()
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

    private fun getEditReminder() = viewModelScope.launch {
        val uid: String? = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key)
        repository.getEditReminder(uid).firstOrNull()?.let {
            editReminderLoaded(it)
        }
    }

    private suspend fun editReminderLoaded(
        editReminder: EditReminder
    ) = withContext(dispatchers.main) {
        editReminderState.populate(editReminder, alarmManager.checkCorrectReminderPermissions())
    }

    private fun saveReminder() = viewModelScope.launch {
        val shoppingList = editReminderState.getShoppingListResult()
            .getOrElse { return@launch }

        val reminder = shoppingList.reminder ?: return@launch

        repository.saveReminder(
            uid = shoppingList.uid,
            reminder = reminder,
            lastModified = shoppingList.lastModified
        )

        withContext(dispatchers.main) {
            alarmManager.createReminder(
                uid = shoppingList.uid,
                reminder = reminder
            )
            _screenEventFlow.emit(EditReminderScreenEvent.ShowBackScreen)
        }
    }

    private fun cancelSavingReminder() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(EditReminderScreenEvent.ShowBackScreen)
    }

    private fun cancelSelectingReminderDate() {
        editReminderState.cancelSelectingReminderDate()
    }

    private fun cancelSelectingReminderTime() {
        editReminderState.cancelSelectingReminderTime()
    }

    private fun selectReminderDate() {
        editReminderState.selectReminderDate()
    }

    private fun selectReminderTime() {
        editReminderState.selectReminderTime()
    }

    private fun deleteReminder() = viewModelScope.launch {
        val shoppingList = editReminderState.getShoppingListResult()
            .getOrElse { return@launch }

        repository.deleteReminder(
            uid = shoppingList.uid,
            lastModified = shoppingList.lastModified
        )

        withContext(dispatchers.main) {
            alarmManager.deleteReminder(shoppingList.uid)
            _screenEventFlow.emit(EditReminderScreenEvent.ShowBackScreen)
        }
    }

    private fun reminderDateChanged(event: EditReminderEvent.ReminderDateChanged) {
        editReminderState.changeReminderDate(event.year, event.month, event.dayOfMonth)
    }

    private fun reminderTimeChanged(event: EditReminderEvent.ReminderTimeChanged) {
        editReminderState.changeReminderTime(event.hourOfDay, event.minute)
    }
}
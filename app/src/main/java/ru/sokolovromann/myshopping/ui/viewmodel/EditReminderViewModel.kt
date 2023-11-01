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
import ru.sokolovromann.myshopping.BuildConfig
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.ui.*
import ru.sokolovromann.myshopping.ui.compose.event.EditReminderScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.EditReminderState
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditReminderEvent
import javax.inject.Inject

@HiltViewModel
class EditReminderViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
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

            EditReminderEvent.ShowPermissions -> showPermissions()
        }
    }

    private fun getEditReminder() = viewModelScope.launch {
        val uid: String? = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key)
        shoppingListsRepository.getShoppingListWithConfig(uid).firstOrNull()?.let {
            shoppingListLoaded(it)
        }
    }

    private suspend fun shoppingListLoaded(
        shoppingListWithConfig: ShoppingListWithConfig
    ) = withContext(AppDispatchers.Main) {
        editReminderState.populate(shoppingListWithConfig, alarmManager.checkCorrectReminderPermissions())
    }

    private fun saveReminder() = viewModelScope.launch {
        val shoppingUid = editReminderState.getShoppingUid()
        val reminder = editReminderState.getReminder()

        shoppingListsRepository.saveReminder(
            shoppingUid = shoppingUid,
            reminder = reminder
        )

        withContext(AppDispatchers.Main) {
            alarmManager.createReminder(
                uid = shoppingUid,
                reminder = reminder.millis
            )
            _screenEventFlow.emit(EditReminderScreenEvent.ShowBackScreen)
        }
    }

    private fun cancelSavingReminder() = viewModelScope.launch(AppDispatchers.Main) {
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
        val shoppingUid = editReminderState.getShoppingUid()
        shoppingListsRepository.deleteReminder(shoppingUid)

        withContext(AppDispatchers.Main) {
            alarmManager.deleteReminder(shoppingUid)
            _screenEventFlow.emit(EditReminderScreenEvent.ShowBackScreen)
        }
    }

    private fun reminderDateChanged(event: EditReminderEvent.ReminderDateChanged) {
        editReminderState.changeReminderDate(event.year, event.month, event.dayOfMonth)
    }

    private fun reminderTimeChanged(event: EditReminderEvent.ReminderTimeChanged) {
        editReminderState.changeReminderTime(event.hourOfDay, event.minute)
    }

    private fun showPermissions() = viewModelScope.launch {
        val event = EditReminderScreenEvent.ShowPermissions(BuildConfig.APPLICATION_ID)
        _screenEventFlow.emit(event)
    }
}
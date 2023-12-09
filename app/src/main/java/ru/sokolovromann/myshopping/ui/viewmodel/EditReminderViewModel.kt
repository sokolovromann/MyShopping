package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.BuildConfig
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.ui.*
import ru.sokolovromann.myshopping.ui.compose.event.EditReminderScreenEvent
import ru.sokolovromann.myshopping.ui.model.EditReminderState
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

    init { onInit() }

    override fun onEvent(event: EditReminderEvent) {
        when (event) {
            EditReminderEvent.OnClickSave -> onClickSave()

            EditReminderEvent.OnClickCancel -> onClickCancel()

            EditReminderEvent.OnClickDelete -> onClickDelete()

            EditReminderEvent.OnClickOpenPermissions -> onClickOpenPermissions()

            is EditReminderEvent.OnDateChanged -> onDateChanged(event)

            is EditReminderEvent.OnSelectDate -> onSelectDate(event)

            is EditReminderEvent.OnTimeChanged -> onTimeChanged(event)

            is EditReminderEvent.OnSelectTime -> onSelectTime(event)
        }
    }

    private fun onInit() = viewModelScope.launch(AppDispatchers.Main) {
        val uid: String? = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key)
        shoppingListsRepository.getShoppingListWithConfig(uid).firstOrNull()?.let {
            editReminderState.populate(it, alarmManager.checkCorrectReminderPermissions())
        }
    }

    private fun onClickSave() = viewModelScope.launch(AppDispatchers.Main) {
        editReminderState.onWaiting()

        val shopping = editReminderState.getCurrentShopping()
        val reminder = shopping.reminder ?: return@launch
        shoppingListsRepository.saveReminder(
            shoppingUid = shopping.uid,
            reminder = reminder
        ).onSuccess {
            alarmManager.createReminder(
                uid = shopping.uid,
                reminder = reminder.millis
            )
            _screenEventFlow.emit(EditReminderScreenEvent.OnShowBackScreen)
        }
    }

    private fun onClickCancel() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(EditReminderScreenEvent.OnShowBackScreen)
    }

    private fun onClickDelete() = viewModelScope.launch(AppDispatchers.Main) {
        editReminderState.onWaiting()

        val shoppingUid = editReminderState.getCurrentShopping().uid
        shoppingListsRepository.deleteReminder(shoppingUid)
            .onSuccess {
                alarmManager.deleteReminder(shoppingUid)
                _screenEventFlow.emit(EditReminderScreenEvent.OnShowBackScreen)
            }
    }

    private fun onClickOpenPermissions() = viewModelScope.launch(AppDispatchers.Main) {
        val event = EditReminderScreenEvent.OnShowPermissions(BuildConfig.APPLICATION_ID)
        _screenEventFlow.emit(event)
    }

    private fun onDateChanged(event: EditReminderEvent.OnDateChanged) {
        editReminderState.onDateChanged(event.year, event.month, event.dayOfMonth)
    }

    private fun onSelectDate(event: EditReminderEvent.OnSelectDate) {
        editReminderState.onSelectDate(event.display)
    }

    private fun onTimeChanged(event: EditReminderEvent.OnTimeChanged) {
        editReminderState.onTimeChanged(event.hourOfDay, event.minute)
    }

    private fun onSelectTime(event: EditReminderEvent.OnSelectTime) {
        editReminderState.onSelectTime(event.display)
    }
}
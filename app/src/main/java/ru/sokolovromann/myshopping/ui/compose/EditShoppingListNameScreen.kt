package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.ui.compose.event.EditShoppingListNameScreenEvent
import ru.sokolovromann.myshopping.ui.viewmodel.EditShoppingListNameViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditShoppingListNameEvent

@Composable
fun EditShoppingListNameScreen(
    navController: NavController,
    viewModel: EditShoppingListNameViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                EditShoppingListNameScreenEvent.ShowBackScreen -> navController.popBackStack()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.keyboardFlow.collect {
            if (it) {
                focusRequester.requestFocus()
            } else {
                focusManager.clearFocus(force = true)
            }
        }
    }

    AppDialog(
        onDismissRequest = {
            viewModel.onEvent(EditShoppingListNameEvent.CancelSavingShoppingListName)
        }
    ) {
        AppDialogHeader(header = viewModel.headerState.value)

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            OutlinedAppTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                state = viewModel.nameState,
                onValueChange = {
                    val event = EditShoppingListNameEvent.ShoppingListNameChanged(it)
                    viewModel.onEvent(event)
                }
            )
        }

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 8.dp)
        ) {
            TextButton(
                onClick = { viewModel.onEvent(EditShoppingListNameEvent.CancelSavingShoppingListName) }
            ) {
                AppText(data = viewModel.cancelState.value)
            }
            OutlinedButton(
                onClick = { viewModel.onEvent(EditShoppingListNameEvent.SaveShoppingListName) }
            ) {
                AppText(data = viewModel.saveState.value)
            }
        }
    }
}
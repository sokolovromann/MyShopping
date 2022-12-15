package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.ui.compose.event.CalculateChangeScreenEvent
import ru.sokolovromann.myshopping.ui.viewmodel.CalculateChangeViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.CalculateChangeEvent

@Composable
fun CalculateChangeScreen(
    navController: NavController,
    viewModel: CalculateChangeViewModel = hiltViewModel()
) {

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                CalculateChangeScreenEvent.ShowBackScreen -> navController.popBackStack()
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
        onDismissRequest = { viewModel.onEvent(CalculateChangeEvent.ShowBackScreen) },
        title = { Title(viewModel) },
        actionButtons = { ActionButtons(viewModel) },
        content = { Content(viewModel, focusRequester) }
    )
}

@Composable
private fun Title(viewModel: CalculateChangeViewModel) {
    AppText(data = viewModel.headerState.value)
}

@Composable
private fun ActionButtons(viewModel: CalculateChangeViewModel) {
    TextButton(
        onClick = { viewModel.onEvent(CalculateChangeEvent.ShowBackScreen) },
        content = { AppText(data = viewModel.backState.value) }
    )
}

@Composable
private fun Content(
    viewModel: CalculateChangeViewModel,
    focusRequester: FocusRequester
) {
    OutlinedAppTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        state = viewModel.userMoneyState,
        onValueChange = {
            val event = CalculateChangeEvent.UserMoneyChanged(it)
            viewModel.onEvent(event)
        }
    )

    AppText(
        modifier = Modifier.padding(vertical = 4.dp),
        data = viewModel.totalState.value
    )

    AppText(
        modifier = Modifier.padding(vertical = 4.dp),
        data = viewModel.changeState.value
    )
}
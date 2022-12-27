package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.CalculateChangeScreenEvent
import ru.sokolovromann.myshopping.ui.utils.toTextField
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
        header = { Text(text = stringResource(R.string.calculateChange_header)) },
        actionButtons = { ActionButtons(viewModel) },
        content = { Content(viewModel, focusRequester) }
    )
}

@Composable
private fun ActionButtons(viewModel: CalculateChangeViewModel) {
    AppDialogActionButton(
        onClick = { viewModel.onEvent(CalculateChangeEvent.ShowBackScreen) },
        content = { Text(text = stringResource(R.string.calculateChange_action_closeCalculatingChange)) }
    )
}

@Composable
private fun Content(
    viewModel: CalculateChangeViewModel,
    focusRequester: FocusRequester
) {
    val screenData = viewModel.calculateChangeState.screenData
    OutlinedAppTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = screenData.userMoneyValue,
        valueFontSize = screenData.fontSize.toTextField().sp,
        onValueChange = {
            val event = CalculateChangeEvent.UserMoneyChanged(it)
            viewModel.onEvent(event)
        },
        label = { Text(text = stringResource(R.string.calculateChange_label_userMoney)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Previous
        ),
        keyboardActions = KeyboardActions(
            onPrevious = { viewModel.onEvent(CalculateChangeEvent.ShowBackScreen) }
        )
    )

    Text(
        modifier = Modifier.padding(vertical = 4.dp),
        text = screenData.totalText.asCompose(),
        style = MaterialTheme.typography.body1,
        color = MaterialTheme.colors.onSurface
    )

    Text(
        modifier = Modifier.padding(vertical = 4.dp),
        text = screenData.changeText.asCompose(),
        style = MaterialTheme.typography.body1,
        color = MaterialTheme.colors.onSurface
    )
}
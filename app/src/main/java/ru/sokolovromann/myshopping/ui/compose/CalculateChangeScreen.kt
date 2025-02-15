package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.CalculateChangeScreenEvent
import ru.sokolovromann.myshopping.ui.viewmodel.CalculateChangeViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.CalculateChangeEvent

@Composable
fun CalculateChangeScreen(
    navController: NavController,
    viewModel: CalculateChangeViewModel = hiltViewModel()
) {
    val state = viewModel.calculateChangeState
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                CalculateChangeScreenEvent.OnShowBackScreen -> {
                    navController.popBackStack()
                    focusManager.clearFocus(force = true)
                }

                CalculateChangeScreenEvent.OnShowKeyboard -> {
                    focusRequester.requestFocus()
                }
            }
        }
    }

    DefaultDialog(
        onDismissRequest = {
            val event = CalculateChangeEvent.OnClickCancel
            viewModel.onEvent(event)
        },
        header = { Text(stringResource(R.string.calculateChange_header)) },
        actionButtons = {
            TextButton(
                onClick = { viewModel.onEvent(CalculateChangeEvent.OnClickCancel) },
                enabled = !state.waiting,
                content = { Text(stringResource(R.string.calculateChange_action_closeCalculatingChange)) }
            )
        }
    ) {
        OutlinedAppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = state.userMoneyValue,
            onValueChange = {
                val event = CalculateChangeEvent.OnUserMoneyChanged(it)
                viewModel.onEvent(event)
            },
            label = { Text(text = stringResource(R.string.calculateChange_label_userMoney)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Previous
            ),
            keyboardActions = KeyboardActions(
                onPrevious = { viewModel.onEvent(CalculateChangeEvent.OnClickCancel) }
            )
        )

        Spacer(modifier = Modifier.size(CalculateChangeSpacerSmallSize))

        ProvideCalculateChangeTextStyle {
            Text(text = state.totalText.asCompose())
            Spacer(modifier = Modifier.size(CalculateChangeSpacerMediumSize))
            Text(text = state.changeText.asCompose())
        }
    }
}

@Composable
private fun ProvideCalculateChangeTextStyle(content: @Composable () -> Unit) {
    ProvideTextStyle(
        value = MaterialTheme.typography.body1.copy(
            color = MaterialTheme.colors.onSurface
        ),
        content = content
    )
}

private val CalculateChangeSpacerSmallSize = 4.dp
private val CalculateChangeSpacerMediumSize = 8.dp
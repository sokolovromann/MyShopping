package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.MaxAutocompletesScreenEvent
import ru.sokolovromann.myshopping.ui.viewmodel.MaxAutocompletesViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.MaxAutocompletesEvent

@Composable
fun MaxAutocompletesScreen(
    navController: NavController,
    viewModel: MaxAutocompletesViewModel = hiltViewModel()
) {
    val state = viewModel.maxAutocompletesState

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                MaxAutocompletesScreenEvent.OnShowBackScreen -> {
                    navController.popBackStack()
                }
            }
        }
    }

    AppDialog(
        onDismissRequest = { viewModel.onEvent(MaxAutocompletesEvent.OnClickCancel) },
        header = { Text(text = stringResource(R.string.maxAutocompletes_header)) },
        actionButtons = {
            AppDialogActionButton(
                onClick = { viewModel.onEvent(MaxAutocompletesEvent.OnClickCancel) },
                enabled = !state.waiting,
                content = { Text(text = stringResource(R.string.maxAutocompletes_action_cancelSavingMaxAutocompletes)) }
            )
            AppDialogActionButton(
                onClick = { viewModel.onEvent(MaxAutocompletesEvent.OnClickSave) },
                primaryButton = true,
                enabled = !state.waiting,
                content = { Text(text = stringResource(R.string.maxAutocompletes_action_saveMaxAutocompletes)) }
            )
        }
    ) {
        MaxAutocompletesItem(
            text = stringResource(R.string.maxAutocompletes_text_names),
            maxCount = state.maxNames,
            onClickMinus = { viewModel.onEvent(MaxAutocompletesEvent.OnClickMinusOneName) },
            onCLickPlus = { viewModel.onEvent(MaxAutocompletesEvent.OnClickPlusOneName) },
            fontSize = state.fontSize.itemBody.sp
        )
        MaxAutocompletesItem(
            text = stringResource(R.string.maxAutocompletes_text_quantities),
            maxCount = state.maxQuantities,
            onClickMinus = { viewModel.onEvent(MaxAutocompletesEvent.OnClickMinusOneQuantity) },
            onCLickPlus = { viewModel.onEvent(MaxAutocompletesEvent.OnClickPlusOneQuantity) },
            fontSize = state.fontSize.itemBody.sp
        )
        MaxAutocompletesItem(
            text = stringResource(R.string.maxAutocompletes_text_moneys),
            maxCount = state.maxMoneys,
            onClickMinus = { viewModel.onEvent(MaxAutocompletesEvent.OnClickMinusOneMoney) },
            onCLickPlus = { viewModel.onEvent(MaxAutocompletesEvent.OnClickPlusOneMoney) },
            fontSize = state.fontSize.itemBody.sp
        )
        MaxAutocompletesItem(
            text = stringResource(R.string.maxAutocompletes_text_other),
            maxCount = state.maxOthers,
            onClickMinus = { viewModel.onEvent(MaxAutocompletesEvent.OnClickMinusOneOther) },
            onCLickPlus = { viewModel.onEvent(MaxAutocompletesEvent.OnClickPlusOneOther) },
            fontSize = state.fontSize.itemBody.sp
        )
    }
}

@Composable
private fun MaxAutocompletesItem(
    text: String,
    maxCount: Int,
    onClickMinus: () -> Unit,
    onCLickPlus: () -> Unit,
    fontSize: TextUnit
) {
    Row(
        modifier = Modifier.height(MaxAutocompletesItemHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = text,
            fontSize = fontSize
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = maxCount.toString(),
            fontSize = fontSize
        )
        Spacer(modifier = Modifier.size(MaxAutocompletesSpacerMediumSize))
        AppChip(onClick = onClickMinus) {
            Text(
                text = stringResource(R.string.maxAutocompletes_action_minusOne),
                fontSize = fontSize
            )
        }
        Spacer(modifier = Modifier.size(MaxAutocompletesSpacerSmallSize))
        AppChip(onClick = onCLickPlus) {
            Text(
                text = stringResource(R.string.maxAutocompletes_action_plusOne),
                fontSize = fontSize
            )
        }
    }
}

private val MaxAutocompletesItemHeight = 48.dp
private val MaxAutocompletesSpacerSmallSize = 4.dp
private val MaxAutocompletesSpacerMediumSize = 8.dp
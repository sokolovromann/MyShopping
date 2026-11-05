package ru.sokolovromann.myshopping.feature.dictionary.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.ui.component.ErrorSupportingText
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.feature.dictionary.R
import ru.sokolovromann.myshopping.feature.dictionary.model.AddEditSuggestionState
import ru.sokolovromann.myshopping.feature.dictionary.model.FabricItem
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AddEditSuggestionContent(
    onSuggestionNameChange: (TextFieldValue) -> Unit,
    onDoneClick: () -> Unit,
    onDeleteFabricClick: (UID) -> Unit,
    addEditSuggestionState: AddEditSuggestionState
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(all = 8.dp)
    ) {
        NameTextField(
            onChange = onSuggestionNameChange,
            onDoneClick = onDoneClick,
            name = addEditSuggestionState.name,
            isNameError = addEditSuggestionState.isNameError,
            showKeyboard = addEditSuggestionState.showKeyboard
        )
        addEditSuggestionState.fabrics.forEach { (text, items) ->
            FabricsSurface(
                onDeleteClick = onDeleteFabricClick,
                text = text,
                items = items
            )
        }
    }
}

@Composable
private fun NameTextField(
    onChange: (TextFieldValue) -> Unit,
    onDoneClick: () -> Unit,
    name: TextFieldValue,
    isNameError: Boolean,
    showKeyboard: Boolean
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(key1 = showKeyboard) {
        if (showKeyboard) {
            delay(100.milliseconds)
            focusRequester.requestFocus()
        }
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = name,
        onValueChange = onChange,
        label = {
            Text(stringResource(R.string.add_edit_suggestion_label_name))
        },
        isError = isNameError,
        supportingText = {
            if (isNameError) {
                ErrorSupportingText(stringResource(R.string.add_edit_suggestion_supporting_text_name_error))
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDoneClick() }
        ),
        singleLine = true
    )
}

@Composable
private fun FabricsSurface(
    onDeleteClick: (UID) -> Unit,
    text: UiText,
    items: List<FabricItem>
) {
    if (items.isEmpty()) return

    val scrollState = rememberScrollState()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text.asCompose())
            Spacer(Modifier.size(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    items.forEach { item ->
                        FabricItemInputChip(onDeleteClick, item)
                    }
                }
            )
        }
    }
}

@Composable
private fun FabricItemInputChip(
    onDeleteClick: (UID) -> Unit,
    item: FabricItem
) {
    InputChip(
        selected = false,
        onClick = {},
        label = { Text(item.text) },
        trailingIcon = {
            IconButton(
                onClick = { onDeleteClick(item.uid) },
                modifier = Modifier.size(InputChipDefaults.IconSize)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        },
        colors = InputChipDefaults.inputChipColors(
            containerColor = MaterialTheme.colorScheme.background,
            disabledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
        )
    )
    Spacer(Modifier.size(4.dp))
}
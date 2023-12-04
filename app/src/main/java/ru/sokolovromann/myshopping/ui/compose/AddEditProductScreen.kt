package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.LockProductElement
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.Quantity
import ru.sokolovromann.myshopping.ui.compose.event.AddEditProductScreenEvent
import ru.sokolovromann.myshopping.ui.model.UiFontSize
import ru.sokolovromann.myshopping.ui.utils.*
import ru.sokolovromann.myshopping.ui.viewmodel.AddEditProductViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditProductEvent

@Composable
fun AddEditProductScreen(
    navController: NavController,
    viewModel: AddEditProductViewModel = hiltViewModel()
) {
    val state = viewModel.addEditProductState
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                AddEditProductScreenEvent.ShowBackScreen -> {
                    navController.popBackStack()
                    focusManager.clearFocus(force = true)
                }

                is AddEditProductScreenEvent.ShowBackScreenAndUpdateProductsWidget -> {
                    updateProductsWidget(context, it.shoppingUid)
                    navController.popBackStack()
                    focusManager.clearFocus(force = true)
                }

                AddEditProductScreenEvent.ShowKeyboard -> {
                    focusRequester.requestFocus()
                }
            }
        }
    }

    BackHandler { viewModel.onEvent(AddEditProductEvent.CancelSavingProduct) }

    AppScaffold(
        topBar = {
            AppTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        enabled = !state.waiting,
                        onClick = { viewModel.onEvent(AddEditProductEvent.CancelSavingProduct) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.addEditProduct_contentDescription_navigationIcon)
                        )
                    }
                },
                actions = {
                    TextButton(
                        enabled = !state.waiting,
                        onClick = { viewModel.onEvent(AddEditProductEvent.SaveProduct) }
                    ) {
                        Text(text = stringResource(R.string.addEditProduct_action_saveProduct).uppercase())
                    }
                }
            )
        },
        backgroundColor = MaterialTheme.colors.surface
    ) { paddings ->
        AddEditProductContent(scaffoldPaddings = paddings) {
            val keyboardActions: KeyboardActions = if (state.enterToSaveProduct) {
                KeyboardActions(onDone = { viewModel.onEvent(AddEditProductEvent.SaveProduct) })
            } else {
                KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) })
            }

            val imeAction: ImeAction = if (state.enterToSaveProduct) {
                ImeAction.Done
            } else {
                ImeAction.Next
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(AddEditProductElementPaddings)
            ) {
                OutlinedAppTextField(
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    value = state.nameValue,
                    valueFontSize = state.fontSize.textField.sp,
                    onValueChange = {
                        val event = AddEditProductEvent.ProductNameChanged(it)
                        viewModel.onEvent(event)
                    },
                    label = { Text(text = stringResource(R.string.addEditProduct_label_name)) },
                    error = { Text(text = stringResource(R.string.addEditProduct_message_nameError)) },
                    showError = state.nameError,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = imeAction
                    ),
                    keyboardActions = keyboardActions
                )

                if (state.displayOtherFields) {
                    Spacer(modifier = Modifier.size(AddEditProductSpacerLargeSize))

                    IconButton(
                        modifier = Modifier
                            .height(AddEditProductButtonHeight)
                            .padding(AddEditProductButtonPaddings)
                            .border(
                                border = getButtonBorderStroke(),
                                shape = MaterialTheme.shapes.small
                            ),
                        onClick = { viewModel.onEvent(AddEditProductEvent.InvertNameOtherFields) }
                    ) {
                        Icon(
                            imageVector = if (state.displayNameOtherFields) {
                                Icons.Default.KeyboardArrowUp
                            } else {
                                Icons.Default.KeyboardArrowDown
                            },
                            contentDescription = stringResource(R.string.addEditProduct_contentDescription_showNameOtherFields),
                            tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                        )
                    }
                }
            }

            AddEditProductAutocompleteNames(
                names = state.autocompletes.names,
                fontSize = state.fontSize,
                onClick = {
                    val event = AddEditProductEvent.AutocompleteNameSelected(it)
                    viewModel.onEvent(event)
                }
            )

            if (state.displayNameOtherFields) {
                OutlinedAppTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AddEditProductElementPaddings)
                        .focusRequester(focusRequester),
                    value = state.brandValue,
                    valueFontSize = state.fontSize.textField.sp,
                    onValueChange = {
                        val event = AddEditProductEvent.ProductBrandChanged(it)
                        viewModel.onEvent(event)
                    },
                    label = { Text(text = stringResource(R.string.addEditProduct_label_brand)) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = imeAction
                    ),
                    keyboardActions = keyboardActions
                )

                AddEditProductAutocompleteStrings(
                    list = state.autocompletes.brands,
                    fontSize = state.fontSize,
                    onClick = {
                        val event = AddEditProductEvent.AutocompleteBrandSelected(it)
                        viewModel.onEvent(event)
                    }
                )

                OutlinedAppTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AddEditProductElementPaddings)
                        .focusRequester(focusRequester),
                    value = state.sizeValue,
                    valueFontSize = state.fontSize.textField.sp,
                    onValueChange = {
                        val event = AddEditProductEvent.ProductSizeChanged(it)
                        viewModel.onEvent(event)
                    },
                    label = { Text(text = stringResource(R.string.addEditProduct_label_size)) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = imeAction
                    ),
                    keyboardActions = keyboardActions
                )

                AddEditProductAutocompleteStrings(
                    list = state.autocompletes.sizes,
                    fontSize = state.fontSize,
                    onClick = {
                        val event = AddEditProductEvent.AutocompleteSizeSelected(it)
                        viewModel.onEvent(event)
                    }
                )

                OutlinedAppTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AddEditProductElementPaddings)
                        .focusRequester(focusRequester),
                    value = state.colorValue,
                    valueFontSize = state.fontSize.textField.sp,
                    onValueChange = {
                        val event = AddEditProductEvent.ProductColorChanged(it)
                        viewModel.onEvent(event)
                    },
                    label = { Text(text = stringResource(R.string.addEditProduct_label_color)) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = imeAction
                    ),
                    keyboardActions = keyboardActions
                )

                AddEditProductAutocompleteStrings(
                    list = state.autocompletes.colors,
                    fontSize = state.fontSize,
                    onClick = {
                        val event = AddEditProductEvent.AutocompleteColorSelected(it)
                        viewModel.onEvent(event)
                    }
                )

                OutlinedAppTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AddEditProductElementPaddings)
                        .focusRequester(focusRequester),
                    value = state.manufacturerValue,
                    valueFontSize = state.fontSize.textField.sp,
                    onValueChange = {
                        val event = AddEditProductEvent.ProductManufacturerChanged(it)
                        viewModel.onEvent(event)
                    },
                    label = { Text(text = stringResource(R.string.addEditProduct_label_manufacturer)) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = imeAction
                    ),
                    keyboardActions = keyboardActions
                )

                AddEditProductAutocompleteStrings(
                    list = state.autocompletes.manufacturers,
                    fontSize = state.fontSize,
                    onClick = {
                        val event = AddEditProductEvent.AutocompleteManufacturerSelected(it)
                        viewModel.onEvent(event)
                    }
                )

                OutlinedAppTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AddEditProductElementPaddings)
                        .focusRequester(focusRequester),
                    value = state.uidValue,
                    valueFontSize = state.fontSize.textField.sp,
                    onValueChange = {
                        val event = AddEditProductEvent.ProductUidChanged(it)
                        viewModel.onEvent(event)
                    },
                    label = { Text(text = stringResource(R.string.addEditProduct_label_uid)) },
                    error = { Text(text = stringResource(R.string.addEditProduct_message_uidError)) },
                    showError = state.uidError,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = imeAction
                    ),
                    keyboardActions = keyboardActions
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(AddEditProductElementPaddings)
            ) {
                OutlinedAppTextField(
                    modifier = Modifier.weight(0.6f),
                    value = state.quantityValue,
                    valueFontSize = state.fontSize.textField.sp,
                    onValueChange = {
                        val event = AddEditProductEvent.ProductQuantityChanged(it)
                        viewModel.onEvent(event)
                    },
                    enabled = state.lockProductElementValue.selected != LockProductElement.QUANTITY,
                    label = { Text(text = stringResource(R.string.addEditProduct_label_quantity)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = imeAction
                    ),
                    keyboardActions = keyboardActions
                )

                Spacer(modifier = Modifier.size(AddEditProductSpacerLargeSize))

                OutlinedAppTextField(
                    modifier = Modifier.weight(0.4f),
                    value = state.quantitySymbolValue,
                    valueFontSize = state.fontSize.textField.sp,
                    onValueChange = {
                        val event = AddEditProductEvent.ProductQuantitySymbolChanged(it)
                        viewModel.onEvent(event)
                    },
                    label = { Text(text = stringResource(R.string.addEditProduct_label_quantitySymbol)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = imeAction
                    ),
                    keyboardActions = keyboardActions
                )
            }

            AddEditProductAutocompleteSymbols(
                quantities = state.autocompletes.quantitySymbols,
                minusOneQuantityChip = {
                    AppChip(
                        onClick = { viewModel.onEvent(AddEditProductEvent.AutocompleteMinusOneQuantitySelected) }
                    ) {
                        Text(
                            text = stringResource(R.string.addEditProduct_action_quantityMinusOne),
                            fontSize = state.fontSize.itemBody.sp
                        )
                    }
                },
                plusOneQuantityChip = {
                    AppChip(
                        onClick = { viewModel.onEvent(AddEditProductEvent.AutocompletePlusOneQuantitySelected) }
                    ) {
                        Text(
                            text = stringResource(R.string.addEditProduct_action_quantityPlusOne),
                            fontSize = state.fontSize.itemBody.sp
                        )
                    }
                },
                defaultQuantitySymbolChips = {
                    stringArrayResource(R.array.data_text_defaultAutocompleteQuantitySymbols).forEach {
                        AppChip(
                            onClick = {
                                val event = AddEditProductEvent.ProductQuantitySymbolChanged(it.toTextFieldValue())
                                viewModel.onEvent(event)
                            },
                            content = {
                                Text(
                                    text = it,
                                    fontSize = state.fontSize.itemBody.sp
                                )
                            }
                        )
                        Spacer(modifier = Modifier.size(AddEditProductSpacerMediumSize))
                    }
                },
                defaultQuantitySymbolChipsEnabled = state.autocompletes.displayDefaultQuantitySymbols,
                fontSize = state.fontSize,
                enabled = state.lockProductElementValue.selected != LockProductElement.QUANTITY,
                onClick = {
                    val event = AddEditProductEvent.AutocompleteQuantitySymbolSelected(it)
                    viewModel.onEvent(event)
                }
            )

            AddEditProductAutocompleteQuantities(
                quantities = state.autocompletes.quantities,
                fontSize = state.fontSize,
                enabled = state.lockProductElementValue.selected != LockProductElement.QUANTITY,
                onClick = {
                    val event = AddEditProductEvent.AutocompleteQuantitySelected(it)
                    viewModel.onEvent(event)
                }
            )

            if (state.displayMoney) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(AddEditProductElementPaddings)
                ) {
                    OutlinedAppTextField(
                        modifier = Modifier.weight(1f),
                        value = state.priceValue,
                        valueFontSize = state.fontSize.textField.sp,
                        onValueChange = {
                            val event = AddEditProductEvent.ProductPriceChanged(it)
                            viewModel.onEvent(event)
                        },
                        enabled = state.lockProductElementValue.selected != LockProductElement.PRICE,
                        label = { Text(text = stringResource(R.string.addEditProduct_label_price)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = imeAction
                        ),
                        keyboardActions = keyboardActions
                    )

                    Spacer(modifier = Modifier.size(AddEditProductSpacerLargeSize))

                    IconButton(
                        modifier = Modifier
                            .height(AddEditProductButtonHeight)
                            .padding(AddEditProductButtonPaddings)
                            .border(
                                border = getButtonBorderStroke(),
                                shape = MaterialTheme.shapes.small
                            ),
                        onClick = { viewModel.onEvent(AddEditProductEvent.InvertPriceOtherFields) }
                    ) {
                        Icon(
                            imageVector = if (state.displayPriceOtherFields) {
                                Icons.Default.KeyboardArrowUp
                            } else {
                                Icons.Default.KeyboardArrowDown
                            },
                            contentDescription = stringResource(R.string.addEditProduct_contentDescription_showPriceOtherFields),
                            tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                        )
                    }
                }

                AddEditProductAutocompletePrices(
                    prices = state.autocompletes.prices,
                    fontSize = state.fontSize,
                    enabled = state.lockProductElementValue.selected != LockProductElement.PRICE,
                    onClick = {
                        val event = AddEditProductEvent.AutocompletePriceSelected(it)
                        viewModel.onEvent(event)
                    }
                )

                if (state.displayPriceOtherFields) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(AddEditProductElementPaddings)
                    ) {
                        OutlinedAppTextField(
                            modifier = Modifier.weight(0.6f),
                            value = state.discountValue,
                            valueFontSize = state.fontSize.textField.sp,
                            onValueChange = {
                                val event = AddEditProductEvent.ProductDiscountChanged(it)
                                viewModel.onEvent(event)
                            },
                            enabled = state.lockProductElementValue.selected == LockProductElement.TOTAL,
                            label = { Text(text = stringResource(R.string.addEditProduct_label_discount)) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = imeAction
                            ),
                            keyboardActions = keyboardActions
                        )

                        Spacer(modifier = Modifier.size(AddEditProductSpacerLargeSize))

                        AddEditProductDiscountAsPercentButton(
                            modifier = Modifier.weight(0.4f),
                            onClick = { viewModel.onEvent(AddEditProductEvent.ShowProductDiscountAsPercentMenu) },
                            enabled = state.lockProductElementValue.selected == LockProductElement.TOTAL
                        ) {
                            Text(
                                text = state.discountAsPercentValue.text.asCompose(),
                                color = MaterialTheme.colors.onSurface,
                                fontSize = state.fontSize.button.sp
                            )

                            AppDropdownMenu(
                                expanded = state.expandedDiscountAsPercent,
                                onDismissRequest = { viewModel.onEvent(AddEditProductEvent.HideProductDiscountAsPercentMenu) }
                            ) {
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(AddEditProductEvent.ProductDiscountAsPercentSelected) },
                                    text = { Text(text = state.discountAsPercentValue.text.asCompose()) },
                                    right = { CheckmarkAppCheckbox(checked = state.discountAsPercentValue.selected) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(AddEditProductEvent.ProductDiscountAsMoneySelected) },
                                    text = { Text(text = state.discountAsPercentValue.text.asCompose()) },
                                    right = { CheckmarkAppCheckbox(checked = !state.discountAsPercentValue.selected) }
                                )
                            }
                        }
                    }

                    AddEditProductAutocompleteDiscounts(
                        discounts = state.autocompletes.discounts,
                        fontSize = state.fontSize,
                        enabled = state.lockProductElementValue.selected == LockProductElement.TOTAL,
                        onClick = {
                            val event = AddEditProductEvent.AutocompleteDiscountSelected(it)
                            viewModel.onEvent(event)
                        }
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(AddEditProductElementPaddings)
                ) {
                    OutlinedAppTextField(
                        modifier = Modifier.weight(1f),
                        value = state.totalValue,
                        valueFontSize = state.fontSize.textField.sp,
                        onValueChange = {
                            val event = AddEditProductEvent.ProductTotalChanged(it)
                            viewModel.onEvent(event)
                        },
                        enabled = state.lockProductElementValue.selected != LockProductElement.TOTAL,
                        label = { Text(text = stringResource(R.string.addEditProduct_label_total)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = imeAction
                        ),
                        keyboardActions = keyboardActions
                    )

                    Spacer(modifier = Modifier.size(AddEditProductSpacerLargeSize))

                    IconButton(
                        modifier = Modifier
                            .height(AddEditProductButtonHeight)
                            .padding(AddEditProductButtonPaddings)
                            .border(
                                border = getButtonBorderStroke(),
                                shape = MaterialTheme.shapes.small
                            ),
                        onClick = { viewModel.onEvent(AddEditProductEvent.SelectLockProductElement) }
                    ) {
                        Icon(
                            painter = state.lockProductElementValue.selected.toButtonIcon().asPainter() ?: return@IconButton,
                            contentDescription = "",
                            tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                        )

                        AppDropdownMenu(
                            expanded = state.expandedLockProductElement,
                            onDismissRequest = { viewModel.onEvent(AddEditProductEvent.HideLockProductElement) },
                            header = { Text(text = stringResource(R.string.addEditProduct_header_productLock)) }
                        ) {
                            AppDropdownMenuItem(
                                onClick = {
                                    val event = AddEditProductEvent.LockProductElementSelected(
                                        LockProductElement.QUANTITY)
                                    viewModel.onEvent(event)
                                },
                                text = { Text(text = state.lockProductElementValue.text.asCompose()) },
                                right = { CheckmarkAppCheckbox(checked = state.lockProductElementValue.selected == LockProductElement.QUANTITY) }
                            )
                            AppDropdownMenuItem(
                                onClick = {
                                    val event = AddEditProductEvent.LockProductElementSelected(
                                        LockProductElement.PRICE)
                                    viewModel.onEvent(event)
                                },
                                text = { Text(text = state.lockProductElementValue.text.asCompose()) },
                                right = { CheckmarkAppCheckbox(checked = state.lockProductElementValue.selected == LockProductElement.PRICE) }
                            )
                            AppDropdownMenuItem(
                                onClick = {
                                    val event = AddEditProductEvent.LockProductElementSelected(
                                        LockProductElement.TOTAL)
                                    viewModel.onEvent(event)
                                },
                                text = { Text(text = state.lockProductElementValue.text.asCompose()) },
                                right = { CheckmarkAppCheckbox(checked = state.lockProductElementValue.selected == LockProductElement.TOTAL) }
                            )
                        }
                    }
                }

                AddEditProductAutocompleteTotals(
                    totals = state.autocompletes.totals,
                    fontSize = state.fontSize,
                    enabled = state.lockProductElementValue.selected != LockProductElement.TOTAL,
                    onClick = {
                        val event = AddEditProductEvent.AutocompleteTotalSelected(it)
                        viewModel.onEvent(event)
                    }
                )
            }

            OutlinedAppTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AddEditProductElementPaddings),
                value = state.noteValue,
                onValueChange = {
                    val event = AddEditProductEvent.ProductNoteChanged(it)
                    viewModel.onEvent(event)
                },
                valueFontSize = state.fontSize.textField.sp,
                label = { Text(text = stringResource(R.string.addEditProduct_label_note)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { viewModel.onEvent(AddEditProductEvent.SaveProduct) }
                )
            )
        }
    }
}

@Composable
private fun AddEditProductContent(
    scaffoldPaddings: PaddingValues,
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(scaffoldPaddings)
            .padding(AddEditProductContentPaddings)
    ) {
        content()
        Spacer(modifier = Modifier.height(AddEditProductSpacerHeight))
    }
}

@Composable
private fun AddEditProductAutocompleteNames(
    names: List<Autocomplete>,
    fontSize: UiFontSize,
    onClick: (Autocomplete) -> Unit
) {
    names.forEach {
        AppItem(
            modifier = Modifier.padding(AddEditProductItemPaddings),
            body = {
                Text(
                    text = it.name,
                    fontSize = fontSize.itemBody.sp
                )
            },
            onClick = { onClick(it) },
            backgroundColor = MaterialTheme.colors.background
        )
    }
}

@Composable
private fun AddEditProductAutocompleteStrings(
    list: List<String>,
    fontSize: UiFontSize,
    onClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(AddEditProductElementPaddings)
            .horizontalScroll(scrollState)
    ) {
        list.forEach {
            AppChip(onClick = { onClick(it) }) {
                Text(
                    text = it,
                    fontSize = fontSize.itemBody.sp
                )
            }
            Spacer(modifier = Modifier.size(AddEditProductSpacerMediumSize))
        }
    }
}

@Composable
private fun AddEditProductAutocompleteQuantities(
    quantities: List<Quantity>,
    fontSize: UiFontSize,
    enabled: Boolean,
    onClick: (Quantity) -> Unit
) {

    if (!enabled) {
        return
    }

    val scrollState = rememberScrollState()

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(AddEditProductElementPaddings)
            .horizontalScroll(scrollState)
    ) {
        quantities.forEach {
            AppChip(onClick = { onClick(it) }) {
                Text(
                    text = it.toString(),
                    fontSize = fontSize.itemBody.sp
                )
            }
            Spacer(modifier = Modifier.size(AddEditProductSpacerMediumSize))
        }
    }
}

@Composable
private fun AddEditProductAutocompleteSymbols(
    quantities: List<Quantity>,
    minusOneQuantityChip: @Composable () -> Unit,
    plusOneQuantityChip: @Composable () -> Unit,
    defaultQuantitySymbolChips: @Composable () -> Unit,
    defaultQuantitySymbolChipsEnabled: Boolean,
    fontSize: UiFontSize,
    enabled: Boolean,
    onClick: (Quantity) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(AddEditProductElementPaddings)
            .horizontalScroll(scrollState)
    ) {
        if (enabled) {
            minusOneQuantityChip()
            Spacer(modifier = Modifier.size(AddEditProductSpacerMediumSize))
            plusOneQuantityChip()
            Spacer(modifier = Modifier.size(AddEditProductSpacerMediumSize))
        }

        if (defaultQuantitySymbolChipsEnabled) {
            defaultQuantitySymbolChips()
        }

        quantities.forEach {
            AppChip(onClick = { onClick(it) }) {
                Text(
                    text = it.symbol,
                    fontSize = fontSize.itemBody.sp
                )
            }
            Spacer(modifier = Modifier.size(AddEditProductSpacerMediumSize))
        }
    }
}

@Composable
private fun AddEditProductAutocompletePrices(
    prices: List<Money>,
    fontSize: UiFontSize,
    enabled: Boolean,
    onClick: (Money) -> Unit
) {
    if (!enabled) {
        return
    }

    val scrollState = rememberScrollState()

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(AddEditProductElementPaddings)
            .horizontalScroll(scrollState)
    ) {
        prices.forEach {
            AppChip(onClick = { onClick(it) }) {
                Text(
                    text = it.toString(),
                    fontSize = fontSize.itemBody.sp
                )
            }
            Spacer(modifier = Modifier.size(AddEditProductSpacerMediumSize))
        }
    }
}

@Composable
private fun AddEditProductAutocompleteDiscounts(
    discounts: List<Money>,
    fontSize: UiFontSize,
    enabled: Boolean,
    onClick: (Money) -> Unit
) {
    if (!enabled) {
        return
    }

    val scrollState = rememberScrollState()

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(AddEditProductElementPaddings)
            .horizontalScroll(scrollState)
    ) {
        discounts.forEach {
            AppChip(onClick = { onClick(it) }) {
                Text(
                    text = it.toString(),
                    fontSize = fontSize.itemBody.sp
                )
            }
            Spacer(modifier = Modifier.size(AddEditProductSpacerMediumSize))
        }
    }
}

@Composable
private fun AddEditProductAutocompleteTotals(
    totals: List<Money>,
    fontSize: UiFontSize,
    enabled: Boolean,
    onClick: (Money) -> Unit
) {
    if (!enabled) {
        return
    }

    val scrollState = rememberScrollState()

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(AddEditProductElementPaddings)
            .horizontalScroll(scrollState)
    ) {
        totals.forEach {
            AppChip(onClick = { onClick(it) }) {
                Text(
                    text = it.toString(),
                    fontSize = fontSize.itemBody.sp
                )
            }
            Spacer(modifier = Modifier.size(AddEditProductSpacerMediumSize))
        }
    }
}

@Composable
private fun AddEditProductDiscountAsPercentButton(
    modifier: Modifier,
    enabled: Boolean,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedButton(
        modifier = Modifier
            .height(AddEditProductButtonHeight)
            .padding(AddEditProductButtonPaddings)
            .then(modifier),
        onClick = onClick,
        contentPadding = AddEditProductButtonNoContentPadding,
        enabled = enabled,
        border = getButtonBorderStroke(enabled)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            content = content
        )
    }
}

@Composable
private fun getButtonBorderStroke(enabled: Boolean = true): BorderStroke {
    val color = if (enabled) {
        MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.05f)
    }
    return BorderStroke(ButtonDefaults.OutlinedBorderSize, color)
}

private val AddEditProductContentPaddings = PaddingValues(
    horizontal = 8.dp,
    vertical = 4.dp
)

private val AddEditProductItemPaddings = PaddingValues(
    horizontal = 8.dp,
    vertical = 4.dp
)

private val AddEditProductElementPaddings = PaddingValues(vertical = 2.dp)
private val AddEditProductSpacerMediumSize = 4.dp
private val AddEditProductSpacerLargeSize = 8.dp
private val AddEditProductButtonHeight = 64.dp
private val AddEditProductButtonPaddings = PaddingValues(top = 8.dp)
private val AddEditProductButtonNoContentPadding = PaddingValues(0.dp)
private val AddEditProductSpacerHeight = 128.dp
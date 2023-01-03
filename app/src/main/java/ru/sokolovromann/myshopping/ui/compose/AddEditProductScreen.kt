package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.Discount
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.data.repository.model.Quantity
import ru.sokolovromann.myshopping.ui.compose.event.AddEditProductScreenEvent
import ru.sokolovromann.myshopping.ui.utils.toButton
import ru.sokolovromann.myshopping.ui.utils.toItemBody
import ru.sokolovromann.myshopping.ui.utils.toTextField
import ru.sokolovromann.myshopping.ui.viewmodel.AddEditProductViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditProductEvent

@Composable
fun AddEditProductScreen(
    navController: NavController,
    viewModel: AddEditProductViewModel = hiltViewModel()
) {
    val screenData = viewModel.addEditProductState.screenData
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                AddEditProductScreenEvent.ShowBackScreen -> {
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
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(AddEditProductEvent.CancelSavingProduct) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.addEditProduct_contentDescription_navigationIcon),
                            tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                        )
                    }
                },
                actions = {
                    AppTopAppBarButton(onClick = { viewModel.onEvent(AddEditProductEvent.SaveProduct) }) {
                        Text(text = stringResource(R.string.addEditProduct_action_saveProduct).uppercase())
                    }
                }
            )
        },
        backgroundColor = MaterialTheme.colors.surface
    ) { paddings ->
        AddEditProductContent(scaffoldPaddings = paddings) {
            OutlinedAppTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AddEditProductElementPaddings)
                    .focusRequester(focusRequester),
                value = screenData.nameValue,
                valueFontSize = screenData.fontSize.toTextField().sp,
                onValueChange = {
                    val event = AddEditProductEvent.ProductNameChanged(it)
                    viewModel.onEvent(event)
                },
                label = { Text(text = stringResource(R.string.addEditProduct_label_name)) },
                error = { Text(text = stringResource(R.string.addEditProduct_message_nameError)) },
                showError = screenData.showNameError,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                )
            )

            AddEditProductAutocompleteNames(
                names = screenData.autocompleteNames,
                fontSize = screenData.fontSize,
                onClick = {
                    val event = AddEditProductEvent.AutocompleteNameSelected(it)
                    viewModel.onEvent(event)
                }
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(AddEditProductElementPaddings)
            ) {
                OutlinedAppTextField(
                    modifier = Modifier.weight(0.6f),
                    value = screenData.quantityValue,
                    valueFontSize = screenData.fontSize.toTextField().sp,
                    onValueChange = {
                        val event = AddEditProductEvent.ProductQuantityChanged(it)
                        viewModel.onEvent(event)
                    },
                    label = { Text(text = stringResource(R.string.addEditProduct_label_quantity)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                    )
                )

                Spacer(modifier = Modifier.size(AddEditProductSpacerLargeSize))

                OutlinedAppTextField(
                    modifier = Modifier.weight(0.4f),
                    value = screenData.quantitySymbolValue,
                    valueFontSize = screenData.fontSize.toTextField().sp,
                    onValueChange = {
                        val event = AddEditProductEvent.ProductQuantitySymbolChanged(it)
                        viewModel.onEvent(event)
                    },
                    label = { Text(text = stringResource(R.string.addEditProduct_label_quantitySymbol)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                    )
                )
            }

            AddEditProductAutocompleteSymbols(
                quantities = screenData.autocompleteQuantitySymbols,
                minusOneQuantityChip = {
                    AppChip(
                        onClick = { viewModel.onEvent(AddEditProductEvent.AutocompleteMinusOneQuantitySelected) }
                    ) {
                        Text(
                            text = stringResource(R.string.addEditProduct_action_quantityMinusOne),
                            fontSize = screenData.fontSize.toItemBody().sp
                        )
                    }
                },
                plusOneQuantityChip = {
                    AppChip(
                        onClick = { viewModel.onEvent(AddEditProductEvent.AutocompletePlusOneQuantitySelected) }
                    ) {
                        Text(
                            text = stringResource(R.string.addEditProduct_action_quantityPlusOne),
                            fontSize = screenData.fontSize.toItemBody().sp
                        )
                    }
                },
                fontSize = screenData.fontSize,
                onClick = {
                    val event = AddEditProductEvent.AutocompleteQuantitySymbolSelected(it)
                    viewModel.onEvent(event)
                }
            )

            AddEditProductAutocompleteQuantities(
                quantities = screenData.autocompleteQuantities,
                fontSize = screenData.fontSize,
                onClick = {
                    val event = AddEditProductEvent.AutocompleteQuantitySelected(it)
                    viewModel.onEvent(event)
                }
            )

            OutlinedAppTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AddEditProductElementPaddings),
                value = screenData.priceValue,
                valueFontSize = screenData.fontSize.toTextField().sp,
                onValueChange = {
                    val event = AddEditProductEvent.ProductPriceChanged(it)
                    viewModel.onEvent(event)
                },
                label = { Text(text = stringResource(R.string.addEditProduct_label_price)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                )
            )

            AddEditProductAutocompletePrices(
                prices = screenData.autocompletePrices,
                fontSize = screenData.fontSize,
                onClick = {
                    val event = AddEditProductEvent.AutocompletePriceSelected(it)
                    viewModel.onEvent(event)
                }
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(AddEditProductElementPaddings)
            ) {
                OutlinedAppTextField(
                    modifier = Modifier.weight(0.6f),
                    value = screenData.discountValue,
                    valueFontSize = screenData.fontSize.toTextField().sp,
                    onValueChange = {
                        val event = AddEditProductEvent.ProductDiscountChanged(it)
                        viewModel.onEvent(event)
                    },
                    label = { Text(text = stringResource(R.string.addEditProduct_label_discount)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { viewModel.onEvent(AddEditProductEvent.SaveProduct) }
                    )
                )

                Spacer(modifier = Modifier.size(AddEditProductSpacerLargeSize))

                AddEditProductDiscountAsPercentButton(
                    modifier = Modifier.weight(0.4f),
                    onClick = { viewModel.onEvent(AddEditProductEvent.ShowProductDiscountAsPercentMenu) }
                ) {
                    Text(
                        text = screenData.discountAsPercentText.asCompose(),
                        color = MaterialTheme.colors.onSurface,
                        fontSize = screenData.fontSize.toButton().sp
                    )

                    AppDropdownMenu(
                        expanded = screenData.showDiscountAsPercent,
                        onDismissRequest = { viewModel.onEvent(AddEditProductEvent.HideProductDiscountAsPercentMenu) }
                    ) {
                        AppDropdownMenuItem(
                            onClick = { viewModel.onEvent(AddEditProductEvent.ProductDiscountAsPercentSelected) },
                            text = { Text(text = stringResource(R.string.addEditProduct_action_selectDiscountAsPercents)) },
                            after = { CheckmarkAppCheckbox(checked = screenData.discountAsPercent) }
                        )
                        AppDropdownMenuItem(
                            onClick = { viewModel.onEvent(AddEditProductEvent.ProductDiscountAsMoneySelected) },
                            text = { Text(text = stringResource(R.string.addEditProduct_action_selectDiscountAsMoney)) },
                            after = { CheckmarkAppCheckbox(checked = !screenData.discountAsPercent) }
                        )
                    }
                }
            }

            AddEditProductAutocompleteDiscounts(
                discounts = screenData.autocompleteDiscounts,
                fontSize = screenData.fontSize,
                onClick = {
                    val event = AddEditProductEvent.AutocompleteDiscountSelected(it)
                    viewModel.onEvent(event)
                }
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
            .padding(AddEditProductContentPaddings),
        content = content
    )
}

@Composable
private fun AddEditProductAutocompleteNames(
    names: List<String>,
    fontSize: FontSize,
    onClick: (String) -> Unit
) {
    names.forEach {
        AppItem(
            modifier = Modifier.padding(AddEditProductItemPaddings),
            body = {
                Text(
                    text = it,
                    fontSize = fontSize.toItemBody().sp
                )
            },
            onClick = { onClick(it) },
            backgroundColor = MaterialTheme.colors.background
        )
    }
}

@Composable
private fun AddEditProductAutocompleteQuantities(
    quantities: List<Quantity>,
    fontSize: FontSize,
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
        quantities.forEach {
            AppChip(onClick = { onClick(it) }) {
                Text(
                    text = it.toString(),
                    fontSize = fontSize.toItemBody().sp
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
    fontSize: FontSize,
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
        minusOneQuantityChip()
        Spacer(modifier = Modifier.size(AddEditProductSpacerMediumSize))
        plusOneQuantityChip()

        Spacer(modifier = Modifier.size(AddEditProductSpacerMediumSize))

        quantities.forEach {
            AppChip(onClick = { onClick(it) }) {
                Text(
                    text = it.symbol,
                    fontSize = fontSize.toItemBody().sp
                )
            }
            Spacer(modifier = Modifier.size(AddEditProductSpacerMediumSize))
        }
    }
}

@Composable
private fun AddEditProductAutocompletePrices(
    prices: List<Money>,
    fontSize: FontSize,
    onClick: (Money) -> Unit
) {
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
                    fontSize = fontSize.toItemBody().sp
                )
            }
            Spacer(modifier = Modifier.size(AddEditProductSpacerMediumSize))
        }
    }
}

@Composable
private fun AddEditProductAutocompleteDiscounts(
    discounts: List<Discount>,
    fontSize: FontSize,
    onClick: (Discount) -> Unit
) {
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
                    fontSize = fontSize.toItemBody().sp
                )
            }
            Spacer(modifier = Modifier.size(AddEditProductSpacerMediumSize))
        }
    }
}

@Composable
private fun AddEditProductDiscountAsPercentButton(
    modifier: Modifier,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedButton(
        modifier = Modifier
            .height(64.dp)
            .padding(top = 8.dp)
            .then(modifier),
        onClick = onClick,
        contentPadding = PaddingValues(0.dp)
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
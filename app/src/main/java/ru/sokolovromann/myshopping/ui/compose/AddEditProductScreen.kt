package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.focus.FocusManager
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
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.AddEditProductScreenEvent
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.utils.toItemBody
import ru.sokolovromann.myshopping.ui.utils.toTextField
import ru.sokolovromann.myshopping.ui.viewmodel.AddEditProductViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditProductEvent

@Composable
fun AddEditProductScreen(
    navController: NavController,
    viewModel: AddEditProductViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val systemUiController = rememberSystemUiController()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                AddEditProductScreenEvent.ShowBackScreen -> navController.popBackStack()
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

    BackHandler { viewModel.onEvent(AddEditProductEvent.CancelSavingProduct) }

    AppSystemUi(systemUiController = systemUiController)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(viewModel) },
        content = { paddingValues -> Content(paddingValues, viewModel, focusRequester, focusManager) }
    )
}

@Composable
private fun TopBar(viewModel: AddEditProductViewModel) {
    TopAppBar(
        title = { Text(text = viewModel.topBarState.value.title.text.asCompose()) },
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
                Text(text = viewModel.saveState.value.text.asCompose().uppercase())
            }
        }
    )
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    viewModel: AddEditProductViewModel,
    focusRequester: FocusRequester,
    focusManager: FocusManager
) {
    val screenData = viewModel.addEditProductState.screenData
    val columnScrollState = rememberScrollState()

    val autocompleteQuantitiesScrollState = rememberScrollState()
    val autocompleteQuantitySymbolsScrollState = rememberScrollState()
    val autocompletePricesScrollState = rememberScrollState()
    val autocompleteDiscountsScrollState = rememberScrollState()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = AppColor.Surface.asCompose())
        .padding(paddingValues)
        .padding(vertical = 4.dp, horizontal = 8.dp)
        .verticalScroll(columnScrollState)
    ) {
        OutlinedAppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
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

        AutocompleteNamesShowing(viewModel)

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 2.dp)
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

            Spacer(modifier = Modifier.width(8.dp))

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

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 2.dp)
                .horizontalScroll(autocompleteQuantitySymbolsScrollState)
        ) {
            AutocompleteQuantityChip(
                text = viewModel.quantityMinusOneState.value.text.asCompose(),
                onClick = {
                    viewModel.onEvent(AddEditProductEvent.AutocompleteMinusOneQuantitySelected)
                }
            )

            Spacer(modifier = Modifier.width(4.dp))

            AutocompleteQuantityChip(
                text = viewModel.quantityPlusOneState.value.text.asCompose(),
                onClick = {
                    viewModel.onEvent(AddEditProductEvent.AutocompletePlusOneQuantitySelected)
                }
            )

            Spacer(modifier = Modifier.width(4.dp))

            AutocompleteQuantitySymbolsShowing(viewModel)
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 2.dp)
                .horizontalScroll(autocompleteQuantitiesScrollState)
        ) {
            AutocompleteQuantitiesShowing(viewModel)
        }

        OutlinedAppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
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

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 2.dp)
                .horizontalScroll(autocompletePricesScrollState)
        ) {
            AutocompletePricesShowing(viewModel)
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 2.dp)
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

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                modifier = Modifier
                    .height(64.dp)
                    .padding(top = 8.dp)
                    .weight(0.4f),
                onClick = {
                    viewModel.onEvent(AddEditProductEvent.ShowProductDiscountAsPercentMenu)
                },
                contentPadding = PaddingValues(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = screenData.discountAsPercentText.asCompose(),
                        color = MaterialTheme.colors.onSurface
                    )
                }

                DiscountAsPercentMenu(viewModel)
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 2.dp)
                .horizontalScroll(autocompleteDiscountsScrollState)
        ) {
            AutocompleteDiscountsShowing(viewModel)
        }
    }
}

@Composable
private fun DiscountAsPercentMenu(viewModel: AddEditProductViewModel) {
    val screenData = viewModel.addEditProductState.screenData

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

@Composable
private fun AutocompleteQuantityChip(
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .defaultMinSize(32.dp)
            .background(color = MaterialTheme.colors.background, shape = CircleShape)
            .clickable { onClick() }
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = text,
            color = MaterialTheme.colors.onBackground
        )
    }
}

@Composable
private fun AutocompleteNamesShowing(viewModel: AddEditProductViewModel) {
    val screenData = viewModel.addEditProductState.screenData
    AppGrid {
        screenData.autocompleteNames.forEach { item ->
            AppItem(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                body = {
                    Text(
                        text = item,
                        fontSize = screenData.fontSize.toItemBody().sp
                    )
                },
                onClick = {
                    val event = AddEditProductEvent.AutocompleteNameSelected(item)
                    viewModel.onEvent(event)
                },
                backgroundColor = MaterialTheme.colors.background
            )
        }
    }
}

@Composable
private fun AutocompleteQuantitiesShowing(viewModel: AddEditProductViewModel) {
    val screenData = viewModel.addEditProductState.screenData
    screenData.autocompleteQuantities.forEach { item ->
        AutocompleteQuantityChip(
            text = item.toString(),
            onClick = {
                val event = AddEditProductEvent.AutocompleteQuantitySelected(item)
                viewModel.onEvent(event)
            }
        )

        Spacer(modifier = Modifier.width(4.dp))
    }
}

@Composable
private fun AutocompleteQuantitySymbolsShowing(viewModel: AddEditProductViewModel) {
    val screenData = viewModel.addEditProductState.screenData
    screenData.autocompleteQuantitySymbols.forEach { item ->
        AutocompleteQuantityChip(
            text = item.symbol,
            onClick = {
                val event = AddEditProductEvent.AutocompleteQuantitySymbolSelected(item)
                viewModel.onEvent(event)
            }
        )

        Spacer(modifier = Modifier.width(4.dp))
    }
}

@Composable
private fun AutocompletePricesShowing(viewModel: AddEditProductViewModel) {
    val screenData = viewModel.addEditProductState.screenData
    screenData.autocompletePrices.forEach { item ->
        AutocompleteQuantityChip(
            text = item.toString(),
            onClick = {
                val event = AddEditProductEvent.AutocompletePriceSelected(item)
                viewModel.onEvent(event)
            }
        )

        Spacer(modifier = Modifier.width(4.dp))
    }
}

@Composable
private fun AutocompleteDiscountsShowing(viewModel: AddEditProductViewModel) {
    val screenData = viewModel.addEditProductState.screenData
    screenData.autocompleteDiscounts.forEach { item ->
        AutocompleteQuantityChip(
            text = item.toString(),
            onClick = {
                val event = AddEditProductEvent.AutocompleteDiscountSelected(item)
                viewModel.onEvent(event)
            }
        )

        Spacer(modifier = Modifier.width(4.dp))
    }
}
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.AddEditProductScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.theme.AppColor
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
            TextButton(onClick = { viewModel.onEvent(AddEditProductEvent.SaveProduct) }) {
                AppText(data = viewModel.saveState.value)
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
        val nameField = viewModel.nameState.currentData
        OutlinedAppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .focusRequester(focusRequester),
            value = nameField.text,
            valueFontSize = nameField.textFontSize,
            onValueChange = {
                val event = AddEditProductEvent.ProductNameChanged(it)
                viewModel.onEvent(event)
            },
            label = { Text(text = nameField.label.text.asCompose()) },
            error = { Text(text = (nameField.error?.text ?: UiText.Nothing).asCompose()) },
            showError = nameField.error?.text != null,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            )
        )

        val namesData = viewModel.autocompleteNamesState.currentData
        when (namesData.result) {
            ListResult.Showing -> AutocompleteNamesShowing(namesData, viewModel)
            else -> {}
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 2.dp)
        ) {
            val quantityField = viewModel.quantityState.currentData
            OutlinedAppTextField(
                modifier = Modifier.weight(0.6f),
                value = quantityField.text,
                valueFontSize = quantityField.textFontSize,
                onValueChange = {
                    val event = AddEditProductEvent.ProductQuantityChanged(it)
                    viewModel.onEvent(event)
                },
                label = { Text(text = quantityField.label.text.asCompose()) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            val quantitySymbolField = viewModel.quantitySymbolState.currentData
            OutlinedAppTextField(
                modifier = Modifier.weight(0.4f),
                value = quantitySymbolField.text,
                valueFontSize = quantitySymbolField.textFontSize,
                onValueChange = {
                    val event = AddEditProductEvent.ProductQuantitySymbolChanged(it)
                    viewModel.onEvent(event)
                },
                label = { Text(text = quantitySymbolField.label.text.asCompose()) },
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
                text = viewModel.quantityMinusOneState.value,
                onClick = {
                    viewModel.onEvent(AddEditProductEvent.AutocompleteMinusOneQuantitySelected)
                }
            )

            Spacer(modifier = Modifier.width(4.dp))

            AutocompleteQuantityChip(
                text = viewModel.quantityPlusOneState.value,
                onClick = {
                    viewModel.onEvent(AddEditProductEvent.AutocompletePlusOneQuantitySelected)
                }
            )

            Spacer(modifier = Modifier.width(4.dp))

            val symbolsData = viewModel.autocompleteQuantitySymbolsState.currentData
            when (symbolsData.result) {
                ListResult.Showing -> AutocompleteQuantitiesShowing(symbolsData, viewModel)
                else -> {}
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 2.dp)
                .horizontalScroll(autocompleteQuantitiesScrollState)
        ) {
            val quantitiesData = viewModel.autocompleteQuantitiesState.currentData
            when (quantitiesData.result) {
                ListResult.Showing -> AutocompleteQuantitiesShowing(quantitiesData, viewModel)
                else -> {}
            }
        }

        val priceField = viewModel.priceState.currentData
        OutlinedAppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            value = priceField.text,
            valueFontSize = priceField.textFontSize,
            onValueChange = {
                val event = AddEditProductEvent.ProductPriceChanged(it)
                viewModel.onEvent(event)
            },
            label = { Text(text = priceField.label.text.asCompose()) },
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
            val pricesData = viewModel.autocompletePricesState.currentData
            when (pricesData.result) {
                ListResult.Showing -> AutocompletePricesShowing(pricesData, viewModel)
                else -> {}
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 2.dp)
        ) {
            val discountField = viewModel.discountState.currentData
            OutlinedAppTextField(
                modifier = Modifier.weight(0.6f),
                value = discountField.text,
                valueFontSize = discountField.textFontSize,
                onValueChange = {
                    val event = AddEditProductEvent.ProductDiscountChanged(it)
                    viewModel.onEvent(event)
                },
                label = { Text(text = discountField.label.text.asCompose()) },
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
                    horizontalAlignment = Alignment.Start
                ) {
                    AppText(data = viewModel.discountAsPercentState.currentData.text)
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
            val discountsData = viewModel.autocompleteDiscountsState.currentData
            when (discountsData.result) {
                ListResult.Showing -> AutocompleteDiscountsShowing(discountsData, viewModel)
                else -> {}
            }
        }
    }
}

@Composable
private fun DiscountAsPercentMenu(viewModel: AddEditProductViewModel) {
    val menuData = viewModel.discountAsPercentState.currentData
    val menu = menuData.menu ?: return

    AppDropdownMenu(
        expanded = menuData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(AddEditProductEvent.HideProductDiscountAsPercentMenu) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(AddEditProductEvent.ProductDiscountAsPercentSelected) },
            text = { Text(text = menu.asPercentBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.asPercentSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(AddEditProductEvent.ProductDiscountAsMoneySelected) },
            text = { Text(text = menu.asMoneyBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.asMoneySelected.selected) }
        )
    }
}

@Composable
private fun AutocompleteQuantityChip(
    text: TextData,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .defaultMinSize(32.dp)
            .background(color = AppColor.Background.asCompose(), shape = CircleShape)
            .clickable { onClick() }
    ) {
        AppText(
            modifier = Modifier.padding(8.dp),
            data = text
        )
    }
}

@Composable
private fun AutocompleteNamesShowing(
    data: ListData<TextData>,
    viewModel: AddEditProductViewModel
) {
    AppGrid(
        modifier = Modifier.background(color = AppColor.Background.asCompose()),
        data = data
    ) {
        data.items.forEach { item ->
            val text = item.text.asCompose()

            AppItem(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                body = { Text(text = item.text.asCompose()) },
                onClick = {
                    val event = AddEditProductEvent.AutocompleteNameSelected(text)
                    viewModel.onEvent(event)
                },
                backgroundColor = MaterialTheme.colors.background
            )
        }
    }
}

@Composable
private fun AutocompleteQuantitiesShowing(
    data: ListData<QuantityItem>,
    viewModel: AddEditProductViewModel
) {
    data.items.forEach { item ->
        AutocompleteQuantityChip(
            text = item.text,
            onClick = {
                val event = AddEditProductEvent.AutocompleteQuantitySelected(item.quantity)
                viewModel.onEvent(event)
            }
        )

        Spacer(modifier = Modifier.width(4.dp))
    }
}

@Composable
private fun AutocompletePricesShowing(
    data: ListData<MoneyItem>,
    viewModel: AddEditProductViewModel
) {
    data.items.forEach { item ->
        AutocompleteQuantityChip(
            text = item.text,
            onClick = {
                val event = AddEditProductEvent.AutocompletePriceSelected(item.money)
                viewModel.onEvent(event)
            }
        )

        Spacer(modifier = Modifier.width(4.dp))
    }
}

@Composable
private fun AutocompleteDiscountsShowing(
    data: ListData<DiscountItem>,
    viewModel: AddEditProductViewModel
) {
    data.items.forEach { item ->
        AutocompleteQuantityChip(
            text = item.text,
            onClick = {
                val event = AddEditProductEvent.AutocompleteDiscountSelected(item.discount)
                viewModel.onEvent(event)
            }
        )

        Spacer(modifier = Modifier.width(4.dp))
    }
}
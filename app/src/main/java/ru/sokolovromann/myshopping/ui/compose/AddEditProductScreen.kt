package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
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
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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

    AppSystemUi(
        systemUiController = systemUiController,
        data = viewModel.systemUiState.value
    )

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(viewModel) },
        content = { paddingValues -> Content(paddingValues, viewModel, focusRequester) }
    )
}

@Composable
private fun TopBar(viewModel: AddEditProductViewModel) {
    AppTopBar(
        data = viewModel.topBarState.value,
        onNavigationIconClick = { viewModel.onEvent(AddEditProductEvent.CancelSavingProduct) },
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
    focusRequester: FocusRequester
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
        OutlinedAppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .focusRequester(focusRequester),
            state = viewModel.nameState,
            onValueChange = {
                val event = AddEditProductEvent.ProductNameChanged(it)
                viewModel.onEvent(event)
            }
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
            OutlinedAppTextField(
                modifier = Modifier.weight(0.6f),
                state = viewModel.quantityState,
                onValueChange = {
                    val event = AddEditProductEvent.ProductQuantityChanged(it)
                    viewModel.onEvent(event)
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedAppTextField(
                modifier = Modifier.weight(0.4f),
                state = viewModel.quantitySymbolState,
                onValueChange = {
                    val event = AddEditProductEvent.ProductQuantitySymbolChanged(it)
                    viewModel.onEvent(event)
                }
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

        OutlinedAppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            state = viewModel.priceState,
            onValueChange = {
                val event = AddEditProductEvent.ProductPriceChanged(it)
                viewModel.onEvent(event)
            }
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
            OutlinedAppTextField(
                modifier = Modifier.weight(0.6f),
                state = viewModel.discountState,
                onValueChange = {
                    val event = AddEditProductEvent.ProductDiscountChanged(it)
                    viewModel.onEvent(event)
                }
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

    DropdownMenu(
        expanded = menuData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(AddEditProductEvent.HideProductDiscountAsPercentMenu) }
    ) {
        AppMenuItem(
            before = { AppRadioButton(data = menu.asPercentSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.asPercentBody
                )
            },
            onClick = { viewModel.onEvent(AddEditProductEvent.ProductDiscountAsPercentSelected) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.asMoneySelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.asMoneyBody
                )
            },
            onClick = { viewModel.onEvent(AddEditProductEvent.ProductDiscountAsMoneySelected) },
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
                modifier = Modifier.padding(8.dp),
                title = { AppText(data = item) },
                onClick = {
                    val event = AddEditProductEvent.AutocompleteNameSelected(text)
                    viewModel.onEvent(event)
                }
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
package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.ui.compose.event.MoveProductScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.ListData
import ru.sokolovromann.myshopping.ui.compose.state.ListResult
import ru.sokolovromann.myshopping.ui.compose.state.ShoppingListItem
import ru.sokolovromann.myshopping.ui.compose.state.TextData
import ru.sokolovromann.myshopping.ui.viewmodel.MoveProductViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.MoveProductEvent

@Composable
fun MoveProductScreen(
    navController: NavController,
    viewModel: MoveProductViewModel = hiltViewModel()
) {

    val scaffoldState = rememberScaffoldState()
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                MoveProductScreenEvent.ShowBackScreen -> navController.popBackStack()
            }
        }
    }

    BackHandler {
        viewModel.onEvent(MoveProductEvent.ShowBackScreen)
    }

    AppSystemUi(systemUiController = systemUiController)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(viewModel) },
        content = { paddingValues -> Content(paddingValues, viewModel) }
    )
}

@Composable
private fun TopBar(viewModel: MoveProductViewModel) {
    TopAppBar(
        title = { Text(text = viewModel.topBarState.value.title.text.asCompose()) },
        navigationIcon = {
            IconButton(onClick = { viewModel.onEvent(MoveProductEvent.ShowBackScreen) }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.moveProduct_contentDescription_navigationIcon),
                    tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                )
            }
        }
    )
}

@Composable
private fun Content(paddingValues: PaddingValues, viewModel: MoveProductViewModel) {
    Box(modifier = Modifier.padding(paddingValues)) {
        val listData = viewModel.shoppingListsState.currentData
        when (listData.result) {
            ListResult.Showing -> PurchasesShowing(listData, viewModel)
            ListResult.NotFound -> PurchasesNotFound(listData.notFoundText, viewModel)
            ListResult.Loading -> PurchasesLoading()
            ListResult.Nothing -> {}
        }
    }
}

@Composable
private fun PurchasesShowing(data: ListData<ShoppingListItem>, viewModel: MoveProductViewModel) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 4.dp)
        .verticalScroll(scrollState)
    ) {
        ShoppingListsBar(viewModel)
        AppGrid(multiColumns = data.multiColumns) {
            data.items.forEach { item -> PurchasesItem(item, viewModel)}
        }
        Spacer(modifier = Modifier.height(128.dp))
    }
}

@Composable
private fun PurchasesNotFound(data: TextData, viewModel: MoveProductViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp),
            content = { ShoppingListsBar(viewModel) }
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = data.text.asCompose(),
                fontSize = data.fontSize,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}

@Composable
private fun PurchasesLoading() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        content = { CircularProgressIndicator() }
    )
}

@Composable
private fun ShoppingListsBar(viewModel: MoveProductViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        TextButton(onClick = { viewModel.onEvent(MoveProductEvent.SelectShoppingListsLocation) }) {
            val locationButtonState = viewModel.locationButtonState.currentData.text
            Text(
                text = locationButtonState.text.asCompose(),
                fontSize = locationButtonState.fontSize
            )
            LocationMenu(viewModel)
        }
    }
}

@Composable
private fun LocationMenu(viewModel: MoveProductViewModel) {
    val locationData = viewModel.locationButtonState.currentData
    val menu = locationData.menu ?: return

    AppDropdownMenu(
        expanded = locationData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(MoveProductEvent.HideShoppingListsLocation) },
        header = { Text(text = menu.title.text.asCompose()) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(MoveProductEvent.DisplayShoppingListsPurchases) },
            text = { Text(text = menu.purchasesBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.purchasesSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(MoveProductEvent.DisplayShoppingListsArchive) },
            text = { Text(text = menu.archiveBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.archiveSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(MoveProductEvent.DisplayShoppingListsTrash) },
            text = { Text(text = menu.trashBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.trashSelected.selected) }
        )
    }
}

@Composable
private fun PurchasesItem(item: ShoppingListItem, viewModel: MoveProductViewModel) {
    ShoppingListSurfaceItem(
        shoppingListItem = item,
        fontSize = FontSize.MEDIUM,
        onClick = {
            val event = MoveProductEvent.MoveProduct(item.uid)
            viewModel.onEvent(event)
        }
    )
}
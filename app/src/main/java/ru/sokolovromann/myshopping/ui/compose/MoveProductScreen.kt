package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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

    AppSystemUi(
        systemUiController = systemUiController,
        data = viewModel.systemUiState.value
    )

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(viewModel) },
        content = { paddingValues -> Content(paddingValues, viewModel) }
    )
}

@Composable
private fun TopBar(viewModel: MoveProductViewModel) {
    AppTopBar(
        data = viewModel.topBarState.value,
        onNavigationIconClick = { viewModel.onEvent(MoveProductEvent.ShowBackScreen) }
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
        AppGrid(data = data) {
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
                .padding(horizontal = 16.dp),
            content = { AppText(data = data) }
        )
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
        TextButton(
            onClick = { viewModel.onEvent(MoveProductEvent.SelectShoppingListsLocation) }
        ) {
            AppText(data = viewModel.locationButtonState.currentData.text)
            LocationMenu(viewModel)
        }
    }
}

@Composable
private fun LocationMenu(viewModel: MoveProductViewModel) {
    val locationData = viewModel.locationButtonState.currentData
    val menu = locationData.menu ?: return

    DropdownMenu(
        expanded = locationData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(MoveProductEvent.HideShoppingListsLocation) }
    ) {
        AppText(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            data = menu.title
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.purchasesSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.purchasesBody
                )
            },
            onClick = { viewModel.onEvent(MoveProductEvent.DisplayShoppingListsPurchases) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.archiveSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.archiveBody
                )
            },
            onClick = { viewModel.onEvent(MoveProductEvent.DisplayShoppingListsArchive) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.trashSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.trashBody
                )
            },
            onClick = { viewModel.onEvent(MoveProductEvent.DisplayShoppingListsTrash) },
        )
    }
}

@Composable
private fun PurchasesItem(item: ShoppingListItem, viewModel: MoveProductViewModel) {
    AppSurfaceItem(
        title = itemTitleOrNull(item),
        body = itemBodyOrNull(item),
        onClick = {
            val event = MoveProductEvent.MoveProduct(item.uid)
            viewModel.onEvent(event)
        }
    )
}

@Composable
private fun itemTitleOrNull(item: ShoppingListItem): @Composable (() -> Unit)? {
    if (item.title.isTextHiding()) {
        return null
    }

    return {
        AppText(data = item.title)
        Spacer(modifier = Modifier.padding(4.dp))
    }
}

@Composable
private fun itemBodyOrNull(item: ShoppingListItem): @Composable (() -> Unit)? {
    return {
        Column {
            item.productsBody.forEach {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    AppIcon(data = it.first)
                    Spacer(modifier = Modifier.size(4.dp))
                    AppText(data = it.second)
                }
            }

            if (item.totalBody.isTextShowing()) {
                AppText(
                    modifier = Modifier.padding(top = 8.dp),
                    data = item.totalBody
                )
            }

            if (item.reminderBody.isTextShowing()) {
                AppText(
                    modifier = Modifier.padding(top = 8.dp),
                    data = item.reminderBody
                )
            }
        }
    }
}
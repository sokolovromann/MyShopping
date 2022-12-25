package ru.sokolovromann.myshopping.ui.compose

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.chooseNavigate
import ru.sokolovromann.myshopping.ui.compose.event.ProductsScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.ProductsViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.ProductsEvent

@Composable
fun ProductsScreen(
    navController: NavController,
    viewModel: ProductsViewModel = hiltViewModel()
) {

    val scaffoldState = rememberScaffoldState()
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                is ProductsScreenEvent.AddProduct -> navController.navigate(
                    route = UiRoute.Products.addProductScreen(it.uid)
                )

                is ProductsScreenEvent.EditProduct -> navController.navigate(
                    route = UiRoute.Products.editProductScreen(it.uid)
                )

                is ProductsScreenEvent.EditShoppingListName -> navController.navigate(
                    route = UiRoute.Products.editShoppingListNameScreen(it.uid)
                )

                is ProductsScreenEvent.EditShoppingListReminder -> navController.navigate(
                    route = UiRoute.Products.editShoppingListReminderScreen(it.uid)
                )

                is ProductsScreenEvent.CopyProductToShoppingList -> navController.navigate(
                    route = UiRoute.Products.copyProductToShoppingList(it.uid)
                )

                is ProductsScreenEvent.MoveProductToShoppingList -> navController.navigate(
                    route = UiRoute.Products.moveProductToShoppingList(it.uid)
                )

                ProductsScreenEvent.ShowBackScreen -> navController.popBackStack()

                is ProductsScreenEvent.CalculateChange -> navController.navigate(
                    route = UiRoute.Products.calculateChange(it.uid)
                )

                is ProductsScreenEvent.ShareProducts -> navController.chooseNavigate(
                    intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, it.products)
                        type = "text/plain"
                    }
                )
            }
        }
    }

    BackHandler { viewModel.onEvent(ProductsEvent.ShowBackScreen) }

    AppSystemUi(systemUiController = systemUiController)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(viewModel) },
        bottomBar = { BottomBar(viewModel) },
        content = { paddingValues -> Content(paddingValues, viewModel) }
    )
}

@Composable
private fun TopBar(viewModel: ProductsViewModel) {
    TopAppBar(
        title = { Text(text = viewModel.topBarState.value.title.text.asCompose()) },
        navigationIcon = {
            IconButton(onClick = { viewModel.onEvent(ProductsEvent.ShowBackScreen) }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.products_contentDescription_navigationIcon),
                    tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                )
            }
        }
    )
}

@Composable
private fun BottomBar(viewModel: ProductsViewModel) {
    AppBottomAppBar(
        content = {
            TextButton(
                onClick = { viewModel.onEvent(ProductsEvent.SelectProductsDisplayTotal)}
            ) {
                val total = viewModel.totalState.currentData.text
                Text(
                    text = total.text.asCompose(),
                    fontSize = total.fontSize
                )
                TotalMenu(viewModel)
            }
        },
        actionButtons = {
            IconButton(onClick = { viewModel.onEvent(ProductsEvent.AddProduct) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.products_contentDescription_addProductIcon),
                    tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                )
            }
            IconButton(onClick = { viewModel.onEvent(ProductsEvent.ShowProductsMenu) }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.products_contentDescription_productsMenuIcon),
                    tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                )
                ProductsMenu(viewModel)
            }
        }
    )
}

@Composable
private fun Content(paddingValues: PaddingValues, viewModel: ProductsViewModel) {
    Box(modifier = Modifier.padding(paddingValues)) {
        val listData = viewModel.productsState.currentData
        when (listData.result) {
            ListResult.Showing -> ProductsShowing(listData, viewModel)
            ListResult.NotFound -> ProductsNotFound(listData.notFoundText)
            ListResult.Loading -> ProductsLoading()
            ListResult.Nothing -> {}
        }
    }
}

@Composable
private fun ProductsShowing(data: ListData<ProductItem>, viewModel: ProductsViewModel) {
    val scrollState = rememberScrollState()
    val horizontalPadding = if (data.multiColumns) 4.dp else 0.dp

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = horizontalPadding)
        .verticalScroll(scrollState)
    ) {
        ProductsBar(viewModel)
        ProductsReminder(data.multiColumns, viewModel)
        AppGrid(multiColumns = data.multiColumns) {
            data.items.forEach { item -> ProductItem(item, data.multiColumns, viewModel) }
        }
        Spacer(modifier = Modifier.height(128.dp))
    }
}

@Composable
private fun ProductsReminder(multiColumns: Boolean, viewModel: ProductsViewModel) {
    val data = viewModel.reminderState.value
    if (data.body.isTextHiding()) {
        return
    }

    AppMultiColumnsItem(
        modifier = Modifier.padding(horizontal = 8.dp),
        multiColumns = multiColumns,
        title = reminderTitleOrNull(data),
        body = reminderBodyOrNull(data),
        onClick = {}
    )
}

@Composable
private fun ProductsBar(viewModel: ProductsViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(onClick = { viewModel.onEvent(ProductsEvent.SelectProductsSort) }) {
                val sortState = viewModel.sortState.currentData.text
                Text(
                    text = sortState.text.asCompose(),
                    fontSize = sortState.fontSize
                )
                SortMenu(viewModel)
            }
            IconButton(onClick = { viewModel.onEvent(ProductsEvent.InvertProductsSort) }) {
                Icon(
                    painter = viewModel.sortAscendingState.value.icon.asPainter() ?: return@IconButton,
                    contentDescription = stringResource(R.string.products_contentDescription_sortAscendingIcon),
                    tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                )
            }
        }
        IconButton(onClick = { viewModel.onEvent(ProductsEvent.SelectProductsDisplayCompleted) }) {
            Icon(
                painter = viewModel.completedState.currentData.icon.icon.asPainter() ?: return@IconButton,
                contentDescription = stringResource(R.string.products_contentDescription_displayCompletedIcon),
                tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
            )
            CompletedMenu(viewModel)
        }
    }
}

@Composable
private fun ProductsNotFound(data: TextData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
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

@Composable
private fun ProductsLoading() {
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
private fun ProductItem(item: ProductItem, multiColumns: Boolean, viewModel: ProductsViewModel) {
    AppMultiColumnsItem(
        multiColumns = multiColumns,
        before = itemBefore(item),
        title = itemTitleOrNull(item),
        body = itemBodyOrNull(item),
        dropdownMenu = { ItemMenu(item.uid, viewModel) },
        onClick = {
            val event = if (item.completed.checked) {
                ProductsEvent.ActiveProduct(item.uid)
            } else {
                ProductsEvent.CompleteProduct(item.uid)
            }
            viewModel.onEvent(event)
        },
        onLongClick = {
            val event = ProductsEvent.ShowProductMenu(item.uid)
            viewModel.onEvent(event)
        }
    )
}

@Composable
private fun TotalMenu(viewModel: ProductsViewModel) {
    val totalData = viewModel.totalState.currentData
    val menu = totalData.menu ?: return

    AppDropdownMenu(
        expanded = totalData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductsDisplayTotal) },
        header = { Text(text = menu.title.text.asCompose()) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.DisplayProductsAllTotal) },
            text = { Text(text = menu.allBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.allSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.DisplayProductsCompletedTotal) },
            text = { Text(text = menu.completedBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.completedSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.DisplayProductsActiveTotal) },
            text = { Text(text = menu.activeBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.activeSelected.selected) }
        )
    }
}

@Composable
private fun SortMenu(viewModel: ProductsViewModel) {
    val sortData = viewModel.sortState.currentData
    val menu = sortData.menu ?: return

    AppDropdownMenu(
        expanded = sortData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductsSort) },
        header = { Text(text = menu.title.text.asCompose()) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.SortProductsByCreated) },
            text = { Text(text = menu.byCreatedBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byCreatedSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.SortProductsByLastModified) },
            text = { Text(text = menu.byLastModifiedBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byLastModifiedSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.SortProductsByName) },
            text = { Text(text = menu.byNameBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byNameSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.SortProductsByTotal) },
            text = { Text(text = menu.byTotalBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byTotalSelected.selected) }
        )
    }
}

@Composable
private fun CompletedMenu(viewModel: ProductsViewModel) {
    val completedData = viewModel.completedState.currentData
    val menu = completedData.menu ?: return

    AppDropdownMenu(
        expanded = completedData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductsDisplayCompleted) },
        header = { Text(text = menu.title.text.asCompose()) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.DisplayProductsCompletedFirst) },
            text = { Text(text = menu.firstBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.firstSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.DisplayProductsCompletedLast) },
            text = { Text(text = menu.lastBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.lastSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.HideProductsCompleted) },
            text = { Text(text = menu.hideBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.hideSelected.selected) }
        )
    }
}

@Composable
private fun ProductsMenu(viewModel: ProductsViewModel) {
    val menuData = viewModel.productsMenu.currentData
    val menu = menuData.menu ?: return

    AppDropdownMenu(
        expanded = menuData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductsMenu) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.EditShoppingListName) },
            text = { Text(text = menu.editNameBody.text.asCompose()) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.EditShoppingListReminder) },
            text = { Text(text = menu.editReminderBody.text.asCompose()) }
        )
        Divider()
        AppDropdownMenuItem(
            text = { Text(text = menu.calculateChangeBody.text.asCompose()) },
            onClick = { viewModel.onEvent(ProductsEvent.CalculateChange) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.DeleteProducts) },
            text = { Text(text = menu.deleteProductsBody.text.asCompose()) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.ShareProducts) },
            text = { Text(text = menu.shareBody.text.asCompose()) }
        )
    }
}

@Composable
private fun ItemMenu(itemUid: String, viewModel: ProductsViewModel) {
    val itemMenuData = viewModel.itemMenuState.currentData
    val menu = itemMenuData.menu ?: return

    AppDropdownMenu(
        expanded = itemMenuData.itemUid == itemUid,
        onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductMenu) }
    ) {
        AppDropdownMenuItem(
            onClick = {
                val event = ProductsEvent.EditProduct(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = menu.editBody.text.asCompose()) }
        )
        AppDropdownMenuItem(
            onClick = {
                val event = ProductsEvent.CopyProductToShoppingList(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = menu.copyToShoppingListBody.text.asCompose()) }
        )
        AppDropdownMenuItem(
            onClick = {
                val event = ProductsEvent.MoveProductToShoppingList(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = menu.moveToShoppingListBody.text.asCompose()) }
        )
        AppDropdownMenuItem(
            onClick = {
                val event = ProductsEvent.DeleteProduct(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = menu.deleteBody.text.asCompose()) }
        )
    }
}

@Composable
private fun itemTitleOrNull(item: ProductItem): @Composable (() -> Unit)? {
    val title = item.title
    return itemOrNull(enabled = title.isTextShowing()) {
        Text(
            text = title.text.asCompose(),
            fontSize = title.fontSize
        )
    }
}

@Composable
private fun itemBodyOrNull(item: ProductItem): @Composable (() -> Unit)? {
    val body = item.body
    return itemOrNull(enabled = body.isTextShowing()) {
        Text(
            text = body.text.asCompose(),
            fontSize = body.fontSize
        )
    }
}

@Composable
private fun itemBefore(item: ProductItem): @Composable (() -> Unit) = {
    AppCheckbox(
        checked = item.completed.checked,
        colors = CheckboxDefaults.colors(
            checkedColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
        )
    )
}

@Composable
private fun reminderTitleOrNull(data: ItemReminderData): @Composable (() -> Unit)? {
    val title = data.title
    return itemOrNull(enabled = data.body.isTextShowing()) {
        Text(
            text = title.text.asCompose(),
            fontSize = title.fontSize
        )
    }
}

@Composable
private fun reminderBodyOrNull(data: ItemReminderData): @Composable (() -> Unit)? {
    val body = data.body
    return itemOrNull(enabled = body.isTextShowing()) {
        Text(
            text = body.text.asCompose(),
            fontSize = body.fontSize
        )
    }
}
package ru.sokolovromann.myshopping.ui.compose

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.viewmodel.ProductsViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.ProductsEvent

@ExperimentalFoundationApi
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

    AppSystemUi(
        systemUiController = systemUiController,
        data = viewModel.systemUiState.value
    )

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
    AppBottomBar(data = viewModel.bottomBarState.value) {
        TextButton(
            onClick = { viewModel.onEvent(ProductsEvent.SelectProductsDisplayTotal)}
        ) {
            AppText(data = viewModel.totalState.currentData.text)
            TotalMenu(viewModel)
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = { viewModel.onEvent(ProductsEvent.AddProduct) }) {
            AppIcon(data = viewModel.addIconState.value)
        }

        Box {
            IconButton(onClick = { viewModel.onEvent(ProductsEvent.ShowProductsMenu) }) {
                AppIcon(data = viewModel.productsMenu.currentData.icon)
            }
            ProductsMenu(viewModel)
        }
    }
}

@ExperimentalFoundationApi
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

@ExperimentalFoundationApi
@Composable
private fun ProductsShowing(data: ListData<ProductItem>, viewModel: ProductsViewModel) {
    val scrollState = rememberScrollState()
    val horizontalPadding = if (data.multiColumns) 4.dp else 0.dp
    val backgroundColor = if (data.multiColumns) AppColor.Transparent else AppColor.Surface

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = horizontalPadding)
        .verticalScroll(scrollState)
    ) {
        ProductsBar(viewModel)
        ProductsReminder(data.multiColumns, viewModel)
        AppGrid(
            modifier = Modifier.background(color = backgroundColor.asCompose()),
            data = data
        ) {
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
            TextButton(
                onClick = { viewModel.onEvent(ProductsEvent.SelectProductsSort) }
            ) {
                AppText(data = viewModel.sortState.currentData.text)
                SortMenu(viewModel)
            }
            IconButton(onClick = { viewModel.onEvent(ProductsEvent.InvertProductsSort) }) {
                AppIcon(data = viewModel.sortAscendingState.value)
            }
        }
        IconButton(onClick = { viewModel.onEvent(ProductsEvent.SelectProductsDisplayCompleted) }) {
            AppIcon(data = viewModel.completedState.currentData.icon)
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
            .padding(horizontal = 16.dp),
        content = { AppText(data = data) }
    )
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

@ExperimentalFoundationApi
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

    DropdownMenu(
        expanded = totalData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductsDisplayTotal) }
    ) {
        AppText(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            data = menu.title
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.allSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.allBody
                )
            },
            onClick = { viewModel.onEvent(ProductsEvent.DisplayProductsAllTotal) }
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.completedSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.completedBody
                )
            },
            onClick = { viewModel.onEvent(ProductsEvent.DisplayProductsCompletedTotal) }
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.activeSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.activeBody
                )
            },
            onClick = { viewModel.onEvent(ProductsEvent.DisplayProductsActiveTotal) }
        )
    }
}

@Composable
private fun SortMenu(viewModel: ProductsViewModel) {
    val sortData = viewModel.sortState.currentData
    val menu = sortData.menu ?: return

    DropdownMenu(
        expanded = sortData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductsSort) }
    ) {
        AppText(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            data = menu.title
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.byCreatedSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.byCreatedBody
                )
            },
            onClick = { viewModel.onEvent(ProductsEvent.SortProductsByCreated) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.byLastModifiedSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.byLastModifiedBody
                )
            },
            onClick = { viewModel.onEvent(ProductsEvent.SortProductsByLastModified) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.byNameSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.byNameBody
                )
            },
            onClick = { viewModel.onEvent(ProductsEvent.SortProductsByName) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.byTotalSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.byTotalBody
                )
            },
            onClick = { viewModel.onEvent(ProductsEvent.SortProductsByTotal) },
        )
    }
}

@Composable
private fun CompletedMenu(viewModel: ProductsViewModel) {
    val completedData = viewModel.completedState.currentData
    val menu = completedData.menu ?: return

    DropdownMenu(
        expanded = completedData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductsDisplayCompleted) }
    ) {
        AppText(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            data = menu.title
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.firstSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.firstBody
                )
            },
            onClick = { viewModel.onEvent(ProductsEvent.DisplayProductsCompletedFirst) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.lastSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.lastBody
                )
            },
            onClick = { viewModel.onEvent(ProductsEvent.DisplayProductsCompletedLast) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.hideSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.hideBody
                )
            },
            onClick = { viewModel.onEvent(ProductsEvent.HideProductsCompleted) },
        )
    }
}

@Composable
private fun ProductsMenu(viewModel: ProductsViewModel) {
    val menuData = viewModel.productsMenu.currentData
    val menu = menuData.menu ?: return

    DropdownMenu(
        expanded = menuData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductsMenu) }
    ) {
        AppMenuItem(
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    data = menu.editNameBody
                )
            },
            onClick = {
                val event = ProductsEvent.EditShoppingListName
                viewModel.onEvent(event)
            }
        )
        AppMenuItem(
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    data = menu.editReminderBody
                )
            },
            onClick = {
                val event = ProductsEvent.EditShoppingListReminder
                viewModel.onEvent(event)
            }
        )
        Divider()
        AppMenuItem(
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    data = menu.calculateChangeBody
                )
            },
            onClick = {
                val event = ProductsEvent.CalculateChange
                viewModel.onEvent(event)
            }
        )
        AppMenuItem(
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    data = menu.deleteProductsBody
                )
            },
            onClick = {
                val event = ProductsEvent.DeleteProducts
                viewModel.onEvent(event)
            }
        )
        AppMenuItem(
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    data = menu.shareBody
                )
            },
            onClick = {
                val event = ProductsEvent.ShareProducts
                viewModel.onEvent(event)
            }
        )
    }
}

@Composable
private fun ItemMenu(itemUid: String, viewModel: ProductsViewModel) {
    val itemMenuData = viewModel.itemMenuState.currentData
    val menu = itemMenuData.menu ?: return

    DropdownMenu(
        expanded = itemMenuData.itemUid == itemUid,
        onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductMenu) }
    ) {
        AppMenuItem(
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    data = menu.editBody
                )
            },
            onClick = {
                val event = ProductsEvent.EditProduct(itemUid)
                viewModel.onEvent(event)
            }
        )
        AppMenuItem(
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    data = menu.copyToShoppingListBody
                )
            },
            onClick = {
                val event = ProductsEvent.CopyProductToShoppingList(itemUid)
                viewModel.onEvent(event)
            }
        )
        AppMenuItem(
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    data = menu.moveToShoppingListBody
                )
            },
            onClick = {
                val event = ProductsEvent.MoveProductToShoppingList(itemUid)
                viewModel.onEvent(event)
            }
        )
        AppMenuItem(
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    data = menu.deleteBody
                )
            },
            onClick = {
                val event = ProductsEvent.DeleteProduct(itemUid)
                viewModel.onEvent(event)
            }
        )
    }
}

@Composable
private fun itemTitleOrNull(item: ProductItem): @Composable (() -> Unit)? {
    if (item.title.isTextHiding()) {
        return null
    }

    return { AppText(data = item.title) }
}

@Composable
private fun itemBodyOrNull(item: ProductItem): @Composable (() -> Unit)? {
    if (item.body.isTextHiding()) {
        return null
    }

    return {
        Column { AppText(data = item.body) }
    }
}

@Composable
private fun itemBefore(item: ProductItem): @Composable (() -> Unit) {
    return {
        Spacer(modifier = Modifier.width(8.dp))
        AppCheckbox(data = item.completed)
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
private fun reminderTitleOrNull(data: ItemReminderData): @Composable (() -> Unit)? {
    if (data.body.isTextHiding()) {
        return null
    }

    return { AppText(data = data.title) }
}

@Composable
private fun reminderBodyOrNull(data: ItemReminderData): @Composable (() -> Unit)? {
    if (data.body.isTextHiding()) {
        return null
    }

    return {
        Column { AppText(data = data.body) }
    }
}
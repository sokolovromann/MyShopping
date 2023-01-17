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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.SortBy
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.chooseNavigate
import ru.sokolovromann.myshopping.ui.compose.event.ProductsScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.utils.*
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
    val screenData = viewModel.productsState.screenData
    AppBottomAppBar(
        content = {
            TextButton(
                onClick = { viewModel.onEvent(ProductsEvent.SelectProductsDisplayTotal)}
            ) {
                Text(
                    text = screenData.totalText.asCompose(),
                    fontSize = screenData.fontSize.toButton().sp
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
        val screenData = viewModel.productsState.screenData
        when (screenData.screenState) {
            ScreenState.Nothing -> ProductsNotFound(viewModel)
            ScreenState.Loading -> ProductsLoading()
            ScreenState.Showing -> ProductsShowing(viewModel)
            ScreenState.Saving -> TODO()
        }
    }
}

@Composable
private fun ProductsShowing(viewModel: ProductsViewModel) {
    val screenData = viewModel.productsState.screenData
    val scrollState = rememberScrollState()
    val horizontalPadding = if (screenData.multiColumns) 4.dp else 0.dp

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = horizontalPadding)
        .verticalScroll(scrollState)
    ) {
        ProductsBar(viewModel)
        ProductsReminder(screenData.multiColumns, viewModel)
        AppGrid(multiColumns = screenData.multiColumns) {
            screenData.products.forEach { item -> ProductItem(item, screenData.multiColumns, viewModel) }
        }
        Spacer(modifier = Modifier.height(128.dp))
    }
}

@Composable
private fun ProductsReminder(multiColumns: Boolean, viewModel: ProductsViewModel) {
    val screenData = viewModel.productsState.screenData
    if (screenData.reminderText == UiText.Nothing) {
        return
    }

    AppMultiColumnsItem(
        modifier = Modifier.padding(horizontal = 8.dp),
        multiColumns = multiColumns,
        title = reminderTitleOrNull(screenData.fontSize),
        body = reminderBodyOrNull(screenData.reminderText, screenData.fontSize),
        onClick = {}
    )
}

@Composable
private fun ProductsBar(viewModel: ProductsViewModel) {
    val screenData = viewModel.productsState.screenData
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
                Text(
                    text = screenData.sort.getProductsText().asCompose(),
                    fontSize = screenData.fontSize.toButton().sp
                )
                SortMenu(viewModel)
            }
            IconButton(onClick = { viewModel.onEvent(ProductsEvent.InvertProductsSort) }) {
                Icon(
                    painter = screenData.sort.getAscendingIcon().asPainter() ?: return@IconButton,
                    contentDescription = stringResource(R.string.products_contentDescription_sortAscendingIcon),
                    tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                )
            }
        }
        IconButton(onClick = { viewModel.onEvent(ProductsEvent.SelectProductsDisplayCompleted) }) {
            Icon(
                painter = painterResource(R.drawable.ic_all_display_completed),
                contentDescription = stringResource(R.string.products_contentDescription_displayCompletedIcon),
                tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
            )
            CompletedMenu(viewModel)
        }
    }
}

@Composable
private fun ProductsNotFound(viewModel: ProductsViewModel) {
    val screenData = viewModel.productsState.screenData
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.products_text_productsNotFound),
            fontSize = screenData.fontSize.toItemTitle().sp,
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
    val screenData = viewModel.productsState.screenData
    AppMultiColumnsItem(
        multiColumns = multiColumns,
        before = itemBefore(item),
        title = itemTitleOrNull(item, screenData.fontSize),
        body = itemBodyOrNull(item, screenData.fontSize),
        dropdownMenu = { ItemMenu(item.uid, viewModel) },
        onClick = {
            val event = if (item.completed) {
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
    val screenData = viewModel.productsState.screenData

    AppDropdownMenu(
        expanded = screenData.showDisplayTotal,
        onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductsDisplayTotal) },
        header = { Text(text = stringResource(R.string.products_header_displayTotal)) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.DisplayProductsAllTotal) },
            text = { Text(text = stringResource(R.string.products_action_displayAllTotal)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayTotal == DisplayTotal.ALL) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.DisplayProductsCompletedTotal) },
            text = { Text(text = stringResource(R.string.products_action_displayCompletedTotal)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayTotal == DisplayTotal.COMPLETED) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.DisplayProductsActiveTotal) },
            text = { Text(text = stringResource(R.string.products_action_displayActiveTotal)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayTotal == DisplayTotal.ACTIVE) }
        )
    }
}

@Composable
private fun SortMenu(viewModel: ProductsViewModel) {
    val screenData = viewModel.productsState.screenData

    AppDropdownMenu(
        expanded = screenData.showSort,
        onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductsSort) },
        header = { Text(text = stringResource(R.string.products_header_sort)) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.SortProductsByCreated) },
            text = { Text(text = stringResource(R.string.products_action_sortByCreated)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.CREATED) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.SortProductsByLastModified) },
            text = { Text(text = stringResource(R.string.products_action_sortByLastModified)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.LAST_MODIFIED) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.SortProductsByName) },
            text = { Text(text = stringResource(R.string.products_action_sortByName)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.NAME) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.SortProductsByTotal) },
            text = { Text(text = stringResource(R.string.products_action_sortByTotal)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.TOTAL) }
        )
    }
}

@Composable
private fun CompletedMenu(viewModel: ProductsViewModel) {
    val screenData = viewModel.productsState.screenData

    AppDropdownMenu(
        expanded = screenData.showDisplayCompleted,
        onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductsDisplayCompleted) },
        header = { Text(text = stringResource(R.string.products_header_displayCompleted)) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.DisplayProductsCompletedFirst) },
            text = { Text(text = stringResource(R.string.products_action_displayCompletedFirst)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayCompleted == DisplayCompleted.FIRST) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.DisplayProductsCompletedLast) },
            text = { Text(text = stringResource(R.string.products_action_displayCompletedLast)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayCompleted == DisplayCompleted.LAST) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.HideProductsCompleted) },
            text = { Text(text = stringResource(R.string.products_action_hideCompleted)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayCompleted == DisplayCompleted.HIDE) }
        )
    }
}

@Composable
private fun ProductsMenu(viewModel: ProductsViewModel) {
    val screenData = viewModel.productsState.screenData

    AppDropdownMenu(
        expanded = screenData.showProductsMenu,
        onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductsMenu) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.EditShoppingListName) },
            text = { Text(text = stringResource(R.string.products_action_editShoppingListName)) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.EditShoppingListReminder) },
            text = { Text(text = stringResource(R.string.products_action_editShoppingListReminder)) }
        )
        Divider()
        AppDropdownMenuItem(
            text = { Text(text = stringResource(R.string.products_action_calculateChange)) },
            onClick = { viewModel.onEvent(ProductsEvent.CalculateChange) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.DeleteProducts) },
            text = { Text(text = stringResource(R.string.products_action_deleteProducts)) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ProductsEvent.ShareProducts) },
            text = { Text(text = stringResource(R.string.products_action_shareProducts)) }
        )
    }
}

@Composable
private fun ItemMenu(itemUid: String, viewModel: ProductsViewModel) {
    val screenData = viewModel.productsState.screenData

    AppDropdownMenu(
        expanded = itemUid == screenData.productMenuUid,
        onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductMenu) }
    ) {
        AppDropdownMenuItem(
            onClick = {
                val event = ProductsEvent.EditProduct(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = stringResource(R.string.products_action_editProduct)) }
        )
        AppDropdownMenuItem(
            onClick = {
                val event = ProductsEvent.CopyProductToShoppingList(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = stringResource(R.string.products_action_copyProductToShoppingList)) }
        )
        AppDropdownMenuItem(
            onClick = {
                val event = ProductsEvent.MoveProductToShoppingList(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = stringResource(R.string.products_action_moveProductToShoppingList)) }
        )
        AppDropdownMenuItem(
            onClick = {
                val event = ProductsEvent.DeleteProduct(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = stringResource(R.string.products_action_deleteProduct)) }
        )
    }
}

@Composable
private fun itemTitleOrNull(item: ProductItem, fontSize: FontSize): @Composable (() -> Unit)? {
    val name = item.nameText
    return itemOrNull(enabled = name !is UiText.Nothing) {
        Text(
            text = name.asCompose(),
            fontSize = fontSize.toItemTitle().sp
        )
    }
}

@Composable
private fun itemBodyOrNull(item: ProductItem, fontSize: FontSize): @Composable (() -> Unit)? {
    val body = item.bodyText
    return itemOrNull(enabled = body !is UiText.Nothing) {
        Text(
            text = body.asCompose(),
            fontSize = fontSize.toItemBody().sp
        )
    }
}

@Composable
private fun itemBefore(item: ProductItem): @Composable (() -> Unit) = {
    AppCheckbox(
        checked = item.completed,
        colors = CheckboxDefaults.colors(
            checkedColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
        )
    )
}

@Composable
private fun reminderTitleOrNull(fontSize: FontSize): @Composable (() -> Unit) = {
    Text(
        text = stringResource(R.string.products_text_reminder),
        fontSize = fontSize.toItemTitle().sp
    )
}

@Composable
private fun reminderBodyOrNull(reminderText: UiText, fontSize: FontSize): @Composable (() -> Unit) = {
    Text(
        text = reminderText.asCompose(),
        fontSize = fontSize.toItemBody().sp
    )
}
package ru.sokolovromann.myshopping.ui.compose

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.*
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
    val screenData = viewModel.productsState.screenData

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                is ProductsScreenEvent.AddProduct -> navController.navigate(
                    route = UiRoute.Products.addProductScreen(it.uid)
                )

                is ProductsScreenEvent.EditProduct -> navController.navigate(
                    route = UiRoute.Products.editProductScreen(it.shoppingUid, it.productUid)
                )

                is ProductsScreenEvent.EditShoppingListName -> navController.navigate(
                    route = UiRoute.Products.editShoppingListNameScreen(it.uid)
                )

                is ProductsScreenEvent.EditShoppingListReminder -> navController.navigate(
                    route = UiRoute.Products.editShoppingListReminderScreen(it.uid)
                )

                is ProductsScreenEvent.CopyProductToShoppingList -> navController.navigate(
                    route = UiRoute.Products.copyProductToShoppingList(it.uids)
                )

                is ProductsScreenEvent.MoveProductToShoppingList -> navController.navigate(
                    route = UiRoute.Products.moveProductToShoppingList(it.uids)
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

    AppScaffold(
        topBar = {
            if (screenData.selectedUids == null) {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.ShowBackScreen) }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.products_contentDescription_navigationIcon),
                                tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                            )
                        }
                    },
                    actions = {
                        screenData.shoppingListLocation?.let {
                            when (it) {
                                ShoppingListLocation.PURCHASES -> {
                                    IconButton(onClick = { viewModel.onEvent(ProductsEvent.MoveShoppingListToArchive) }) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_all_archive),
                                            contentDescription = stringResource(R.string.products_contentDescription_moveShoppingListToArchive),
                                            tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                                        )
                                    }
                                    IconButton(onClick = { viewModel.onEvent(ProductsEvent.MoveShoppingListToTrash)}) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = stringResource(R.string.products_contentDescription_moveShoppingListToTrash),
                                            tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                                        )
                                    }
                                }
                                ShoppingListLocation.ARCHIVE -> {
                                    IconButton(onClick = { viewModel.onEvent(ProductsEvent.MoveShoppingListToPurchases) }) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_all_unarchive),
                                            contentDescription = stringResource(R.string.products_contentDescription_moveShoppingListToPurchases),
                                            tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                                        )
                                    }
                                    IconButton(onClick = { viewModel.onEvent(ProductsEvent.MoveShoppingListToTrash)}) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = stringResource(R.string.products_contentDescription_moveShoppingListToTrash),
                                            tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                                        )
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text(text = screenData.selectedUids.size.toString()) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.CancelSelectingProducts) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.products_contentDescription_cancelSelectingProducts),
                                tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.CopyProductsToShoppingList) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_all_copy_to),
                                contentDescription = stringResource(R.string.products_contentDescription_copyProductsToShoppingList),
                                tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.MoveProductsToShoppingList) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_all_move_to),
                                contentDescription = stringResource(R.string.products_contentDescription_moveProductsToShoppingList),
                                tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.DeleteProducts) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.products_contentDescription_deleteProducts),
                                tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (screenData.shoppingListLocation != ShoppingListLocation.TRASH) {
                AppBottomAppBar(
                    content = {
                        if (screenData.totalText != UiText.Nothing) {
                            ProductsTotalContent(
                                displayTotal = screenData.displayTotal,
                                totalText = screenData.totalText,
                                fontSize = screenData.fontSize.toButton().sp,
                                expanded = screenData.showDisplayTotal,
                                onExpanded = {
                                    if (it) {
                                        viewModel.onEvent(ProductsEvent.SelectDisplayPurchasesTotal)
                                    } else {
                                        viewModel.onEvent(ProductsEvent.HideDisplayPurchasesTotal)
                                    }
                                },
                                onSelected = {
                                    val event = ProductsEvent.DisplayPurchasesTotal(it)
                                    viewModel.onEvent(event)
                                }
                            )
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
                                    text = { Text(text = stringResource(R.string.products_action_sort)) },
                                    right = {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowRight,
                                            contentDescription = "",
                                            tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                                        )
                                    },
                                    onClick = { viewModel.onEvent(ProductsEvent.SelectProductsSort) }
                                )
                                AppDropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.products_action_selectProducts)) },
                                    right = {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowRight,
                                            contentDescription = "",
                                            tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                                        )
                                    },
                                    onClick = { viewModel.onEvent(ProductsEvent.SelectSelectProducts) }
                                )
                                if (screenData.displayMoney) {
                                    AppDropdownMenuItem(
                                        text = { Text(text = stringResource(R.string.products_action_calculateChange)) },
                                        onClick = { viewModel.onEvent(ProductsEvent.CalculateChange) }
                                    )
                                }
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.DeleteProducts) },
                                    text = { Text(text = stringResource(R.string.products_action_deleteProducts)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.ShareProducts) },
                                    text = { Text(text = stringResource(R.string.products_action_shareProducts)) }
                                )
                            }

                            AppDropdownMenu(
                                expanded = screenData.showSort,
                                onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductsSort) },
                                header = { Text(text = stringResource(id = R.string.products_action_sort)) }
                            ) {
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.SortProducts(SortBy.CREATED)) },
                                    text = { Text(text = stringResource(R.string.products_action_sortByCreated)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.SortProducts(SortBy.LAST_MODIFIED)) },
                                    text = { Text(text = stringResource(R.string.products_action_sortByLastModified)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.SortProducts(SortBy.NAME)) },
                                    text = { Text(text = stringResource(R.string.products_action_sortByName)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.SortProducts(SortBy.TOTAL)) },
                                    text = { Text(text = stringResource(R.string.products_action_sortByTotal)) }
                                )
                            }

                            AppDropdownMenu(
                                expanded = screenData.showSelectingMenu,
                                onDismissRequest = { viewModel.onEvent(ProductsEvent.HideSelectProducts) },
                                header = { Text(text = stringResource(R.string.products_action_selectProducts)) }
                            ) {
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.SelectAllProducts) },
                                    text = { Text(text = stringResource(R.string.products_action_selectAllProductsTo)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.SelectCompletedProducts) },
                                    text = { Text(text = stringResource(R.string.products_action_selectCompletedProductsTo)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.SelectActiveProducts) },
                                    text = { Text(text = stringResource(R.string.products_action_selectActiveProductsTo)) }
                                )
                            }
                        }
                    }
                )
            }
        }
    ) { paddings ->
        ProductsGrid(
            modifier = Modifier.padding(paddings),
            screenState = screenData.screenState,
            multiColumns = screenData.multiColumns,
            smartphoneScreen = screenData.smartphoneScreen,
            items = screenData.products,
            topBar = {
                if (screenData.shoppingListName != UiText.Nothing || screenData.reminderText != UiText.Nothing) {
                    val backgroundColor = if (screenData.shoppingListCompleted) {
                        MaterialTheme.colors.background
                    } else {
                        MaterialTheme.colors.surface
                    }
                    val shape = if (screenData.multiColumns) MaterialTheme.shapes.medium else RectangleShape
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .background(color = backgroundColor, shape = shape)
                        .padding(ProductsGridBarPaddings)
                    ) {
                        if (screenData.shoppingListName != UiText.Nothing) {
                            ProductsNameContent(
                                nameText = screenData.shoppingListName,
                                color = contentColorFor(backgroundColor = backgroundColor),
                                fontSize = screenData.fontSize
                            )

                            if (screenData.reminderText == UiText.Nothing) {
                                Spacer(modifier = Modifier.size(ProductsNameWithoutReminderSpacerSize))
                            }
                        }

                        if (screenData.reminderText != UiText.Nothing) {
                            ProductsReminderContent(
                                reminderText = screenData.reminderText,
                                fontSize = screenData.fontSize,
                                onClick = { viewModel.onEvent(ProductsEvent.EditShoppingListReminder) }
                            )
                        }
                    }
                }
            },
            bottomBar = {
                if (screenData.showHiddenProducts) {
                    ProductsHiddenContent(
                        fontSize = screenData.fontSize,
                        onClick = { viewModel.onEvent(ProductsEvent.DisplayHiddenProducts) }
                    )
                }
            },
            notFound = {
                Text(
                    text = screenData.productsNotFoundText.asCompose(),
                    fontSize = screenData.fontSize.toItemTitle().sp,
                    textAlign = TextAlign.Center
                )
            },
            fontSize = screenData.fontSize,
            dropdownMenu = {
                val expanded = screenData.selectedUids?.count() == 1 &&
                        screenData.selectedUids.contains(it)
                AppDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {},
                    properties = PopupProperties(focusable = false)
                ) {
                    Row {
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.EditProduct(it)) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(id = R.string.products_contentDescription_editProduct),
                                tint = contentColorFor(MaterialTheme.colors.background).copy(ContentAlpha.medium)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.MoveProductUp(it)) }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = stringResource(id = R.string.products_contentDescription_moveProductUp),
                                tint = contentColorFor(MaterialTheme.colors.background).copy(ContentAlpha.medium)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.MoveProductDown(it)) }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = stringResource(id = R.string.products_contentDescription_moveProductDown),
                                tint = contentColorFor(MaterialTheme.colors.background).copy(ContentAlpha.medium)
                            )
                        }
                    }
                }
            },
            completedWithCheckbox = screenData.completedWithCheckbox,
            location = screenData.shoppingListLocation,
            onClick = { uid, completed ->
                val uids = screenData.selectedUids
                val event = if (uids == null) {
                    if (completed) {
                        ProductsEvent.ActiveProduct(uid)
                    } else {
                        ProductsEvent.CompleteProduct(uid)
                    }
                } else {
                    if (uids.contains(uid)) {
                        ProductsEvent.UnselectProduct(uid)
                    } else {
                        ProductsEvent.SelectProduct(uid)
                    }
                }
                viewModel.onEvent(event)
            },
            onLongClick = {
                if (screenData.selectedUids == null) {
                    val event = ProductsEvent.SelectProduct(it)
                    viewModel.onEvent(event)
                }
            },
            selectedUids = screenData.selectedUids
        )
    }
}

@Composable
private fun ProductsNameContent(
    nameText: UiText,
    color: Color,
    fontSize: FontSize
) {
    Text(
        modifier = Modifier.padding(ProductsNamePaddings),
        text = nameText.asCompose(),
        color = color,
        fontSize = fontSize.toHeader6().sp,
        style = MaterialTheme.typography.h6
    )
}

@Composable
private fun ProductsReminderContent(
    reminderText: UiText,
    fontSize: FontSize,
    onClick: () -> Unit
) {
    Column {
        TextButton(onClick = onClick) {
            Icon(
                modifier = Modifier.size(fontSize.toButton().dp),
                painter = painterResource(R.drawable.ic_all_reminder),
                contentDescription = "",
                tint = MaterialTheme.colors.primary.copy(ContentAlpha.medium)
            )
            Spacer(modifier = Modifier.size(ProductsReminderSpacerSize))
            Text(
                text = reminderText.asCompose(),
                fontSize = fontSize.toButton().sp
            )
        }
    }
}

@Composable
private fun ProductsTotalContent(
    modifier: Modifier = Modifier,
    displayTotal: DisplayTotal,
    totalText: UiText,
    fontSize: TextUnit,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    onSelected: (DisplayTotal) -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = { onExpanded(true) }
    ) {
        Text(
            text = totalText.asCompose(),
            fontSize = fontSize
        )

        AppDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpanded(false) },
            header = { Text(text = stringResource(R.string.products_header_displayTotal)) }
        ) {
            AppDropdownMenuItem(
                onClick = { onSelected(DisplayTotal.ALL) },
                text = { Text(text = stringResource(R.string.products_action_displayAllTotal)) },
                right = { CheckmarkAppCheckbox(checked = displayTotal == DisplayTotal.ALL) }
            )
            AppDropdownMenuItem(
                onClick = { onSelected(DisplayTotal.COMPLETED) },
                text = { Text(text = stringResource(R.string.products_action_displayCompletedTotal)) },
                right = { CheckmarkAppCheckbox(checked = displayTotal == DisplayTotal.COMPLETED) }
            )
            AppDropdownMenuItem(
                onClick = { onSelected(DisplayTotal.ACTIVE) },
                text = { Text(text = stringResource(R.string.products_action_displayActiveTotal)) },
                right = { CheckmarkAppCheckbox(checked = displayTotal == DisplayTotal.ACTIVE) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProductsGrid(
    modifier: Modifier = Modifier,
    screenState: ScreenState,
    multiColumns: Boolean,
    smartphoneScreen: Boolean,
    items: List<ProductItem>,
    topBar: @Composable RowScope.() -> Unit,
    bottomBar: @Composable RowScope.() -> Unit,
    notFound: @Composable ColumnScope.() -> Unit,
    fontSize: FontSize,
    dropdownMenu: @Composable ((String) -> Unit)? = null,
    completedWithCheckbox: Boolean,
    location: ShoppingListLocation?,
    onClick: (String, Boolean) -> Unit,
    onLongClick: (String) -> Unit,
    selectedUids: List<String>?
) {
    SmartphoneTabletAppGrid(
        modifier = modifier,
        screenState = screenState,
        multiColumns = multiColumns,
        smartphoneScreen = smartphoneScreen,
        topBar = topBar,
        bottomBar = bottomBar,
        notFound = notFound
    ) {
        items(items) { item ->
            val selected = selectedUids?.contains(item.uid) ?: false

            val leftOnClickEnabled = selectedUids == null &&
                    location != ShoppingListLocation.TRASH &&
                    completedWithCheckbox
            val leftOnClick: ((Boolean) -> Unit)? = if (leftOnClickEnabled) {
                { onClick(item.uid, item.completed) }
            } else {
                null
            }

            val clickableEnabled = if (selectedUids == null) {
                location != ShoppingListLocation.TRASH && !completedWithCheckbox
            } else {
                true
            }

            val longClickableEnabled = location != ShoppingListLocation.TRASH

            AppMultiColumnsItem(
                multiColumns = multiColumns,
                left = getProductItemLeft(item.completed, leftOnClick),
                title = getProductItemTitleOrNull(item.nameText, fontSize),
                body = getProductItemBodyOrNull(item.bodyText, fontSize),
                dropdownMenu = { dropdownMenu?.let { it(item.uid) } },
                clickableEnabled = clickableEnabled,
                longClickableEnabled = longClickableEnabled,
                onClick = { onClick(item.uid, item.completed) },
                onLongClick = { onLongClick(item.uid) },
                backgroundColor = getAppItemBackgroundColor(selected, item.completed)
            )
        }
    }
}

@Composable
fun ProductsHiddenContent(
    fontSize: FontSize,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(ProductsHiddenProductsPaddings),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.products_text_hiddenProducts),
            fontSize = fontSize.toItemBody().sp,
            color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
            style = MaterialTheme.typography.body1
        )
        TextButton(onClick = onClick) {
            Text(
                text = stringResource(R.string.products_action_displayCompletedPurchases),
                fontSize = fontSize.toButton().sp,
            )
        }
    }
}

@Composable
private fun getProductItemLeft(
    completed: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?
): @Composable () -> Unit = {
    AppCheckbox(
        checked = completed,
        onCheckedChange = onCheckedChange,
        colors = CheckboxDefaults.colors(
            checkedColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
        )
    )
}

@Composable
private fun getProductItemTitleOrNull(
    text: UiText,
    fontSize: FontSize
) = itemOrNull(enabled = text.asCompose().isNotEmpty()) {
    Text(
        text = text.asCompose(),
        fontSize = fontSize.toItemTitle().sp
    )
}

@Composable
private fun getProductItemBodyOrNull(
    text: UiText,
    fontSize: FontSize
) = itemOrNull(enabled = text.asCompose().isNotEmpty()) {
    Text(
        text = text.asCompose(),
        fontSize = fontSize.toItemBody().sp
    )
}

private val ProductsReminderSpacerSize = 4.dp
private val ProductsNameWithoutReminderSpacerSize = 8.dp
private val ProductsHiddenProductsPaddings = PaddingValues(
    start = 8.dp,
    top = 8.dp,
    end = 8.dp
)
private val ProductsNamePaddings = PaddingValues(horizontal = 8.dp)
private val ProductsGridBarPaddings = PaddingValues(all = 8.dp)
package ru.sokolovromann.myshopping.ui.compose

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
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
import androidx.compose.ui.platform.LocalContext
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
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.chooseNavigate
import ru.sokolovromann.myshopping.ui.compose.event.ProductsScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.model.mapper.UiShoppingListsMapper
import ru.sokolovromann.myshopping.ui.utils.*
import ru.sokolovromann.myshopping.ui.viewmodel.ProductsViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.ProductsEvent

@Composable
fun ProductsScreen(
    navController: NavController,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val state = viewModel.productsState
    val context = LocalContext.current

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

                is ProductsScreenEvent.EditShoppingListTotal -> navController.navigate(
                    route = UiRoute.Products.editShoppingListTotalScreen(it.uid)
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

                is ProductsScreenEvent.UpdateProductsWidget -> updateProductsWidget(
                    context = context,
                    shoppingUid = it.shoppingUid
                )
            }
        }
    }

    BackHandler {
        if (state.selectedUids == null) {
            viewModel.onEvent(ProductsEvent.ShowBackScreen)
        } else {
            viewModel.onEvent(ProductsEvent.CancelSelectingProducts)
        }
    }

    AppScaffold(
        topBar = {
            if (state.selectedUids == null) {
                AppTopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.ShowBackScreen) }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.products_contentDescription_navigationIcon)
                            )
                        }
                    },
                    actions = {
                        when (state.locationValue.selected) {
                            ShoppingLocation.PURCHASES, ShoppingLocation.ARCHIVE -> {
                                IconButton(onClick = { viewModel.onEvent(ProductsEvent.EditShoppingListName) }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_all_rename),
                                        contentDescription = stringResource(R.string.products_contentDescription_editShoppingListName)
                                    )
                                }
                                IconButton(onClick = { viewModel.onEvent(ProductsEvent.EditShoppingListReminder) }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_all_reminder),
                                        contentDescription = stringResource(R.string.products_contentDescription_editShoppingListReminder)
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
                )
            } else {
                AppTopAppBar(
                    title = { Text(text = state.selectedUids?.size.toString()) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.CancelSelectingProducts) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.products_contentDescription_cancelSelectingProducts)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                if (state.isOnlyPinned()) {
                                    viewModel.onEvent(ProductsEvent.UnpinProducts)
                                } else {
                                    viewModel.onEvent(ProductsEvent.PinProducts)
                                }
                            }
                        ) {
                            Icon(
                                painter = if (state.isOnlyPinned()) {
                                    painterResource(R.drawable.ic_all_pin)
                                } else {
                                    painterResource(R.drawable.ic_all_unpin)
                                },
                                contentDescription = stringResource(R.string.products_contentDescription_pinOrUnpinProduct)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.DeleteProducts) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.products_contentDescription_deleteProducts)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.ShowSelectedMenu) }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = ""
                            )
                            AppDropdownMenu(
                                expanded = state.expandedItemMoreMenu,
                                onDismissRequest = { viewModel.onEvent(ProductsEvent.HideSelectedMenu) },
                            ) {
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.CopyProductsToShoppingList) },
                                    text = { Text(text = stringResource(R.string.products_action_copyProductsToShoppingList)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.MoveProductsToShoppingList) },
                                    text = { Text(text = stringResource(R.string.products_action_moveProductsToShoppingList)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.SelectAllProducts) },
                                    text = { Text(text = stringResource(R.string.products_action_selectAllProducts)) }
                                )
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (state.locationValue.selected != ShoppingLocation.TRASH) {
                AppBottomAppBar(
                    content = {
                        val totalValue = state.totalValue ?: return@AppBottomAppBar
                        if (totalValue.text.isNotEmpty()) {
                            ProductsTotalContent(
                                displayTotal = totalValue.selected,
                                totalText = totalValue.text.toUiText(),
                                totalFormatted = state.totalFormatted,
                                fontSize = state.fontSize.button.sp,
                                expanded = state.expandedDisplayTotal,
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
                                },
                                onEditTotal = { viewModel.onEvent(ProductsEvent.EditShoppingListTotal) },
                                onDeleteTotal = { viewModel.onEvent(ProductsEvent.DeleteShoppingListTotal) }
                            )
                        }
                    },
                    actionButtons = {
                        if (state.selectedUids == null) {
                            IconButton(onClick = { viewModel.onEvent(ProductsEvent.AddProduct) }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(R.string.products_contentDescription_addProductIcon)
                                )
                            }
                            IconButton(onClick = { viewModel.onEvent(ProductsEvent.ShowProductsMenu) }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = stringResource(R.string.products_contentDescription_productsMenuIcon)
                                )
                                AppDropdownMenu(
                                    expanded = state.expandedProductsMenu,
                                    onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductsMenu) }
                                ) {
                                    AppDropdownMenuItem(
                                        text = { Text(text = stringResource(R.string.products_action_showShoppingListMenu)) },
                                        right = {
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowRight,
                                                contentDescription = "",
                                                tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                                            )
                                        },
                                        onClick = { viewModel.onEvent(ProductsEvent.ShowShoppingListMenu) }
                                    )
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.InvertProductsMultiColumns) },
                                        text = { Text(text = state.multiColumnsValue.text.asCompose()) }
                                    )
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
                                    if (state.displayMoney) {
                                        AppDropdownMenuItem(
                                            text = { Text(text = stringResource(R.string.products_action_calculateChange)) },
                                            onClick = { viewModel.onEvent(ProductsEvent.CalculateChange) }
                                        )
                                    }
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.ShareProducts) },
                                        text = { Text(text = stringResource(R.string.products_action_shareProducts)) }
                                    )
                                }

                                AppDropdownMenu(
                                    expanded = state.expandedShoppingMenu,
                                    onDismissRequest = { viewModel.onEvent(ProductsEvent.HideShoppingListMenu) },
                                    header = { Text(text = stringResource(R.string.products_action_showShoppingListMenu)) }
                                ) {
                                    when (state.locationValue.selected) {
                                        ShoppingLocation.PURCHASES -> {
                                            AppDropdownMenuItem(
                                                onClick = { viewModel.onEvent(ProductsEvent.MoveShoppingListToArchive) },
                                                text = { Text(text = stringResource(R.string.products_action_moveShoppingListToArchive)) }
                                            )
                                            AppDropdownMenuItem(
                                                onClick = { viewModel.onEvent(ProductsEvent.MoveShoppingListToTrash) },
                                                text = { Text(text = stringResource(R.string.products_action_moveShoppingListToTrash)) }
                                            )
                                            AppDropdownMenuItem(
                                                onClick = { viewModel.onEvent(ProductsEvent.CopyShoppingList) },
                                                text = { Text(text = stringResource(R.string.products_action_copyShoppingList)) }
                                            )
                                        }
                                        ShoppingLocation.ARCHIVE -> {
                                            AppDropdownMenuItem(
                                                onClick = { viewModel.onEvent(ProductsEvent.MoveShoppingListToPurchases) },
                                                text = { Text(text = stringResource(R.string.products_action_moveShoppingListToPurchases)) }
                                            )
                                            AppDropdownMenuItem(
                                                onClick = { viewModel.onEvent(ProductsEvent.MoveShoppingListToTrash) },
                                                text = { Text(text = stringResource(R.string.products_action_moveShoppingListToTrash)) }
                                            )
                                        }
                                        else -> {}
                                    }
                                }

                                AppDropdownMenu(
                                    expanded = state.expandedSort,
                                    onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductsSort) },
                                    header = { Text(text = stringResource(id = R.string.products_action_sort)) }
                                ) {
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.SortProducts(
                                            SortBy.CREATED)) },
                                        text = { Text(text = stringResource(R.string.products_action_sortByCreated)) },
                                        right = {
                                            val checked = state.sortFormatted && state.sortValue.selected.sortBy == SortBy.CREATED
                                            CheckmarkAppCheckbox(checked = checked)
                                        }
                                    )
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.SortProducts(
                                            SortBy.LAST_MODIFIED)) },
                                        text = { Text(text = stringResource(R.string.products_action_sortByLastModified)) },
                                        right = {
                                            val checked = state.sortFormatted && state.sortValue.selected.sortBy == SortBy.LAST_MODIFIED
                                            CheckmarkAppCheckbox(checked = checked)
                                        }
                                    )
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.SortProducts(
                                            SortBy.NAME)) },
                                        text = { Text(text = stringResource(R.string.products_action_sortByName)) },
                                        right = {
                                            val checked = state.sortFormatted && state.sortValue.selected.sortBy == SortBy.NAME
                                            CheckmarkAppCheckbox(checked = checked)
                                        }
                                    )
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.SortProducts(
                                            SortBy.TOTAL)) },
                                        text = { Text(text = stringResource(R.string.products_action_sortByTotal)) },
                                        right = {
                                            val checked = state.sortFormatted && state.sortValue.selected.sortBy == SortBy.TOTAL
                                            CheckmarkAppCheckbox(checked = checked)
                                        }
                                    )
                                    Divider()
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.ReverseSortProducts) },
                                        text = { Text(text = stringResource(R.string.products_action_reverseSort)) },
                                        right = {
                                            val checked = !state.sortValue.selected.ascending
                                            CheckmarkAppCheckbox(checked = checked)
                                        }
                                    )
                                    Divider()
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.InvertAutomaticSorting) },
                                        text = { Text(text = stringResource(R.string.products_action_automaticSorting)) },
                                        right = { AppSwitch(checked = state.sortFormatted) }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    ) { paddings ->
        ProductsGrid(
            modifier = Modifier.padding(paddings),
            screenState = ScreenState.create(
                waiting = state.waiting,
                notFound = state.isNotFound()
            ),
            multiColumns = state.multiColumnsValue.selected,
            smartphoneScreen = state.smartphoneScreen,
            coloredCheckbox = state.coloredCheckbox,
            displayCompleted = state.displayCompleted,
            pinnedItems = UiShoppingListsMapper.toOldProductsItems(state.pinnedProducts),
            otherItems = UiShoppingListsMapper.toOldProductsItems(state.otherProducts),
            topBar = {
                if (state.nameText.isNotEmpty() || state.reminderText.isNotEmpty()) {
                    val backgroundColor = if (state.displayCompleted == DisplayCompleted.NO_SPLIT) {
                        MaterialTheme.colors.surface
                    } else {
                        if (state.completed) {
                            MaterialTheme.colors.background
                        } else {
                            MaterialTheme.colors.surface
                        }
                    }
                    val shape = if (state.multiColumnsValue.selected) MaterialTheme.shapes.medium else RectangleShape
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .background(color = backgroundColor, shape = shape)
                        .padding(ProductsGridBarPaddings)
                    ) {
                        if (state.nameText.isNotEmpty()) {
                            ProductsNameContent(
                                nameText = state.nameText.toUiText(),
                                color = contentColorFor(backgroundColor = backgroundColor),
                                fontSize = state.oldFontSize
                            )

                            if (state.reminderText.isNotEmpty()) {
                                Spacer(modifier = Modifier.size(ProductsNameWithoutReminderSpacerSize))
                            }
                        }

                        if (state.reminderText.isNotEmpty()) {
                            ProductsReminderContent(
                                reminderText = state.reminderText.toUiText(),
                                fontSize = state.oldFontSize,
                                onClick = { viewModel.onEvent(ProductsEvent.EditShoppingListReminder) }
                            )
                        }
                    }
                }
            },
            bottomBar = {
                if (state.displayHiddenProducts) {
                    ProductsHiddenContent(
                        fontSize = state.oldFontSize,
                        onClick = { viewModel.onEvent(ProductsEvent.DisplayHiddenProducts) }
                    )
                }
            },
            notFound = {
                Text(
                    text = state.notFoundText.asCompose(),
                    fontSize = state.fontSize.itemTitle.sp,
                    textAlign = TextAlign.Center
                )
            },
            fontSize = state.oldFontSize,
            dropdownMenu = {
                AppDropdownMenu(
                    expanded = state.expandedItemFavoriteMenu(it),
                    onDismissRequest = {},
                    properties = PopupProperties(focusable = false)
                ) {
                    Row {
                        if (!state.sortFormatted) {
                            IconButton(onClick = { viewModel.onEvent(ProductsEvent.MoveProductUp(it)) }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_all_arrow_up),
                                    contentDescription = stringResource(id = R.string.products_contentDescription_moveProductUp),
                                    tint = contentColorFor(MaterialTheme.colors.background).copy(ContentAlpha.medium)
                                )
                            }
                            IconButton(onClick = { viewModel.onEvent(ProductsEvent.MoveProductDown(it)) }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_all_arrow_down),
                                    contentDescription = stringResource(id = R.string.products_contentDescription_moveProductDown),
                                    tint = contentColorFor(MaterialTheme.colors.background).copy(ContentAlpha.medium)
                                )
                            }
                            AppVerticalDivider()
                        }
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.EditProduct(it)) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(id = R.string.products_contentDescription_editProduct),
                                tint = contentColorFor(MaterialTheme.colors.background).copy(ContentAlpha.medium)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.DeleteProducts) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.products_contentDescription_deleteProducts),
                                tint = contentColorFor(MaterialTheme.colors.background).copy(ContentAlpha.medium)
                            )
                        }
                    }
                }
            },
            completedWithCheckbox = state.completedWithCheckbox,
            location = state.locationValue.selected,
            onClick = { uid, completed ->
                val uids = state.selectedUids
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
                if (state.selectedUids == null) {
                    val event = ProductsEvent.SelectProduct(it)
                    viewModel.onEvent(event)
                }
            },
            selectedUids = state.selectedUids
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
    totalFormatted: Boolean,
    totalText: UiText,
    fontSize: TextUnit,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    onSelected: (DisplayTotal) -> Unit,
    onEditTotal: () -> Unit,
    onDeleteTotal: () -> Unit
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
            if (totalFormatted) {
                AppDropdownMenuItem(
                    onClick = onEditTotal,
                    text = { Text(text = stringResource(R.string.products_action_editShoppingListTotal)) }
                )
                AppDropdownMenuItem(
                    onClick = onDeleteTotal,
                    text = { Text(text = stringResource(R.string.products_action_deleteShoppingListTotal)) }
                )
            } else {
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
                Divider()
                AppDropdownMenuItem(
                    onClick = onEditTotal,
                    text = { Text(text = stringResource(R.string.products_action_addShoppingListTotal)) }
                )
            }
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
    coloredCheckbox: Boolean,
    displayCompleted: DisplayCompleted,
    pinnedItems: List<ProductItem>,
    otherItems: List<ProductItem>,
    topBar: @Composable RowScope.() -> Unit,
    bottomBar: @Composable RowScope.() -> Unit,
    notFound: @Composable ColumnScope.() -> Unit,
    fontSize: FontSize,
    dropdownMenu: @Composable ((String) -> Unit)? = null,
    completedWithCheckbox: Boolean,
    location: ShoppingLocation?,
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
        if (pinnedItems.isNotEmpty()) {
            item(span = StaggeredGridItemSpan.FullLine) {
                AppTextGridHeader(
                    text = stringResource(R.string.products_text_pinnedProducts),
                    fontSize = fontSize
                )
            }

            items(pinnedItems) { item ->
                val selected = selectedUids?.contains(item.uid) ?: false

                val leftOnClickEnabled = selectedUids == null &&
                        location != ShoppingLocation.TRASH &&
                        completedWithCheckbox
                val leftOnClick: ((Boolean) -> Unit)? = if (leftOnClickEnabled) {
                    { onClick(item.uid, item.completed) }
                } else {
                    null
                }

                val clickableEnabled = if (selectedUids == null) {
                    location != ShoppingLocation.TRASH && !completedWithCheckbox
                } else {
                    true
                }

                val longClickableEnabled = location != ShoppingLocation.TRASH

                AppMultiColumnsItem(
                    multiColumns = multiColumns,
                    left = getProductItemLeft(coloredCheckbox, item.completed, leftOnClick),
                    title = getProductItemTitleOrNull(item.nameText, fontSize),
                    body = getProductItemBodyOrNull(item.bodyText, fontSize),
                    right = getProductItemRightOrNull(selected),
                    dropdownMenu = { dropdownMenu?.let { it(item.uid) } },
                    clickableEnabled = clickableEnabled,
                    longClickableEnabled = longClickableEnabled,
                    onClick = { onClick(item.uid, item.completed) },
                    onLongClick = { onLongClick(item.uid) },
                    backgroundColor = getAppItemBackgroundColor(selected, item.completed, displayCompleted == DisplayCompleted.NO_SPLIT)
                )
            }

            if (otherItems.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    AppTextGridHeader(
                        text = stringResource(R.string.products_text_otherProducts),
                        fontSize = fontSize
                    )
                }
            }
        }

        items(otherItems) { item ->
            val selected = selectedUids?.contains(item.uid) ?: false

            val leftOnClickEnabled = selectedUids == null &&
                    location != ShoppingLocation.TRASH &&
                    completedWithCheckbox
            val leftOnClick: ((Boolean) -> Unit)? = if (leftOnClickEnabled) {
                { onClick(item.uid, item.completed) }
            } else {
                null
            }

            val clickableEnabled = if (selectedUids == null) {
                location != ShoppingLocation.TRASH && !completedWithCheckbox
            } else {
                true
            }

            val longClickableEnabled = location != ShoppingLocation.TRASH

            AppMultiColumnsItem(
                multiColumns = multiColumns,
                left = getProductItemLeft(coloredCheckbox, item.completed, leftOnClick),
                title = getProductItemTitleOrNull(item.nameText, fontSize),
                body = getProductItemBodyOrNull(item.bodyText, fontSize),
                right = getProductItemRightOrNull(selected),
                dropdownMenu = { dropdownMenu?.let { it(item.uid) } },
                clickableEnabled = clickableEnabled,
                longClickableEnabled = longClickableEnabled,
                onClick = { onClick(item.uid, item.completed) },
                onLongClick = { onLongClick(item.uid) },
                backgroundColor = getAppItemBackgroundColor(selected, item.completed, displayCompleted == DisplayCompleted.NO_SPLIT)
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
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.products_contentDescription_displayCompletedPurchases),
                tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
            )
        }
    }
}

@Composable
private fun getProductItemLeft(
    highlightCheckbox: Boolean,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?
): @Composable () -> Unit = {
    val checkedColor = if (highlightCheckbox) {
        MaterialTheme.colors.primary.copy(alpha = ContentAlpha.medium)
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    }

    val uncheckedColor = if (highlightCheckbox) {
        MaterialTheme.colors.error.copy(alpha = ContentAlpha.medium)
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    }

    AppCheckbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = CheckboxDefaults.colors(
            checkedColor = checkedColor,
            uncheckedColor = uncheckedColor
        )
    )
}

@Composable
private fun getProductItemRightOrNull(
    selected: Boolean,
)= itemOrNull(enabled = selected) {
    CheckmarkAppCheckbox(
        checked = true,
        checkmarkColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
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
    start = 16.dp,
    top = 8.dp,
    end = 8.dp
)
private val ProductsNamePaddings = PaddingValues(horizontal = 8.dp)
private val ProductsGridBarPaddings = PaddingValues(all = 8.dp)
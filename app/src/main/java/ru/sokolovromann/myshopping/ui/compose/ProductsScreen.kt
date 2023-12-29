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
import ru.sokolovromann.myshopping.ui.model.ProductItem
import ru.sokolovromann.myshopping.ui.model.UiFontSize
import ru.sokolovromann.myshopping.ui.model.UiString
import ru.sokolovromann.myshopping.ui.utils.updateProductsWidget
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
                ProductsScreenEvent.OnShowBackScreen -> {
                    navController.popBackStack()
                }

                is ProductsScreenEvent.OnShowAddProduct -> navController.navigate(
                    route = UiRoute.Products.addProductScreen(it.shoppingUid)
                )

                is ProductsScreenEvent.OnShowEditProduct -> navController.navigate(
                    route = UiRoute.Products.editProductScreen(it.shoppingUid, it.productUid)
                )

                is ProductsScreenEvent.OnShowEditName -> navController.navigate(
                    route = UiRoute.Products.editShoppingListNameScreen(it.shoppingUid)
                )

                is ProductsScreenEvent.OnShowEditReminder -> navController.navigate(
                    route = UiRoute.Products.editShoppingListReminderScreen(it.shoppingUid)
                )

                is ProductsScreenEvent.OnShowEditTotal -> navController.navigate(
                    route = UiRoute.Products.editShoppingListTotalScreen(it.shoppingUid)
                )

                is ProductsScreenEvent.OnShowCopyProduct -> navController.navigate(
                    route = UiRoute.Products.copyProductToShoppingList(it.productUids)
                )

                is ProductsScreenEvent.OnShowMoveProduct -> navController.navigate(
                    route = UiRoute.Products.moveProductToShoppingList(it.productUids)
                )

                is ProductsScreenEvent.OnShowCalculateChange -> navController.navigate(
                    route = UiRoute.Products.calculateChange(it.shoppingUid)
                )

                is ProductsScreenEvent.OnShareProducts -> navController.chooseNavigate(
                    intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, it.products)
                        type = "text/plain"
                    }
                )

                is ProductsScreenEvent.OnUpdateProductsWidget -> updateProductsWidget(
                    context = context,
                    shoppingUid = it.shoppingUid
                )
            }
        }
    }

    BackHandler {
        if (state.selectedUids == null) {
            viewModel.onEvent(ProductsEvent.OnClickBack)
        } else {
            viewModel.onEvent(ProductsEvent.OnAllProductsSelected(false))
        }
    }

    AppScaffold(
        topBar = {
            if (state.selectedUids == null) {
                AppTopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnClickBack) }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.products_contentDescription_navigationIcon)
                            )
                        }
                    },
                    actions = {
                        when (state.locationValue.selected) {
                            ShoppingLocation.PURCHASES, ShoppingLocation.ARCHIVE -> {
                                IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnClickEditName) }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_all_rename),
                                        contentDescription = stringResource(R.string.products_contentDescription_editShoppingListName)
                                    )
                                }
                                IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnClickEditReminder) }) {
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
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnAllProductsSelected(false)) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.products_contentDescription_cancelSelectingProducts)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnClickPin) }) {
                            Icon(
                                painter = if (state.isOnlyPinned()) {
                                    painterResource(R.drawable.ic_all_pin)
                                } else {
                                    painterResource(R.drawable.ic_all_unpin)
                                },
                                contentDescription = stringResource(R.string.products_contentDescription_pinOrUnpinProduct)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnClickDeleteProducts) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.products_contentDescription_deleteProducts)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnShowItemMoreMenu(true)) }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = ""
                            )
                            AppDropdownMenu(
                                expanded = state.expandedItemMoreMenu,
                                onDismissRequest = { viewModel.onEvent(ProductsEvent.OnShowItemMoreMenu(false)) },
                            ) {
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.OnClickCopyProductsToShoppingList) },
                                    text = { Text(text = stringResource(R.string.products_action_copyProductsToShoppingList)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.OnClickMoveProductsToShoppingList) },
                                    text = { Text(text = stringResource(R.string.products_action_moveProductsToShoppingList)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.OnAllProductsSelected(true)) },
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
                                totalText = totalValue.text,
                                totalFormatted = state.totalFormatted,
                                fontSize = state.fontSize.button.sp,
                                expanded = state.expandedDisplayTotal,
                                onExpanded = { viewModel.onEvent(ProductsEvent.OnSelectDisplayTotal(it)) },
                                onSelected = {
                                    val event = ProductsEvent.OnDisplayTotalSelected(it)
                                    viewModel.onEvent(event)
                                },
                                onEditTotal = { viewModel.onEvent(ProductsEvent.OnClickEditTotal) },
                                onDeleteTotal = { viewModel.onEvent(ProductsEvent.OnClickDeleteTotal) }
                            )
                        }
                    },
                    actionButtons = {
                        if (state.selectedUids == null) {
                            IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnClickAddProduct) }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(R.string.products_contentDescription_addProductIcon)
                                )
                            }
                            IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnShowProductsMenu(true)) }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = stringResource(R.string.products_contentDescription_productsMenuIcon)
                                )
                                AppDropdownMenu(
                                    expanded = state.expandedProductsMenu,
                                    onDismissRequest = { viewModel.onEvent(ProductsEvent.OnShowProductsMenu(false)) }
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
                                        onClick = { viewModel.onEvent(ProductsEvent.OnShowShoppingMenu(true)) }
                                    )
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.OnInvertMultiColumns) },
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
                                        onClick = { viewModel.onEvent(ProductsEvent.OnSelectSort(true)) }
                                    )
                                    if (state.displayMoney) {
                                        AppDropdownMenuItem(
                                            text = { Text(text = stringResource(R.string.products_action_calculateChange)) },
                                            onClick = { viewModel.onEvent(ProductsEvent.OnClickCalculateChange) }
                                        )
                                    }
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.OnClickShareProducts) },
                                        text = { Text(text = stringResource(R.string.products_action_shareProducts)) }
                                    )
                                }

                                AppDropdownMenu(
                                    expanded = state.expandedShoppingMenu,
                                    onDismissRequest = { viewModel.onEvent(ProductsEvent.OnShowShoppingMenu(false)) },
                                    header = { Text(text = stringResource(R.string.products_action_showShoppingListMenu)) }
                                ) {
                                    when (state.locationValue.selected) {
                                        ShoppingLocation.PURCHASES -> {
                                            AppDropdownMenuItem(
                                                onClick = { viewModel.onEvent(ProductsEvent.OnMoveShoppingListSelected(ShoppingLocation.ARCHIVE)) },
                                                text = { Text(text = stringResource(R.string.products_action_moveShoppingListToArchive)) }
                                            )
                                            AppDropdownMenuItem(
                                                onClick = { viewModel.onEvent(ProductsEvent.OnMoveShoppingListSelected(ShoppingLocation.TRASH)) },
                                                text = { Text(text = stringResource(R.string.products_action_moveShoppingListToTrash)) }
                                            )
                                            AppDropdownMenuItem(
                                                onClick = { viewModel.onEvent(ProductsEvent.OnClickCopyShoppingList) },
                                                text = { Text(text = stringResource(R.string.products_action_copyShoppingList)) }
                                            )
                                        }
                                        ShoppingLocation.ARCHIVE -> {
                                            AppDropdownMenuItem(
                                                onClick = { viewModel.onEvent(ProductsEvent.OnMoveShoppingListSelected(ShoppingLocation.PURCHASES)) },
                                                text = { Text(text = stringResource(R.string.products_action_moveShoppingListToPurchases)) }
                                            )
                                            AppDropdownMenuItem(
                                                onClick = { viewModel.onEvent(ProductsEvent.OnMoveShoppingListSelected(ShoppingLocation.TRASH)) },
                                                text = { Text(text = stringResource(R.string.products_action_moveShoppingListToTrash)) }
                                            )
                                        }
                                        else -> {}
                                    }
                                }

                                AppDropdownMenu(
                                    expanded = state.expandedSort,
                                    onDismissRequest = { viewModel.onEvent(ProductsEvent.OnSelectSort(false)) },
                                    header = { Text(text = stringResource(id = R.string.products_action_sort)) }
                                ) {
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.OnSortSelected(SortBy.CREATED)) },
                                        text = { Text(text = stringResource(R.string.products_action_sortByCreated)) },
                                        right = {
                                            val checked = state.sortFormatted && state.sortValue.selected.sortBy == SortBy.CREATED
                                            CheckmarkAppCheckbox(checked = checked)
                                        }
                                    )
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.OnSortSelected(SortBy.LAST_MODIFIED)) },
                                        text = { Text(text = stringResource(R.string.products_action_sortByLastModified)) },
                                        right = {
                                            val checked = state.sortFormatted && state.sortValue.selected.sortBy == SortBy.LAST_MODIFIED
                                            CheckmarkAppCheckbox(checked = checked)
                                        }
                                    )
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.OnSortSelected(SortBy.NAME)) },
                                        text = { Text(text = stringResource(R.string.products_action_sortByName)) },
                                        right = {
                                            val checked = state.sortFormatted && state.sortValue.selected.sortBy == SortBy.NAME
                                            CheckmarkAppCheckbox(checked = checked)
                                        }
                                    )
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.OnSortSelected(SortBy.TOTAL)) },
                                        text = { Text(text = stringResource(R.string.products_action_sortByTotal)) },
                                        right = {
                                            val checked = state.sortFormatted && state.sortValue.selected.sortBy == SortBy.TOTAL
                                            CheckmarkAppCheckbox(checked = checked)
                                        }
                                    )
                                    Divider()
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.OnReverseSort) },
                                        text = { Text(text = stringResource(R.string.products_action_reverseSort)) },
                                        right = {
                                            val checked = !state.sortValue.selected.ascending
                                            CheckmarkAppCheckbox(checked = checked)
                                        }
                                    )
                                    Divider()
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.OnInvertSortFormatted) },
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
            pinnedItems = state.pinnedProducts,
            otherItems = state.otherProducts,
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
                                nameText = state.nameText,
                                color = contentColorFor(backgroundColor = backgroundColor),
                                fontSize = state.fontSize
                            )

                            if (state.reminderText.isNotEmpty()) {
                                Spacer(modifier = Modifier.size(ProductsNameWithoutReminderSpacerSize))
                            }
                        }

                        if (state.reminderText.isNotEmpty()) {
                            ProductsReminderContent(
                                reminderText = state.reminderText,
                                fontSize = state.fontSize,
                                onClick = { viewModel.onEvent(ProductsEvent.OnClickEditReminder) }
                            )
                        }
                    }
                }
            },
            bottomBar = {
                if (state.displayHiddenProducts) {
                    ProductsHiddenContent(
                        fontSize = state.fontSize,
                        onClick = { viewModel.onEvent(ProductsEvent.OnShowHiddenProducts(true)) }
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
            fontSize = state.fontSize,
            oldFontSize = state.oldFontSize,
            dropdownMenu = {
                AppDropdownMenu(
                    expanded = state.expandedItemFavoriteMenu(it),
                    onDismissRequest = {},
                    properties = PopupProperties(focusable = false)
                ) {
                    Row {
                        if (!state.sortFormatted) {
                            IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnClickMoveProductUp(it)) }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_all_arrow_up),
                                    contentDescription = stringResource(id = R.string.products_contentDescription_moveProductUp),
                                    tint = contentColorFor(MaterialTheme.colors.background).copy(ContentAlpha.medium)
                                )
                            }
                            IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnClickMoveProductDown(it)) }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_all_arrow_down),
                                    contentDescription = stringResource(id = R.string.products_contentDescription_moveProductDown),
                                    tint = contentColorFor(MaterialTheme.colors.background).copy(ContentAlpha.medium)
                                )
                            }
                            AppVerticalDivider()
                        }
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnClickEditProduct(it)) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(id = R.string.products_contentDescription_editProduct),
                                tint = contentColorFor(MaterialTheme.colors.background).copy(ContentAlpha.medium)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnClickDeleteProducts) }) {
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
                    ProductsEvent.OnClickProduct(uid, completed)
                } else {
                    ProductsEvent.OnProductSelected(
                        selected = !uids.contains(uid),
                        productUid = uid
                    )
                }
                viewModel.onEvent(event)
            },
            onLongClick = {
                if (state.selectedUids == null) {
                    val event = ProductsEvent.OnProductSelected(
                        selected = true,
                        productUid = it
                    )
                    viewModel.onEvent(event)
                }
            },
            selectedUids = state.selectedUids
        )
    }
}

@Composable
private fun ProductsNameContent(
    nameText: UiString,
    color: Color,
    fontSize: UiFontSize
) {
    Text(
        modifier = Modifier.padding(ProductsNamePaddings),
        text = nameText.asCompose(),
        color = color,
        fontSize = fontSize.itemsHeader.sp,
        style = MaterialTheme.typography.h6
    )
}

@Composable
private fun ProductsReminderContent(
    reminderText: UiString,
    fontSize: UiFontSize,
    onClick: () -> Unit
) {
    Column {
        TextButton(onClick = onClick) {
            Icon(
                modifier = Modifier.size(fontSize.button.dp),
                painter = painterResource(R.drawable.ic_all_reminder),
                contentDescription = "",
                tint = MaterialTheme.colors.primary.copy(ContentAlpha.medium)
            )
            Spacer(modifier = Modifier.size(ProductsReminderSpacerSize))
            Text(
                text = reminderText.asCompose(),
                fontSize = fontSize.button.sp
            )
        }
    }
}

@Composable
private fun ProductsTotalContent(
    modifier: Modifier = Modifier,
    displayTotal: DisplayTotal,
    totalFormatted: Boolean,
    totalText: UiString,
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
    fontSize: UiFontSize,
    oldFontSize: FontSize,
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
                    fontSize = oldFontSize
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
                    title = getProductItemTitleOrNull(item.name, fontSize),
                    body = getProductItemBodyOrNull(item.body, fontSize),
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
                        fontSize = oldFontSize
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
                title = getProductItemTitleOrNull(item.name, fontSize),
                body = getProductItemBodyOrNull(item.body, fontSize),
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
    fontSize: UiFontSize,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(ProductsHiddenProductsPaddings),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.products_text_hiddenProducts),
            fontSize = fontSize.itemBody.sp,
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
    text: UiString,
    fontSize: UiFontSize
) = itemOrNull(enabled = text.asCompose().isNotEmpty()) {
    Text(
        text = text.asCompose(),
        fontSize = fontSize.itemTitle.sp
    )
}

@Composable
private fun getProductItemBodyOrNull(
    text: UiString,
    fontSize: UiFontSize
) = itemOrNull(enabled = text.asCompose().isNotEmpty()) {
    Text(
        text = text.asCompose(),
        fontSize = fontSize.itemBody.sp
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
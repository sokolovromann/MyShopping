package ru.sokolovromann.myshopping.ui.compose

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.chooseNavigate
import ru.sokolovromann.myshopping.ui.compose.event.ProductsScreenEvent
import ru.sokolovromann.myshopping.ui.model.ProductItem
import ru.sokolovromann.myshopping.ui.model.UiIcon
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
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                ProductsScreenEvent.OnShowBackScreen -> {
                    navController.popBackStack()
                }

                is ProductsScreenEvent.OnShowAddProductScreen -> navController.navigate(
                    route = UiRoute.Products.addProductScreen(it.shoppingUid)
                )

                is ProductsScreenEvent.OnShowEditProductScreen -> navController.navigate(
                    route = UiRoute.Products.editProductScreen(it.shoppingUid, it.productUid)
                )

                is ProductsScreenEvent.OnShowEditNameScreen -> navController.navigate(
                    route = UiRoute.Products.editShoppingListNameScreen(it.shoppingUid)
                )

                is ProductsScreenEvent.OnShowEditReminderScreen -> navController.navigate(
                    route = UiRoute.Products.editShoppingListReminderScreen(it.shoppingUid)
                )

                is ProductsScreenEvent.OnShowEditTotalScreen -> navController.navigate(
                    route = UiRoute.Products.editShoppingListTotalScreen(it.shoppingUid)
                )

                is ProductsScreenEvent.OnShowCopyProductsScreen -> navController.navigate(
                    route = UiRoute.Products.copyProductsToShoppingList(it.productUids)
                )

                is ProductsScreenEvent.OnShowMoveProductsScreen -> navController.navigate(
                    route = UiRoute.Products.moveProductsToShoppingList(it.productUids)
                )

                is ProductsScreenEvent.OnShowCalculateChangeScreen -> navController.navigate(
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

                is ProductsScreenEvent.OnHideKeyboard -> {
                    focusManager.clearFocus(force = true)
                }
            }
        }
    }

    BackHandler {
        if (state.selectedUids == null) {
            if (state.displaySearch) {
                viewModel.onEvent(ProductsEvent.OnInvertSearch)
            } else {
                viewModel.onEvent(ProductsEvent.OnClickBack)
            }
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
                        if (state.displaySearch) {
                            IconButton(
                                onClick = { viewModel.onEvent(ProductsEvent.OnInvertSearch) },
                                content = {
                                    CancelSearchIcon(
                                        contentDescription = UiString.FromResources(R.string.products_contentDescription_cancelSearchIcon)
                                    )
                                }
                            )
                        } else {
                            IconButton(
                                onClick = { viewModel.onEvent(ProductsEvent.OnClickBack) },
                                content = {
                                    BackScreenIcon(
                                        contentDescription = UiString.FromResources(R.string.products_contentDescription_backScreenIcon)
                                    )
                                }
                            )
                        }
                    },
                    actions = {
                        when (state.locationValue.selected) {
                            ShoppingLocation.PURCHASES, ShoppingLocation.ARCHIVE -> {
                                IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnClickEditName) }) {
                                    AppTopBarIcon(
                                        icon = UiIcon.FromResources(R.drawable.ic_all_rename),
                                        contentDescription = UiString.FromResources(R.string.products_contentDescription_editShoppingListNameIcon)
                                    )
                                }
                                IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnClickEditReminder) }) {
                                    AppTopBarIcon(
                                        icon = UiIcon.FromResources(R.drawable.ic_all_reminder),
                                        contentDescription = UiString.FromResources(R.string.products_contentDescription_editShoppingListReminderIcon)
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
                        IconButton(
                            onClick = {
                                val event = ProductsEvent.OnAllProductsSelected(selected = false)
                                viewModel.onEvent(event)
                            },
                            content = {
                                CancelSelectionIcon(
                                    contentDescription = UiString.FromResources(R.string.products_contentDescription_cancelSelectionIcon)
                                )
                            }
                        )
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnClickPinProducts) }) {
                            val iconResId = if (state.isOnlyPinned()) {
                                R.drawable.ic_all_pin
                            } else {
                                R.drawable.ic_all_unpin
                            }
                            AppTopBarIcon(
                                icon = UiIcon.FromResources(iconResId),
                                contentDescription = UiString.FromResources(R.string.products_contentDescription_pinOrUnpinProductIcon)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.onEvent(ProductsEvent.OnClickDeleteProducts) },
                            content = {
                                DeleteDataIcon(
                                    contentDescription = UiString.FromResources(R.string.products_contentDescription_deleteDataIcon)
                                )
                            }
                        )
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnShowItemMoreMenu(true)) }) {
                            AppTopBarIcon(icon = UiIcon.FromVector(Icons.Default.MoreVert))
                            AppDropdownMenu(
                                expanded = state.expandedItemMoreMenu,
                                onDismissRequest = { viewModel.onEvent(ProductsEvent.OnShowItemMoreMenu(false)) },
                            ) {
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.OnClickCopyProducts) },
                                    text = { Text(text = stringResource(R.string.products_action_copyProductsToShoppingList)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.OnClickMoveProducts) },
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
                        if (state.displaySearch) {
                            OutlinedAppTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(ProductsSearchPaddings)
                                    .focusRequester(focusRequester),
                                value = state.searchValue,
                                onValueChange = {
                                    val event = ProductsEvent.OnSearchValueChanged(it)
                                    viewModel.onEvent(event)
                                },
                                label = { Text(text = stringResource(R.string.products_label_search)) },
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(onSearch = { viewModel.onEvent(ProductsEvent.OnClickSearchProducts) })
                            )

                            LaunchedEffect(state.displaySearch) {
                                focusRequester.requestFocus()
                            }
                        } else {
                            val totalValue = state.totalValue ?: return@AppBottomAppBar
                            if (totalValue.text.isNotEmpty()) {
                                ProductsTotalContent(
                                    displayTotal = totalValue.selected,
                                    displayLongTotal = state.displayLongTotal,
                                    totalText = totalValue.text,
                                    totalFormatted = state.totalFormatted,
                                    expanded = state.expandedDisplayTotal,
                                    onExpanded = { viewModel.onEvent(ProductsEvent.OnSelectDisplayTotal(it)) },
                                    onSelected = {
                                        val event = ProductsEvent.OnDisplayTotalSelected(it)
                                        viewModel.onEvent(event)
                                    },
                                    onInvertDisplayLongTotal = { viewModel.onEvent(ProductsEvent.OnInvertDisplayLongTotal) },
                                    onEditTotal = { viewModel.onEvent(ProductsEvent.OnClickEditTotal) },
                                    onDeleteTotal = { viewModel.onEvent(ProductsEvent.OnClickDeleteTotal) },
                                    selectedUidsIsEmpty = state.selectedUids == null,
                                    budgetText = state.budgetText,
                                    isOverBudget = state.isOverBudget()
                                )
                            }
                        }
                    },
                    actionButtons = {
                        if (state.selectedUids == null) {
                            if (state.displaySearch) {
                                return@AppBottomAppBar
                            }

                            IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnClickAddProduct) }) {
                                AddDataIcon(contentDescription = UiString.FromResources(R.string.products_contentDescription_addProductIcon))
                            }
                            IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnShowProductsMenu(true)) }) {
                                MoreIcon(contentDescription = UiString.FromResources(R.string.products_contentDescription_productsMenuIcon))
                                AppDropdownMenu(
                                    expanded = state.expandedProductsMenu,
                                    onDismissRequest = { viewModel.onEvent(ProductsEvent.OnShowProductsMenu(false)) }
                                ) {
                                    AppDropdownMenuItem(
                                        text = { Text(text = stringResource(R.string.products_action_showShoppingListMenu)) },
                                        right = { MoreMenuIcon() },
                                        onClick = { viewModel.onEvent(ProductsEvent.OnShowShoppingMenu(true)) }
                                    )
                                    AppDropdownMenuItem(
                                        text = { Text(text = stringResource(R.string.products_header_view)) },
                                        right = { MoreMenuIcon() },
                                        onClick = { viewModel.onEvent(ProductsEvent.OnSelectView(true)) }
                                    )
                                    AppDropdownMenuItem(
                                        text = { Text(text = stringResource(R.string.products_action_sort)) },
                                        right = { MoreMenuIcon() },
                                        onClick = { viewModel.onEvent(ProductsEvent.OnSelectSort(true)) }
                                    )
                                    AppDropdownMenuItem(
                                        text = { Text(text = stringResource(R.string.products_action_markAs)) },
                                        right = { MoreMenuIcon() },
                                        onClick = { viewModel.onEvent(ProductsEvent.OnSelectMarkAs(true)) }
                                    )
                                    if (state.displayMoney) {
                                        AppDropdownMenuItem(
                                            text = { Text(text = stringResource(R.string.products_action_calculateChange)) },
                                            onClick = { viewModel.onEvent(ProductsEvent.OnClickCalculateChange) }
                                        )
                                    }
                                    AppDropdownMenuItem(
                                        text = { Text(text = stringResource(R.string.products_action_search)) },
                                        onClick = { viewModel.onEvent(ProductsEvent.OnInvertSearch) }
                                    )
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
                                                onClick = { viewModel.onEvent(ProductsEvent.OnInvertPinShoppingList) },
                                                text = { Text(text = state.shoppingListPinnedValue.text.asCompose()) }
                                            )
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
                                    expanded = state.expandedViewMenu,
                                    onDismissRequest = {
                                        val event = ProductsEvent.OnSelectView(false)
                                        viewModel.onEvent(event)
                                    },
                                    header = { Text(text = stringResource(id = R.string.products_header_view)) }
                                ) {
                                    AppDropdownMenuItem(
                                        onClick = {
                                            val event = ProductsEvent.OnViewSelected(false)
                                            viewModel.onEvent(event)
                                        },
                                        text = { Text(text = stringResource(R.string.products_action_selectListView)) },
                                        right = { CheckmarkAppCheckbox(checked = !state.multiColumnsValue.selected) }
                                    )
                                    AppDropdownMenuItem(
                                        onClick = {
                                            val event = ProductsEvent.OnViewSelected(true)
                                            viewModel.onEvent(event)
                                        },
                                        text = { Text(text = stringResource(R.string.products_action_selectGridView)) },
                                        right = { CheckmarkAppCheckbox(checked = state.multiColumnsValue.selected) }
                                    )
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
                                            if (state.sortFormatted) {
                                                val checked = !state.sortValue.selected.ascending
                                                AppSwitch(checked = checked)
                                            }
                                        }
                                    )
                                    Divider()
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.OnInvertSortFormatted) },
                                        text = { Text(text = stringResource(R.string.products_action_automaticSorting)) },
                                        right = { AppSwitch(checked = state.sortFormatted) }
                                    )
                                }

                                AppDropdownMenu(
                                    expanded = state.expandedMarkAsMenu,
                                    onDismissRequest = { viewModel.onEvent(ProductsEvent.OnSelectMarkAs(false)) },
                                    header = { Text(text = stringResource(id = R.string.products_action_markAs)) }
                                ) {
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.OnMarkAsSelected(true)) },
                                        text = { Text(text = stringResource(R.string.products_action_markAsCompleted)) },
                                    )
                                    AppDropdownMenuItem(
                                        onClick = { viewModel.onEvent(ProductsEvent.OnMarkAsSelected(false)) },
                                        text = { Text(text = stringResource(R.string.products_action_markAsActive)) },
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
            multiColumns = state.multiColumnsValue.selected,
            deviceSize = state.deviceSize,
            coloredCheckbox = state.coloredCheckbox,
            displayCompleted = state.displayCompleted,
            strikethroughCompletedProducts = state.strikethroughCompletedProducts,
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
                            )

                            if (state.reminderText.isNotEmpty()) {
                                Spacer(modifier = Modifier.size(ProductsNameWithoutReminderSpacerSize))
                            }
                        }

                        if (state.reminderText.isNotEmpty()) {
                            ProductsReminderContent(
                                reminderText = state.reminderText,
                                onClick = { viewModel.onEvent(ProductsEvent.OnClickEditReminder) }
                            )
                        }
                    }
                }
            },
            bottomBar = {
                if (state.displayHiddenProducts) {
                    ProductsHiddenContent {
                        viewModel.onEvent(ProductsEvent.OnShowHiddenProducts(true))
                    }
                }
            },
            isWaiting = state.waiting,
            notFound = {
                Text(
                    text = state.notFoundText.asCompose(),
                    textAlign = TextAlign.Center
                )
            },
            isNotFound = state.isNotFound(),
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
                                    contentDescription = stringResource(id = R.string.products_contentDescription_moveProductUpIcon),
                                    tint = contentColorFor(MaterialTheme.colors.background).copy(ContentAlpha.medium)
                                )
                            }
                            IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnClickMoveProductDown(it)) }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_all_arrow_down),
                                    contentDescription = stringResource(id = R.string.products_contentDescription_moveProductDownIcon),
                                    tint = contentColorFor(MaterialTheme.colors.background).copy(ContentAlpha.medium)
                                )
                            }
                            AppVerticalDivider()
                        }
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnClickEditProduct(it)) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(id = R.string.products_contentDescription_editProductIcon),
                                tint = contentColorFor(MaterialTheme.colors.background).copy(ContentAlpha.medium)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.OnClickDeleteProducts) }) {
                            DeleteDataIcon(
                                contentDescription = UiString.FromResources(R.string.products_contentDescription_deleteDataIcon),
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
    color: Color
) {
    Text(
        modifier = Modifier.padding(ProductsNamePaddings),
        text = nameText.asCompose(),
        color = color,
        style = MaterialTheme.typography.h6
    )
}

@Composable
private fun ProductsReminderContent(
    reminderText: UiString,
    onClick: () -> Unit
) {
    Column {
        TextButton(onClick = onClick) {
            Icon(
                painter = painterResource(R.drawable.ic_all_reminder),
                contentDescription = "",
                tint = MaterialTheme.colors.primary.copy(ContentAlpha.medium)
            )
            Spacer(modifier = Modifier.size(ProductsReminderSpacerSize))
            Text(text = reminderText.asCompose())
        }
    }
}

@Composable
private fun ProductsTotalContent(
    modifier: Modifier = Modifier,
    displayTotal: DisplayTotal,
    displayLongTotal: Boolean,
    totalFormatted: Boolean,
    totalText: UiString,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    onSelected: (DisplayTotal) -> Unit,
    onInvertDisplayLongTotal: () -> Unit,
    onEditTotal: () -> Unit,
    onDeleteTotal: () -> Unit,
    selectedUidsIsEmpty: Boolean,
    budgetText: UiString,
    isOverBudget: Boolean
) {
    TextButton(
        modifier = modifier,
        onClick = { onExpanded(true) }
    ) {
        Text(
            text = buildAnnotatedString {
                append(totalText.asCompose())

                if (selectedUidsIsEmpty && budgetText.isNotEmpty()) {
                    withStyle(
                        style = SpanStyle(
                            fontStyle = MaterialTheme.typography.body2.fontStyle,
                            color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                        ),
                        block = {
                            append(" ")
                            append(stringResource(R.string.products_text_of).lowercase())
                            append(" ")
                            append(budgetText.asCompose())
                        }
                    )
                }
            },
            color = if (selectedUidsIsEmpty && isOverBudget) MaterialTheme.colors.error else Color.Unspecified
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
                Divider()
                AppDropdownMenuItem(
                    onClick = onInvertDisplayLongTotal,
                    text = { Text(text = stringResource(R.string.products_action_displayLongTotal)) },
                    right = { AppSwitch(checked = displayLongTotal) }
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
                    onClick = onInvertDisplayLongTotal,
                    text = { Text(text = stringResource(R.string.products_action_displayLongTotal)) },
                    right = { AppSwitch(checked = displayLongTotal) }
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

@Composable
private fun ProductsGrid(
    modifier: Modifier = Modifier,
    multiColumns: Boolean,
    deviceSize: DeviceSize,
    coloredCheckbox: Boolean,
    displayCompleted: DisplayCompleted,
    strikethroughCompletedProducts: Boolean,
    pinnedItems: List<ProductItem>,
    otherItems: List<ProductItem>,
    topBar: @Composable RowScope.() -> Unit,
    bottomBar: @Composable RowScope.() -> Unit,
    isWaiting: Boolean,
    notFound: @Composable ColumnScope.() -> Unit,
    isNotFound: Boolean,
    dropdownMenu: @Composable ((String) -> Unit)? = null,
    completedWithCheckbox: Boolean,
    location: ShoppingLocation?,
    onClick: (String, Boolean) -> Unit,
    onLongClick: (String) -> Unit,
    selectedUids: List<String>?
) {
    SmartphoneTabletAppGrid(
        modifier = modifier,
        multiColumns = multiColumns,
        deviceSize = deviceSize,
        topBar = topBar,
        bottomBar = bottomBar,
        isWaiting = isWaiting,
        notFound = notFound,
        isNotFound = isNotFound
    ) {
        if (pinnedItems.isNotEmpty()) {
            item(span = StaggeredGridItemSpan.FullLine) {
                AppTextGridHeader(text = stringResource(R.string.products_text_pinnedProducts))
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

                val textDecoration = if (strikethroughCompletedProducts && item.completed) {
                    TextDecoration.LineThrough
                } else {
                    TextDecoration.None
                }

                AppMultiColumnsItem(
                    multiColumns = multiColumns,
                    left = getProductItemLeft(coloredCheckbox, item.completed, leftOnClick),
                    title = getProductItemTitleOrNull(item.name, textDecoration),
                    body = getProductItemBodyOrNull(item.body, textDecoration),
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
                    AppTextGridHeader(text = stringResource(R.string.products_text_otherProducts))
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

            val textDecoration = if (strikethroughCompletedProducts && item.completed) {
                TextDecoration.LineThrough
            } else {
                TextDecoration.None
            }

            AppMultiColumnsItem(
                multiColumns = multiColumns,
                left = getProductItemLeft(coloredCheckbox, item.completed, leftOnClick),
                title = getProductItemTitleOrNull(item.name, textDecoration),
                body = getProductItemBodyOrNull(item.body, textDecoration),
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
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(ProductsHiddenProductsPaddings),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.products_text_hiddenProducts),
            color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.products_contentDescription_displayCompletedPurchasesIcon),
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
    textDecoration: TextDecoration
) = itemOrNull(enabled = text.asCompose().isNotEmpty()) {
    Text(
        text = text.asCompose(),
        textDecoration = textDecoration
    )
}

@Composable
private fun getProductItemBodyOrNull(
    text: UiString,
    textDecoration: TextDecoration
) = itemOrNull(enabled = text.asCompose().isNotEmpty()) {
    Text(
        text = text.asCompose(),
        textDecoration = textDecoration
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
private val ProductsSearchPaddings = PaddingValues(horizontal = 8.dp)
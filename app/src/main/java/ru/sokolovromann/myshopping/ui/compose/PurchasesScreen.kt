package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.ui.DrawerScreen
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.PurchasesScreenEvent
import ru.sokolovromann.myshopping.ui.model.UiString
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.viewmodel.PurchasesViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.PurchasesEvent

@Composable
fun PurchasesScreen(
    navController: NavController,
    onFinishApp: () -> Unit,
    viewModel: PurchasesViewModel = hiltViewModel()
) {
    val state = viewModel.purchasesState
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                is PurchasesScreenEvent.OnFinishApp -> {
                    onFinishApp()
                }

                is PurchasesScreenEvent.OnShowProductsScreen -> navController.navigate(
                    route = UiRoute.Products.productsScreen(it.shoppingUid)
                )

                is PurchasesScreenEvent.OnDrawerScreenSelected -> {
                    navController.navigateWithDrawerOption(route = it.drawerScreen.getScreen())
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                is PurchasesScreenEvent.OnSelectDrawerScreen -> coroutineScope.launch {
                    if (it.display) {
                        scaffoldState.drawerState.open()
                    } else {
                        scaffoldState.drawerState.close()
                    }
                }

                is PurchasesScreenEvent.OnHideKeyboard -> {
                    focusManager.clearFocus(force = true)
                }
            }
        }
    }

    BackHandler {
        if (scaffoldState.drawerState.isOpen) {
            viewModel.onEvent(PurchasesEvent.OnSelectDrawerScreen(false))
        } else {
            if (state.selectedUids == null) {
                if (state.displaySearch) {
                    viewModel.onEvent(PurchasesEvent.OnInvertSearch)
                } else {
                    viewModel.onEvent(PurchasesEvent.OnClickBack)
                }
            } else {
                viewModel.onEvent(PurchasesEvent.OnAllShoppingListsSelected(false))
            }
        }
    }

    AppScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (state.selectedUids == null) {
                AppTopAppBar(
                    title = { Text(text = stringResource(R.string.shoppingLists_header_purchases)) },
                    navigationIcon = {
                        if (state.displaySearch) {
                            ShoppingListsCancelSearchButton { viewModel.onEvent(PurchasesEvent.OnInvertSearch) }
                        } else {
                            ShoppingListsOpenNavigationButton {
                                val event = PurchasesEvent.OnSelectDrawerScreen(display = true)
                                viewModel.onEvent(event)
                            }
                        }
                    }
                )
            } else {
                AppTopAppBar(
                    title = { Text(text = state.selectedUids?.size.toString()) },
                    navigationIcon = {
                        ShoppingListsCancelSelectionButton {
                            val event = PurchasesEvent.OnAllShoppingListsSelected(selected = false)
                            viewModel.onEvent(event)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.OnClickPinShoppingLists) }) {
                            Icon(
                                painter = if (state.isOnlyPinned()) {
                                    painterResource(R.drawable.ic_all_pin)
                                } else {
                                    painterResource(R.drawable.ic_all_unpin)
                                },
                                contentDescription = stringResource(R.string.shoppingLists_contentDescription_pinOrUnpinShoppingListsIcon)
                            )
                        }
                        ShoppingListsArchiveDataButton {
                            val event = PurchasesEvent.OnMoveShoppingListSelected(ShoppingLocation.ARCHIVE)
                            viewModel.onEvent(event)
                        }
                        ShoppingListsDeleteDataButton {
                            val event = PurchasesEvent.OnMoveShoppingListSelected(ShoppingLocation.TRASH)
                            viewModel.onEvent(event)
                        }
                        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.OnShowItemMoreMenu(true)) }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = ""
                            )
                            AppDropdownMenu(
                                expanded = state.expandedItemMoreMenu,
                                onDismissRequest = { viewModel.onEvent(PurchasesEvent.OnShowItemMoreMenu(false)) }
                            ) {
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(PurchasesEvent.OnClickCopyShoppingLists) },
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_copyShoppingLists)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(PurchasesEvent.OnAllShoppingListsSelected(true)) },
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_selectAllShoppingLists)) }
                                )
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            AppBottomAppBar(
                content = {
                    if (state.displaySearch) {
                        OutlinedAppTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(ShoppingListsSearchPaddings)
                                .focusRequester(focusRequester),
                            value = state.searchValue,
                            onValueChange = {
                                val event = PurchasesEvent.OnSearchValueChanged(it)
                                viewModel.onEvent(event)
                            },
                            label = { Text(text = stringResource(R.string.shoppingLists_label_search)) },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(onSearch = { viewModel.onEvent(PurchasesEvent.OnClickSearchShoppingLists) })
                        )

                        LaunchedEffect(state.displaySearch) {
                            focusRequester.requestFocus()
                        }
                    } else {
                        val totalValue = state.totalValue ?: return@AppBottomAppBar
                        if (totalValue.text.isNotEmpty()) {
                            ShoppingListsTotalContent(
                                displayTotal = totalValue.selected,
                                totalText = totalValue.text,
                                expanded = state.expandedDisplayTotal,
                                onExpanded = { viewModel.onEvent(PurchasesEvent.OnSelectDisplayTotal(it)) },
                                onSelected = {
                                    val event = PurchasesEvent.OnDisplayTotalSelected(it)
                                    viewModel.onEvent(event)
                                }
                            )
                        }
                    }
                },
                actionButtons = {
                    if (state.selectedUids == null) {
                        if (state.displaySearch) {
                            return@AppBottomAppBar
                        }

                        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.OnClickAddShoppingList) }) {
                            AddDataIcon(contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_addShoppingListIcon))
                        }
                        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.OnShowPurchasesMenu(true)) }) {
                            MoreIcon(contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_purchasesMenuIcon))
                            AppDropdownMenu(
                                expanded = state.expandedPurchasesMenu,
                                onDismissRequest = { viewModel.onEvent(PurchasesEvent.OnShowPurchasesMenu(false)) }
                            ) {
//                                AppDropdownMenuItem(
//                                    onClick = { viewModel.onEvent(PurchasesEvent.OnInvertMultiColumns) },
//                                    text = { Text(text = state.multiColumnsValue.text.asCompose()) }
//                                )
//                                AppDropdownMenuItem(
//                                    text = { Text(text = stringResource(R.string.shoppingLists_action_displayProducts)) },
//                                    right = { MoreMenuIcon() },
//                                    onClick = { viewModel.onEvent(PurchasesEvent.OnSelectDisplayProducts(true)) }
//                                )
                                AppDropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_selectView)) },
                                    right = { MoreMenuIcon() },
                                    onClick = {
                                        val event = PurchasesEvent.OnSelectView(true)
                                        viewModel.onEvent(event)
                                    }
                                )
                                AppDropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_sort)) },
                                    right = { MoreMenuIcon() },
                                    onClick = { viewModel.onEvent(PurchasesEvent.OnSelectSort(true)) }
                                )
                                AppDropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_search)) },
                                    onClick = { viewModel.onEvent(PurchasesEvent.OnInvertSearch) }
                                )
                            }

//                            ShoppingListsDisplayProductsMenu(
//                                expanded = state.expandedDisplayProducts,
//                                displayProducts = state.displayProducts,
//                                onDismissRequest = {
//                                    val event = PurchasesEvent.OnSelectDisplayProducts(false)
//                                    viewModel.onEvent(event)
//                                },
//                                onSelected = {
//                                    val event = PurchasesEvent.OnDisplayProductsSelected(it)
//                                    viewModel.onEvent(event)
//                                }
//                            )
                            ShoppingListsViewMenu(
                                expanded = state.expandedShoppingsView,
                                multiColumns = state.multiColumnsValue.selected,
                                displayProducts = state.displayProducts.isDisplay(),
                                onDismissRequest = {
                                    val event = PurchasesEvent.OnSelectView(false)
                                    viewModel.onEvent(event)
                                },
                                onInvertMultiColumns = { viewModel.onEvent(PurchasesEvent.OnInvertMultiColumns) },
                                onInvertDisplayProducts = { viewModel.onEvent(PurchasesEvent.OnInvertDisplayProducts) }
                            )
                            ShoppingListsSortByMenu(
                                expanded = state.expandedSort,
                                sortValue = state.sortValue,
                                sortFormatted = state.sortFormatted,
                                onDismissRequest = { viewModel.onEvent(PurchasesEvent.OnSelectSort(false)) },
                                onSelected = {
                                    val event = PurchasesEvent.OnSortSelected(it)
                                    viewModel.onEvent(event)
                                },
                                onReverse = { viewModel.onEvent(PurchasesEvent.OnReverseSort) },
                                onInvertSortFormatted = { viewModel.onEvent(PurchasesEvent.OnInvertSortFormatted) }
                            )
                        }
                    }
                }
            )
        },
        drawerContent = {
            AppDrawerContent(
                selected = DrawerScreen.PURCHASES.toUiRoute(),
                onItemClick = {
                    val event = PurchasesEvent.OnDrawerScreenSelected(it.toDrawerScreen())
                    viewModel.onEvent(event)
                }
            )
        }
    ) { paddings ->
        ShoppingListsGrid(
            modifier = Modifier.padding(paddings),
            multiColumns = state.multiColumnsValue.selected,
            deviceSize = state.deviceSize,
            pinnedItems = state.pinnedShoppingLists,
            otherItems = state.otherShoppingLists,
            displayProducts = state.displayProducts,
            displayCompleted = state.displayCompleted,
            strikethroughCompletedProducts = state.strikethroughCompletedProducts,
            coloredCheckbox = state.coloredCheckbox,
            bottomBar = {
                if (state.displayHiddenShoppingLists) {
                    ShoppingListsHiddenContent{
                        viewModel.onEvent(PurchasesEvent.OnShowHiddenShoppingLists(true))
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
                            IconButton(onClick = { viewModel.onEvent(PurchasesEvent.OnClickMoveShoppingListUp(it)) }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_all_arrow_up),
                                    contentDescription = stringResource(R.string.shoppingLists_contentDescription_moveShoppingListUpIcon),
                                    tint = contentColorFor(MaterialTheme.colors.background).copy(ContentAlpha.medium)
                                )
                            }
                            IconButton(onClick = { viewModel.onEvent(PurchasesEvent.OnClickMoveShoppingListDown(it)) }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_all_arrow_down),
                                    contentDescription = stringResource(R.string.shoppingLists_contentDescription_moveShoppingListDownIcon),
                                    tint = contentColorFor(MaterialTheme.colors.background).copy(ContentAlpha.medium)
                                )
                            }
                        }
                    }
                }
            },
            onClick = {
                val uids = state.selectedUids
                val event = if (uids == null) {
                    PurchasesEvent.OnClickShoppingList(it)
                } else {
                    PurchasesEvent.OnShoppingListSelected(
                        selected = !uids.contains(it),
                        uid = it
                    )
                }
                viewModel.onEvent(event)
            },
            onLongClick = {
                if (state.selectedUids == null) {
                    val event = PurchasesEvent.OnShoppingListSelected(
                        selected = true,
                        uid = it
                    )
                    viewModel.onEvent(event)
                }
            },
            selectedUids = state.selectedUids
        )
    }
}
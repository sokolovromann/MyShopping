package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.ui.DrawerScreen
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.PurchasesScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.ScreenState
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.viewmodel.PurchasesViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.PurchasesEvent

@Composable
fun PurchasesScreen(
    navController: NavController,
    viewModel: PurchasesViewModel = hiltViewModel()
) {
    val state = viewModel.purchasesState
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                is PurchasesScreenEvent.OnFinishApp -> {
                    navController.popBackStack()
                }

                is PurchasesScreenEvent.OnShowShoppingList -> navController.navigate(
                    route = UiRoute.Products.productsScreen(it.uid)
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
            }
        }
    }

    BackHandler(enabled = scaffoldState.drawerState.isOpen) {
        viewModel.onEvent(PurchasesEvent.OnSelectDrawerScreen(false))
    }

    BackHandler(enabled = state.selectedUids != null) {
        viewModel.onEvent(PurchasesEvent.OnAllShoppingListsSelected(false))
    }

    AppScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (state.selectedUids == null) {
                AppTopAppBar(
                    title = { Text(text = stringResource(R.string.purchases_header)) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.OnSelectDrawerScreen(true)) }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.purchases_contentDescription_navigationIcon)
                            )
                        }
                    }
                )
            } else {
                AppTopAppBar(
                    title = { Text(text = state.selectedUids?.size.toString()) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.OnAllShoppingListsSelected(false)) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.purchases_contentDescription_cancelSelectingShoppingLists)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.OnClickPin) }) {
                            Icon(
                                painter = if (state.isOnlyPinned()) {
                                    painterResource(R.drawable.ic_all_pin)
                                } else {
                                    painterResource(R.drawable.ic_all_unpin)
                                },
                                contentDescription = stringResource(R.string.purchases_contentDescription_pinOrUnpinShoppingLists)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.OnClickMoveToArchive) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_all_archive),
                                contentDescription = stringResource(R.string.purchases_contentDescription_moveShoppingListsToArchive)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.OnClickMoveToTrash) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.purchases_contentDescription_moveShoppingListsToTrash)
                            )
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
                                    onClick = { viewModel.onEvent(PurchasesEvent.OnClickCopy) },
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
                    val totalValue = state.totalValue ?: return@AppBottomAppBar
                    if (totalValue.text.isNotEmpty()) {
                        ShoppingListsTotalContent(
                            displayTotal = totalValue.selected,
                            totalText = totalValue.text,
                            fontSize = state.fontSize.button.sp,
                            expanded = state.expandedDisplayTotal,
                            onExpanded = { viewModel.onEvent(PurchasesEvent.OnSelectDisplayTotal(it)) },
                            onSelected = {
                                val event = PurchasesEvent.OnDisplayTotalSelected(it)
                                viewModel.onEvent(event)
                            }
                        )
                    }
                },
                actionButtons = {
                    if (state.selectedUids == null) {
                        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.OnClickAdd) }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.purchases_contentDescription_addShoppingListIcon)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.OnShowPurchasesMenu(true)) }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.purchases_contentDescription_purchasesMenuIcon)
                            )
                            AppDropdownMenu(
                                expanded = state.expandedPurchasesMenu,
                                onDismissRequest = { viewModel.onEvent(PurchasesEvent.OnShowPurchasesMenu(false)) }
                            ) {
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(PurchasesEvent.OnInvertMultiColumns) },
                                    text = { Text(text = state.multiColumnsValue.text.asCompose()) }
                                )
                                AppDropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_sort)) },
                                    right = {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowRight,
                                            contentDescription = "",
                                            tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                                        )
                                    },
                                    onClick = { viewModel.onEvent(PurchasesEvent.OnSelectSort(true)) }
                                )
                            }

                            AppDropdownMenu(
                                expanded = state.expandedSort,
                                onDismissRequest = { viewModel.onEvent(PurchasesEvent.OnSelectSort(false)) },
                                header = { Text(text = stringResource(id = R.string.shoppingLists_action_sort)) }
                            ) {
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(PurchasesEvent.OnSortSelected(SortBy.CREATED)) },
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_sortByCreated)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(PurchasesEvent.OnSortSelected(SortBy.LAST_MODIFIED)) },
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_sortByLastModified)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(PurchasesEvent.OnSortSelected(SortBy.NAME)) },
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_sortByName)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(PurchasesEvent.OnSortSelected(SortBy.TOTAL)) },
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_sortByTotal)) }
                                )
                                Divider()
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(PurchasesEvent.OnReverseSort) },
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_reverseSort)) }
                                )
                            }
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
            screenState = ScreenState.create(
                waiting = state.waiting,
                notFound = state.isNotFound()
            ),
            multiColumns = state.multiColumnsValue.selected,
            smartphoneScreen = state.smartphoneScreen,
            pinnedItems = state.pinnedShoppingLists,
            otherItems = state.otherShoppingLists,
            displayProducts = state.displayProducts,
            displayCompleted = state.displayCompleted,
            coloredCheckbox = state.coloredCheckbox,
            bottomBar = {
                if (state.displayHiddenShoppingLists) {
                    ShoppingListsHiddenContent(
                        fontSize = state.fontSize,
                        onClick = { viewModel.onEvent(PurchasesEvent.OnShowHiddenShoppingLists(true)) }
                    )
                }
            },
            notFound = {
                Text(
                    text = stringResource(R.string.purchases_text_shoppingListsNotFound),
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
                        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.OnClickMoveUp(it)) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_all_arrow_up),
                                contentDescription = stringResource(R.string.shoppingLists_contentDescription_moveShoppingListUp),
                                tint = contentColorFor(MaterialTheme.colors.background).copy(ContentAlpha.medium)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.OnClickMoveDown(it)) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_all_arrow_down),
                                contentDescription = stringResource(R.string.shoppingLists_contentDescription_moveShoppingListDown),
                                tint = contentColorFor(MaterialTheme.colors.background).copy(ContentAlpha.medium)
                            )
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
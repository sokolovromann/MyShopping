package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.ui.DrawerScreen
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.TrashScreenEvent
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.viewmodel.TrashViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.TrashEvent

@Composable
fun TrashScreen(
    navController: NavController,
    viewModel: TrashViewModel = hiltViewModel()
) {
    val state = viewModel.trashState
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                TrashScreenEvent.OnShowBackScreen -> {
                    navController.popBackStack()
                }

                is TrashScreenEvent.OnShowProductsScreen -> navController.navigate(
                    route = UiRoute.Products.productsScreen(it.shoppingUid)
                )

                is TrashScreenEvent.OnDrawerScreenSelected -> {
                    navController.navigateWithDrawerOption(route = it.drawerScreen.getScreen())
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                is TrashScreenEvent.OnSelectDrawerScreen -> coroutineScope.launch {
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
        viewModel.onEvent(TrashEvent.OnSelectDrawerScreen(false))
    }

    BackHandler(enabled = state.selectedUids != null) {
        viewModel.onEvent(TrashEvent.OnAllShoppingListsSelected(false))
    }

    AppScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (state.selectedUids == null) {
                AppTopAppBar(
                    title = { Text(text = stringResource(R.string.trash_header)) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onEvent(TrashEvent.OnSelectDrawerScreen(true)) }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.trash_contentDescription_navigationIcon)
                            )
                        }
                    }
                )
            } else {
                AppTopAppBar(
                    title = { Text(text = state.selectedUids?.size.toString()) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onEvent(TrashEvent.OnAllShoppingListsSelected(false)) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.trash_contentDescription_cancelSelectingShoppingLists)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onEvent(TrashEvent.OnMoveShoppingListSelected(ShoppingLocation.PURCHASES)) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_all_restore),
                                contentDescription = stringResource(R.string.trash_contentDescription_moveShoppingListsToPurchases)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(TrashEvent.OnMoveShoppingListSelected(ShoppingLocation.ARCHIVE)) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_all_archive),
                                contentDescription = stringResource(R.string.trash_contentDescription_moveShoppingListsToArchive)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(TrashEvent.OnMoveShoppingListSelected(ShoppingLocation.TRASH)) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.trash_contentDescription_deleteShoppingLists)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(TrashEvent.OnAllShoppingListsSelected(true)) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_all_select_all),
                                contentDescription = stringResource(R.string.shoppingLists_action_selectAllShoppingLists)
                            )
                        }
                    }
                )
            }
        },
        drawerContent = {
            AppDrawerContent(
                selected = DrawerScreen.TRASH.toUiRoute(),
                onItemClick = {
                    val event = TrashEvent.OnDrawerScreenSelected(it.toDrawerScreen())
                    viewModel.onEvent(event)
                }
            )
        }
    ) { paddings ->
        ShoppingListsGrid(
            modifier = Modifier.padding(paddings),
            multiColumns = state.multiColumnsValue.selected,
            deviceSize = state.deviceSize,
            otherItems = state.shoppingLists,
            displayProducts = state.displayProducts,
            displayCompleted = state.displayCompleted,
            coloredCheckbox = state.coloredCheckbox,
            topBar = {
                TextButton(
                    modifier = Modifier.padding(TrashGridBarPaddings),
                    onClick = { viewModel.onEvent(TrashEvent.OnClickEmptyTrash) }
                ) {
                    Text(
                        text = stringResource(R.string.trash_action_deleteShoppingLists),
                        fontSize = state.fontSize.button.sp
                    )
                }
            },
            isWaiting = state.waiting,
            notFound = {
                Text(
                    text = stringResource(R.string.trash_text_shoppingListsNotFound),
                    fontSize = state.fontSize.itemTitle.sp,
                    textAlign = TextAlign.Center
                )
            },
            isNotFound = state.isNotFound(),
            fontSize = state.fontSize,
            onClick = {
                val uids = state.selectedUids
                val event = if (uids == null) {
                    TrashEvent.OnClickShoppingList(it)
                } else {
                    TrashEvent.OnShoppingListSelected(
                        selected = !uids.contains(it),
                        uid = it
                    )
                }
                viewModel.onEvent(event)
            },
            onLongClick = {
                if (state.selectedUids == null) {
                    val event = TrashEvent.OnShoppingListSelected(
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

private val TrashGridBarPaddings = PaddingValues(horizontal = 8.dp)
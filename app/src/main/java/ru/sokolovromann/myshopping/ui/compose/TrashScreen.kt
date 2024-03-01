package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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
                        ShoppingListsOpenNavigationButton {
                            val event = TrashEvent.OnSelectDrawerScreen(display = true)
                            viewModel.onEvent(event)
                        }
                    }
                )
            } else {
                AppTopAppBar(
                    title = { Text(text = state.selectedUids?.size.toString()) },
                    navigationIcon = {
                        ShoppingListsCancelSelectionButton {
                            val event = TrashEvent.OnAllShoppingListsSelected(selected = false)
                            viewModel.onEvent(event)
                        }
                    },
                    actions = {
                        ShoppingListsRestoreDataButton {
                            val event = TrashEvent.OnMoveShoppingListSelected(ShoppingLocation.PURCHASES)
                            viewModel.onEvent(event)
                        }
                        ShoppingListsArchiveDataButton {
                            val event = TrashEvent.OnMoveShoppingListSelected(ShoppingLocation.ARCHIVE)
                            viewModel.onEvent(event)
                        }
                        ShoppingListsDeleteDataButton {
                            val event = TrashEvent.OnMoveShoppingListSelected(ShoppingLocation.TRASH)
                            viewModel.onEvent(event)
                        }
                        ShoppingListsSelectAllDataButton {
                            val event = TrashEvent.OnAllShoppingListsSelected(true)
                            viewModel.onEvent(event)
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
                    text = stringResource(R.string.shoppingLists_text_trashShoppingListsNotFound),
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
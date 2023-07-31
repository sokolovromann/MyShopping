package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.MoveProductScreenEvent
import ru.sokolovromann.myshopping.ui.utils.toButton
import ru.sokolovromann.myshopping.ui.utils.toItemTitle
import ru.sokolovromann.myshopping.ui.utils.updateProductsWidgets
import ru.sokolovromann.myshopping.ui.viewmodel.MoveProductViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.MoveProductEvent

@Composable
fun MoveProductScreen(
    navController: NavController,
    viewModel: MoveProductViewModel = hiltViewModel()
) {
    val screenData = viewModel.moveProductState.screenData
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                MoveProductScreenEvent.ShowBackScreen -> navController.popBackStack()

                MoveProductScreenEvent.ShowBackScreenAndUpdateProductsWidgets -> {
                    updateProductsWidgets(context)
                    navController.popBackStack()
                }
            }
        }
    }

    BackHandler {
        viewModel.onEvent(MoveProductEvent.CancelMovingProduct)
    }

    AppScaffold(
        topBar = {
            AppTopAppBar(
                title = { Text(text = stringResource(R.string.moveProduct_header)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(MoveProductEvent.CancelMovingProduct) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.moveProduct_contentDescription_navigationIcon)
                        )
                    }
                }
            )
        }
    ) { paddings ->
        ShoppingListsGrid(
            modifier = Modifier.padding(paddings),
            screenState = screenData.screenState,
            multiColumns = screenData.multiColumns,
            smartphoneScreen = screenData.smartphoneScreen,
            pinnedItems = screenData.pinnedShoppingLists,
            otherItems = screenData.otherShoppingLists,
            displayProducts = screenData.displayProducts,
            displayCompleted = screenData.displayCompleted,
            highlightCheckbox = screenData.highlightCheckbox,
            topBar = {
                Row {
                    ShoppingListsLocationContent(
                        location = screenData.location,
                        fontSize = screenData.fontSize.toButton().sp,
                        expanded = screenData.showLocation,
                        onExpanded = {
                            if (it) {
                                viewModel.onEvent(MoveProductEvent.SelectShoppingListLocation)
                            } else {
                                viewModel.onEvent(MoveProductEvent.HideShoppingListsLocation)
                            }
                        },
                        onSelected = {
                            val event = MoveProductEvent.ShowShoppingLists(it)
                            viewModel.onEvent(event)
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.onEvent(MoveProductEvent.AddShoppingList) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.moveProduct_contentDescription_addShoppingListIcon),
                            tint = MaterialTheme.colors.onBackground.copy(ContentAlpha.medium)
                        )
                    }
                }
            },
            bottomBar = {
                if (screenData.showHiddenShoppingLists) {
                    ShoppingListsHiddenContent(
                        fontSize = screenData.fontSize,
                        onClick = { viewModel.onEvent(MoveProductEvent.DisplayHiddenShoppingLists) }
                    )
                }
            },
            notFound = {
                Text(
                    text = stringResource(R.string.moveProduct_text_shoppingListsNotFound),
                    fontSize = screenData.fontSize.toItemTitle().sp,
                    textAlign = TextAlign.Center
                )
            },
            fontSize = screenData.fontSize,
            onClick = {
                val event = MoveProductEvent.MoveProduct(it)
                viewModel.onEvent(event)
            },
            onLongClick = {}
        )
    }
}
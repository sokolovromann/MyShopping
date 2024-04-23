package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.CopyMoveProductsScreenEvent
import ru.sokolovromann.myshopping.ui.model.UiString
import ru.sokolovromann.myshopping.ui.utils.updateProductsWidgets
import ru.sokolovromann.myshopping.ui.viewmodel.CopyMoveProductsViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.CopyMoveProductsEvent

@Composable
fun CopyMoveProductsScreen(
    navController: NavController,
    viewModel: CopyMoveProductsViewModel = hiltViewModel()
) {

    val state = viewModel.copyMoveProductsState
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                CopyMoveProductsScreenEvent.OnShowBackScreen -> {
                    updateProductsWidgets(context)
                    navController.popBackStack()
                }
            }
        }
    }

    BackHandler {
        viewModel.onEvent(CopyMoveProductsEvent.OnClickCancel)
    }

    AppScaffold(
        topBar = {
            AppTopAppBar(
                title = { Text(text = stringResource(R.string.copyMoveProducts_header)) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.onEvent(CopyMoveProductsEvent.OnClickCancel) },
                        content = {
                            CloseScreenIcon(
                                contentDescription = UiString.FromResources(R.string.copyMoveProducts_contentDescription_closeScreenIcon)
                            )
                        }
                    )
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
            topBar = {
                Row {
                    ShoppingListsLocationContent(
                        location = state.locationValue,
                        expanded = state.expandedLocation,
                        onExpanded = { viewModel.onEvent(CopyMoveProductsEvent.OnSelectLocation(it)) },
                        onSelected = {
                            val event = CopyMoveProductsEvent.OnLocationSelected(it)
                            viewModel.onEvent(event)
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.onEvent(CopyMoveProductsEvent.OnClickAddShoppingList) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.copyMoveProducts_contentDescription_addShoppingListIcon),
                            tint = MaterialTheme.colors.onBackground.copy(ContentAlpha.medium)
                        )
                    }
                }
            },
            bottomBar = {
                if (state.displayHiddenShoppingLists) {
                    ShoppingListsHiddenContent {
                        viewModel.onEvent(CopyMoveProductsEvent.OnShowHiddenShoppingLists(true))
                    }
                }
            },
            isWaiting = state.waiting,
            notFound = {
                Text(
                    text = stringResource(R.string.copyMoveProducts_text_shoppingListsNotFound),
                    textAlign = TextAlign.Center
                )
            },
            isNotFound = state.isNotFound(),
            onClick = {
                val event = CopyMoveProductsEvent.OnClickCopyOrMoveProducts(it)
                viewModel.onEvent(event)
            },
            onLongClick = {}
        )
    }
}
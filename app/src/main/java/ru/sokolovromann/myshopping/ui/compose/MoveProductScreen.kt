package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.MoveProductScreenEvent
import ru.sokolovromann.myshopping.ui.utils.toButton
import ru.sokolovromann.myshopping.ui.utils.toItemTitle
import ru.sokolovromann.myshopping.ui.viewmodel.MoveProductViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.MoveProductEvent

@Composable
fun MoveProductScreen(
    navController: NavController,
    viewModel: MoveProductViewModel = hiltViewModel()
) {
    val screenData = viewModel.moveProductState.screenData

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                MoveProductScreenEvent.ShowBackScreen -> navController.popBackStack()
            }
        }
    }

    BackHandler {
        viewModel.onEvent(MoveProductEvent.CancelMovingProduct)
    }

    AppScaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.moveProduct_header)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(MoveProductEvent.CancelMovingProduct) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.moveProduct_contentDescription_navigationIcon),
                            tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
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
            items = screenData.shoppingLists,
            productsOneLine = screenData.productsOneLine,
            topBar = {
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
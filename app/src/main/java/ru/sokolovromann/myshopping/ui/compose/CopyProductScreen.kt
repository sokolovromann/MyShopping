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
import ru.sokolovromann.myshopping.ui.compose.event.CopyProductScreenEvent
import ru.sokolovromann.myshopping.ui.utils.toButton
import ru.sokolovromann.myshopping.ui.utils.toItemTitle
import ru.sokolovromann.myshopping.ui.viewmodel.CopyProductViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.CopyProductEvent

@Composable
fun CopyProductScreen(
    navController: NavController,
    viewModel: CopyProductViewModel = hiltViewModel()
) {
    val screenData = viewModel.copyProductState.screenData

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                CopyProductScreenEvent.ShowBackScreen -> navController.popBackStack()
            }
        }
    }

    BackHandler {
        viewModel.onEvent(CopyProductEvent.CancelCopingProduct)
    }

    AppScaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.copyProduct_header)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(CopyProductEvent.CancelCopingProduct) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.copyProduct_contentDescription_navigationIcon),
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
            displayProducts = screenData.displayProducts,
            highlightCheckbox = screenData.highlightCheckbox,
            topBar = {
                ShoppingListsLocationContent(
                    location = screenData.location,
                    fontSize = screenData.fontSize.toButton().sp,
                    expanded = screenData.showLocation,
                    onExpanded = {
                        if (it) {
                            viewModel.onEvent(CopyProductEvent.SelectShoppingListLocation)
                        } else {
                            viewModel.onEvent(CopyProductEvent.HideShoppingListsLocation)
                        }
                    },
                    onSelected = {
                        val event = CopyProductEvent.ShowShoppingLists(it)
                        viewModel.onEvent(event)
                    }
                )
            },
            bottomBar = {
                if (screenData.showHiddenShoppingLists) {
                    ShoppingListsHiddenContent(
                        fontSize = screenData.fontSize,
                        onClick = { viewModel.onEvent(CopyProductEvent.DisplayHiddenShoppingLists) }
                    )
                }
            },
            notFound = {
                Text(
                    text = stringResource(R.string.copyProduct_text_shoppingListsNotFound),
                    fontSize = screenData.fontSize.toItemTitle().sp,
                    textAlign = TextAlign.Center
                )
            },
            fontSize = screenData.fontSize,
            onClick = {
                val event = CopyProductEvent.CopyProduct(it)
                viewModel.onEvent(event)
            },
            onLongClick = {}
        )
    }
}
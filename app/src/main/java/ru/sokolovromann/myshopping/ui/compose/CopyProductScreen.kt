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
import ru.sokolovromann.myshopping.ui.compose.event.CopyProductScreenEvent
import ru.sokolovromann.myshopping.ui.utils.toButton
import ru.sokolovromann.myshopping.ui.utils.toItemTitle
import ru.sokolovromann.myshopping.ui.utils.updateProductsWidgets
import ru.sokolovromann.myshopping.ui.viewmodel.CopyProductViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.CopyProductEvent

@Composable
fun CopyProductScreen(
    navController: NavController,
    viewModel: CopyProductViewModel = hiltViewModel()
) {
    val screenData = viewModel.copyProductState.screenData
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                CopyProductScreenEvent.ShowBackScreen -> navController.popBackStack()

                CopyProductScreenEvent.ShowBackScreenAndUpdateProductsWidgets -> {
                    updateProductsWidgets(context)
                    navController.popBackStack()
                }
            }
        }
    }

    BackHandler {
        viewModel.onEvent(CopyProductEvent.CancelCopingProduct)
    }

    AppScaffold(
        topBar = {
            AppTopAppBar(
                title = { Text(text = stringResource(R.string.copyProduct_header)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(CopyProductEvent.CancelCopingProduct) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.copyProduct_contentDescription_navigationIcon)
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
            coloredCheckbox = screenData.coloredCheckbox,
            topBar = {
                Row {
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
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.onEvent(CopyProductEvent.AddShoppingList) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.copyProduct_contentDescription_addShoppingListIcon),
                            tint = MaterialTheme.colors.onBackground.copy(ContentAlpha.medium)
                        )
                    }
                }
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
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
import ru.sokolovromann.myshopping.ui.compose.state.ScreenState
import ru.sokolovromann.myshopping.ui.utils.updateProductsWidgets
import ru.sokolovromann.myshopping.ui.viewmodel.CopyProductViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.CopyProductEvent

@Composable
fun CopyProductScreen(
    navController: NavController,
    viewModel: CopyProductViewModel = hiltViewModel()
) {
    val state = viewModel.copyProductState
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                CopyProductScreenEvent.OnShowBackScreen -> {
                    updateProductsWidgets(context)
                    navController.popBackStack()
                }
            }
        }
    }

    BackHandler {
        viewModel.onEvent(CopyProductEvent.OnClickCancel)
    }

    AppScaffold(
        topBar = {
            AppTopAppBar(
                title = { Text(text = stringResource(R.string.copyProduct_header)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(CopyProductEvent.OnClickCancel) }) {
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
            topBar = {
                Row {
                    ShoppingListsLocationContent(
                        location = state.locationValue.selected,
                        fontSize = state.fontSize.button.sp,
                        expanded = state.expandedLocation,
                        onExpanded = { viewModel.onEvent(CopyProductEvent.OnSelectLocation(it)) },
                        onSelected = {
                            val event = CopyProductEvent.OnLocationSelected(it)
                            viewModel.onEvent(event)
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.onEvent(CopyProductEvent.OnClickAdd) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.copyProduct_contentDescription_addShoppingListIcon),
                            tint = MaterialTheme.colors.onBackground.copy(ContentAlpha.medium)
                        )
                    }
                }
            },
            bottomBar = {
                if (state.displayHiddenShoppingLists) {
                    ShoppingListsHiddenContent(
                        fontSize = state.fontSize,
                        onClick = { viewModel.onEvent(CopyProductEvent.OnShowHiddenShoppingLists(true)) }
                    )
                }
            },
            notFound = {
                Text(
                    text = stringResource(R.string.copyProduct_text_shoppingListsNotFound),
                    fontSize = state.fontSize.itemTitle.sp,
                    textAlign = TextAlign.Center
                )
            },
            fontSize = state.fontSize,
            oldFontSize = state.oldFontSize,
            onClick = {
                val event = CopyProductEvent.OnClickCopy(it)
                viewModel.onEvent(event)
            },
            onLongClick = {}
        )
    }
}
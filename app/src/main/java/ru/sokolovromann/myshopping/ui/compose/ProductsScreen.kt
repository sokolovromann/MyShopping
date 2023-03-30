package ru.sokolovromann.myshopping.ui.compose

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.chooseNavigate
import ru.sokolovromann.myshopping.ui.compose.event.ProductsScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.utils.*
import ru.sokolovromann.myshopping.ui.viewmodel.ProductsViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.ProductsEvent

@Composable
fun ProductsScreen(
    navController: NavController,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val screenData = viewModel.productsState.screenData

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                is ProductsScreenEvent.AddProduct -> navController.navigate(
                    route = UiRoute.Products.addProductScreen(it.uid)
                )

                is ProductsScreenEvent.EditProduct -> navController.navigate(
                    route = UiRoute.Products.editProductScreen(it.shoppingUid, it.productUid)
                )

                is ProductsScreenEvent.EditShoppingListName -> navController.navigate(
                    route = UiRoute.Products.editShoppingListNameScreen(it.uid)
                )

                is ProductsScreenEvent.EditShoppingListReminder -> navController.navigate(
                    route = UiRoute.Products.editShoppingListReminderScreen(it.uid)
                )

                is ProductsScreenEvent.CopyProductToShoppingList -> navController.navigate(
                    route = UiRoute.Products.copyProductToShoppingList(it.uid)
                )

                is ProductsScreenEvent.MoveProductToShoppingList -> navController.navigate(
                    route = UiRoute.Products.moveProductToShoppingList(it.uid)
                )

                ProductsScreenEvent.ShowBackScreen -> navController.popBackStack()

                is ProductsScreenEvent.CalculateChange -> navController.navigate(
                    route = UiRoute.Products.calculateChange(it.uid)
                )

                is ProductsScreenEvent.ShareProducts -> navController.chooseNavigate(
                    intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, it.products)
                        type = "text/plain"
                    }
                )
            }
        }
    }

    BackHandler { viewModel.onEvent(ProductsEvent.ShowBackScreen) }

    AppGridScaffold(
        screenState = screenData.screenState,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(ProductsEvent.ShowBackScreen) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.products_contentDescription_navigationIcon),
                            tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                        )
                    }
                },
                actions = {
                    screenData.shoppingListLocation?.let {
                        when (it) {
                            ShoppingListLocation.PURCHASES -> {
                                IconButton(onClick = { viewModel.onEvent(ProductsEvent.MoveShoppingListToArchive) }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_all_archive),
                                        contentDescription = stringResource(R.string.products_contentDescription_moveShoppingListToArchive),
                                        tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                                    )
                                }
                                IconButton(onClick = { viewModel.onEvent(ProductsEvent.MoveShoppingListToTrash)}) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.products_contentDescription_moveShoppingListToTrash),
                                        tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                                    )
                                }
                            }
                            ShoppingListLocation.ARCHIVE -> {
                                IconButton(onClick = { viewModel.onEvent(ProductsEvent.MoveShoppingListToPurchases) }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_all_unarchive),
                                        contentDescription = stringResource(R.string.products_contentDescription_moveShoppingListToPurchases),
                                        tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                                    )
                                }
                                IconButton(onClick = { viewModel.onEvent(ProductsEvent.MoveShoppingListToTrash)}) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.products_contentDescription_moveShoppingListToTrash),
                                        tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (screenData.shoppingListLocation != ShoppingListLocation.TRASH) {
                AppBottomAppBar(
                    content = {
                        if (screenData.totalText != UiText.Nothing) {
                            ProductsTotalContent(
                                displayTotal = screenData.displayTotal,
                                totalText = screenData.totalText,
                                fontSize = screenData.fontSize.toButton().sp,
                                expanded = screenData.showDisplayTotal,
                                onExpanded = {
                                    if (it) {
                                        viewModel.onEvent(ProductsEvent.SelectDisplayPurchasesTotal)
                                    } else {
                                        viewModel.onEvent(ProductsEvent.HideDisplayPurchasesTotal)
                                    }
                                },
                                onSelected = {
                                    val event = ProductsEvent.DisplayPurchasesTotal(it)
                                    viewModel.onEvent(event)
                                }
                            )
                        }
                    },
                    actionButtons = {
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.AddProduct) }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.products_contentDescription_addProductIcon),
                                tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(ProductsEvent.ShowProductsMenu) }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.products_contentDescription_productsMenuIcon),
                                tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                            )
                            AppDropdownMenu(
                                expanded = screenData.showProductsMenu,
                                onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductsMenu) }
                            ) {
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.EditShoppingListName) },
                                    text = { Text(text = stringResource(R.string.products_action_editShoppingListName)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.EditShoppingListReminder) },
                                    text = { Text(text = stringResource(R.string.products_action_editShoppingListReminder)) }
                                )
                                Divider()
                                AppDropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.products_action_sort)) },
                                    after = {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowRight,
                                            contentDescription = "",
                                            tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                                        )
                                    },
                                    onClick = { viewModel.onEvent(ProductsEvent.SelectProductsSort) }
                                )
                                if (screenData.displayMoney) {
                                    AppDropdownMenuItem(
                                        text = { Text(text = stringResource(R.string.products_action_calculateChange)) },
                                        onClick = { viewModel.onEvent(ProductsEvent.CalculateChange) }
                                    )
                                }
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.DeleteProducts) },
                                    text = { Text(text = stringResource(R.string.products_action_deleteProducts)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.ShareProducts) },
                                    text = { Text(text = stringResource(R.string.products_action_shareProducts)) }
                                )
                            }

                            AppDropdownMenu(
                                expanded = screenData.showSort,
                                onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductsSort) },
                                header = { Text(text = stringResource(id = R.string.products_action_sort)) }
                            ) {
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.SortProducts(SortBy.CREATED)) },
                                    text = { Text(text = stringResource(R.string.products_action_sortByCreated)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.SortProducts(SortBy.LAST_MODIFIED)) },
                                    text = { Text(text = stringResource(R.string.products_action_sortByLastModified)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.SortProducts(SortBy.NAME)) },
                                    text = { Text(text = stringResource(R.string.products_action_sortByName)) }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ProductsEvent.SortProducts(SortBy.TOTAL)) },
                                    text = { Text(text = stringResource(R.string.products_action_sortByTotal)) }
                                )
                            }
                        }
                    }
                )
            }
        },
        loadingContent = {
            AppLoadingContent(indicator = { CircularProgressIndicator() })
        },
        notFoundContent = {
            AppNotFoundContent {
                Text(
                    text = screenData.productsNotFoundText.asCompose(),
                    fontSize = screenData.fontSize.toItemTitle().sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        gridBar = {
            if (screenData.shoppingListName != UiText.Nothing || screenData.reminderText != UiText.Nothing) {
                val backgroundColor = if (screenData.shoppingListCompleted) {
                    MaterialTheme.colors.background
                } else {
                    MaterialTheme.colors.surface
                }
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(color = backgroundColor)
                    .padding(ProductsGridBarPaddings)
                ) {
                    if (screenData.shoppingListName != UiText.Nothing) {
                        ProductsNameContent(
                            nameText = screenData.shoppingListName,
                            color = contentColorFor(backgroundColor = backgroundColor),
                            fontSize = screenData.fontSize
                        )

                        if (screenData.reminderText == UiText.Nothing) {
                            Spacer(modifier = Modifier.size(ProductsNameWithoutReminderSpacerSize))
                        }
                    }

                    if (screenData.reminderText != UiText.Nothing) {
                        ProductsReminderContent(
                            reminderText = screenData.reminderText,
                            fontSize = screenData.fontSize,
                            onClick = { viewModel.onEvent(ProductsEvent.EditShoppingListReminder) }
                        )
                    }
                }
            }
        },
        gridBottomBar = {
            if (screenData.showHiddenProducts) {
                Row(
                    modifier = Modifier.padding(ProductsHiddenProductsPaddings),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.products_text_hiddenProducts),
                        fontSize = screenData.fontSize.toItemBody().sp,
                        color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
                        style = MaterialTheme.typography.body1
                    )
                    TextButton(onClick = { viewModel.onEvent(ProductsEvent.DisplayHiddenProducts) }) {
                        Text(
                            text = stringResource(R.string.products_action_displayCompletedPurchases),
                            fontSize = screenData.fontSize.toButton().sp,
                        )
                    }
                }
            }
        },
        gridMultiColumnsSpace = screenData.multiColumns
    ) {
        ProductsGrid(
            multiColumns = screenData.multiColumns,
            smartphoneScreen = screenData.smartphoneScreen,
            items = screenData.products,
            fontSize = screenData.fontSize,
            dropdownMenu = {
                AppDropdownMenu(
                    expanded = it == screenData.productMenuUid,
                    onDismissRequest = { viewModel.onEvent(ProductsEvent.HideProductMenu) }
                ) {
                    AppDropdownMenuItem(
                        onClick = {
                            val event = ProductsEvent.EditProduct(it)
                            viewModel.onEvent(event)
                        },
                        text = { Text(text = stringResource(R.string.products_action_editProduct)) }
                    )
                    AppDropdownMenuItem(
                        onClick = {
                            val event = ProductsEvent.DeleteProduct(it)
                            viewModel.onEvent(event)
                        },
                        text = { Text(text = stringResource(R.string.products_action_deleteProduct)) }
                    )
                    Divider()
                    AppDropdownMenuItem(
                        onClick = {
                            val event = ProductsEvent.MoveProductUp(it)
                            viewModel.onEvent(event)
                        },
                        text = { Text(text = stringResource(R.string.products_action_moveProductUp)) }
                    )
                    AppDropdownMenuItem(
                        onClick = {
                            val event = ProductsEvent.MoveProductDown(it)
                            viewModel.onEvent(event)
                        },
                        text = { Text(text = stringResource(R.string.products_action_moveProductDown)) }
                    )
                    Divider()
                    AppDropdownMenuItem(
                        onClick = {
                            val event = ProductsEvent.CopyProductToShoppingList(it)
                            viewModel.onEvent(event)
                        },
                        text = { Text(text = stringResource(R.string.products_action_copyProductToShoppingList)) }
                    )
                    AppDropdownMenuItem(
                        onClick = {
                            val event = ProductsEvent.MoveProductToShoppingList(it)
                            viewModel.onEvent(event)
                        },
                        text = { Text(text = stringResource(R.string.products_action_moveProductToShoppingList)) }
                    )
                }
            },
            completedWithCheckbox = screenData.completedWithCheckbox,
            location = screenData.shoppingListLocation,
            onClick = { uid, completed ->
                val event = if (completed) {
                    ProductsEvent.ActiveProduct(uid)
                } else {
                    ProductsEvent.CompleteProduct(uid)
                }
                viewModel.onEvent(event)
            },
            onLongClick = {
                val event = ProductsEvent.ShowProductMenu(it)
                viewModel.onEvent(event)
            }
        )
    }
}

@Composable
private fun ProductsNameContent(
    nameText: UiText,
    color: Color,
    fontSize: FontSize
) {
    Text(
        modifier = Modifier.padding(ProductsNamePaddings),
        text = nameText.asCompose(),
        color = color,
        fontSize = fontSize.toHeader6().sp,
        style = MaterialTheme.typography.h6
    )
}

@Composable
private fun ProductsReminderContent(
    reminderText: UiText,
    fontSize: FontSize,
    onClick: () -> Unit
) {
    Column {
        TextButton(onClick = onClick) {
            Icon(
                modifier = Modifier.size(fontSize.toButton().dp),
                painter = painterResource(R.drawable.ic_all_reminder),
                contentDescription = "",
                tint = MaterialTheme.colors.primary.copy(ContentAlpha.medium)
            )
            Spacer(modifier = Modifier.size(ProductsReminderSpacerSize))
            Text(
                text = reminderText.asCompose(),
                fontSize = fontSize.toButton().sp
            )
        }
    }
}

@Composable
private fun ProductsTotalContent(
    modifier: Modifier = Modifier,
    displayTotal: DisplayTotal,
    totalText: UiText,
    fontSize: TextUnit,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    onSelected: (DisplayTotal) -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = { onExpanded(true) }
    ) {
        Text(
            text = totalText.asCompose(),
            fontSize = fontSize
        )

        AppDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpanded(false) },
            header = { Text(text = stringResource(R.string.products_header_displayTotal)) }
        ) {
            AppDropdownMenuItem(
                onClick = { onSelected(DisplayTotal.ALL) },
                text = { Text(text = stringResource(R.string.products_action_displayAllTotal)) },
                after = { CheckmarkAppCheckbox(checked = displayTotal == DisplayTotal.ALL) }
            )
            AppDropdownMenuItem(
                onClick = { onSelected(DisplayTotal.COMPLETED) },
                text = { Text(text = stringResource(R.string.products_action_displayCompletedTotal)) },
                after = { CheckmarkAppCheckbox(checked = displayTotal == DisplayTotal.COMPLETED) }
            )
            AppDropdownMenuItem(
                onClick = { onSelected(DisplayTotal.ACTIVE) },
                text = { Text(text = stringResource(R.string.products_action_displayActiveTotal)) },
                after = { CheckmarkAppCheckbox(checked = displayTotal == DisplayTotal.ACTIVE) }
            )
        }
    }
}

@Composable
private fun ProductsGrid(
    modifier: Modifier = Modifier,
    multiColumns: Boolean,
    smartphoneScreen: Boolean,
    items: List<ProductItem>,
    fontSize: FontSize,
    dropdownMenu: @Composable ((String) -> Unit)? = null,
    completedWithCheckbox: Boolean,
    location: ShoppingListLocation?,
    onClick: (String, Boolean) -> Unit,
    onLongClick: (String) -> Unit
) {
    SmartphoneTabletAppGrid(
        modifier = modifier,
        multiColumns = multiColumns,
        smartphoneScreen = smartphoneScreen
    ) {
        items.forEach { item ->
            val beforeOnClick: ((Boolean) -> Unit)? = if (location != ShoppingListLocation.TRASH && completedWithCheckbox) {
                { onClick(item.uid, item.completed) }
            } else {
                null
            }

            val clickableEnabled = location != ShoppingListLocation.TRASH && !completedWithCheckbox

            val longClickableEnabled = location != ShoppingListLocation.TRASH

            AppMultiColumnsItem(
                multiColumns = multiColumns,
                before = getProductItemBefore(item.completed, beforeOnClick),
                title = getProductItemTitleOrNull(item.nameText, fontSize),
                body = getProductItemBodyOrNull(item.bodyText, fontSize),
                dropdownMenu = { dropdownMenu?.let { it(item.uid) } },
                clickableEnabled = clickableEnabled,
                longClickableEnabled = longClickableEnabled,
                onClick = { onClick(item.uid, item.completed) },
                onLongClick = { onLongClick(item.uid) },
                backgroundColor = if (item.completed) {
                    MaterialTheme.colors.background
                } else {
                    MaterialTheme.colors.surface
                }
            )
        }
    }
}

@Composable
private fun getProductItemBefore(
    completed: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?
): @Composable () -> Unit = {
    AppCheckbox(
        checked = completed,
        onCheckedChange = onCheckedChange,
        colors = CheckboxDefaults.colors(
            checkedColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
        )
    )
}

@Composable
private fun getProductItemTitleOrNull(
    text: UiText,
    fontSize: FontSize
) = itemOrNull(enabled = text.asCompose().isNotEmpty()) {
    Text(
        text = text.asCompose(),
        fontSize = fontSize.toItemTitle().sp
    )
}

@Composable
private fun getProductItemBodyOrNull(
    text: UiText,
    fontSize: FontSize
) = itemOrNull(enabled = text.asCompose().isNotEmpty()) {
    Text(
        text = text.asCompose(),
        fontSize = fontSize.toItemBody().sp
    )
}

private val ProductsReminderSpacerSize = 4.dp
private val ProductsNameWithoutReminderSpacerSize = 8.dp
private val ProductsHiddenProductsPaddings = PaddingValues(
    start = 8.dp,
    top = 8.dp,
    end = 8.dp
)
private val ProductsNamePaddings = PaddingValues(horizontal = 8.dp)
private val ProductsGridBarPaddings = PaddingValues(all = 8.dp)
package ru.sokolovromann.myshopping.feature.carts.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.ui.component.NavigationDrawer
import ru.sokolovromann.myshopping.core.ui.component.SimpleScaffold
import ru.sokolovromann.myshopping.core.ui.component.isCloseableNavigationDrawer
import ru.sokolovromann.myshopping.core.ui.component.rememberDefaultDrawerState
import ru.sokolovromann.myshopping.core.ui.model.NavigationItem
import ru.sokolovromann.myshopping.feature.carts.model.PurchasesState
import ru.sokolovromann.myshopping.feature.carts.viewmodel.PurchasesViewModel

@Composable
fun PurchasesScreen(purchasesViewModel: PurchasesViewModel = hiltViewModel()) {
    val purchasesState by purchasesViewModel.purchasesState.collectAsStateWithLifecycle()
    PurchasesScreenImpl(
        onNavigationItemSelected = { purchasesViewModel.onNavigationItemSelected(it) },
        onBackClick = { purchasesViewModel.onBackClick() },
        onCartItemClick = { purchasesViewModel.onCartItemClick(it) },
        onCartItemLongClick = { purchasesViewModel.onCartItemLongClick(it) },
        purchasesState = purchasesState
    )
}

@Composable
private fun PurchasesScreenImpl(
    onNavigationItemSelected: (NavigationItem) -> Unit,
    onBackClick: () -> Unit,
    onCartItemClick: (UID) -> Unit,
    onCartItemLongClick: (UID) -> Unit,
    purchasesState: PurchasesState
) {
    val drawerState = rememberDefaultDrawerState()
    val isCloseableNavigationDrawer = isCloseableNavigationDrawer()
    val scope = rememberCoroutineScope()
    fun openDrawer() = scope.launch { drawerState.open() }
    fun closeDrawer() = scope.launch { drawerState.close() }

    NavigationDrawer(
        drawerState = drawerState,
        initialItem = NavigationItem.Purchases,
        onItemClick = {
            if (isCloseableNavigationDrawer) closeDrawer()
            onNavigationItemSelected(it)
        }
    ) {
        SimpleScaffold(
            topBar = {
                PurchasesTopAppBar(
                    onNavigationIconClick = { openDrawer() }
                )
            },
            onBackClick = {
                if (isCloseableNavigationDrawer && drawerState.isOpen) {
                    closeDrawer()
                } else {
                    onBackClick()
                }
            },
            content = {
                PurchasesContent(
                    onItemClick = onCartItemClick,
                    onItemLongClick = onCartItemLongClick,
                    purchasesState = purchasesState
                )
            }
        )
    }
}
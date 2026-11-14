package ru.sokolovromann.myshopping.feature.carts.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.sokolovromann.myshopping.core.ui.component.NavigationTopAppBar
import ru.sokolovromann.myshopping.feature.carts.R

@Composable
fun PurchasesTopAppBar(
    onNavigationIconClick: () -> Unit
) {
    PurchasesNavigationTopAppBar(onNavigationIconClick)
}

@Composable
private fun PurchasesNavigationTopAppBar(
    onNavigationIconClick: () -> Unit
) {
    NavigationTopAppBar(
        header = stringResource(R.string.purchases_header),
        onNavigationIconClick = onNavigationIconClick
    )
}
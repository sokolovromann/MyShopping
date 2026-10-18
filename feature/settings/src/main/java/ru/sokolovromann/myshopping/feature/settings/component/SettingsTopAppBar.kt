package ru.sokolovromann.myshopping.feature.settings.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.sokolovromann.myshopping.core.ui.component.NavigationTopAppBar
import ru.sokolovromann.myshopping.feature.settings.R

@Composable
fun SettingsNavigationTopAppBar(onNavigationIconClick: () -> Unit) {
    NavigationTopAppBar(
        header = stringResource(R.string.settings_header),
        onNavigationIconClick = onNavigationIconClick
    )
}
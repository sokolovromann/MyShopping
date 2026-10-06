package ru.sokolovromann.myshopping.feature.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.core.ui.component.LoadingContent
import ru.sokolovromann.myshopping.core.ui.component.NavigationDrawer
import ru.sokolovromann.myshopping.core.ui.component.NavigationTopAppBar
import ru.sokolovromann.myshopping.core.ui.component.SimpleScaffold
import ru.sokolovromann.myshopping.core.ui.component.isCloseableNavigationDrawer
import ru.sokolovromann.myshopping.core.ui.component.rememberDefaultDrawerState
import ru.sokolovromann.myshopping.core.ui.model.NavigationItem

@Composable
fun AboutScreen(viewModel: AboutViewModel = hiltViewModel()) {
    val aboutState by viewModel.aboutState.collectAsStateWithLifecycle()
    AboutScreenImpl(
        onNavigationItemSelected = { viewModel.onNavigationItemSelected(it) },
        onBackClick = { viewModel.onBackClick() },
        state = aboutState
    )
}

@Composable
private fun AboutScreenImpl(
    onNavigationItemSelected: (NavigationItem) -> Unit,
    onBackClick: () -> Unit,
    state: AboutState
) {
    val drawerState = rememberDefaultDrawerState()
    val isCloseableNavigationDrawer = isCloseableNavigationDrawer()
    val scope = rememberCoroutineScope()
    fun openDrawer() = scope.launch { drawerState.open() }
    fun closeDrawer() = scope.launch { drawerState.close() }

    NavigationDrawer(
        drawerState = drawerState,
        initialItem = NavigationItem.About,
        onItemClick = {
            if (isCloseableNavigationDrawer) closeDrawer()
            onNavigationItemSelected(it)
        }
    ) {
        SimpleScaffold(
            topBar = {
                NavigationTopAppBar(
                    header = stringResource(R.string.about_header),
                    onNavigationIconClick = { openDrawer() }
                )
            },
            onBackClick = onBackClick,
            content = {
                if (state.isLoading) {
                    LoadingContent()
                } else {
                    AboutContent(state.apiName)
                }
            }
        )
    }
}
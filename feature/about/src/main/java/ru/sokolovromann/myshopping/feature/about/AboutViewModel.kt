package ru.sokolovromann.myshopping.feature.about

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.sokolovromann.myshopping.core.domain.repository.BuildInfo
import ru.sokolovromann.myshopping.core.navigation.Navigator
import ru.sokolovromann.myshopping.core.navigation.Screen
import ru.sokolovromann.myshopping.core.ui.extension.toUiText
import ru.sokolovromann.myshopping.core.ui.model.NavigationItem

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val buildInfo: BuildInfo,
    private val navigator: Navigator
) : ViewModel() {

    private val _aboutState: MutableStateFlow<AboutState> = MutableStateFlow(AboutState())
    val aboutState: StateFlow<AboutState> = _aboutState

    init { onInit() }

    fun onNavigationItemSelected(navigationItem: NavigationItem) = when (navigationItem) {
        NavigationItem.Purchases -> navigator.navigateTo(Screen.Purchases)
        NavigationItem.Archive -> navigator.navigateTo(Screen.Archive)
        NavigationItem.Trash -> navigator.navigateTo(Screen.Trash)
        NavigationItem.Dictionary -> navigator.navigateTo(Screen.Dictionary)
        NavigationItem.Settings -> navigator.navigateTo(Screen.Settings)
        NavigationItem.About -> {}
    }

    fun onBackClick() {
        navigator.navigateBack()
    }

    private fun onInit() {
        val state = AboutState(
            isLoading = false,
            apiName = buildInfo.getApiName().toUiText()
        )
        _aboutState.tryEmit(state)
    }
}
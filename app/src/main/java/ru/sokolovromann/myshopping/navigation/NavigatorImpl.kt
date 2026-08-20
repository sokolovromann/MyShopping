package ru.sokolovromann.myshopping.navigation

import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.sokolovromann.myshopping.core.navigation.Navigator
import ru.sokolovromann.myshopping.core.navigation.Screen

@Singleton
class NavigatorImpl @Inject constructor() : Navigator {

    private val _navigationActions = MutableSharedFlow<NavigationAction>(extraBufferCapacity = 1)
    val navigationActions = _navigationActions.asSharedFlow()

    override fun navigateTo(screen: Screen) {
        _navigationActions.tryEmit(NavigationAction.NavigateTo(screen))
    }

    override fun navigateBack() {
        _navigationActions.tryEmit(NavigationAction.PopBackStack)
    }
}
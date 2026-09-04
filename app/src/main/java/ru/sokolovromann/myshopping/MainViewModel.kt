package ru.sokolovromann.myshopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.MainDispatcher
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.Theme
import ru.sokolovromann.myshopping.core.domain.usecase.ObserveGeneralPreferencesUseCase
import ru.sokolovromann.myshopping.core.ui.theme.MyShoppingThemeFontSize
import ru.sokolovromann.myshopping.core.ui.theme.MyShoppingThemeType

@HiltViewModel
class MainViewModel @Inject constructor(
    private val observeGeneralPreferencesUseCase: ObserveGeneralPreferencesUseCase,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    navigator: NavigatorImpl
) : ViewModel() {

    private val _mainState: MutableStateFlow<MainState> = MutableStateFlow(MainState())
    val mainState: StateFlow<MainState> = _mainState

    val navigationActions = navigator.navigationActions

    init { onInit() }

    private fun onInit() = viewModelScope.launch {
        observeGeneralPreferencesUseCase().collect { generalPreferences ->
            withContext(mainDispatcher) {
                val state = createMainState(generalPreferences)
                _mainState.tryEmit(state)
            }
        }
    }

    private fun createMainState(generalPreferences: GeneralPreferences) = MainState(
        isWaiting = false,
        themeType = when (generalPreferences.theme) {
            Theme.Default -> MyShoppingThemeType.Dynamic
            Theme.Light -> MyShoppingThemeType.Light
            Theme.Dark -> MyShoppingThemeType.Dark
        },
        themeFontSize = when (generalPreferences.fontSize) {
            FontSize.Small -> MyShoppingThemeFontSize.Small
            FontSize.Medium -> MyShoppingThemeFontSize.Medium
            FontSize.Large -> MyShoppingThemeFontSize.Large
            FontSize.ExtraLarge -> MyShoppingThemeFontSize.ExtraLarge
            FontSize.Huge -> MyShoppingThemeFontSize.Huge
            FontSize.ExtraHuge -> MyShoppingThemeFontSize.ExtraHuge
        }
    )
}
package ru.sokolovromann.myshopping.data.repository.model

import ru.sokolovromann.myshopping.data.exception.InvalidNameException
import ru.sokolovromann.myshopping.data.model.AppConfig

@Deprecated("Use SettingsWithConfig")
data class EditCurrencySymbol(
    private val appConfig: AppConfig = AppConfig()
) {

    private val userPreferences = appConfig.userPreferences

    fun createSymbol(symbol: String?): Result<String> {
        return if (symbol.isNullOrEmpty()) {
            val exception = InvalidNameException("Symbol must not be null or empty")
            Result.failure(exception)
        } else {
            return Result.success(symbol)
        }
    }

    fun getFieldSymbol(): String {
        return userPreferences.currency.symbol
    }

    fun getFontSize(): FontSize {
        return userPreferences.fontSize
    }
}
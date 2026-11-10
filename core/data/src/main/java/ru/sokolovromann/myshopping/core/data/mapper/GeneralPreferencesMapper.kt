package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.datasource.LocalDataStoreScheme
import ru.sokolovromann.myshopping.core.domain.model.Currency
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.MoneyFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.Theme
import ru.sokolovromann.myshopping.core.domain.utils.EnumUtils
import kotlin.text.orEmpty

@Singleton
class GeneralPreferencesMapper @Inject constructor() : DataStoreMapper<GeneralPreferences>() {

    override fun toModel(preferences: Preferences) = GeneralPreferences(
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.General.THEME],
            Theme.Default
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.General.FONT_SIZE],
            FontSize.Medium
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.General.MONEY_FORMATTING_MODE],
            MoneyFormattingMode.Simple
        ),
        toCurrency(
            preferences[LocalDataStoreScheme.General.CURRENCY],
            preferences[LocalDataStoreScheme.General.CURRENCY_DISPLAY_SIDE]
        )
    )

    override fun toPreferences(model: GeneralPreferences) = preferencesOf(
        LocalDataStoreScheme.General.THEME
                to model.theme.toString(),
        LocalDataStoreScheme.General.FONT_SIZE
                to model.fontSize.toString(),
        LocalDataStoreScheme.General.MONEY_FORMATTING_MODE
                to model.moneyFormattingMode.toString(),
        LocalDataStoreScheme.General.CURRENCY
                to model.currency.getSign(),
        LocalDataStoreScheme.General.CURRENCY_DISPLAY_SIDE
                to model.currency.javaClass.simpleName
    )

    fun toCurrency(sign: String?, displaySide: String?): Currency {
        val currencySign = sign.orEmpty()
        return when (displaySide) {
            "Left" -> Currency.Left(currencySign)
            "Right" -> Currency.Right(currencySign)
            else -> Currency.Right(currencySign)
        }
    }
}
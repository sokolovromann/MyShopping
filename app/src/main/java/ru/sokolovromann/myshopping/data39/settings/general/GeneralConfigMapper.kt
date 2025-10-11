package ru.sokolovromann.myshopping.data39.settings.general

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.data39.Mapper
import ru.sokolovromann.myshopping.utils.Decimal
import ru.sokolovromann.myshopping.utils.DecimalFormatter
import ru.sokolovromann.myshopping.utils.DecimalFormattingMode
import ru.sokolovromann.myshopping.utils.EnumExtensions
import javax.inject.Inject

class GeneralConfigMapper @Inject constructor() : Mapper<Preferences, GeneralConfig>() {

    override fun mapEntityTo(entity: Preferences): GeneralConfig {
        return GeneralConfig(
            theme = mapThemeTo(entity),
            fontSize = mapFontSizeTo(entity),
            dateTime = mapDateTimeTo(entity),
            money = mapMoneyTo(entity)
        )
    }

    override fun mapEntityFrom(model: GeneralConfig): Preferences {
        return mutablePreferencesOf().apply {
            val theme = mapThemeFrom(model.theme)
            plusAssign(theme)

            val fontSize = mapFontSizeFrom(model.fontSize)
            plusAssign(fontSize)

            val dateTime = mapDateTimeFrom(model.dateTime)
            plusAssign(dateTime)

            val money = mapMoneyFrom(model.money)
            plusAssign(money)
        }
    }

    fun mapThemeTo(entity: Preferences): Theme {
        return EnumExtensions.valueOfOrDefault(
            entity[GeneralConfigScheme.THEME],
            GeneralConfigDefaults.THEME
        )
    }

    fun mapThemeFrom(model: Theme): Preferences {
        return preferencesOf(GeneralConfigScheme.THEME to model.name)
    }

    fun mapFontSizeTo(entity: Preferences): FontSize {
        return EnumExtensions.valueOfOrDefault(
            entity[GeneralConfigScheme.FONT_SIZE],
            GeneralConfigDefaults.FONT_SIZE
        )
    }

    fun mapFontSizeFrom(model: FontSize): Preferences {
        return preferencesOf(GeneralConfigScheme.FONT_SIZE to model.name)
    }

    fun mapDateTimeTo(entity: Preferences): DateTimeConfig {
        val default = GeneralConfigDefaults.DATE_TIME
        return DateTimeConfig(
            dateFormattingMode = EnumExtensions.valueOfOrDefault(
                entity[GeneralConfigScheme.DATE_FORMATTING_MODE],
                default.dateFormattingMode
            ),
            timeFormattingMode = EnumExtensions.valueOfOrDefault(
                entity[GeneralConfigScheme.TIME_FORMATTING_MODE],
                default.timeFormattingMode
            ),
        )
    }

    fun mapDateTimeFrom(model: DateTimeConfig): Preferences {
        return preferencesOf(
            GeneralConfigScheme.DATE_FORMATTING_MODE to model.dateFormattingMode.name,
            GeneralConfigScheme.TIME_FORMATTING_MODE to model.dateFormattingMode.name
        )
    }

    fun mapMoneyTo(entity: Preferences): MoneyConfig {
        val defaultMoney = GeneralConfigDefaults.MONEY
        val currencyDisplaySide: CurrencyDisplaySide = EnumExtensions.valueOfOrDefault(
            entity[GeneralConfigScheme.CURRENCY_DISPLAY_SIDE],
            defaultMoney.currency.displaySide
        )
        return MoneyConfig(
            formattingMode = EnumExtensions.valueOfOrDefault(
                entity[GeneralConfigScheme.MONEY_FORMATTING_MODE],
                defaultMoney.formattingMode
            ),
            currency = Currency(
                entity[GeneralConfigScheme.CURRENCY_SYMBOL] ?: defaultMoney.currency.symbol,
                currencyDisplaySide
            ),
            taxRate = Decimal.createOrDefault(
                entity[GeneralConfigScheme.TAX_RATE],
                defaultMoney.taxRate
            )
        )
    }

    fun mapMoneyFrom(model: MoneyConfig): Preferences {
        val taxRate = DecimalFormatter(DecimalFormattingMode.Percent)
            .getString(model.taxRate)
        return preferencesOf(
            GeneralConfigScheme.MONEY_FORMATTING_MODE to model.formattingMode.name,
            GeneralConfigScheme.CURRENCY_SYMBOL to model.currency.symbol,
            GeneralConfigScheme.CURRENCY_DISPLAY_SIDE to model.currency.displaySide.name,
            GeneralConfigScheme.TAX_RATE to taxRate
        )
    }
}
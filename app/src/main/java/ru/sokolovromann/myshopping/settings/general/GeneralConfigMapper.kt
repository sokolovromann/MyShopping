package ru.sokolovromann.myshopping.settings.general

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.io.TwoSidedMapper
import ru.sokolovromann.myshopping.utils.Decimal
import ru.sokolovromann.myshopping.utils.DecimalFormatter
import ru.sokolovromann.myshopping.utils.DecimalFormattingMode
import ru.sokolovromann.myshopping.utils.EnumExtensions
import javax.inject.Inject

class GeneralConfigMapper @Inject constructor() : TwoSidedMapper<Preferences, GeneralConfig>() {

    override fun mapTo(a: Preferences): GeneralConfig {
        val theme: Theme = EnumExtensions.valueOfOrDefault(
            name = a[GeneralConfigScheme.THEME],
            defaultValue = GeneralConfigDefaults.THEME
        )
        val fontSize: FontSize = EnumExtensions.valueOfOrDefault(
            name = a[GeneralConfigScheme.FONT_SIZE],
            defaultValue = GeneralConfigDefaults.FONT_SIZE
        )
        val dateTime = DateTimeConfig(
            dateFormattingMode = EnumExtensions.valueOfOrDefault(
                name = a[GeneralConfigScheme.DATE_FORMATTING_MODE],
                defaultValue = GeneralConfigDefaults.DATE_TIME.dateFormattingMode
            ),
            timeFormattingMode = EnumExtensions.valueOfOrDefault(
                name = a[GeneralConfigScheme.TIME_FORMATTING_MODE],
                defaultValue = GeneralConfigDefaults.DATE_TIME.timeFormattingMode
            ),
        )
        val defaultMoney = GeneralConfigDefaults.MONEY
        val currencyDisplaySide: CurrencyDisplaySide = EnumExtensions.valueOfOrDefault(
            name = a[GeneralConfigScheme.CURRENCY_DISPLAY_SIDE],
            defaultValue = defaultMoney.currency.displaySide
        )
        val money = MoneyConfig(
            formattingMode = EnumExtensions.valueOfOrDefault(
                name = a[GeneralConfigScheme.MONEY_FORMATTING_MODE],
                defaultValue = defaultMoney.formattingMode
            ),
            currency = Currency(
                symbol = a[GeneralConfigScheme.CURRENCY_SYMBOL] ?: defaultMoney.currency.symbol,
                displaySide = currencyDisplaySide
            ),
            taxRate = TaxRate(
                Decimal.createOrDefault(
                    value = a[GeneralConfigScheme.TAX_RATE],
                    defaultValue = defaultMoney.taxRate.value
                )
            )
        )
        return GeneralConfig(
            theme = theme,
            fontSize = fontSize,
            dateTime = dateTime,
            money = money
        )
    }

    override fun mapFrom(b: GeneralConfig): Preferences {
        val taxRate = DecimalFormatter(DecimalFormattingMode.Percent)
            .getString(b.money.taxRate.value)
        return preferencesOf(
            GeneralConfigScheme.THEME to b.theme.name,
            GeneralConfigScheme.FONT_SIZE to b.fontSize.name,
            GeneralConfigScheme.DATE_FORMATTING_MODE to b.dateTime.dateFormattingMode.name,
            GeneralConfigScheme.TIME_FORMATTING_MODE to b.dateTime.dateFormattingMode.name,
            GeneralConfigScheme.MONEY_FORMATTING_MODE to b.money.formattingMode.name,
            GeneralConfigScheme.CURRENCY_SYMBOL to b.money.currency.symbol,
            GeneralConfigScheme.CURRENCY_DISPLAY_SIDE to b.money.currency.displaySide.name,
            GeneralConfigScheme.TAX_RATE to taxRate,
        )
    }
}
package ru.sokolovromann.myshopping.data.model.mapper

import ru.sokolovromann.myshopping.data39.old.Api15AutocompleteEntity
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.AutocompleteWithConfig
import ru.sokolovromann.myshopping.data.model.AutocompletesWithConfig
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.Quantity
import ru.sokolovromann.myshopping.data.model.DateTime
import ru.sokolovromann.myshopping.data.model.UserPreferences

object AutocompletesMapper {

    fun toAutocompleteEntity(autocomplete: Autocomplete): Api15AutocompleteEntity {
        return Api15AutocompleteEntity(
            id = autocomplete.id,
            uid = autocomplete.uid,
            lastModified = autocomplete.lastModified.millis,
            name = autocomplete.name,
            quantity = autocomplete.quantity.value.toFloat(),
            quantitySymbol = autocomplete.quantity.symbol,
            price = autocomplete.price.value.toFloat(),
            discount = autocomplete.discount.value.toFloat(),
            discountAsPercent = autocomplete.discount.asPercent,
            taxRate = autocomplete.taxRate.value.toFloat(),
            taxRateAsPercent = autocomplete.taxRate.asPercent,
            total = autocomplete.total.value.toFloat(),
            manufacturer = autocomplete.manufacturer,
            brand = autocomplete.brand,
            size = autocomplete.size,
            color = autocomplete.color,
            provider = autocomplete.provider,
            personal = autocomplete.personal,
            language = autocomplete.language
        )
    }

    fun toAutocompleteEntities(autocompletes: List<Autocomplete>): List<Api15AutocompleteEntity> {
        return autocompletes.map { toAutocompleteEntity(it) }
    }

    fun toAutocompleteWithConfig(appConfig: AppConfig): AutocompleteWithConfig {
        return AutocompleteWithConfig(
            autocomplete = Autocomplete(),
            appConfig = appConfig
        )
    }

    fun toAutocompleteWithConfig(
        entity: Api15AutocompleteEntity,
        appConfig: AppConfig
    ): AutocompleteWithConfig {
        return AutocompleteWithConfig(
            autocomplete = toAutocomplete(entity, appConfig.userPreferences),
            appConfig = appConfig
        )
    }

    fun toAutocompletesWithConfig(
        entities: List<Api15AutocompleteEntity>,
        appConfig: AppConfig
    ): AutocompletesWithConfig {
        val autocompletes = toAutocompletes(entities, null, appConfig, null)
        return AutocompletesWithConfig(
            autocompletes = autocompletes,
            appConfig = appConfig
        )
    }

    fun toAutocompletesWithConfig(
        entities: List<Api15AutocompleteEntity>,
        resources: List<String>?,
        appConfig: AppConfig,
        language: String?
    ): AutocompletesWithConfig {
        val autocompletes = toAutocompletes(entities, resources, appConfig, language)
        return AutocompletesWithConfig(
            autocompletes = autocompletes,
            appConfig = appConfig
        )
    }

    fun toAutocompletes(
        entities: List<Api15AutocompleteEntity>,
        resources: List<String>?,
        appConfig: AppConfig,
        language: String?
    ): List<Autocomplete> {
        val autocompletes = if (language == null) { entities } else {
            val default = entities.filter { !it.personal && it.language == language }
            val personal = entities.filter { it.personal }

            mutableListOf<Api15AutocompleteEntity>().apply {
                addAll(default)
                addAll(personal)
            }
        }
            .map { toAutocomplete(it, appConfig.userPreferences) }
            .toMutableList()

        resources?.forEach {
            val autocomplete = Autocomplete(
                name = it,
                personal = false
            )
            autocompletes.add(autocomplete)
        }

        return autocompletes.toList()
    }

    private fun toAutocomplete(entity: Api15AutocompleteEntity, userPreferences: UserPreferences): Autocomplete {
        return Autocomplete(
            id = entity.id,
            position = entity.id,
            uid = entity.uid,
            lastModified = DateTime(entity.lastModified),
            name = entity.name,
            quantity = Quantity(
                value = entity.quantity.toBigDecimal(),
                symbol = entity.quantitySymbol,
                decimalFormat = userPreferences.quantityDecimalFormat
            ),
            price = Money(
                value = entity.price.toBigDecimal(),
                currency = userPreferences.currency,
                asPercent = false,
                decimalFormat = userPreferences.moneyDecimalFormat
            ),
            discount = Money(
                value = entity.discount.toBigDecimal(),
                currency = userPreferences.currency,
                asPercent = entity.discountAsPercent,
                decimalFormat = userPreferences.moneyDecimalFormat
            ),
            taxRate = Money(
                value = entity.taxRate.toBigDecimal(),
                currency = userPreferences.currency,
                asPercent = entity.taxRateAsPercent,
                decimalFormat = userPreferences.moneyDecimalFormat
            ),
            total = Money(
                value = entity.total.toBigDecimal(),
                currency = userPreferences.currency,
                asPercent = false,
                decimalFormat = userPreferences.moneyDecimalFormat
            ),
            manufacturer = entity.manufacturer,
            brand = entity.brand,
            size = entity.size,
            color = entity.color,
            provider = entity.provider,
            personal = entity.personal,
            language = entity.language
        )
    }
}
package ru.sokolovromann.myshopping.data.model.mapper

import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.AutocompleteWithConfig
import ru.sokolovromann.myshopping.data.model.AutocompletesWithConfig
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.data.repository.model.Quantity
import ru.sokolovromann.myshopping.data.repository.model.Time
import ru.sokolovromann.myshopping.data.model.UserPreferences

object AutocompletesMapper {

    fun toAutocomplete(autocomplete: ru.sokolovromann.myshopping.data.repository.model.Autocomplete): Autocomplete {
        return Autocomplete(
            id = autocomplete.id,
            position = autocomplete.id,
            uid = autocomplete.uid,
            lastModified = Time(autocomplete.lastModified),
            name = autocomplete.name,
            quantity = autocomplete.quantity,
            price = autocomplete.price,
            discount = autocomplete.discount,
            taxRate = autocomplete.taxRate,
            total = autocomplete.total,
            manufacturer = autocomplete.manufacturer,
            brand = autocomplete.brand,
            size = autocomplete.size,
            color = autocomplete.color,
            provider = autocomplete.provider,
            personal = autocomplete.personal,
            language = autocomplete.language
        )
    }

    fun toAutocompletes(autocompletes: List<ru.sokolovromann.myshopping.data.repository.model.Autocomplete>): List<Autocomplete> {
        return autocompletes.map { toAutocomplete(it) }
    }

    fun toRepositoryAutocompletes(autocompletes: List<Autocomplete>): List<ru.sokolovromann.myshopping.data.repository.model.Autocomplete> {
        return autocompletes.map {
            ru.sokolovromann.myshopping.data.repository.model.Autocomplete(
                id = it.id,
                uid = it.uid,
                created = Time.NO_TIME.millis,
                lastModified = it.lastModified.millis,
                name = it.name,
                quantity = it.quantity,
                price = it.price,
                discount = it.discount,
                taxRate = it.taxRate,
                total = it.total,
                manufacturer = it.manufacturer,
                brand = it.brand,
                size = it.size,
                color = it.color,
                provider = it.provider,
                personal = it.personal
            )
        }
    }

    fun toAutocompleteEntity(autocomplete: Autocomplete): AutocompleteEntity {
        return AutocompleteEntity(
            id = autocomplete.id,
            uid = autocomplete.uid,
            created = Time.NO_TIME.millis,
            lastModified = autocomplete.lastModified.millis,
            name = autocomplete.name,
            quantity = autocomplete.quantity.value,
            quantitySymbol = autocomplete.quantity.symbol,
            price = autocomplete.price.value,
            discount = autocomplete.discount.value,
            discountAsPercent = autocomplete.discount.asPercent,
            taxRate = autocomplete.taxRate.value,
            taxRateAsPercent = autocomplete.taxRate.asPercent,
            total = autocomplete.total.value,
            manufacturer = autocomplete.manufacturer,
            brand = autocomplete.brand,
            size = autocomplete.size,
            color = autocomplete.color,
            provider = autocomplete.provider,
            personal = autocomplete.personal,
            language = autocomplete.language
        )
    }

    fun toAutocompleteEntities(autocompletes: List<Autocomplete>): List<AutocompleteEntity> {
        return autocompletes.map { toAutocompleteEntity(it) }
    }

    fun toAutocompleteWithConfig(appConfig: AppConfig): AutocompleteWithConfig {
        return AutocompleteWithConfig(
            autocomplete = Autocomplete(),
            appConfig = appConfig
        )
    }

    fun toAutocompleteWithConfig(
        entity: AutocompleteEntity,
        appConfig: AppConfig
    ): AutocompleteWithConfig {
        return AutocompleteWithConfig(
            autocomplete = toAutocomplete(entity, appConfig.userPreferences),
            appConfig = appConfig
        )
    }

    fun toAutocompletesWithConfig(
        entities: List<AutocompleteEntity>,
        appConfig: AppConfig
    ): AutocompletesWithConfig {
        val autocompletes = toAutocompletes(entities, null, appConfig, null)
        return AutocompletesWithConfig(
            autocompletes = autocompletes,
            appConfig = appConfig
        )
    }

    fun toAutocompletesWithConfig(
        entities: List<AutocompleteEntity>,
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
        entities: List<AutocompleteEntity>,
        resources: List<String>?,
        appConfig: AppConfig,
        language: String?
    ): List<Autocomplete> {
        val autocompletes = if (language == null) { entities } else {
            val default = entities.filter { !it.personal && it.language == language }
            val personal = entities.filter { it.personal }

            mutableListOf<AutocompleteEntity>().apply {
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

    private fun toAutocomplete(entity: AutocompleteEntity, userPreferences: UserPreferences): Autocomplete {
        return Autocomplete(
            id = entity.id,
            position = entity.id,
            uid = entity.uid,
            lastModified = Time(entity.lastModified),
            name = entity.name,
            quantity = Quantity(
                value = entity.quantity,
                symbol = entity.quantitySymbol,
                decimalFormat = userPreferences.quantityDecimalFormat
            ),
            price = Money(
                value = entity.price,
                currency = userPreferences.currency,
                asPercent = false,
                decimalFormat = userPreferences.moneyDecimalFormat
            ),
            discount = Money(
                value = entity.discount,
                currency = userPreferences.currency,
                asPercent = entity.discountAsPercent,
                decimalFormat = userPreferences.moneyDecimalFormat
            ),
            taxRate = Money(
                value = entity.taxRate,
                currency = userPreferences.currency,
                asPercent = entity.taxRateAsPercent,
                decimalFormat = userPreferences.moneyDecimalFormat
            ),
            total = Money(
                value = entity.total,
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
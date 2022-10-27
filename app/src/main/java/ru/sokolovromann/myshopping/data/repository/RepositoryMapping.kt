package ru.sokolovromann.myshopping.data.repository

import ru.sokolovromann.myshopping.BuildConfig
import ru.sokolovromann.myshopping.data.local.entity.*
import ru.sokolovromann.myshopping.data.repository.model.*
import javax.inject.Inject

class RepositoryMapping @Inject constructor() {

    fun toShoppingEntity(shoppingList: ShoppingList): ShoppingEntity {
        return ShoppingEntity(
            uid = shoppingList.uid,
            created = shoppingList.created,
            lastModified = shoppingList.lastModified,
            name = shoppingList.name,
            reminder = toReminderEntity(shoppingList.reminder),
            archived = shoppingList.archived,
            deleted = shoppingList.deleted
        )
    }

    fun toShoppingList(shoppingListEntity: ShoppingListEntity, preferencesEntity: ShoppingPreferencesEntity): ShoppingList {
        val entity = shoppingListEntity.shoppingEntity
        return ShoppingList(
            uid = entity.uid,
            created = entity.created,
            lastModified = entity.lastModified,
            reminder = toReminder(entity.reminder),
            archived = entity.archived,
            deleted = entity.deleted,
            products = shoppingListEntity.productEntities.map { toProduct(it, preferencesEntity) },
            currency = toCurrency(preferencesEntity.currency, preferencesEntity.currencyDisplayToLeft),
            displayTotal = toDisplayTotal(preferencesEntity.displayTotal)
        )
    }

    fun toShoppingList(shoppingListEntity: ShoppingListEntity, preferencesEntity: ProductPreferencesEntity): ShoppingList {
        val entity = shoppingListEntity.shoppingEntity
        return ShoppingList(
            uid = entity.uid,
            created = entity.created,
            lastModified = entity.lastModified,
            reminder = toReminder(entity.reminder),
            archived = entity.archived,
            deleted = entity.deleted,
            products = shoppingListEntity.productEntities.map { toProduct(it, preferencesEntity) },
            currency = toCurrency(preferencesEntity.currency, preferencesEntity.currencyDisplayToLeft),
            displayTotal = toDisplayTotal(preferencesEntity.displayTotal)
        )
    }

    fun toShoppingListPreferences(entity: ShoppingPreferencesEntity): ShoppingListPreferences {
        return ShoppingListPreferences(
            currency = toCurrency(entity.currency, entity.currencyDisplayToLeft),
            taxRate = toTaxRate(entity.taxRate, entity.taxRateAsPercent),
            fontSize = toFontSize(entity.fontSize),
            firstLetterUppercase = entity.firstLetterUppercase,
            multiColumns = entity.multiColumns,
            sort = toSort(entity.sortBy, entity.sortAscending),
            displayMoney = entity.displayMoney,
            displayCompleted = toDisplayCompleted(entity.displayCompleted),
            displayTotal = toDisplayTotal(entity.displayTotal),
            screenSize = toScreenSize(entity.screenSize)
        )
    }

    fun toShoppingLists(
        entities: List<ShoppingListEntity>,
        preferencesEntity: ShoppingPreferencesEntity
    ): ShoppingLists {
        return ShoppingLists(
            shoppingLists = entities.map { toShoppingList(it, preferencesEntity) },
            preferences = toShoppingListPreferences(preferencesEntity)
        )
    }

    fun toShoppingListNotification(
        entity: ShoppingListEntity,
        preferencesEntity: ShoppingPreferencesEntity
    ): ShoppingListNotification {
        return ShoppingListNotification(
            shoppingList = toShoppingList(entity, preferencesEntity),
            preferences = toShoppingListPreferences(preferencesEntity)
        )
    }

    fun toShoppingListNotifications(
        entities: List<ShoppingListEntity>,
        preferencesEntity: ShoppingPreferencesEntity
    ): ShoppingListNotifications {
        return ShoppingListNotifications(
            shoppingLists = entities.map { toShoppingList(it, preferencesEntity) }
        )
    }

    fun toProductEntity(product: Product): ProductEntity {
        return ProductEntity(
            productUid = product.productUid,
            shoppingUid = product.shoppingUid,
            created = product.created,
            lastModified = product.lastModified,
            name = product.name,
            quantity = toQuantityValue(product.quantity),
            quantitySymbol = toQuantitySymbol(product.quantity),
            price = toMoneyValue(product.price),
            discount = toDiscountValue(product.discount),
            discountAsPercent = toDiscountAsPercent(product.discount),
            taxRate = toTaxRateValue(product.taxRate),
            taxRateAsPercent = toTaxRateAsPercent(product.taxRate),
            completed = product.completed
        )
    }

    fun toProduct(entity: ProductEntity, preferencesEntity: ShoppingPreferencesEntity): Product {
        return Product(
            productUid = entity.productUid,
            shoppingUid = entity.shoppingUid,
            created = entity.created,
            lastModified = entity.lastModified,
            name = entity.name,
            quantity = toQuantity(entity.quantity, entity.quantitySymbol),
            price = toMoney(
                entity.price,
                toCurrency(preferencesEntity.currency, preferencesEntity.currencyDisplayToLeft)
            ),
            discount = toDiscount(entity.discount, entity.discountAsPercent),
            taxRate = toTaxRate(entity.taxRate, entity.taxRateAsPercent),
            completed = entity.completed
        )
    }

    fun toProduct(entity: ProductEntity, preferencesEntity: ProductPreferencesEntity): Product {
        return Product(
            productUid = entity.productUid,
            shoppingUid = entity.shoppingUid,
            created = entity.created,
            lastModified = entity.lastModified,
            name = entity.name,
            quantity = toQuantity(entity.quantity, entity.quantitySymbol),
            price = toMoney(
                entity.price,
                toCurrency(preferencesEntity.currency, preferencesEntity.currencyDisplayToLeft)
            ),
            discount = toDiscount(entity.discount, entity.discountAsPercent),
            taxRate = toTaxRate(entity.taxRate, entity.taxRateAsPercent),
            completed = entity.completed
        )
    }

    fun toProductPreferences(entity: ProductPreferencesEntity): ProductPreferences {
        return ProductPreferences(
            currency = toCurrency(entity.currency, entity.currencyDisplayToLeft),
            taxRate = toTaxRate(entity.taxRate, entity.taxRateAsPercent),
            fontSize = toFontSize(entity.fontSize),
            firstLetterUppercase = entity.firstLetterUppercase,
            multiColumns = entity.multiColumns,
            sort = toSort(entity.sortBy, entity.sortAscending),
            displayMoney = entity.displayMoney,
            displayCompleted = toDisplayCompleted(entity.displayCompleted),
            displayTotal = toDisplayTotal(entity.displayTotal),
            displayAutocomplete = toDisplayAutocomplete(entity.displayAutocomplete),
            lockQuantity = entity.lockQuantity,
            addLastProduct = entity.addLastProduct,
            screenSize = toScreenSize(entity.screenSize)
        )
    }

    fun toProducts(entity: ShoppingListEntity, preferencesEntity: ProductPreferencesEntity): Products {
        return Products(
            shoppingList = toShoppingList(entity, preferencesEntity),
            preferences = toProductPreferences(preferencesEntity)
        )
    }

    fun toAddEditProduct(entity: ProductEntity, preferencesEntity: ProductPreferencesEntity): AddEditProduct {
        return AddEditProduct(
            product = toProduct(entity, preferencesEntity),
            preferences = toProductPreferences(preferencesEntity)
        )
    }

    fun toAddEditProductAutocomplete(
        entities: List<AutocompleteEntity>,
        resources: List<String>,
        preferencesEntity: ProductPreferencesEntity
    ): AddEditProductAutocomplete {
        val autocompletes: MutableList<Autocomplete> = entities
            .map { toAutocomplete(it, preferencesEntity) }
            .toMutableList()

        resources.forEach {
            val autocomplete = Autocomplete(name = it)
            autocompletes.add(autocomplete)
        }

        return AddEditProductAutocomplete(
            autocompletes = autocompletes,
            preferences = toProductPreferences(preferencesEntity)
        )
    }

    fun toAutocompleteEntity(autocomplete: Autocomplete): AutocompleteEntity {
        return AutocompleteEntity(
            uid = autocomplete.uid,
            created = autocomplete.created,
            lastModified = autocomplete.lastModified,
            name = autocomplete.name,
            quantity = toQuantityValue(autocomplete.quantity),
            quantitySymbol = toQuantitySymbol(autocomplete.quantity),
            price = toMoneyValue(autocomplete.price),
            discount = toDiscountValue(autocomplete.discount),
            discountAsPercent = toDiscountAsPercent(autocomplete.discount),
            taxRate = toTaxRateValue(autocomplete.taxRate),
            taxRateAsPercent = toTaxRateAsPercent(autocomplete.taxRate)
        )
    }

    fun toAutocomplete(entity: AutocompleteEntity, preferencesEntity: ProductPreferencesEntity): Autocomplete {
        return Autocomplete(
            uid = entity.uid,
            created = entity.created,
            lastModified = entity.lastModified,
            name = entity.name,
            quantity = toQuantity(entity.quantity, entity.quantitySymbol),
            price = toMoney(
                entity.price,
                toCurrency(preferencesEntity.currency, preferencesEntity.currencyDisplayToLeft)
            ),
            discount = toDiscount(entity.discount, entity.discountAsPercent),
            taxRate = toTaxRate(entity.taxRate, entity.taxRateAsPercent)
        )
    }

    fun toAutocomplete(entity: AutocompleteEntity, preferencesEntity: AutocompletePreferencesEntity): Autocomplete {
        return Autocomplete(
            uid = entity.uid,
            created = entity.created,
            lastModified = entity.lastModified,
            name = entity.name,
            quantity = toQuantity(entity.quantity, entity.quantitySymbol),
            price = toMoney(
                entity.price,
                toCurrency(preferencesEntity.currency, preferencesEntity.currencyDisplayToLeft)
            ),
            discount = toDiscount(entity.discount, entity.discountAsPercent),
            taxRate = toTaxRate(entity.taxRate, entity.taxRateAsPercent)
        )
    }

    fun toAutocompletePreferences(entity: AutocompletePreferencesEntity): AutocompletePreferences {
        return AutocompletePreferences(
            currency = toCurrency(entity.currency, entity.currencyDisplayToLeft),
            fontSize = toFontSize(entity.fontSize),
            firstLetterUppercase = entity.firstLetterUppercase,
            sort = toSort(entity.sortBy, entity.sortAscending),
            screenSize = toScreenSize(entity.screenSize)
        )
    }

    fun toAutocompletes(
        entities: List<AutocompleteEntity>,
        preferencesEntity: AutocompletePreferencesEntity
    ): Autocompletes {
        return Autocompletes(
            autocompletes = entities.map { toAutocomplete(it, preferencesEntity) },
            preferences = toAutocompletePreferences(preferencesEntity)
        )
    }

    fun toAddEditAutocomplete(
        entity: AutocompleteEntity,
        preferencesEntity: AutocompletePreferencesEntity
    ): AddEditAutocomplete {
        return AddEditAutocomplete(
            autocomplete = toAutocomplete(entity, preferencesEntity),
            preferences = toAutocompletePreferences(preferencesEntity)
        )
    }

    fun toSettingsValues(
        entity: SettingsEntity,
        resourcesEntity: SettingsResourcesEntity
    ): SettingsValues {
        return SettingsValues(
            nightTheme = entity.nightTheme,
            currency = toCurrency(entity.currency, entity.currencyDisplayToLeft),
            fontSize = toFontSize(entity.fontSize),
            firstLetterUppercase = entity.firstLetterUppercase,
            displayMoney = entity.displayMoney,
            shoppingsMultiColumns = entity.shoppingsMultiColumns,
            productsMultiColumns = entity.productsMultiColumns,
            productsDisplayAutocomplete = toDisplayAutocomplete(entity.productsDisplayAutocomplete),
            productsEditCompleted = entity.productsEditCompleted,
            productsAddLastProduct = entity.productsAddLastProduct,
            developerName = toDeveloperName(resourcesEntity),
            developerEmail = toDeveloperEmail(resourcesEntity),
            appVersion = BuildConfig.VERSION_NAME,
            appGithubLink = toAppGithubLink(resourcesEntity)
        )
    }

    fun toSettingsPreferences(entity: SettingsPreferencesEntity): SettingsPreferences {
        return SettingsPreferences(
            fontSize = toFontSize(entity.fontSize),
            screenSize = toScreenSize(entity.screenSize)
        )
    }

    fun toSettings(
        entity: SettingsEntity,
        preferencesEntity: SettingsPreferencesEntity,
        resourcesEntity: SettingsResourcesEntity
    ): Settings {
        return Settings(
            settingsValues = toSettingsValues(entity, resourcesEntity),
            preferences = toSettingsPreferences(preferencesEntity)
        )
    }

    fun toMainPreferences(entity: MainPreferencesEntity): MainPreferences {
        return MainPreferences(
            appOpenedAction = toAppOpenedAction(entity.appOpenedAction),
            nightTheme = entity.nightTheme
        )
    }

    fun toEditCurrencySymbol(
        entity: EditCurrencySymbolEntity,
        preferencesEntity: SettingsPreferencesEntity
    ): EditCurrencySymbol {
        return EditCurrencySymbol(
            currency = entity.currency,
            preferences = toSettingsPreferences(preferencesEntity)
        )
    }

    fun toEditTaxRate(
        entity: EditTaxRateEntity,
        preferencesEntity: SettingsPreferencesEntity
    ): EditTaxRate {
        return EditTaxRate(
            taxRate = toTaxRate(entity.taxRate, entity.taxRateAsPercent),
            preferences = toSettingsPreferences(preferencesEntity)
        )
    }

    fun toEditReminder(
        entity: ShoppingListEntity,
        preferencesEntity: ProductPreferencesEntity
    ): EditReminder {
        return EditReminder(
            shoppingList = toShoppingList(entity, preferencesEntity),
            preferences = toProductPreferences(preferencesEntity)
        )
    }

    fun toEditShoppingListName(
        entity: ShoppingListEntity,
        preferencesEntity: ProductPreferencesEntity
    ): EditShoppingListName {
        return EditShoppingListName(
            shoppingList = toShoppingList(entity, preferencesEntity),
            preferences = toProductPreferences(preferencesEntity)
        )
    }

    fun toCalculateChange(
        entity: ShoppingListEntity,
        preferencesEntity: ProductPreferencesEntity
    ): CalculateChange {
        return CalculateChange(
            shoppingList = toShoppingList(entity, preferencesEntity),
            preferences = toProductPreferences(preferencesEntity)
        )
    }

    fun toDeveloperName(entity: SettingsResourcesEntity): String {
        return entity.developerName
    }

    fun toDeveloperEmail(entity: SettingsResourcesEntity): String {
        return entity.developerEmail
    }

    fun toAppGithubLink(entity: SettingsResourcesEntity): String {
        return entity.appGithubLink
    }

    fun toAppOpenedAction(name: String): AppOpenedAction {
        return AppOpenedAction.valueOfOrDefault(name)
    }

    fun toAppOpenedActionName(appOpenedAction: AppOpenedAction): String {
        return appOpenedAction.name
    }

    fun toCurrency(symbol: String, displayToLeft: Boolean): Currency {
        return Currency(symbol, displayToLeft)
    }

    fun toCurrencySymbol(currency: Currency): String {
        return currency.symbol
    }

    fun toCurrencyDisplayToLeft(currency: Currency): Boolean {
        return currency.displayToLeft
    }

    fun toMoney(value: Float, currency: Currency): Money {
        return Money(value, currency)
    }

    fun toMoneyValue(money: Money): Float {
        return money.value
    }

    fun toQuantity(value: Float, symbol: String): Quantity {
        return Quantity(value, symbol)
    }

    fun toQuantityValue(quantity: Quantity): Float {
        return quantity.value
    }

    fun toQuantitySymbol(quantity: Quantity): String {
        return quantity.symbol
    }

    fun toDiscount(value: Float, asPercent: Boolean): Discount {
        return Discount(value, asPercent)
    }

    fun toDiscountValue(discount: Discount): Float {
        return discount.value
    }

    fun toDiscountAsPercent(discount: Discount): Boolean {
        return discount.asPercent
    }

    fun toTaxRate(value: Float, asPercent: Boolean): TaxRate {
        return TaxRate(value, asPercent)
    }

    fun toTaxRateValue(taxRate: TaxRate): Float {
        return taxRate.value
    }

    fun toTaxRateAsPercent(taxRate: TaxRate): Boolean {
        return taxRate.asPercent
    }

    fun toFontSize(name: String): FontSize {
        return FontSize.valueOfOrDefault(name)
    }

    fun toFontSizeName(fontSize: FontSize): String {
        return fontSize.name
    }

    fun toSort(sortByName: String, ascending: Boolean): Sort {
        return Sort(SortBy.valueOfOrDefault(sortByName), ascending)
    }

    fun toSortByName(sort: Sort): String {
        return sort.sortBy.name
    }

    fun toSortByName(sortBy: SortBy): String {
        return sortBy.name
    }

    fun toSortAscending(sort: Sort): Boolean {
        return sort.ascending
    }

    fun toDisplayCompleted(name: String): DisplayCompleted {
        return DisplayCompleted.valueOfOrDefault(name)
    }

    fun toDisplayCompletedName(displayCompleted: DisplayCompleted): String {
        return displayCompleted.name
    }

    fun toDisplayTotal(name: String): DisplayTotal {
        return DisplayTotal.valueOfOrDefault(name)
    }

    fun toDisplayTotalName(displayTotal: DisplayTotal): String {
        return displayTotal.name
    }

    fun toDisplayAutocomplete(name: String): DisplayAutocomplete {
        return DisplayAutocomplete.valueOfOrDefault(name)
    }

    fun toDisplayAutocompleteName(displayAutocomplete: DisplayAutocomplete): String {
        return displayAutocomplete.name
    }

    fun toScreenSize(name: String): ScreenSize {
        return ScreenSize.valueOfOrDefault(name)
    }

    fun toScreenSizeName(screenSize: ScreenSize): String {
        return screenSize.name
    }

    fun toReminder(reminder: Long): Long? {
        return if (reminder == 0L) null else reminder
    }

    fun toReminderEntity(reminder: Long?): Long {
        return reminder ?: 0L
    }
}
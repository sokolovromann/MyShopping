package ru.sokolovromann.myshopping.data.repository

import android.database.Cursor
import ru.sokolovromann.myshopping.BuildConfig
import ru.sokolovromann.myshopping.data.local.entity.*
import ru.sokolovromann.myshopping.data.repository.model.*
import javax.inject.Inject

class RepositoryMapping @Inject constructor() {

    fun toShoppingEntity(shoppingList: ShoppingList): ShoppingEntity {
        return ShoppingEntity(
            id = shoppingList.id,
            position = shoppingList.position,
            uid = shoppingList.uid,
            created = shoppingList.created,
            lastModified = shoppingList.lastModified,
            name = shoppingList.name,
            reminder = toReminderEntity(shoppingList.reminder),
            total = toMoneyValue(shoppingList.total),
            totalFormatted = shoppingList.totalFormatted,
            budget = toMoneyValue(shoppingList.budget),
            archived = shoppingList.archived,
            deleted = shoppingList.deleted,
            sortBy = toSortName(shoppingList.sort),
            sortAscending = toSortAscending(shoppingList.sort),
            sortFormatted = shoppingList.sortFormatted,
            pinned = shoppingList.pinned
        )
    }

    fun toShoppingEntities(shoppingLists: List<ShoppingList>): List<ShoppingEntity> {
        return shoppingLists.map { toShoppingEntity(it) }
    }

    fun toShoppingLists(
        entities: List<ShoppingListEntity>,
        lastPosition: Int?,
        preferencesEntity: AppPreferencesEntity
    ): ShoppingLists {
        return ShoppingLists(
            shoppingLists = entities.map { toShoppingList(it, preferencesEntity) },
            shoppingListsLastPosition = lastPosition,
            preferences = toAppPreferences(preferencesEntity)
        )
    }

    fun toShoppingListNotification(
        entity: ShoppingListEntity,
        preferencesEntity: AppPreferencesEntity
    ): ShoppingListNotification {
        return ShoppingListNotification(
            shoppingList = toShoppingList(entity, preferencesEntity),
            preferences = toAppPreferences(preferencesEntity)
        )
    }

    fun toShoppingListNotifications(
        entities: List<ShoppingListEntity>,
        preferencesEntity: AppPreferencesEntity
    ): ShoppingListNotifications {
        return ShoppingListNotifications(
            shoppingLists = entities.map { toShoppingList(it, preferencesEntity) }
        )
    }

    fun toProductEntity(product: Product): ProductEntity {
        return ProductEntity(
            id = product.id,
            position = product.position,
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
            total = toMoneyValue(product.total),
            totalFormatted = product.totalFormatted,
            note = product.note,
            manufacturer = product.manufacturer,
            brand = product.brand,
            size = product.size,
            color = product.color,
            provider = product.provider,
            completed = product.completed,
            pinned = product.pinned
        )
    }

    fun toProductEntities(products: List<Product>): List<ProductEntity> {
        return products.map { toProductEntity(it) }
    }

    fun toProduct(entity: ProductEntity, preferencesEntity: AppPreferencesEntity): Product {
        return Product(
            id = entity.id,
            position = entity.position,
            productUid = entity.productUid,
            shoppingUid = entity.shoppingUid,
            created = entity.created,
            lastModified = entity.lastModified,
            name = entity.name,
            quantity = toQuantity(entity.quantity, entity.quantitySymbol),
            price = toMoney(
                entity.price,
                toCurrency(preferencesEntity.currency, preferencesEntity.displayCurrencyToLeft)
            ),
            discount = toDiscount(
                entity.discount,
                entity.discountAsPercent,
                toCurrency(preferencesEntity.currency, preferencesEntity.displayCurrencyToLeft)
            ),
            taxRate = toTaxRate(preferencesEntity.taxRate, preferencesEntity.taxRateAsPercent),
            total = toMoney(
                entity.total,
                toCurrency(preferencesEntity.currency, preferencesEntity.displayCurrencyToLeft)
            ),
            totalFormatted = entity.totalFormatted,
            note = entity.note,
            manufacturer = entity.manufacturer,
            brand = entity.brand,
            size = entity.size,
            color = entity.color,
            provider = entity.provider,
            completed = entity.completed,
            pinned = entity.pinned
        )
    }

    fun toProducts(
        entity: ShoppingListEntity,
        lastPosition: Int?,
        preferencesEntity: AppPreferencesEntity
    ): Products {
        return Products(
            shoppingList = toShoppingList(entity, preferencesEntity),
            shoppingListsLastPosition = lastPosition,
            preferences = toAppPreferences(preferencesEntity)
        )
    }

    fun toProducts(entities: List<ProductEntity>, preferencesEntity: AppPreferencesEntity): List<Product> {
        return entities.map { toProduct(it, preferencesEntity) }
    }

    fun toAddEditProduct(
        entity: ProductEntity?,
        lastPosition: Int?,
        preferencesEntity: AppPreferencesEntity
    ): AddEditProduct {
        return AddEditProduct(
            product = if (entity == null) null else toProduct(entity, preferencesEntity),
            productsLastPosition = lastPosition,
            preferences = toAppPreferences(preferencesEntity)
        )
    }

    fun toAutocompleteEntity(autocomplete: Autocomplete): AutocompleteEntity {
        return AutocompleteEntity(
            id = autocomplete.id,
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
            taxRateAsPercent = toTaxRateAsPercent(autocomplete.taxRate),
            total = toMoneyValue(autocomplete.total),
            manufacturer = autocomplete.manufacturer,
            brand = autocomplete.brand,
            size = autocomplete.size,
            color = autocomplete.color,
            provider = autocomplete.provider,
            personal = autocomplete.personal,
            language = autocomplete.language
        )
    }

    fun toAutocompletes(
        entities: List<AutocompleteEntity>,
        resources: List<String>?,
        preferencesEntity: AppPreferencesEntity,
        language: String?
    ): Autocompletes {
        val autocompletes = if (language == null) { entities } else {
            val default = entities.filter { !it.personal && it.language == language }
            val personal = entities.filter { it.personal }

            mutableListOf<AutocompleteEntity>().apply {
                addAll(default)
                addAll(personal)
            }
        }
            .map { toAutocomplete(it, preferencesEntity) }
            .toMutableList()

        resources?.forEach {
            val autocomplete = Autocomplete(name = it, personal = false)
            autocompletes.add(autocomplete)
        }

        return Autocompletes(
            autocompletes = autocompletes,
            preferences = toAppPreferences(preferencesEntity)
        )
    }

    fun toAddEditAutocomplete(
        entity: AutocompleteEntity?,
        preferencesEntity: AppPreferencesEntity
    ): AddEditAutocomplete {
        return AddEditAutocomplete(
            autocomplete = if (entity == null) null else toAutocomplete(entity, preferencesEntity),
            preferences = toAppPreferences(preferencesEntity)
        )
    }

    fun toSettings(
        preferencesEntity: AppPreferencesEntity,
        resourcesEntity: SettingsResourcesEntity
    ): Settings {
        return Settings(
            developerName = resourcesEntity.developerName,
            developerEmail = resourcesEntity.developerEmail,
            appVersion = BuildConfig.VERSION_NAME,
            appGithubLink = resourcesEntity.appGithubLink,
            privacyPolicyLink = resourcesEntity.privacyPolicyLink,
            termsAndConditionsLink = resourcesEntity.termsAndConditionsLink,
            preferences = toAppPreferences(preferencesEntity)
        )
    }

    fun toAppPreferences(
        entity: AppPreferencesEntity,
        appVersion14FirstOpened: Boolean = false
    ): AppPreferences {
        return AppPreferences(
            appFirstTime = toAppFirstTime(entity.appFirstTime, appVersion14FirstOpened),
            firstAppVersion = entity.firstAppVersion,
            nightTheme = entity.nightTheme,
            fontSize = toFontSize(entity.fontSize),
            smartphoneScreen = entity.smartphoneScreen,
            currency = toCurrency(entity.currency, entity.displayCurrencyToLeft),
            taxRate = toTaxRate(entity.taxRate, entity.taxRateAsPercent),
            shoppingsMultiColumns = entity.shoppingsMultiColumns,
            productsMultiColumns = entity.productsMultiColumns,
            displayCompletedPurchases = toDisplayCompleted(entity.displayCompletedPurchases),
            displayPurchasesTotal = toDisplayTotal(entity.displayPurchasesTotal),
            editProductAfterCompleted = entity.editProductAfterCompleted,
            saveProductToAutocompletes = entity.saveProductToAutocompletes,
            lockProductElement = toLockProductElement(entity.lockProductElement),
            displayMoney = entity.displayMoney,
            displayDefaultAutocompletes = entity.displayDefaultAutocompletes,
            completedWithCheckbox = entity.completedWithCheckbox,
            displayShoppingsProducts = toDisplayProducts(entity.displayShoppingsProducts),
            enterToSaveProduct = entity.enterToSaveProduct,
            coloredCheckbox = entity.coloredCheckbox,
            displayOtherFields = entity.displayOtherFields
        )
    }

    fun toAppPreferencesEntity(appPreferences: AppPreferences): AppPreferencesEntity {
        return AppPreferencesEntity(
            appFirstTime = toAppFirstTimeName(appPreferences.appFirstTime),
            firstAppVersion = appPreferences.firstAppVersion,
            nightTheme = appPreferences.nightTheme,
            fontSize = toFontSizeName(appPreferences.fontSize),
            smartphoneScreen = appPreferences.smartphoneScreen,
            currency = toCurrencySymbol(appPreferences.currency),
            displayCurrencyToLeft = toCurrencyDisplayToLeft(appPreferences.currency),
            taxRate = toTaxRateValue(appPreferences.taxRate),
            taxRateAsPercent = toTaxRateAsPercent(appPreferences.taxRate),
            shoppingsMultiColumns = appPreferences.shoppingsMultiColumns,
            productsMultiColumns = appPreferences.productsMultiColumns,
            displayCompletedPurchases = toDisplayCompletedName(appPreferences.displayCompletedPurchases),
            displayPurchasesTotal = toDisplayTotalName(appPreferences.displayPurchasesTotal),
            editProductAfterCompleted = appPreferences.editProductAfterCompleted,
            saveProductToAutocompletes = appPreferences.saveProductToAutocompletes,
            lockProductElement = toLockProductElementName(appPreferences.lockProductElement),
            displayMoney = appPreferences.displayMoney,
            displayDefaultAutocompletes = appPreferences.displayDefaultAutocompletes,
            completedWithCheckbox = appPreferences.completedWithCheckbox,
            displayShoppingsProducts = toDisplayProductsName(appPreferences.displayShoppingsProducts),
            enterToSaveProduct = appPreferences.enterToSaveProduct,
            coloredCheckbox = appPreferences.coloredCheckbox,
            displayOtherFields = appPreferences.displayOtherFields
        )
    }

    fun toAppVersion14(
        shoppingListsCursor: Cursor,
        productsCursor: Cursor,
        autocompletesCursor: Cursor,
        defaultAutocompleteNames: List<String>,
        preferences: AppVersion14PreferencesEntity
    ): AppVersion14 {
        val shoppingLists = mutableListOf<ShoppingList>()
        val autocompletes = mutableListOf<Autocomplete>()
        while (shoppingListsCursor.moveToNext()) {
            val shoppingList = toShoppingList(shoppingListsCursor)

            val products = mutableListOf<Product>()
            while (productsCursor.moveToNext()) {
                val product = toProduct(productsCursor, preferences)
                if (product.shoppingUid == shoppingList.uid) {
                    products.add(product)

                    if (preferences.saveProductToAutocompletes) {
                        val personal = isPersonalAutocomplete(defaultAutocompleteNames, product.name)
                        val autocomplete = toAutocomplete(product, personal)
                        autocompletes.add(autocomplete)
                    }
                }
            }
            shoppingLists.add(
                shoppingList.copy(
                    products = products.formatAppVersion14Products(
                        preferences.sort,
                        preferences.firstLetterUppercase
                    )
                )
            )

            productsCursor.moveToFirst()
        }

        while (autocompletesCursor.moveToNext()) {
            val autocompleteFromCursor = toAutocomplete(autocompletesCursor)
            val autocomplete = autocompleteFromCursor.copy(
                personal = isPersonalAutocomplete(defaultAutocompleteNames, autocompleteFromCursor.name)
            )
            autocompletes.add(autocomplete)
        }

        return AppVersion14(
            shoppingLists = shoppingLists.formatAppVersion14ShoppingLists(
                preferences.sort,
                preferences.firstLetterUppercase
            ).toList(),
            autocompletes = autocompletes.toList(),
            preferences = toAppVersion14Preferences(preferences)
        )
    }

    fun toEditCurrencySymbol(preferencesEntity: AppPreferencesEntity): EditCurrencySymbol {
        return EditCurrencySymbol(
            preferences = toAppPreferences(preferencesEntity)
        )
    }

    fun toEditTaxRate(preferencesEntity: AppPreferencesEntity): EditTaxRate {
        return EditTaxRate(
            preferences = toAppPreferences(preferencesEntity)
        )
    }

    fun toEditReminder(
        entity: ShoppingListEntity?,
        preferencesEntity: AppPreferencesEntity
    ): EditReminder {
        return EditReminder(
            shoppingList = if (entity == null) null else toShoppingList(entity, preferencesEntity),
            preferences = toAppPreferences(preferencesEntity)
        )
    }

    fun toEditShoppingListName(
        entity: ShoppingListEntity?,
        preferencesEntity: AppPreferencesEntity
    ): EditShoppingListName {
        return EditShoppingListName(
            shoppingList = if (entity == null) null else toShoppingList(entity, preferencesEntity),
            preferences = toAppPreferences(preferencesEntity)
        )
    }

    fun toEditShoppingListTotal(
        entity: ShoppingListEntity?,
        preferencesEntity: AppPreferencesEntity
    ): EditShoppingListTotal {
        return EditShoppingListTotal(
            shoppingList = if (entity == null) null else toShoppingList(entity, preferencesEntity),
            preferences = toAppPreferences(preferencesEntity)
        )
    }

    fun toCalculateChange(
        entity: ShoppingListEntity?,
        preferencesEntity: AppPreferencesEntity
    ): CalculateChange {
        return CalculateChange(
            shoppingList = if (entity == null) null else toShoppingList(entity, preferencesEntity),
            preferences = toAppPreferences(preferencesEntity)
        )
    }

    fun toBackupEntity(backup: Backup): BackupFileEntity {
        return BackupFileEntity(
            shoppingEntities = backup.shoppingLists.map { toShoppingEntity(it) },
            productEntities = backup.products.map { toProductEntity(it) },
            autocompleteEntities = backup.autocompletes.map { toAutocompleteEntity(it) },
            preferencesEntity = toAppPreferencesEntity(backup.preferences),
            appVersion = backup.appVersion
        )
    }

    fun toBackup(
        shoppingListEntities: List<ShoppingEntity>,
        productEntities: List<ProductEntity>,
        autocompleteEntities: List<AutocompleteEntity>,
        preferencesEntity: AppPreferencesEntity,
        currentAppVersion: Int
    ): Backup {
        return Backup(
            shoppingLists = shoppingListEntities.map { toShoppingList(it, preferencesEntity) },
            products = productEntities.map { toProduct(it, preferencesEntity) },
            autocompletes = autocompleteEntities.map { toAutocomplete(it, preferencesEntity) },
            preferences = toAppPreferences(preferencesEntity),
            appVersion = currentAppVersion
        )
    }

    fun toBackup(entity: BackupFileEntity): Backup {
        val preferencesEntity = entity.preferencesEntity
        return Backup(
            shoppingLists = entity.shoppingEntities.map { toShoppingList(it, preferencesEntity) },
            products = entity.productEntities.map { toProduct(it, preferencesEntity) },
            autocompletes = entity.autocompleteEntities.map { toAutocomplete(it, preferencesEntity) },
            preferences = toAppPreferences(preferencesEntity),
            appVersion = entity.appVersion
        )
    }

    fun toShoppingEntities(backup: Backup): List<ShoppingEntity> {
        return backup.shoppingLists.map { toShoppingEntity(it) }
    }

    fun toProductEntities(backup: Backup): List<ProductEntity> {
        return backup.products.map { toProductEntity(it) }
    }

    fun toAutocompleteEntities(backup: Backup): List<AutocompleteEntity> {
        return backup.autocompletes.map { toAutocompleteEntity(it) }
    }

    fun toCurrency(symbol: String, displayToLeft: Boolean): Currency {
        return Currency(symbol, displayToLeft)
    }

    fun toTaxRateValue(taxRate: TaxRate): Float {
        return taxRate.value
    }

    fun toTaxRateAsPercent(taxRate: TaxRate): Boolean {
        return taxRate.asPercent
    }

    fun toFontSizeName(fontSize: FontSize): String {
        return fontSize.name
    }

    fun toDisplayCompletedName(displayCompleted: DisplayCompleted): String {
        return displayCompleted.name
    }

    fun toDisplayTotalName(displayTotal: DisplayTotal): String {
        return displayTotal.name
    }

    fun toLockProductElementName(lockProductElement: LockProductElement): String {
        return lockProductElement.name
    }

    fun toMoneyValue(money: Money): Float {
        return money.value
    }

    fun toDisplayProductsName(displayProducts: DisplayProducts): String {
        return displayProducts.name
    }

    fun toSortByName(sortBy: SortBy): String {
        return sortBy.name
    }

    private fun toShoppingList(
        shoppingListEntity: ShoppingListEntity,
        preferencesEntity: AppPreferencesEntity
    ): ShoppingList {
        val entity = shoppingListEntity.shoppingEntity
        return ShoppingList(
            id = entity.id,
            position = entity.position,
            uid = entity.uid,
            created = entity.created,
            lastModified = entity.lastModified,
            name = entity.name,
            reminder = toReminder(entity.reminder),
            total = toMoney(
                entity.total,
                toCurrency(preferencesEntity.currency, preferencesEntity.displayCurrencyToLeft)
            ),
            totalFormatted = entity.totalFormatted,
            budget = toMoney(
                entity.budget,
                toCurrency(preferencesEntity.currency, preferencesEntity.displayCurrencyToLeft)
            ),
            archived = entity.archived,
            deleted = entity.deleted,
            completed = toCompleted(shoppingListEntity.productEntities),
            products = shoppingListEntity.productEntities.map { toProduct(it, preferencesEntity) },
            currency = toCurrency(preferencesEntity.currency, preferencesEntity.displayCurrencyToLeft),
            displayTotal = toDisplayTotal(preferencesEntity.displayPurchasesTotal),
            sort = toSort(entity.sortBy, entity.sortAscending),
            sortFormatted = entity.sortFormatted,
            pinned = entity.pinned
        )
    }

    private fun toShoppingList(
        entity: ShoppingEntity,
        preferencesEntity: AppPreferencesEntity
    ): ShoppingList {
        return ShoppingList(
            id = entity.id,
            position = entity.position,
            uid = entity.uid,
            created = entity.created,
            lastModified = entity.lastModified,
            name = entity.name,
            reminder = toReminder(entity.reminder),
            total = toMoney(
                entity.total,
                toCurrency(preferencesEntity.currency, preferencesEntity.displayCurrencyToLeft)
            ),
            totalFormatted = entity.totalFormatted,
            budget = toMoney(
                entity.budget,
                toCurrency(preferencesEntity.currency, preferencesEntity.displayCurrencyToLeft)
            ),
            archived = entity.archived,
            deleted = entity.deleted,
            currency = toCurrency(preferencesEntity.currency, preferencesEntity.displayCurrencyToLeft),
            displayTotal = toDisplayTotal(preferencesEntity.displayPurchasesTotal),
            sort = toSort(entity.sortBy, entity.sortAscending),
            sortFormatted = entity.sortFormatted,
            pinned = entity.pinned
        )
    }

    private fun toShoppingList(cursor: Cursor): ShoppingList {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"))
        val name = cursor.getString(cursor.getColumnIndexOrThrow("listname"))
        val alarm = cursor.getLong(cursor.getColumnIndexOrThrow("alarm"))

        return ShoppingList(
            id = id.toInt(),
            position = cursor.position,
            uid = toAppVersion14ShoppingUid(id),
            name = name,
            reminder = toReminder(alarm)
        )
    }

    private fun toProduct(cursor: Cursor, preferences: AppVersion14PreferencesEntity): Product {
        val listId = cursor.getLong(cursor.getColumnIndexOrThrow("listid"))
        val name = cursor.getString(cursor.getColumnIndexOrThrow("goodsname"))
        val number = cursor.getString(cursor.getColumnIndexOrThrow("number"))
        val numberMeasure = cursor.getString(cursor.getColumnIndexOrThrow("numbermeasure"))
        val priceMeasure = cursor.getFloat(cursor.getColumnIndexOrThrow("pricemeasure"))
        val buy = cursor.getInt(cursor.getColumnIndexOrThrow("goodsbuy"))

        val quantity = Quantity(
            value = number.toFloatOrNull() ?: 0f,
            symbol = numberMeasure.replace(".", "")
        )
        val price = Money(value = priceMeasure)
        val taxRate = toTaxRate(preferences.taxRate, true)
        val total = Money(value = quantity.value * price.value)

        return Product(
            position = cursor.position,
            shoppingUid = toAppVersion14ShoppingUid(listId),
            name = name,
            quantity = quantity,
            price = price,
            taxRate = taxRate,
            total = total,
            totalFormatted = false,
            completed = toAppVersion14Completed(buy)
        )
    }

    private fun toAutocomplete(entity: AutocompleteEntity, preferencesEntity: AppPreferencesEntity): Autocomplete {
        return Autocomplete(
            id = entity.id,
            uid = entity.uid,
            created = entity.created,
            lastModified = entity.lastModified,
            name = entity.name,
            quantity = toQuantity(entity.quantity, entity.quantitySymbol),
            price = toMoney(
                entity.price,
                toCurrency(preferencesEntity.currency, preferencesEntity.displayCurrencyToLeft)
            ),
            discount = toDiscount(
                entity.discount,
                entity.discountAsPercent,
                toCurrency(preferencesEntity.currency, preferencesEntity.displayCurrencyToLeft)
            ),
            taxRate = toTaxRate(entity.taxRate, entity.taxRateAsPercent),
            total = toMoney(
                entity.total,
                toCurrency(preferencesEntity.currency, preferencesEntity.displayCurrencyToLeft)
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

    private fun toAutocomplete(cursor: Cursor): Autocomplete {
        val name = cursor.getString(cursor.getColumnIndexOrThrow("completename"))
        return Autocomplete(name = name)
    }

    private fun toAutocomplete(product: Product, personal: Boolean): Autocomplete {
        return Autocomplete(
            name = product.name,
            quantity = product.quantity,
            price = product.price,
            discount = product.discount,
            taxRate = product.taxRate,
            total = product.total,
            manufacturer = product.manufacturer,
            brand = product.brand,
            size = product.size,
            color = product.color,
            provider = product.provider,
            personal = personal
        )
    }

    private fun toAppVersion14Preferences(entity: AppVersion14PreferencesEntity): AppVersion14Preferences {
        return AppVersion14Preferences(
            firstOpened = entity.firstOpened,
            currency = toCurrency(entity.currency, entity.displayCurrencyToLeft),
            taxRate = toTaxRate(entity.taxRate, true),
            fontSize = toFontSize(entity.titleFontSize),
            multiColumns = toMultiColumns(entity.columnCount),
            displayMoney = entity.displayMoney,
            displayTotal = toDisplayTotal(entity.displayTotal),
            editProductAfterCompleted = entity.editProductAfterCompleted,
            saveProductToAutocompletes = entity.saveProductToAutocompletes
        )
    }

    private fun toAppFirstTimeName(appFirstTime: AppFirstTime): String {
        return appFirstTime.name
    }

    private fun toCurrencySymbol(currency: Currency): String {
        return currency.symbol
    }

    private fun toCurrencyDisplayToLeft(currency: Currency): Boolean {
        return currency.displayToLeft
    }

    private fun toAppFirstTime(name: String, appVersion14FirstOpened: Boolean): AppFirstTime {
        val appFirstTime = AppFirstTime.valueOfOrDefault(name)
        return if (appFirstTime == AppFirstTime.FIRST_TIME && appVersion14FirstOpened) {
            AppFirstTime.FIRST_TIME_FROM_APP_VERSION_14
        } else {
            appFirstTime
        }
    }

    private fun toMoney(value: Float, currency: Currency): Money {
        return Money(value, currency)
    }

    private fun toQuantity(value: Float, symbol: String): Quantity {
        return Quantity(value, symbol)
    }

    private fun toQuantityValue(quantity: Quantity): Float {
        return quantity.value
    }

    private fun toQuantitySymbol(quantity: Quantity): String {
        return quantity.symbol
    }

    private fun toDiscount(value: Float, asPercent: Boolean, currency: Currency): Discount {
        return Discount(
            value = value,
            asPercent = asPercent,
            currency = currency
        )
    }

    private fun toDiscountValue(discount: Discount): Float {
        return discount.value
    }

    private fun toDiscountAsPercent(discount: Discount): Boolean {
        return discount.asPercent
    }

    private fun toTaxRate(value: Float, asPercent: Boolean): TaxRate {
        return TaxRate(value, asPercent)
    }

    private fun toFontSize(name: String): FontSize {
        return FontSize.valueOfOrDefault(name)
    }

    private fun toFontSize(value: Int): FontSize {
        return if (value <= 14) {
            FontSize.SMALL
        } else if (value <= 16) {
            FontSize.MEDIUM
        } else if (value <= 18) {
            FontSize.LARGE
        } else if (value <= 20) {
            FontSize.HUGE
        } else if (value <= 22) {
            FontSize.HUGE_2
        } else if (value <= 24) {
            FontSize.HUGE_3
        } else {
            FontSize.MEDIUM
        }
    }

    private fun toMultiColumns(columnCount: Int): Boolean {
        return columnCount > 1
    }

    private fun toDisplayCompleted(name: String): DisplayCompleted {
        return DisplayCompleted.valueOfOrDefault(name)
    }

    private fun toDisplayTotal(name: String): DisplayTotal {
        return DisplayTotal.valueOfOrDefault(name)
    }

    private fun toDisplayTotal(value: Int): DisplayTotal {
        return when (value) {
            0 -> DisplayTotal.ALL
            1 -> DisplayTotal.ACTIVE
            2 -> DisplayTotal.COMPLETED
            else -> DisplayTotal.ALL
        }
    }

    private fun toSort(sortBy: String, ascending: Boolean): Sort {
        return Sort(SortBy.valueOfOrDefault(sortBy), ascending)
    }

    private fun toSortName(sort: Sort): String {
        return sort.sortBy.name
    }

    private fun toSortAscending(sort: Sort): Boolean {
        return sort.ascending
    }

    private fun toReminder(reminder: Long): Long? {
        return if (reminder == 0L) null else reminder
    }

    private fun toReminderEntity(reminder: Long?): Long {
        return reminder ?: 0L
    }

    private fun toLockProductElement(name: String): LockProductElement {
        return LockProductElement.valueOfOrDefault(name)
    }

    private fun toDisplayProducts(name: String): DisplayProducts {
        return DisplayProducts.valueOfOrDefault(name)
    }

    private fun toCompleted(entities: List<ProductEntity>): Boolean {
        return if (entities.isEmpty()) {
            false
        } else {
            entities.find { !it.completed } == null
        }
    }

    private fun isPersonalAutocomplete(defaultNames: List<String>, search: String): Boolean {
        return defaultNames.find { it.equals(search, true) } == null
    }

    private fun toAppVersion14ShoppingUid(listId: Long): String {
        return listId.toString()
    }

    private fun toAppVersion14Completed(buy: Int): Boolean {
        val completed = 2130837592
        return buy == completed
    }

    private fun List<ShoppingList>.formatAppVersion14ShoppingLists(
        sort: Int,
        firstLetterUppercase: Boolean
    ): List<ShoppingList> {
        return when (sort) {
            1, 3 -> this.sortedBy { it.name }
            2, 4 -> this.sortedBy { it.calculateTotal().value }
            else -> this.sortedBy { it.id }
        }.withIndex().map {
            it.value.copy(
                position = it.index,
                name = it.value.name.formatFirst(firstLetterUppercase)
            )
        }
    }

    private fun List<Product>.formatAppVersion14Products(
        sort: Int,
        firstLetterUppercase: Boolean
    ): List<Product> {
        return when (sort) {
            1, 3 -> this.sortedBy { it.name }
            2, 4 -> this.sortedBy { it.total.value }
            else -> this.sortedBy { it.id }
        }.withIndex().map {
            it.value.copy(
                position = it.index,
                name = it.value.name.formatFirst(firstLetterUppercase)
            )
        }
    }
}
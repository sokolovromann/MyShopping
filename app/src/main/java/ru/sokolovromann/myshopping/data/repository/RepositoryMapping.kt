package ru.sokolovromann.myshopping.data.repository

import android.database.Cursor
import ru.sokolovromann.myshopping.BuildConfig
import ru.sokolovromann.myshopping.data.local.entity.*
import ru.sokolovromann.myshopping.data.repository.model.*
import java.text.DecimalFormat
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
        appConfigEntity: AppConfigEntity
    ): ShoppingLists {
        return ShoppingLists(
            shoppingLists = entities.map { toShoppingList(it, appConfigEntity) },
            shoppingListsLastPosition = lastPosition,
            appConfig = toAppConfig(appConfigEntity)
        )
    }

    fun toShoppingListNotification(
        entity: ShoppingListEntity,
        appConfigEntity: AppConfigEntity
    ): ShoppingListNotification {
        return ShoppingListNotification(
            shoppingList = toShoppingList(entity, appConfigEntity),
            appConfig = toAppConfig(appConfigEntity)
        )
    }

    fun toShoppingListNotifications(
        entities: List<ShoppingListEntity>,
        appConfigEntity: AppConfigEntity
    ): ShoppingListNotifications {
        return ShoppingListNotifications(
            shoppingLists = entities.map { toShoppingList(it, appConfigEntity) }
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

    fun toProduct(entity: ProductEntity, appConfigEntity: AppConfigEntity): Product {
        val userPreferences = appConfigEntity.userPreferences
        return Product(
            id = entity.id,
            position = entity.position,
            productUid = entity.productUid,
            shoppingUid = entity.shoppingUid,
            created = entity.created,
            lastModified = entity.lastModified,
            name = entity.name,
            quantity = toQuantity(
                entity.quantity,
                entity.quantitySymbol,
                toQuantityDecimalFormat(
                    appConfigEntity.userPreferences.minQuantityFractionDigits,
                    appConfigEntity.userPreferences.maxQuantityFractionDigits
                )
            ),
            price = toMoney(
                value = entity.price,
                userPreferences = userPreferences
            ),
            discount = toMoney(
                value = entity.discount,
                asPercent = entity.discountAsPercent,
                userPreferences = userPreferences
            ),
            taxRate = toMoney(
                value = userPreferences.taxRate,
                asPercent = userPreferences.taxRateAsPercent,
                userPreferences = userPreferences
            ),
            total = toMoney(
                value = entity.total,
                userPreferences = userPreferences
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
        appConfigEntity: AppConfigEntity
    ): Products {
        return Products(
            shoppingList = toShoppingList(entity, appConfigEntity),
            shoppingListsLastPosition = lastPosition,
            appConfig = toAppConfig(appConfigEntity)
        )
    }

    fun toProducts(entities: List<ProductEntity>, appConfigEntity: AppConfigEntity): List<Product> {
        return entities.map { toProduct(it, appConfigEntity) }
    }

    fun toAddEditProduct(
        entity: ProductEntity?,
        lastPosition: Int?,
        appConfigEntity: AppConfigEntity
    ): AddEditProduct {
        return AddEditProduct(
            product = if (entity == null) null else toProduct(entity, appConfigEntity),
            productsLastPosition = lastPosition,
            appConfig = toAppConfig(appConfigEntity)
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
        appConfigEntity: AppConfigEntity,
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
            .map { toAutocomplete(it, appConfigEntity) }
            .toMutableList()

        resources?.forEach {
            val autocomplete = Autocomplete(name = it, personal = false)
            autocompletes.add(autocomplete)
        }

        return Autocompletes(
            autocompletes = autocompletes,
            appConfig = toAppConfig(appConfigEntity)
        )
    }

    fun toSettings(
        appConfigEntity: AppConfigEntity,
        resourcesEntity: SettingsResourcesEntity
    ): Settings {
        return Settings(
            developerName = resourcesEntity.developerName,
            developerEmail = resourcesEntity.developerEmail,
            appVersion = BuildConfig.VERSION_NAME,
            appGithubLink = resourcesEntity.appGithubLink,
            privacyPolicyLink = resourcesEntity.privacyPolicyLink,
            termsAndConditionsLink = resourcesEntity.termsAndConditionsLink,
            appConfig = toAppConfig(appConfigEntity)
        )
    }

    fun toEditCurrencySymbol(appConfigEntity: AppConfigEntity): EditCurrencySymbol {
        return EditCurrencySymbol(
            appConfig = toAppConfig(appConfigEntity)
        )
    }

    fun toEditTaxRate(appConfigEntity: AppConfigEntity): EditTaxRate {
        return EditTaxRate(
            appConfig = toAppConfig(appConfigEntity)
        )
    }

    fun toEditReminder(
        entity: ShoppingListEntity?,
        appConfigEntity: AppConfigEntity
    ): EditReminder {
        return EditReminder(
            shoppingList = if (entity == null) null else toShoppingList(entity, appConfigEntity),
            appConfig = toAppConfig(appConfigEntity)
        )
    }

    fun toEditShoppingListName(
        entity: ShoppingListEntity?,
        appConfigEntity: AppConfigEntity
    ): EditShoppingListName {
        return EditShoppingListName(
            shoppingList = if (entity == null) null else toShoppingList(entity, appConfigEntity),
            appConfig = toAppConfig(appConfigEntity)
        )
    }

    fun toEditShoppingListTotal(
        entity: ShoppingListEntity?,
        appConfigEntity: AppConfigEntity
    ): EditShoppingListTotal {
        return EditShoppingListTotal(
            shoppingList = if (entity == null) null else toShoppingList(entity, appConfigEntity),
            appConfig = toAppConfig(appConfigEntity)
        )
    }

    fun toCalculateChange(
        entity: ShoppingListEntity?,
        appConfigEntity: AppConfigEntity
    ): CalculateChange {
        return CalculateChange(
            shoppingList = if (entity == null) null else toShoppingList(entity, appConfigEntity),
            appConfig = toAppConfig(appConfigEntity)
        )
    }

    fun toBackupEntity(backup: Backup): BackupFileEntity {
        return BackupFileEntity(
            shoppingEntities = backup.shoppingLists.map { toShoppingEntity(it) },
            productEntities = backup.products.map { toProductEntity(it) },
            autocompleteEntities = backup.autocompletes.map { toAutocompleteEntity(it) },
            appConfigEntity = toAppConfigEntity(backup.appConfig),
            appVersion = backup.appVersion
        )
    }

    fun toBackup(
        shoppingLists: List<ShoppingListEntity>,
        autocompleteEntities: List<AutocompleteEntity>,
        appConfigEntity: AppConfigEntity,
        currentAppVersion: Int
    ): Backup {
        val products = shoppingLists.map { it.productEntities }.single()
        return Backup(
            shoppingLists = shoppingLists.map { toShoppingList(it, appConfigEntity) },
            products = products.map { toProduct(it, appConfigEntity) },
            autocompletes = autocompleteEntities.map { toAutocomplete(it, appConfigEntity) },
            appConfig = toAppConfig(appConfigEntity),
            appVersion = currentAppVersion
        )
    }

    fun toBackup(entity: BackupFileEntity): Backup {
        val appConfigEntity = entity.appConfigEntity
        return Backup(
            shoppingLists = entity.shoppingEntities.map { toShoppingList(it, appConfigEntity) },
            products = entity.productEntities.map { toProduct(it, appConfigEntity) },
            autocompletes = entity.autocompleteEntities.map { toAutocomplete(it, appConfigEntity) },
            appConfig = toAppConfig(appConfigEntity),
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
        appConfigEntity: AppConfigEntity
    ): ShoppingList {
        val entity = shoppingListEntity.shoppingEntity
        val userPreferences = appConfigEntity.userPreferences
        return ShoppingList(
            id = entity.id,
            position = entity.position,
            uid = entity.uid,
            created = entity.created,
            lastModified = entity.lastModified,
            name = entity.name,
            reminder = toReminder(entity.reminder),
            total = toMoney(
                value = entity.total,
                userPreferences = userPreferences
            ),
            totalFormatted = entity.totalFormatted,
            budget = toMoney(
                value = entity.budget,
                userPreferences = userPreferences
            ),
            archived = entity.archived,
            deleted = entity.deleted,
            completed = toCompleted(shoppingListEntity.productEntities),
            products = shoppingListEntity.productEntities.map { toProduct(it, appConfigEntity) },
            currency = toCurrency(userPreferences.currency, userPreferences.displayCurrencyToLeft),
            displayTotal = toDisplayTotal(userPreferences.displayTotal),
            sort = toSort(entity.sortBy, entity.sortAscending),
            sortFormatted = entity.sortFormatted,
            pinned = entity.pinned
        )
    }

    private fun toShoppingList(
        entity: ShoppingEntity,
        appConfigEntity: AppConfigEntity
    ): ShoppingList {
        val userPreferences = appConfigEntity.userPreferences
        return ShoppingList(
            id = entity.id,
            position = entity.position,
            uid = entity.uid,
            created = entity.created,
            lastModified = entity.lastModified,
            name = entity.name,
            reminder = toReminder(entity.reminder),
            total = toMoney(
                value = entity.total,
                userPreferences = userPreferences
            ),
            totalFormatted = entity.totalFormatted,
            budget = toMoney(
                value = entity.budget,
                userPreferences = userPreferences
            ),
            archived = entity.archived,
            deleted = entity.deleted,
            currency = toCurrency(userPreferences.currency, userPreferences.displayCurrencyToLeft),
            displayTotal = toDisplayTotal(userPreferences.displayTotal),
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

    private fun toProduct(cursor: Cursor, preferences: CodeVersion14UserPreferencesEntity): Product {
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
        val taxRate = toMoney(
            value = preferences.taxRate,
            asPercent = true,
            userPreferences = preferences
        )
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

    private fun toQuantity(value: Float, symbol: String, decimalFormat: DecimalFormat): Quantity {
        return Quantity(value, symbol, decimalFormat)
    }

    private fun toQuantityValue(quantity: Quantity): Float {
        return quantity.value
    }

    private fun toQuantitySymbol(quantity: Quantity): String {
        return quantity.symbol
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

    // -----
    // MONEY
    // -----

    fun toMoney(
        value: Float?,
        asPercent: Boolean? = null,
        userPreferences: UserPreferencesEntity
    ): Money {
        return Money(
            value = value ?: 0f,
            currency = toCurrency(
                userPreferences.currency,
                userPreferences.displayCurrencyToLeft
            ),
            asPercent = asPercent ?: false,
            decimalFormat = toMoneyDecimalFormat(
                userPreferences.minMoneyFractionDigits,
                userPreferences.maxMoneyFractionDigits
            )
        )
    }

    fun toMoney(
        value: Float?,
        asPercent: Boolean? = null,
        userPreferences: CodeVersion14UserPreferencesEntity
    ): Money {
        return Money(
            value = value ?: 0f,
            currency = toCurrency(
                userPreferences.currency,
                userPreferences.displayCurrencyToLeft
            ),
            asPercent = asPercent ?: false
        )
    }

    //
    // AUTOCOMPLETES
    //

    fun toAddEditAutocomplete(
        entity: AutocompleteEntity?,
        appConfigEntity: AppConfigEntity
    ): AddEditAutocomplete {
        return AddEditAutocomplete(
            autocomplete = if (entity == null) null else toAutocomplete(entity, appConfigEntity),
            appConfig = toAppConfig(appConfigEntity)
        )
    }

    fun toAutocomplete(entity: AutocompleteEntity, appConfigEntity: AppConfigEntity): Autocomplete {
        val userPreferences = appConfigEntity.userPreferences
        return Autocomplete(
            id = entity.id,
            uid = entity.uid,
            created = entity.created,
            lastModified = entity.lastModified,
            name = entity.name,
            quantity = toQuantity(
                entity.quantity,
                entity.quantitySymbol,
                toQuantityDecimalFormat(
                    appConfigEntity.userPreferences.minQuantityFractionDigits,
                    appConfigEntity.userPreferences.maxQuantityFractionDigits
                )
            ),
            price = toMoney(
                value = entity.price,
                userPreferences = userPreferences
            ),
            discount = toMoney(
                value = entity.discount,
                asPercent = entity.discountAsPercent,
                userPreferences = userPreferences
            ),
            taxRate = toMoney(
                value = entity.taxRate,
                asPercent = entity.taxRateAsPercent,
                userPreferences = userPreferences
            ),
            total = toMoney(
                value = entity.total,
                userPreferences = userPreferences
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

    // ----------
    // APP CONFIG
    // ----------

    fun toAppConfig(entity: AppConfigEntity): AppConfig {
        return AppConfig(
            deviceConfig = toDeviceConfig(entity.deviceConfig),
            appBuildConfig = toAppBuildConfig(entity.appBuildConfig),
            userPreferences = toUserPreferences(entity.userPreferences)
        )
    }

    fun toAppConfigEntity(appConfig: AppConfig): AppConfigEntity {
        return AppConfigEntity(
            deviceConfig = toDeviceConfigEntity(appConfig.deviceConfig),
            appBuildConfig = toAppBuildConfigEntity(appConfig.appBuildConfig),
            userPreferences = toUserPreferencesEntity(appConfig.userPreferences)
        )
    }

    fun toDeviceConfig(entity: DeviceConfigEntity): DeviceConfig {
        return DeviceConfig(
            screenWidthDp = toScreenDp(entity.screenWidthDp),
            screenHeightDp = toScreenDp(entity.screenHeightDp)
        )
    }

    fun toScreenDp(value: Int?): Int {
        return value ?: DeviceConfig.UNKNOWN_SIZE_DP
    }

    fun toDeviceConfigEntity(deviceConfig: DeviceConfig): DeviceConfigEntity {
        return DeviceConfigEntity(
            screenWidthDp = deviceConfig.screenWidthDp,
            screenHeightDp = deviceConfig.screenHeightDp
        )
    }

    fun toAppBuildConfig(entity: AppBuildConfigEntity): AppBuildConfig {
        return AppBuildConfig(
            appId = BuildConfig.APPLICATION_ID,
            appVersionName = BuildConfig.VERSION_NAME,
            userCodeVersion = toUserCodeVersion(
                entity.userCodeVersion,
                entity.appFirstTime,
                entity.codeVersion14
            )
        )
    }

    fun toUserCodeVersion(userCodeVersion: Int?, appFirstTime: String?, fromCodeVersion14: Boolean?): Int {
        return userCodeVersion ?: if (appFirstTime == "NOTHING") {
            AppBuildConfig.CODE_VERSION_18
        } else {
            if (fromCodeVersion14 == true) {
                AppBuildConfig.CODE_VERSION_14
            } else {
                AppBuildConfig.UNKNOWN_CODE_VERSION
            }
        }
    }

    fun toAppBuildConfigEntity(appBuildConfig: AppBuildConfig): AppBuildConfigEntity {
        return AppBuildConfigEntity(
            appFirstTime = "",
            userCodeVersion = appBuildConfig.userCodeVersion
        )
    }

    fun toUserPreferences(entity: UserPreferencesEntity): UserPreferences {
        return UserPreferences(
            nightTheme = toNightTheme(entity.nightTheme),
            fontSize = toFontSize(entity.fontSize),
            shoppingsMultiColumns = toMultiColumns(entity.shoppingsMultiColumns),
            productsMultiColumns = toMultiColumns(entity.productsMultiColumns),
            displayCompleted = toDisplayCompleted(entity.displayCompleted),
            displayTotal = toDisplayTotal(entity.displayTotal),
            displayOtherFields = toDisplayOtherFields(entity.displayOtherFields),
            coloredCheckbox = toColoredCheckbox(entity.coloredCheckbox),
            displayShoppingsProducts = toDisplayShoppingsProducts(entity.displayShoppingsProducts),
            purchasesSeparator = toPurchasesSeparator(entity.purchasesSeparator),
            editProductAfterCompleted = toEditProductAfterCompleted(entity.editProductAfterCompleted),
            lockProductElement = toLockProductElement(entity.lockProductElement),
            completedWithCheckbox = toCompletedWithCheckbox(entity.completedWithCheckbox),
            enterToSaveProduct = toEnterToSaveProduct(entity.enterToSaveProduct),
            displayDefaultAutocompletes = toDisplayDefaultAutocompletes(entity.displayDefaultAutocompletes),
            maxAutocompletesNames = toMaxAutocompleteNames(entity.maxAutocompletesNames),
            maxAutocompletesQuantities = toMaxAutocompleteQuantities(entity.maxAutocompletesQuantities),
            maxAutocompletesMoneys = toMaxAutocompleteMoneys(entity.maxAutocompletesMoneys),
            maxAutocompletesOthers = toMaxAutocompleteOthers(entity.maxAutocompletesOthers),
            saveProductToAutocompletes = toSaveProductToAutocompletes(entity.saveProductToAutocompletes),
            displayMoney = toDisplayMoney(entity.displayMoney),
            currency = toCurrency(entity.currency, entity.displayCurrencyToLeft),
            taxRate = toMoney(
                value = entity.taxRate,
                asPercent = entity.taxRateAsPercent,
                userPreferences = entity
            ),
            moneyDecimalFormat = toMoneyDecimalFormat(entity.minMoneyFractionDigits, entity.maxMoneyFractionDigits),
            quantityDecimalFormat = toQuantityDecimalFormat(entity.minQuantityFractionDigits, entity.maxQuantityFractionDigits)
        )
    }

    fun toUserPreferencesEntity(userPreferences: UserPreferences): UserPreferencesEntity {
        return UserPreferencesEntity(
            nightTheme = userPreferences.nightTheme,
            fontSize = toFontSizeString(userPreferences.fontSize),
            shoppingsMultiColumns = userPreferences.shoppingsMultiColumns,
            productsMultiColumns = userPreferences.productsMultiColumns,
            displayCompleted = toDisplayCompletedString(userPreferences.displayCompleted),
            displayTotal = toDisplayTotalString(userPreferences.displayTotal),
            displayOtherFields = userPreferences.displayOtherFields,
            coloredCheckbox = userPreferences.coloredCheckbox,
            displayShoppingsProducts = toDisplayShoppingsProductsString(userPreferences.displayShoppingsProducts),
            purchasesSeparator = userPreferences.purchasesSeparator,
            editProductAfterCompleted = userPreferences.editProductAfterCompleted,
            lockProductElement = toLockProductString(userPreferences.lockProductElement),
            completedWithCheckbox = userPreferences.completedWithCheckbox,
            enterToSaveProduct = userPreferences.enterToSaveProduct,
            displayDefaultAutocompletes = userPreferences.displayDefaultAutocompletes,
            maxAutocompletesNames = userPreferences.maxAutocompletesNames,
            maxAutocompletesQuantities = userPreferences.maxAutocompletesQuantities,
            maxAutocompletesMoneys = userPreferences.maxAutocompletesMoneys,
            maxAutocompletesOthers = userPreferences.maxAutocompletesOthers,
            saveProductToAutocompletes = userPreferences.saveProductToAutocompletes,
            displayMoney = userPreferences.displayMoney,
            currency = toCurrencySymbol(userPreferences.currency),
            displayCurrencyToLeft = toCurrencyDisplayToLeft(userPreferences.currency),
            taxRate = toTaxRateValue(userPreferences.taxRate),
            taxRateAsPercent = toTaxRateAsPercent(userPreferences.taxRate),
            minMoneyFractionDigits = toMinMoneyFractionDigits(userPreferences.moneyDecimalFormat),
            minQuantityFractionDigits = toMinQuantityFractionDigits(userPreferences.quantityDecimalFormat),
            maxMoneyFractionDigits = toMaxMoneyFractionDigits(userPreferences.moneyDecimalFormat),
            maxQuantityFractionDigits = toMaxQuantityFractionDigits(userPreferences.quantityDecimalFormat),
        )
    }

    fun toNightTheme(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.NIGHT_THEME
    }

    fun toFontSize(value: String?): FontSize {
        return FontSize.valueOfOrDefault(value)
    }

    fun toFontSizeString(value: FontSize): String {
        return value.toString()
    }

    fun toMultiColumns(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.MULTI_COLUMNS
    }

    fun toDisplayCompleted(value: String?): DisplayCompleted {
        return DisplayCompleted.valueOfOrDefault(value)
    }

    fun toDisplayCompletedString(value: DisplayCompleted): String {
        return value.toString()
    }

    fun toDisplayTotal(value: String?): DisplayTotal {
        return DisplayTotal.valueOfOrDefault(value)
    }

    fun toDisplayTotalString(displayTotal: DisplayTotal): String {
        return displayTotal.toString()
    }

    fun toDisplayOtherFields(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.DISPLAY_OTHER_FIELDS
    }

    fun toColoredCheckbox(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.COLORED_CHECKBOX
    }

    fun toDisplayShoppingsProducts(value: String?): DisplayProducts {
        return DisplayProducts.valueOfOrDefault(value)
    }

    fun toDisplayShoppingsProductsString(value: DisplayProducts): String {
        return value.toString()
    }

    fun toPurchasesSeparator(value: String?): String {
        return value ?: UserPreferencesDefaults.PURCHASES_SEPARATOR
    }

    fun toEditProductAfterCompleted(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.EDIT_PRODUCT_AFTER_COMPLETED
    }

    fun toLockProductElement(value: String?): LockProductElement {
        return LockProductElement.valueOfOrDefault(value)
    }

    fun toLockProductString(value: LockProductElement): String {
        return value.toString()
    }

    fun toCompletedWithCheckbox(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.COMPLETED_WITH_CHECKBOX
    }

    fun toEnterToSaveProduct(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.ENTER_TO_SAVE_PRODUCTS
    }

    fun toDisplayDefaultAutocompletes(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.DISPLAY_DEFAULT_AUTOCOMPLETES
    }

    fun toMaxAutocompleteNames(value: Int?): Int {
        return value ?: UserPreferencesDefaults.MAX_AUTOCOMPLETES_NAMES
    }

    fun toMaxAutocompleteQuantities(value: Int?): Int {
        return value ?: UserPreferencesDefaults.MAX_AUTOCOMPLETES_QUANTITIES
    }

    fun toMaxAutocompleteMoneys(value: Int?): Int {
        return value ?: UserPreferencesDefaults.MAX_AUTOCOMPLETES_MONEYS
    }

    fun toMaxAutocompleteOthers(value: Int?): Int {
        return value ?: UserPreferencesDefaults.MAX_AUTOCOMPLETES_OTHERS
    }

    fun toSaveProductToAutocompletes(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.SAVE_PRODUCT_TO_AUTOCOMPLETES
    }

    fun toDisplayMoney(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.DISPLAY_MONEY
    }

    fun toCurrency(symbol: String?, displayToLeft: Boolean?): Currency {
        return if (symbol == null || displayToLeft == null) {
            UserPreferencesDefaults.CURRENCY
        } else {
            Currency(symbol, displayToLeft)
        }
    }

    fun toCurrencySymbol(value: Currency): String {
        return value.symbol
    }

    fun toCurrencyDisplayToLeft(value: Currency): Boolean {
        return value.displayToLeft
    }

    fun toTaxRateValue(value: Money): Float {
        return value.value
    }

    fun toTaxRateAsPercent(value: Money): Boolean {
        return value.asPercent
    }

    fun toDiscountValue(value: Money): Float {
        return value.value
    }

    fun toDiscountAsPercent(value: Money): Boolean {
        return value.asPercent
    }

    fun toMoneyDecimalFormat(minFractionDigits: Int?, maxFractionDigits: Int?): DecimalFormat {
        return UserPreferencesDefaults.getMoneyDecimalFormat().apply {
            minFractionDigits?.let { minimumFractionDigits = it }
            maxFractionDigits?.let { maximumFractionDigits = it }
        }
    }

    fun toMinMoneyFractionDigits(value: DecimalFormat): Int {
        return value.minimumFractionDigits
    }

    fun toMaxMoneyFractionDigits(value: DecimalFormat): Int {
        return value.maximumFractionDigits
    }

    fun toQuantityDecimalFormat(minFractionDigits: Int?, maxFractionDigits: Int?): DecimalFormat {
        return UserPreferencesDefaults.getQuantityDecimalFormat().apply {
            minFractionDigits?.let { minimumFractionDigits = it }
            maxFractionDigits?.let { maximumFractionDigits = it }
        }
    }

    fun toMinQuantityFractionDigits(decimalFormat: DecimalFormat): Int {
        return decimalFormat.minimumFractionDigits
    }

    fun toMaxQuantityFractionDigits(decimalFormat: DecimalFormat): Int {
        return decimalFormat.maximumFractionDigits
    }

    //
    // CODE VERSION 14
    //

    fun toCodeVersion14(
        shoppingListsCursor: Cursor,
        productsCursor: Cursor,
        autocompletesCursor: Cursor,
        defaultAutocompleteNames: List<String>,
        preferences: CodeVersion14UserPreferencesEntity
    ): CodeVersion14 {
        val shoppingLists = mutableListOf<ShoppingList>()
        val autocompletes = mutableListOf<Autocomplete>()
        while (shoppingListsCursor.moveToNext()) {
            val shoppingList = toShoppingList(shoppingListsCursor)

            val products = mutableListOf<Product>()
            while (productsCursor.moveToNext()) {
                val product = toProduct(productsCursor, preferences)
                if (product.shoppingUid == shoppingList.uid) {
                    products.add(product)

                    if (preferences.saveProductToAutocompletes == true) {
                        val personal = isPersonalAutocomplete(defaultAutocompleteNames, product.name)
                        val autocomplete = toAutocomplete(product, personal)
                        autocompletes.add(autocomplete)
                    }
                }
            }
            shoppingLists.add(
                shoppingList.copy(
                    products = products.formatAppVersion14Products(
                        preferences.sort ?: 0,
                        preferences.firstLetterUppercase ?: false
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

        return CodeVersion14(
            shoppingLists = shoppingLists.formatAppVersion14ShoppingLists(
                preferences.sort ?: 0,
                preferences.firstLetterUppercase ?: false
            ).toList(),
            autocompletes = autocompletes.toList(),
            preferences = toCodeVersion14Preferences(preferences)
        )
    }

    fun toCodeVersion14Preferences(entity: CodeVersion14UserPreferencesEntity): CodeVersion14Preferences {
        return CodeVersion14Preferences(
            firstOpened = entity.firstOpened ?: false,
            currency = toCurrency(entity.currency, entity.displayCurrencyToLeft),
            taxRate = toMoney(
                value = entity.taxRate,
                asPercent = true,
                userPreferences = entity
            ),
            fontSize = toFontSize(entity.titleFontSize ?: 18),
            multiColumns = toMultiColumns(entity.columnCount ?: 1),
            displayMoney = entity.displayMoney ?: true,
            displayTotal = toDisplayTotal(entity.displayTotal ?: 0),
            editProductAfterCompleted = entity.editProductAfterCompleted ?: false,
            saveProductToAutocompletes = entity.saveProductToAutocompletes ?: false
        )
    }
}
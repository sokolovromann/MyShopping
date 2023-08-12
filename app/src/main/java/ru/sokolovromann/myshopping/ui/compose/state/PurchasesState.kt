package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.calculateTotalToText
import ru.sokolovromann.myshopping.ui.utils.getActivePinnedShoppingListItems
import ru.sokolovromann.myshopping.ui.utils.getOtherShoppingListItems
import java.util.UUID

class PurchasesState {

    private var shoppingLists by mutableStateOf(ShoppingLists())

    private var savedSelectedUid by mutableStateOf("")

    var screenData by mutableStateOf(PurchasesScreenData())
        private set

    fun showLoading() {
        screenData = PurchasesScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(appConfig: AppConfig) {
        shoppingLists = ShoppingLists(appConfig = appConfig)
        val preferences = shoppingLists.appConfig.userPreferences

        val totalText: UiText = if (preferences.displayMoney) {
            shoppingLists.calculateTotalToText()
        } else {
            UiText.Nothing
        }

        val multiColumnsText: UiText = if (preferences.shoppingsMultiColumns) {
            UiText.FromResources(R.string.shoppingLists_action_disableShoppingsMultiColumns)
        } else {
            UiText.FromResources(R.string.shoppingLists_action_enableShoppingsMultiColumns)
        }

        screenData = PurchasesScreenData(
            screenState = ScreenState.Nothing,
            displayProducts = preferences.displayShoppingsProducts,
            displayCompleted = preferences.displayCompleted,
            coloredCheckbox = preferences.coloredCheckbox,
            totalText = totalText,
            multiColumnsText = multiColumnsText,
            smartphoneScreen = shoppingLists.appConfig.deviceConfig.getDeviceSize() == DeviceSize.Medium,
            displayTotal = preferences.displayTotal,
            fontSize = preferences.fontSize
        )
    }

    fun showShoppingLists(shoppingLists: ShoppingLists) {
        this.shoppingLists = shoppingLists
        val preferences = shoppingLists.appConfig.userPreferences

        val totalText: UiText = if (preferences.displayMoney) {
            shoppingLists.calculateTotalToText()
        } else {
            UiText.Nothing
        }

        val multiColumnsText: UiText = if (preferences.shoppingsMultiColumns) {
            UiText.FromResources(R.string.shoppingLists_action_disableShoppingsMultiColumns)
        } else {
            UiText.FromResources(R.string.shoppingLists_action_enableShoppingsMultiColumns)
        }

        val showHiddenShoppingLists = preferences.displayCompleted == DisplayCompleted.HIDE
                && shoppingLists.hasHiddenShoppingLists()

        val selectedUids = if (savedSelectedUid.isEmpty()) {
            null
        } else {
            listOf(savedSelectedUid)
        }

        screenData = PurchasesScreenData(
            screenState = ScreenState.Showing,
            pinnedShoppingLists = shoppingLists.getActivePinnedShoppingListItems(),
            otherShoppingLists = shoppingLists.getOtherShoppingListItems(),
            displayProducts = preferences.displayShoppingsProducts,
            displayCompleted = preferences.displayCompleted,
            coloredCheckbox = preferences.coloredCheckbox,
            totalText = totalText,
            multiColumns = preferences.shoppingsMultiColumns,
            multiColumnsText = multiColumnsText,
            smartphoneScreen = shoppingLists.appConfig.deviceConfig.getDeviceSize() == DeviceSize.Medium,
            displayTotal = preferences.displayTotal,
            fontSize = preferences.fontSize,
            showHiddenShoppingLists = showHiddenShoppingLists,
            selectedUids = selectedUids
        )
    }

    fun showPurchasesMenu() {
        screenData = screenData.copy(showPurchasesMenu = true)
    }

    fun showSort() {
        screenData = screenData.copy(
            showSort = true,
            showPurchasesMenu = false
        )
    }

    fun showSelectedMenu() {
        screenData = screenData.copy(showSelectedMenu = true)
    }

    fun displayHiddenShoppingLists() {
        screenData = screenData.copy(
            otherShoppingLists = shoppingLists.getOtherShoppingListItems(DisplayCompleted.LAST),
            showHiddenShoppingLists = false
        )
    }

    fun selectDisplayPurchasesTotal() {
        screenData = screenData.copy(showDisplayTotal = true)
    }

    fun selectShoppingList(uid: String) {
        val uids = (screenData.selectedUids?.toMutableList() ?: mutableListOf())
            .apply { add(uid) }

        val totalText = if (shoppingLists.appConfig.userPreferences.displayMoney) {
            shoppingLists.calculateTotalToText(uids)
        } else {
            UiText.Nothing
        }

        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = uids
        )
    }

    fun selectAllShoppingLists() {
        val uids = shoppingLists.shoppingLists.map { it.uid }

        val totalText = if (shoppingLists.appConfig.userPreferences.displayMoney) {
            shoppingLists.calculateTotalToText(uids)
        } else {
            UiText.Nothing
        }

        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = uids,
            showSelectedMenu = false
        )
    }

    fun unselectShoppingList(uid: String) {
        savedSelectedUid = ""

        val uids = (screenData.selectedUids?.toMutableList() ?: mutableListOf())
            .apply { remove(uid) }
        val checkedUids = if (uids.isEmpty()) null else uids

        val totalText = if (shoppingLists.appConfig.userPreferences.displayMoney) {
            if (checkedUids == null) {
                shoppingLists.calculateTotalToText()
            } else {
                shoppingLists.calculateTotalToText(checkedUids)
            }
        } else {
            UiText.Nothing
        }

        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = checkedUids
        )
    }

    fun unselectAllShoppingLists() {
        val totalText = if (shoppingLists.appConfig.userPreferences.displayMoney) {
            shoppingLists.calculateTotalToText()
        } else {
            UiText.Nothing
        }

        savedSelectedUid = ""
        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = null
        )
    }

    fun hideDisplayPurchasesTotal() {
        screenData = screenData.copy(showDisplayTotal = false)
    }

    fun hidePurchasesMenu() {
        screenData = screenData.copy(showPurchasesMenu = false)
    }

    fun hideSort() {
        screenData = screenData.copy(showSort = false)
    }

    fun hideSelectedMenu() {
        screenData = screenData.copy(showSelectedMenu = false)
    }

    fun sortShoppingListsResult(sortBy: SortBy): Result<List<ShoppingList>> {
        val sortShoppingLists = shoppingLists.shoppingLists.sortShoppingLists(sort = Sort(sortBy))
        return if (sortShoppingLists.isEmpty()) {
            Result.failure(Exception())
        } else {
            val success = sortShoppingLists.mapIndexed { index, shoppingList ->
                shoppingList.copy(
                    position = index,
                    lastModified = System.currentTimeMillis()
                )
            }
            Result.success(success)
        }
    }

    fun reverseSortShoppingListsResult(): Result<List<ShoppingList>> {
        val sortShoppingLists = shoppingLists.getAllShoppingLists().reversed()
        return if (sortShoppingLists.isEmpty()) {
            Result.failure(Exception())
        } else {
            val success = sortShoppingLists.mapIndexed { index, shoppingList ->
                shoppingList.copy(
                    position = index,
                    lastModified = System.currentTimeMillis()
                )
            }
            Result.success(success)
        }
    }

    fun getShoppingListResult(): Result<ShoppingList> {
        return if (shoppingLists.shoppingListsLastPosition == null) {
            Result.failure(Exception())
        } else {
            val position = shoppingLists.shoppingListsLastPosition?.plus(1) ?: 0
            val success = ShoppingList(position = position)
            Result.success(success)
        }
    }

    fun getCopyShoppingListsResult(): Result<List<ShoppingList>> {
        return if (screenData.selectedUids == null) {
            Result.failure(Exception())
        } else {
            val success = mutableListOf<ShoppingList>()
            screenData.selectedUids?.forEach { uid ->
                val shoppingUid = UUID.randomUUID().toString()
                val created = System.currentTimeMillis()
                val selectedShoppingList = shoppingLists.shoppingLists.find { it.uid == uid } ?: ShoppingList()
                val newShoppingList = selectedShoppingList.copy(
                    id = 0,
                    position = shoppingLists.shoppingListsLastPosition?.plus(1) ?: 0,
                    uid = shoppingUid,
                    created = created,
                    lastModified = created,
                    products = selectedShoppingList.products.map {
                        it.copy(
                            id = 0,
                            shoppingUid = shoppingUid,
                            productUid = UUID.randomUUID().toString(),
                            created = created,
                            lastModified = created
                        )
                    }
                )
                success.add(newShoppingList)
            }
            Result.success(success)
        }
    }

    fun getShoppingListsUpResult(uid: String): Result<Pair<ShoppingList, ShoppingList>> {
        val allShoppingList = shoppingLists.getAllShoppingLists()
        return if (allShoppingList.size < 2) {
            Result.failure(Exception())
        } else {
            savedSelectedUid = uid

            var previousIndex = 0
            var currentIndex = 0
            for (index in allShoppingList.indices) {
                val shoppingList = allShoppingList[index]
                if (currentIndex > 0) {
                    previousIndex = index - 1
                }
                currentIndex = index

                if (shoppingList.uid == uid) {
                    break
                }
            }

            val lastModified = System.currentTimeMillis()
            val currentShoppingList = allShoppingList[currentIndex].copy(
                position = allShoppingList[previousIndex].position,
                lastModified = lastModified
            )
            val previousShoppingList = allShoppingList[previousIndex].copy(
                position = allShoppingList[currentIndex].position,
                lastModified = lastModified
            )

            val success = Pair(currentShoppingList, previousShoppingList)
            Result.success(success)
        }
    }

    fun getShoppingListsDownResult(uid: String): Result<Pair<ShoppingList, ShoppingList>> {
        val allShoppingList = shoppingLists.getAllShoppingLists()
        return if (allShoppingList.size < 2) {
            Result.failure(Exception())
        } else {
            savedSelectedUid = uid

            var currentIndex = 0
            var nextIndex = 0
            for (index in allShoppingList.indices) {
                val shoppingList = allShoppingList[index]

                currentIndex = index
                if (index < allShoppingList.lastIndex) {
                    nextIndex = index + 1
                }

                if (shoppingList.uid == uid) {
                    break
                }
            }

            val lastModified = System.currentTimeMillis()
            val currentShoppingList = allShoppingList[currentIndex].copy(
                position = allShoppingList[nextIndex].position,
                lastModified = lastModified
            )
            val nextShoppingList = allShoppingList[nextIndex].copy(
                position = allShoppingList[currentIndex].position,
                lastModified = lastModified
            )

            val success = Pair(currentShoppingList, nextShoppingList)
            Result.success(success)
        }
    }
}

data class PurchasesScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val pinnedShoppingLists: List<ShoppingListItem> = listOf(),
    val otherShoppingLists: List<ShoppingListItem> = listOf(),
    val displayProducts: DisplayProducts = DisplayProducts.DefaultValue,
    val displayCompleted: DisplayCompleted = DisplayCompleted.DefaultValue,
    val coloredCheckbox: Boolean = false,
    val totalText: UiText = UiText.Nothing,
    val multiColumns: Boolean = false,
    val multiColumnsText: UiText = UiText.Nothing,
    val smartphoneScreen: Boolean = true,
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val showDisplayTotal: Boolean = false,
    val showPurchasesMenu: Boolean = false,
    val showSort: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val showHiddenShoppingLists: Boolean = false,
    val selectedUids: List<String>? = null,
    val showSelectedMenu: Boolean = false
) {

    fun isOnlyPinned(): Boolean {
        var notPinned = false
        selectedUids?.forEach { uid ->
            if (otherShoppingLists.find { it.uid == uid } != null) {
                notPinned = true
                return@forEach
            }
        }
        return pinnedShoppingLists.isNotEmpty() && !notPinned
    }
}
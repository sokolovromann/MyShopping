package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.model.ShoppingList
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.data.model.mapper.ShoppingListsMapper
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.CopyProductScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.CopyProductEvent
import javax.inject.Inject

@HiltViewModel
class CopyProductViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<CopyProductEvent> {

    val copyProductState = CopyProductState()

    private val _screenEventFlow: MutableSharedFlow<CopyProductScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<CopyProductScreenEvent> = _screenEventFlow

    init {
        getPurchases()
        getProducts()
    }

    override fun onEvent(event: CopyProductEvent) {
        when (event) {
            CopyProductEvent.AddShoppingList -> addShoppingList()

            is CopyProductEvent.CopyProduct -> copyProduct(event)

            CopyProductEvent.SelectShoppingListLocation -> selectShoppingListLocation()

            CopyProductEvent.DisplayHiddenShoppingLists -> displayHiddenShoppingLists()

            is CopyProductEvent.ShowShoppingLists -> showShoppingLists(event)

            CopyProductEvent.CancelCopingProduct -> cancelCopingProduct()

            CopyProductEvent.HideShoppingListsLocation -> hideShoppingListsLocation()
        }
    }

    private fun getPurchases() = viewModelScope.launch {
        withContext(dispatchers.main) {
            copyProductState.showLoading()
        }

        shoppingListsRepository.getPurchasesWithConfig().collect {
            shoppingListsLoaded(
                shoppingListsWithConfig = it,
                location = ShoppingListLocation.PURCHASES
            )
        }
    }

    private fun getArchive() = viewModelScope.launch {
        withContext(dispatchers.main) {
            copyProductState.showLoading()
        }

        shoppingListsRepository.getArchiveWithConfig().collect {
            shoppingListsLoaded(
                shoppingListsWithConfig = it,
                location = ShoppingListLocation.ARCHIVE
            )
        }
    }

    private suspend fun shoppingListsLoaded(
        shoppingListsWithConfig: ShoppingListsWithConfig,
        location: ShoppingListLocation
    ) = withContext(dispatchers.main) {
        val shoppingLists = ShoppingListsMapper.toShoppingLists(shoppingListsWithConfig)
        if (shoppingLists.isShoppingListsEmpty()) {
            copyProductState.showNotFound(shoppingLists, location)
        } else {
            copyProductState.showShoppingLists(shoppingLists, location)
        }
    }

    private fun getProducts() = viewModelScope.launch {
        val productUid: String = savedStateHandle.get<String>(UiRouteKey.ProductUid.key) ?: ""
        val uids = productUid.split(",")
        val products = shoppingListsRepository.getProducts(uids).firstOrNull()

        if (products == null) {
            cancelCopingProduct()
        } else {
            withContext(dispatchers.main) {
                val repositoryProducts = ShoppingListsMapper.toRepositoryProductList(products)
                copyProductState.saveProducts(repositoryProducts)
            }
        }
    }

    private fun addShoppingList() = viewModelScope.launch {
        copyProductState.getShoppingListResult()
            .onSuccess {
                val shoppingList = ShoppingListsMapper.toShoppingList(it)
                shoppingListsRepository.saveShoppingList(shoppingList)
            }
            .onFailure {
                shoppingListsRepository.saveShoppingList(ShoppingList())
            }
    }

    private fun copyProduct(event: CopyProductEvent.CopyProduct) = viewModelScope.launch {
        copyProductState.selectShoppingList(event.uid)
        val repositoryProducts = copyProductState.getProductsResult()
            .getOrElse { return@launch }

        val products = ShoppingListsMapper.toProductList(repositoryProducts)
        shoppingListsRepository.saveProducts(products)

        withContext(dispatchers.main) {
            val screenEvent = CopyProductScreenEvent.ShowBackScreenAndUpdateProductsWidgets
            _screenEventFlow.emit(screenEvent)
        }
    }

    private fun selectShoppingListLocation() {
        copyProductState.showLocation()
    }

    private fun displayHiddenShoppingLists() {
        copyProductState.displayHiddenShoppingLists()
    }

    private fun showShoppingLists(event: CopyProductEvent.ShowShoppingLists) {
        hideShoppingListsLocation()

        when (event.location) {
            ShoppingListLocation.PURCHASES -> getPurchases()
            ShoppingListLocation.ARCHIVE -> getArchive()
            else -> {}
        }
    }

    private fun cancelCopingProduct() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(CopyProductScreenEvent.ShowBackScreen)
    }

    private fun hideShoppingListsLocation() {
        copyProductState.hideLocation()
    }
}
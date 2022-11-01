package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class ListState<D> {

    var currentData by mutableStateOf(ListData<D>())
        private set

    fun showList(items: List<D>, multiColumns: Boolean) {
        currentData = ListData(
            items = items,
            multiColumns = multiColumns,
            result = ListResult.Showing
        )
    }

    fun showLoading() {
        currentData = ListData(result = ListResult.Loading)
    }

    fun showNotFound(text: TextData) {
        currentData = ListData(
            notFoundText = text,
            result = ListResult.NotFound
        )
    }

    fun showMenu(uid: String) {
        currentData = currentData.copy(menuUid = uid)
    }

    fun hideMenu() {
        currentData = currentData.copy(menuUid = null)
    }

    fun hideAll() {
        currentData = ListData()
    }
}

data class ListData<D>(
    val items: List<D> = listOf(),
    val notFoundText: TextData = TextData.Title,
    val multiColumns: Boolean = false,
    val maxColumnWidth: Dp = 300.dp,
    val menuUid: String? = null,
    val result: ListResult = ListResult.Nothing
)

enum class ListResult {
    Showing, NotFound, Loading, Nothing
}
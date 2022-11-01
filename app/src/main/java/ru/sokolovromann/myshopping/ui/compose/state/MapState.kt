package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class MapState<K,D> {

    var currentData by mutableStateOf(MapData<K,D>())
        private set

    fun showMap(items: Map<K,D>, multiColumns: Boolean) {
        currentData = MapData(
            items = items,
            multiColumns = multiColumns,
            result = MapResult.Showing
        )
    }

    fun showLoading() {
        currentData = MapData(result = MapResult.Loading)
    }

    fun showNotFound(text: TextData) {
        currentData = MapData(
            notFoundText = text,
            result = MapResult.NotFound
        )
    }

    fun showMenu(uid: String) {
        currentData = currentData.copy(menuUid = uid)
    }

    fun hideMenu() {
        currentData = currentData.copy(menuUid = null)
    }

    fun hideAll() {
        currentData = MapData()
    }
}

data class MapData<K,D>(
    val items: Map<K,D> = mapOf(),
    val notFoundText: TextData = TextData.Title,
    val multiColumns: Boolean = false,
    val maxColumnWidth: Dp = 300.dp,
    val menuUid: String? = null,
    val result: MapResult = MapResult.Nothing
)

enum class MapResult {
    Showing, NotFound, Loading, Nothing
}
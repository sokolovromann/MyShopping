package ru.sokolovromann.myshopping.core.data.datasource

import android.os.Environment

object LocalEnvironment {

    val ROOT_DIRECTORY: String = "${Environment.getExternalStorageDirectory()}/MyShoppingList"
}
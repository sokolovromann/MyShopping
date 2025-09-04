package ru.sokolovromann.myshopping.io

import android.os.Environment
import ru.sokolovromann.myshopping.data.local.datasource.AppContent
import java.io.File

object LocalEnvironment {

    val ROOT_DIRECTORY: String = "${Environment.getExternalStorageDirectory()}/MyShopping"

    val ABSOLUTE_OLD_ROOT_DIRECTORY: String = "${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_DOCUMENTS}/MyShoppingList"

    val RELATIVE_OLD_ROOT_DIRECTORY = "${Environment.DIRECTORY_DOCUMENTS}/MyShoppingList"

    fun createFilePath(
        fileName: String,
        rootDirectory: String = ROOT_DIRECTORY
    ): String = File(rootDirectory, fileName).path
}
package ru.sokolovromann.myshopping.data39

import android.os.Environment
import ru.sokolovromann.myshopping.R
import java.io.File

object LocalEnvironment {

    val ROOT_DIRECTORY: String = "${Environment.getExternalStorageDirectory()}/MyShopping"

    val ABSOLUTE_OLD_ROOT_DIRECTORY: String = "${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_DOCUMENTS}/MyShoppingList"

    val RELATIVE_OLD_ROOT_DIRECTORY = "${Environment.DIRECTORY_DOCUMENTS}/MyShoppingList"

    val DEFAULT_AUTOCOMPLETES_RES_ID = R.array.data_text_defaultAutocompleteNames

    fun createFilePath(
        fileName: String,
        rootDirectory: String = ROOT_DIRECTORY
    ): String = File(rootDirectory, fileName).path
}
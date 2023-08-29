package ru.sokolovromann.myshopping.data.local.dao

import android.database.Cursor
import ru.sokolovromann.myshopping.data.local.datasource.AppSQLiteOpenHelper

class CodeVersion14Dao(openHelper: AppSQLiteOpenHelper) {

    private val database = openHelper.readableDatabase

    fun getShoppingsCursor(): Cursor {
        val query = "SELECT * FROM list"
        return database.rawQuery(query, null)
    }

    fun getProductsCursor(): Cursor {
        val query = "SELECT * FROM goods"
        return database.rawQuery(query, null)
    }

    fun getAutocompletesCursor(): Cursor {
        val query = "SELECT * FROM complete"
        return database.rawQuery(query, null)
    }
}
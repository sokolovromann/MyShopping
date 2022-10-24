package ru.sokolovromann.myshopping.data.local.datasource

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import javax.inject.Inject

class AppVersion14LocalDatabase @Inject constructor(
    private val context: Context
) {

    private val databaseHelper: DatabaseHelper = DatabaseHelper(context)
    private val database: SQLiteDatabase = databaseHelper.readableDatabase

    fun getShoppings(): Cursor {
        val query = "SELECT * FROM list"
        return database.rawQuery(query, null)
    }

    fun getProducts(): Cursor {
        val query = "SELECT * FROM goods"
        return database.rawQuery(query, null)
    }

    fun getAutocompletes(): Cursor {
        val query = "SELECT * FROM complete"
        return database.rawQuery(query, null)
    }

    private class DatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, "database", null, 22) {

        override fun onCreate(db: SQLiteDatabase?) {}
        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
    }
}
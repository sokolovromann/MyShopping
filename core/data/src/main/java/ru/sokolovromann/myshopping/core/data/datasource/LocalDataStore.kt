package ru.sokolovromann.myshopping.core.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import jakarta.inject.Inject

class LocalDataStore @Inject constructor(private val context: Context) {

    private val Context.general: DataStore<Preferences> by preferencesDataStore(
        name = GeneralPreferencesScheme.FILE_NAME
    )

    private val Context.carts: DataStore<Preferences> by preferencesDataStore(
        name = CartsPreferencesScheme.FILE_NAME
    )

    private val Context.products: DataStore<Preferences> by preferencesDataStore(
        name = ProductsPreferencesScheme.FILE_NAME
    )

    private val Context.productsWidget: DataStore<Preferences> by preferencesDataStore(
        name = ProductsWidgetPreferencesScheme.FILE_NAME
    )

    private val Context.addEditProduct: DataStore<Preferences> by preferencesDataStore(
        name = AddEditProductPreferencesScheme.FILE_NAME
    )

    private val Context.suggestions: DataStore<Preferences> by preferencesDataStore(
        name = SuggestionsPreferencesScheme.FILE_NAME
    )

    private val Context.backup: DataStore<Preferences> by preferencesDataStore(
        name = BackupPreferencesScheme.FILE_NAME
    )

    private val Context.userConfig: DataStore<Preferences> by preferencesDataStore(
        name = UserConfigScheme.FILE_NAME
    )

    fun getGeneralPreferencesDataStore(): DataStore<Preferences> = context.general

    fun getCartsPreferencesDataStore(): DataStore<Preferences> = context.carts

    fun getProductsPreferencesDataStore(): DataStore<Preferences> = context.products

    fun getProductsWidgetPreferencesDataStore(): DataStore<Preferences> = context.productsWidget

    fun getAddEditProductPreferencesDataStore(): DataStore<Preferences> = context.addEditProduct

    fun getSuggestionsPreferencesDataStore(): DataStore<Preferences> = context.suggestions

    fun getBackupPreferencesDataStore(): DataStore<Preferences> = context.backup

    fun getUserConfig(): DataStore<Preferences> = context.userConfig
}
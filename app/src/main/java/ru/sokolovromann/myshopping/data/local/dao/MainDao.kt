package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity
import ru.sokolovromann.myshopping.data.local.entity.ProductEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingEntity

@Dao
interface MainDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShopping(shoppingEntity: ShoppingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(productEntity: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAutocomplete(autocompleteEntity: AutocompleteEntity)
}
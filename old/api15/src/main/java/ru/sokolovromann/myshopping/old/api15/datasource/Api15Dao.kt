package ru.sokolovromann.myshopping.old.api15.datasource

import androidx.room.Dao
import androidx.room.Query
import ru.sokolovromann.myshopping.old.api15.model.Api15AutocompleteEntity
import ru.sokolovromann.myshopping.old.api15.model.Api15ProductEntity
import ru.sokolovromann.myshopping.old.api15.model.Api15ShoppingEntity
import ru.sokolovromann.myshopping.old.api15.model.Api39SuggestionDetailEntity
import ru.sokolovromann.myshopping.old.api15.model.Api39SuggestionEntity

@Dao
interface Api15Dao {

    @Query("SELECT * FROM shoppings")
    fun getShoppings(): List<Api15ShoppingEntity>

    @Query("SELECT * FROM products")
    fun getProducts(): List<Api15ProductEntity>

    @Query("SELECT * FROM autocompletes")
    fun getAutocompletes(): List<Api15AutocompleteEntity>

    @Query("SELECT * FROM api39_suggestions")
    fun getSuggestions(): List<Api39SuggestionEntity>

    @Query("SELECT * FROM api39_suggestion_details")
    fun getSuggestionDetails(): List<Api39SuggestionDetailEntity>
}
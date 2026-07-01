package ru.sokolovromann.myshopping.core.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import ru.sokolovromann.myshopping.core.data.model.FabricEntity

@Dao
interface FabricsDao {

    @Insert(onConflict = REPLACE)
    fun insertFabrics(fabrics: List<FabricEntity>)

    @Query("DELETE FROM fabrics WHERE directory = :directory")
    fun deleteFabrics(directory: String)

    @Query("DELETE FROM fabrics WHERE uid IN(:uids)")
    fun deleteFabrics(uids: Collection<String>)

    @Query("DELETE FROM fabrics")
    fun clearFabrics()
}
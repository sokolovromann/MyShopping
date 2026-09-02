package ru.sokolovromann.myshopping.core.data.datasource

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import jakarta.inject.Inject
import jakarta.inject.Provider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.core.data.R
import ru.sokolovromann.myshopping.core.data.model.SuggestionEntity
import ru.sokolovromann.myshopping.core.data.old.Api15RoomMigrationManager
import ru.sokolovromann.myshopping.core.domain.model.SuggestionDirectory
import ru.sokolovromann.myshopping.core.domain.model.TimeInMillis
import ru.sokolovromann.myshopping.core.domain.model.UID

class LocalRoomDatabaseCallback @Inject constructor(
    private val context: Context,
    private val suggestionsDaoProvider: Provider<SuggestionsDao>,
    private val api15RoomMigrationManager: Provider<Api15RoomMigrationManager>,
    private val applicationScope: CoroutineScope,
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        applicationScope.launch(Dispatchers.IO) {
            val migrationManager = api15RoomMigrationManager.get()
            if (migrationManager.exists()) {
                migrationManager.apply {
                    migrate()
                    deleteDatabase()
                }
            } else {
                populateDefaultData()
            }
        }
    }

    private fun populateDefaultData() = applicationScope.launch(Dispatchers.IO) {
        val suggestions = context.resources.getStringArray(R.array.data_default_suggestion_names)
            .map { name ->
                val timeInMillis = TimeInMillis.getCurrent().value.toString()
                SuggestionEntity(
                    uid = UID.createRandom().value,
                    directory = SuggestionDirectory.NoDirectory.toString(),
                    created = timeInMillis,
                    lastModified = timeInMillis,
                    name = name,
                    used = "0"
                )
            }
        suggestionsDaoProvider.get().insertSuggestions(suggestions)
    }
}
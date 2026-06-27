package ru.sokolovromann.myshopping.core.data.mapper

import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.model.SuggestionEntity
import ru.sokolovromann.myshopping.core.domain.model.Suggestion
import ru.sokolovromann.myshopping.core.domain.model.SuggestionDirectory
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.utils.EnumUtils

@Singleton
class SuggestionsMapper @Inject constructor() : RoomDatabaseMapper<SuggestionEntity, Suggestion>() {

    override fun toEntity(model: Suggestion) = SuggestionEntity(
        model.uid.value,
        model.directory.toString(),
        model.created.value.toString(),
        model.lastModified.value.toString(),
        model.name,
        model.used.toString()
    )

    override fun toModel(entity: SuggestionEntity) = Suggestion(
        UID(entity.uid),
        EnumUtils.valueOfOrDefault(
            entity.directory,
            SuggestionDirectory.NoDirectory
        ),
        toTimeInMillisOrCurrent(entity.created),
        toTimeInMillisOrCurrent(entity.lastModified),
        entity.name,
        entity.used.toIntOrNull() ?: 0
    )
}
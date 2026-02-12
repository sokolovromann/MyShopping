package ru.sokolovromann.myshopping.data39.suggestions

import ru.sokolovromann.myshopping.data39.LocalRoomMapper
import javax.inject.Inject

class SuggestionsMapper @Inject constructor(
    private val detailsMapper: SuggestionDetailsMapper
) : LocalRoomMapper<SuggestionRoomEntity, Suggestion>() {

    override fun toEntity(model: Suggestion): SuggestionRoomEntity {
        return SuggestionRoomEntity(
            uid = fromUid(model.uid),
            directory = model.directory.toString(),
            created = fromDateTime(model.created),
            lastModified = fromDateTime(model.lastModified),
            name = model.name,
            used = model.used.toString()
        )
    }

    override fun fromEntity(entity: SuggestionRoomEntity): Suggestion {
        return Suggestion(
            uid = toUid(entity.uid),
            directory = try {
                enumValueOf(entity.directory)
            } catch (_: Exception) { SuggestionDirectory.NoDirectory },
            created = toDateTime(entity.created),
            lastModified = toDateTime(entity.lastModified),
            name = entity.name,
            used = entity.used.toIntOrNull() ?: 0
        )
    }

    fun fromEntityWithDetails(entity: SuggestionWithDetailsRoomEntity): SuggestionWithDetails {
        return SuggestionWithDetails(
            suggestion = fromEntity(entity.suggestion),
            details = detailsMapper.toDetails(entity.details)
        )
    }

    fun fromEntitiesWithDetails(
        entities: Collection<SuggestionWithDetailsRoomEntity>
    ): Collection<SuggestionWithDetails> {
        return entities.map { fromEntityWithDetails(it) }
    }
}
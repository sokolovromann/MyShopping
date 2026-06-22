package ru.sokolovromann.myshopping.core.data.mapper

import ru.sokolovromann.myshopping.core.data.model.SuggestionWithFabricsEntity
import ru.sokolovromann.myshopping.core.domain.model.SuggestionWithFabrics

class SuggestionsWithFabricsMapper(
    private val suggestionsMapper: SuggestionsMapper,
    private val fabricsMapper: FabricsMapper
) : RoomDatabaseMapper<SuggestionWithFabricsEntity, SuggestionWithFabrics>() {

    override fun toEntity(model: SuggestionWithFabrics) = SuggestionWithFabricsEntity(
        suggestionsMapper.toEntity(model.suggestion),
        fabricsMapper.toEntities(model.fabrics)
    )

    override fun toModel(entity: SuggestionWithFabricsEntity) = SuggestionWithFabrics(
        suggestionsMapper.toModel(entity.suggestion),
        fabricsMapper.toModels(entity.fabrics)
    )
}
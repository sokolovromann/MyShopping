package ru.sokolovromann.myshopping.data39

abstract class LocalRoomMapper<E,M> : LocalDataMapper() {

    abstract fun toEntity(model: M): E

    abstract fun fromEntity(entity: E): M

    fun toEntities(models: Collection<M>): List<E> {
        return models.map { toEntity(it) }.toList()
    }

    fun fromEntities(entities: Collection<E>): Collection<M> {
        return entities.map { fromEntity(it) }
    }
}
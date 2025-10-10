package ru.sokolovromann.myshopping.data39

abstract class Mapper <E, M> {

    abstract fun mapEntityTo(entity: E): M

    abstract fun mapEntityFrom(model: M): E

    fun mapEntitiesTo(collection: Collection<E>): Collection<M> {
        return collection.map { mapEntityTo(it) }
    }

    fun mapEntitiesFrom(collection: Collection<M>): Collection<E> {
        return collection.map { mapEntityFrom(it) }
    }
}
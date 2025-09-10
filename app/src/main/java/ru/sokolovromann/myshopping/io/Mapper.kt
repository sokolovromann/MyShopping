package ru.sokolovromann.myshopping.io

abstract class Mapper<A,B> {

    abstract fun mapTo(a: A): B

    fun mapCollectionTo(collection: Collection<A>): Collection<B> {
        return collection.map { mapTo(it) }
    }
}
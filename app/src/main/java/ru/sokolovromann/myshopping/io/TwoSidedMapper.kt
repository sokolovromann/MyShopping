package ru.sokolovromann.myshopping.io

abstract class TwoSidedMapper<A,B> : Mapper<A,B>() {

    abstract fun mapFrom(b: B): A

    fun mapCollectionFrom(collection: Collection<B>): Collection<A> {
        return collection.map { mapFrom(it) }
    }
}
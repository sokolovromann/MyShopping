package ru.sokolovromann.myshopping.core.domain.model

@JvmInline
value class Position(val value: Int) {

    init {
        require(value >= MIN.value && value <= MAX.value) {
            "The value must be greater than or equal ${MIN.value} and less than ${MAX.value}."
        }
    }

    companion object {
        val MIN: Position = Position(1)
        val MAX: Position = Position(Int.MAX_VALUE)
    }

    fun next(): Position? = try {
        Position(value.plus(1))
    } catch (_: Exception) { null }

    fun previous(): Position? = try {
        Position(value.minus(1))
    } catch (_: Exception) { null }

    fun isStart(): Boolean = value.equals(MIN)

    fun isEnd(): Boolean = value.equals(MAX)
}
package ru.sokolovromann.myshopping.ui.compose.state

@Deprecated("Will be deleted")
enum class ScreenState {
    Nothing, Loading, Showing, Saving;

    companion object {
        fun create(waiting: Boolean, notFound: Boolean): ScreenState {
            return if (waiting) {
                Loading
            } else {
                if (notFound) Nothing else Showing
            }
        }
    }
}
package ru.sokolovromann.myshopping.data.repository.model

import ru.sokolovromann.myshopping.data.exception.InvalidNameException

@Deprecated("Use model/AutocompleteWithConfig")
data class AddEditAutocomplete(
    private val autocomplete: Autocomplete? = null,
    private val appConfig: AppConfig = AppConfig()
) {

    private val _autocomplete = autocomplete ?: Autocomplete()
    private val userPreferences = appConfig.userPreferences

    fun createAutocomplete(name: String?): Result<Autocomplete> {
        return if (name.isNullOrEmpty()) {
            val exception = InvalidNameException("Name must not be null or empty")
            Result.failure(exception)
        } else {
            val success = _autocomplete.copy(
                name = name.trim(),
                lastModified = System.currentTimeMillis()
            )
            Result.success(success)
        }
    }

    fun getFieldName(): String {
        return _autocomplete.name
    }

    fun getFontSize(): FontSize {
        return userPreferences.fontSize
    }

    fun isNewAutocomplete(): Boolean {
        return autocomplete == null
    }
}
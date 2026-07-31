package ru.sokolovromann.myshopping.old.api15.model

data class Api15Data(
    val shoppings: Collection<Shopping> = emptyList(),
    val products: Collection<Product> = emptyList(),
    val autocompletes: Collection<Autocomplete> = emptyList(),
    val suggestions: Collection<Suggestion> = emptyList(),
    val suggestionDetails: Collection<SuggestionDetail> = emptyList(),
    val appBuildConfig: AppBuildConfig = AppBuildConfig(),
    val userPreferences: UserPreferences = UserPreferences()
)
package ru.sokolovromann.myshopping.data39.settings.suggestions

object SuggestionsConfigDefaults {
    val VIEW_MODE: SuggestionsViewMode = SuggestionsViewMode.List
    val SORT: SortSuggestions = SortSuggestions(
        name = SortSuggestionsName.Name,
        params = SortSuggestionsParams.ByAscending
    )
    val TAKE: TakeSuggestions = TakeSuggestions(
        names = 10,
        images = 3,
        manufacturers = 3,
        brands = 3,
        sizes = 3,
        colors = 3,
        quantities = 5,
        unitPrices = 3,
        discounts = 3,
        taxRates = 3,
        costs = 5
    )
}
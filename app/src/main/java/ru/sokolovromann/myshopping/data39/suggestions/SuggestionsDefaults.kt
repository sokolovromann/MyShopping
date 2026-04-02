package ru.sokolovromann.myshopping.data39.suggestions

import ru.sokolovromann.myshopping.utils.math.DiscountType

object SuggestionsDefaults {
    val DISCOUNT_TYPE: DiscountType = DiscountType.Money
    val PRE_INSTALLED: SuggestionsPreInstalled = SuggestionsPreInstalled.Add
    val VIEW_MODE: SuggestionsViewMode = SuggestionsViewMode.List
    val SORT_ORDER: SortSuggestions.Order = SortSuggestions.Order.ByAscending
    val SORT: SortSuggestions = SortSuggestions.Name(SORT_ORDER)
    val ADD: AddSuggestionWithDetails = AddSuggestionWithDetails.SuggestionAndDetails
    val TAKE_SUGGESTIONS: TakeSuggestions = TakeSuggestions.Medium
    val TAKE_DETAILS: TakeSuggestionDetails = TakeSuggestionDetails.Medium
}
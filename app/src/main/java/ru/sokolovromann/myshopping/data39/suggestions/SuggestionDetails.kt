package ru.sokolovromann.myshopping.data39.suggestions

data class SuggestionDetails(
    val images: Collection<SuggestionDetail.Image>,
    val manufacturers: Collection<SuggestionDetail.Manufacturer>,
    val brands: Collection<SuggestionDetail.Brand>,
    val sizes: Collection<SuggestionDetail.Size>,
    val colors: Collection<SuggestionDetail.Color>,
    val quantities: Collection<SuggestionDetail.Quantity>,
    val unitPrices: Collection<SuggestionDetail.UnitPrice>,
    val discounts: Collection<SuggestionDetail.Discount>,
    val taxRates: Collection<SuggestionDetail.TaxRate>,
    val costs: Collection<SuggestionDetail.Cost>
)
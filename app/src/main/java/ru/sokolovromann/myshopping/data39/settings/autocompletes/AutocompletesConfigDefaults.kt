package ru.sokolovromann.myshopping.data39.settings.autocompletes

object AutocompletesConfigDefaults {
    val VIEW_MODE: AutocompletesViewMode = AutocompletesViewMode(
        name = AutocompletesViewModeName.List,
        params = AutocompletesViewModeParams.AllFields
    )
    val SORT: SortAutocompletes = SortAutocompletes(
        name = SortAutocompletesName.Name,
        params = SortAutocompletesParams.ByAscending
    )
    val MAX_NUMBER = MaxAutocompletesNumber(
        names = 10,
        images = 3,
        manufacturers = 3,
        brands = 3,
        sizes = 3,
        colors = 3,
        quantities = 5,
        prices = 5,
        discounts = 3,
        taxRates = 3,
        costs = 5
    )
}
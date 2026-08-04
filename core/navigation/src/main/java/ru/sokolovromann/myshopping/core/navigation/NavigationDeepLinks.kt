package ru.sokolovromann.myshopping.core.navigation

object NavigationDeepLinks {

    private const val SCHEME = "ru-sokolovromann-myshopping"

    private const val HOST = "navigation"

    const val PRODUCTS_PATTERN = "$SCHEME://$HOST/products/{directory}"

    const val ADD_EDIT_PRODUCT_PATTERN =
        "$SCHEME://$HOST/product-form?cartDirectory={cartDirectory}&productDirectory={productDirectory}"

    fun createProductsUri(directory: String): String {
        return "$SCHEME://$HOST/products/$directory"
    }

    fun createAddEditProductUri(
        cartDirectory: String,
        productDirectory: String? = null
    ): String {
        var link = "$SCHEME://$HOST/product-form?cartDirectory=$cartDirectory"
        if (productDirectory != null) {
            link += "&productDirectory=$productDirectory"
        }
        return link
    }
}
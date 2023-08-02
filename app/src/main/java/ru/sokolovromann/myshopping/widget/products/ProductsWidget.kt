package ru.sokolovromann.myshopping.widget.products

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import androidx.glance.unit.ColorProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.ProductsWidgetRepository
import ru.sokolovromann.myshopping.data.repository.model.AppPreferences
import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.Products
import ru.sokolovromann.myshopping.ui.MainActivity
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.state.ProductWidgetItem
import ru.sokolovromann.myshopping.ui.utils.getActivePinnedProductWidgetItems
import ru.sokolovromann.myshopping.ui.utils.getOtherProductWidgetItems
import ru.sokolovromann.myshopping.ui.utils.toWidgetBody
import ru.sokolovromann.myshopping.ui.utils.toWidgetTitle
import ru.sokolovromann.myshopping.widget.WidgetKey

class ProductsWidget : GlanceAppWidget() {

    companion object {
        internal val GreenColor = Color(0xFF1B5E20)
        internal val BlackOpacity75Color = Color(0xBF000000)
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ProductsWidgetEntryPoint {
        fun productsWidgetRepository(): ProductsWidgetRepository
        fun dispatchers(): AppDispatchers
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { ProductsWidgetContent(context) }
    }

    @Composable
    private fun ProductsWidgetContent(context: Context) {
        val shoppingUid = currentState(key = stringPreferencesKey(WidgetKey.SHOPPING_UID.name)) ?: ""

        val coroutineScope = rememberCoroutineScope()
        val products: MutableState<Products> = remember { mutableStateOf(Products()) }

        val entryPoint = EntryPointAccessors.fromApplication(
            context = context,
            entryPoint = ProductsWidgetEntryPoint::class.java
        )

        LaunchedEffect(shoppingUid, Unit) {
            coroutineScope.launch(entryPoint.dispatchers().io) {
                entryPoint.productsWidgetRepository().getProducts(shoppingUid).collectLatest {
                    withContext(entryPoint.dispatchers().main) {
                        it?.let { products.value = it }
                    }
                }
            }
        }

        Column(modifier = GlanceModifier.fillMaxSize()) {
            if (products.value.isEmpty()) {
                ProductsWidgetName(
                    name = products.value.shoppingList.name,
                    fontSize = products.value.preferences.fontSize,
                    completed = products.value.isCompleted(),
                    noSplit = products.value.preferences.displayCompletedPurchases == DisplayCompleted.NO_SPLIT
                )

                ProductsWidgetNotFound(
                    modifier = GlanceModifier.defaultWeight(),
                    text = context.getString(R.string.productsWidget_text_productsNotFound),
                    fontSize = products.value.preferences.fontSize
                )
            } else {
                ProductsWidgetProducts(
                    modifier = GlanceModifier.defaultWeight(),
                    name = products.value.shoppingList.name,
                    completed = products.value.isCompleted(),
                    pinnedItems = products.value.getActivePinnedProductWidgetItems(),
                    otherItems = products.value.getOtherProductWidgetItems(),
                    preferences = products.value.preferences
                ) {
                    coroutineScope.launch(entryPoint.dispatchers().io) {
                        if (it.completed) {
                            entryPoint.productsWidgetRepository().activeProduct(
                                it.uid, System.currentTimeMillis()
                            )
                        } else {
                            entryPoint.productsWidgetRepository().completeProduct(
                                it.uid, System.currentTimeMillis()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = GlanceModifier
                .fillMaxWidth()
                .height(ProductsWidgetSpacerHeight)
                .background(ColorProvider(R.color.gray_200_opacity_75))
            )
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(
                        vertical = ProductsWidgetMediumSize,
                        horizontal = ProductsWidgetLargeSize
                    )
                    .background(ColorProvider(R.color.gray_200)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProductsWidgetTotal(
                    products.value.shoppingList.calculateTotal().toString(),
                    products.value.preferences.fontSize
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                Image(
                    modifier = GlanceModifier.clickable { startMainActivity(context, shoppingUid) },
                    provider = ImageProvider(R.drawable.ic_all_open),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(ColorProvider(R.color.black_opacity_75)),
                )
            }
        }
    }
}

private fun startMainActivity(context: Context, uid: String) {
    val intent = Intent(context, MainActivity::class.java).apply {
        action = createAction(uid)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val args = Bundle().apply { putExtra(UiRouteKey.ShoppingUid.key, uid) }
        putExtras(args)
    }
    context.startActivity(intent)
}

private fun createAction(uid: String): String {
    return "ru.sokolovromann.myshopping.products_widget_$uid"
}

@Composable
private fun ProductsWidgetName(
    name: String,
    fontSize: FontSize,
    completed: Boolean,
    noSplit: Boolean
) {
    if (name.isEmpty()) {
        return
    }

    val background = if (noSplit) {
        R.color.white
    } else {
        if (completed) {
            R.color.gray_200
        } else {
            R.color.white
        }
    }

    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(background)
            .padding(
                vertical = ProductsWidgetMediumSize,
                horizontal = ProductsWidgetLargeSize
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = name,
            style = TextDefaults.defaultTextStyle.copy(
                color = ColorProvider(R.color.black),
                fontSize = fontSize.toWidgetTitle().sp,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1
        )
    }
}

@Composable
private fun ProductsWidgetTotal(
    total: String,
    fontSize: FontSize
) {
    if (total.isEmpty()) {
        return
    }

    Text(
        text = total,
        style = TextDefaults.defaultTextStyle.copy(
            color = ColorProvider(R.color.black),
            fontSize = fontSize.toWidgetBody().sp
        ),
        maxLines = 1
    )
}

@Composable
private fun ProductsWidgetNotFound(
    modifier: GlanceModifier,
    text: String,
    fontSize: FontSize
) {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(R.color.white)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = GlanceModifier.padding(
                vertical = ProductsWidgetMediumSize,
                horizontal = ProductsWidgetLargeSize
            ),
            text = text,
            style = TextDefaults.defaultTextStyle.copy(
                color = ColorProvider(R.color.black),
                fontSize = fontSize.toWidgetBody().sp
            )
        )
    }
}

@Composable
private fun ProductsWidgetProducts(
    modifier: GlanceModifier,
    name: String,
    completed: Boolean,
    pinnedItems: List<ProductWidgetItem>,
    otherItems: List<ProductWidgetItem>,
    preferences: AppPreferences,
    onCheckedChange: (ProductWidgetItem) -> Unit
) {
    LazyColumn(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(ColorProvider(R.color.gray_200))
            .then(modifier)
    ) {
        item {
            ProductsWidgetName(
                name = name,
                fontSize = preferences.fontSize,
                completed = completed,
                noSplit = preferences.displayCompletedPurchases == DisplayCompleted.NO_SPLIT
            )
        }
        items(pinnedItems) {
            ProductsWidgetItem(
                widgetItem = it,
                preferences = preferences,
                onCheckedChange = onCheckedChange
            )
        }
        items(otherItems) {
            ProductsWidgetItem(
                widgetItem = it,
                preferences = preferences,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun ProductsWidgetItem(
    widgetItem: ProductWidgetItem,
    preferences: AppPreferences,
    onCheckedChange: (ProductWidgetItem) -> Unit
) {
    val backgroundColorResId = if (preferences.displayCompletedPurchases == DisplayCompleted.NO_SPLIT) {
        R.color.white
    } else {
        if (widgetItem.completed) R.color.gray_200 else R.color.white
    }

    val rowModifier = GlanceModifier
        .fillMaxWidth()
        .background(ColorProvider(backgroundColorResId))
        .padding(all = ProductsWidgetMediumSize)

    val rowModifierWithChecked = if (preferences.completedWithCheckbox) {
        rowModifier
    } else {
        rowModifier.clickable { onCheckedChange(widgetItem) }
    }

    Row(
        modifier = rowModifierWithChecked,
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.Start
    ) {
        ProductsWidgetCheckbox(
            checked = widgetItem.completed,
            checkedWithCheckbox = preferences.completedWithCheckbox,
            coloredCheckbox = preferences.coloredCheckbox,
            onCheckedChange = { onCheckedChange(widgetItem) }
        )

        Text(
            text = widgetItem.body,
            style = TextDefaults.defaultTextStyle.copy(
                color = ColorProvider(R.color.black),
                fontSize = preferences.fontSize.toWidgetBody().sp
            )
        )
    }
}

@Composable
private fun ProductsWidgetCheckbox(
    checked: Boolean,
    checkedWithCheckbox: Boolean,
    coloredCheckbox: Boolean,
    onCheckedChange: () -> Unit
) {
    val color = if (coloredCheckbox) {
        if (checked) ProductsWidget.GreenColor else Color.Red
    } else {
        ProductsWidget.BlackOpacity75Color
    }

    val imageResId = if (checked) {
        R.drawable.ic_all_check_box
    } else {
        R.drawable.ic_all_check_box_outline
    }

    val imageModifier = GlanceModifier
        .size(ProductsWidgetCheckedSize)
        .padding(horizontal = ProductsWidgetSmallSize)
    val imageModifierWithClickable = if (checkedWithCheckbox) {
        imageModifier.clickable { onCheckedChange() }
    } else {
        imageModifier
    }

    Image(
        modifier = imageModifierWithClickable,
        provider = ImageProvider(imageResId),
        contentDescription = null,
        colorFilter = ColorFilter.tint(ColorProvider(color))
    )
}

private val ProductsWidgetSmallSize = 2.dp
private val ProductsWidgetMediumSize = 4.dp
private val ProductsWidgetLargeSize = 8.dp
private val ProductsWidgetSpacerHeight = 1.dp
private val ProductsWidgetCheckedSize = 28.dp
package ru.sokolovromann.myshopping.widget.products

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
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
        internal val GreenOpacity75Color = Color(0xBF1B5E20)
        internal val RedOpacity75Color = Color(0xBFFF0000)
        internal val BlackOpacity75Color = Color(0xBF000000)
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ProductsWidgetEntryPoint {
        fun shoppingListsRepository(): ShoppingListsRepository
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { ProductsWidgetContent(context) }
    }

    @Composable
    private fun ProductsWidgetContent(context: Context) {
        val shoppingUid = currentState(key = stringPreferencesKey(WidgetKey.SHOPPING_UID.name)) ?: ""

        val coroutineScope = rememberCoroutineScope()
        val shoppingListWithConfigFlow: MutableStateFlow<ShoppingListWithConfig> = remember {
            MutableStateFlow(ShoppingListWithConfig())
        }

        val entryPoint = EntryPointAccessors.fromApplication(
            context = context,
            entryPoint = ProductsWidgetEntryPoint::class.java
        )

        LaunchedEffect(shoppingUid, Unit) {
            coroutineScope.launch {
                entryPoint.shoppingListsRepository().getShoppingListWithConfig(shoppingUid).collect {
                    shoppingListWithConfigFlow.emit(it)
                }
            }
        }

        Column(modifier = GlanceModifier.fillMaxSize()) {
            val shoppingListWithConfig = shoppingListWithConfigFlow.collectAsState().value
            val shoppingList = shoppingListWithConfig.shoppingList
            val userPreferences = shoppingListWithConfig.appConfig.userPreferences

            if (shoppingListWithConfig.shoppingList.products.isEmpty()) {
                ProductsWidgetName(
                    name = shoppingList.shopping.name,
                    fontSize = userPreferences.fontSize,
                    completed = shoppingList.isCompleted(),
                    noSplit = userPreferences.displayCompleted == DisplayCompleted.NO_SPLIT
                )

                ProductsWidgetNotFound(
                    modifier = GlanceModifier.defaultWeight(),
                    text = context.getString(R.string.productsWidget_text_productsNotFound),
                    fontSize = userPreferences.fontSize
                )
            } else {
                ProductsWidgetProducts(
                    modifier = GlanceModifier.defaultWeight(),
                    name = shoppingList.shopping.name,
                    completed = shoppingList.isCompleted(),
                    pinnedItems = shoppingListWithConfig.getActivePinnedProductWidgetItems(),
                    otherItems = shoppingListWithConfig.getOtherProductWidgetItems(),
                    displayCompleted = userPreferences.displayCompleted,
                    fontSize = userPreferences.fontSize,
                    coloredCheckbox = userPreferences.coloredCheckbox,
                    completedWithCheckbox = userPreferences.completedWithCheckbox
                ) {
                    coroutineScope.launch {
                        if (it.completed) {
                            entryPoint.shoppingListsRepository().activeProduct(it.uid)
                        } else {
                            entryPoint.shoppingListsRepository().completeProduct(it.uid)
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
                if (userPreferences.displayMoney) {
                    ProductsWidgetTotal(
                        shoppingList.shopping.total.getDisplayValue(),
                        userPreferences.fontSize
                    )
                }
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
    displayCompleted: DisplayCompleted,
    fontSize: FontSize,
    coloredCheckbox: Boolean,
    completedWithCheckbox: Boolean,
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
                fontSize = fontSize,
                completed = completed,
                noSplit = displayCompleted == DisplayCompleted.NO_SPLIT
            )
        }
        items(pinnedItems) {
            ProductsWidgetItem(
                widgetItem = it,
                displayCompleted = displayCompleted,
                fontSize = fontSize,
                coloredCheckbox = coloredCheckbox,
                completedWithCheckbox = completedWithCheckbox,
                onCheckedChange = onCheckedChange
            )
        }
        items(otherItems) {
            ProductsWidgetItem(
                widgetItem = it,
                displayCompleted = displayCompleted,
                fontSize = fontSize,
                coloredCheckbox = coloredCheckbox,
                completedWithCheckbox = completedWithCheckbox,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun ProductsWidgetItem(
    widgetItem: ProductWidgetItem,
    displayCompleted: DisplayCompleted,
    fontSize: FontSize,
    coloredCheckbox: Boolean,
    completedWithCheckbox: Boolean,
    onCheckedChange: (ProductWidgetItem) -> Unit
) {
    val backgroundColorResId = if (displayCompleted == DisplayCompleted.NO_SPLIT) {
        R.color.white
    } else {
        if (widgetItem.completed) R.color.gray_200 else R.color.white
    }

    val rowModifier = GlanceModifier
        .fillMaxWidth()
        .background(ColorProvider(backgroundColorResId))
        .padding(all = ProductsWidgetMediumSize)

    val rowModifierWithChecked = if (completedWithCheckbox) {
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
            checkedWithCheckbox = completedWithCheckbox,
            coloredCheckbox = coloredCheckbox,
            onCheckedChange = { onCheckedChange(widgetItem) }
        )

        Text(
            text = widgetItem.body,
            style = TextDefaults.defaultTextStyle.copy(
                color = ColorProvider(R.color.black),
                fontSize = fontSize.toWidgetBody().sp
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
        if (checked) ProductsWidget.GreenOpacity75Color else ProductsWidget.RedOpacity75Color
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
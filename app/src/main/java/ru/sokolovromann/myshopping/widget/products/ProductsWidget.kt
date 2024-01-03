package ru.sokolovromann.myshopping.widget.products

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.ui.activity.MainActivity
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.model.ProductWidgetItem
import ru.sokolovromann.myshopping.ui.model.ProductsWidgetState
import ru.sokolovromann.myshopping.ui.model.UiFontSize
import ru.sokolovromann.myshopping.ui.model.UiString
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

    @SuppressLint("RestrictedApi")
    @Composable
    private fun ProductsWidgetContent(context: Context) {
        val shoppingUid: String? = currentState(key = stringPreferencesKey(WidgetKey.SHOPPING_UID.name))

        val coroutineScope = rememberCoroutineScope()
        val productsWidgetState: ProductsWidgetState = remember { ProductsWidgetState() }
        val entryPoint = EntryPointAccessors.fromApplication(
            context = context,
            entryPoint = ProductsWidgetEntryPoint::class.java
        )

        LaunchedEffect(shoppingUid, Unit) {
            coroutineScope.launch {
                entryPoint.shoppingListsRepository().getShoppingListWithConfig(shoppingUid).collect {
                    productsWidgetState.populate(it)
                }
            }
        }

        Column(modifier = GlanceModifier.fillMaxSize()) {
            if (shoppingUid == null) {
                ProductsWidgetNotFound(
                    modifier = GlanceModifier.defaultWeight(),
                    text = context.getString(R.string.productsWidget_message_loadingError),
                    fontSize = productsWidgetState.fontSize
                )
                return@Column
            }

            if (productsWidgetState.isNotFound()) {
                ProductsWidgetName(
                    name = productsWidgetState.nameText,
                    fontSize = productsWidgetState.fontSize,
                    completed = productsWidgetState.completed,
                    noSplit = productsWidgetState.displayCompleted == DisplayCompleted.NO_SPLIT
                )

                ProductsWidgetNotFound(
                    modifier = GlanceModifier.defaultWeight(),
                    text = context.getString(R.string.productsWidget_text_productsNotFound),
                    fontSize = productsWidgetState.fontSize
                )
            } else {
                ProductsWidgetProducts(
                    modifier = GlanceModifier.defaultWeight(),
                    name = productsWidgetState.nameText,
                    completed = productsWidgetState.completed,
                    pinnedItems = productsWidgetState.pinnedProducts,
                    otherItems = productsWidgetState.otherProducts,
                    displayCompleted = productsWidgetState.displayCompleted,
                    fontSize = productsWidgetState.fontSize,
                    coloredCheckbox = productsWidgetState.coloredCheckbox,
                    completedWithCheckbox = productsWidgetState.completedWithCheckbox
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
                if (productsWidgetState.displayMoney) {
                    ProductsWidgetTotal(
                        total = productsWidgetState.totalText,
                        fontSize = productsWidgetState.fontSize
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

@SuppressLint("RestrictedApi")
@Composable
private fun ProductsWidgetName(
    name: UiString,
    fontSize: UiFontSize,
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
            text = name.asCompose(),
            style = TextDefaults.defaultTextStyle.copy(
                color = ColorProvider(R.color.black),
                fontSize = fontSize.widgetHeader.sp,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1
        )
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun ProductsWidgetTotal(
    total: UiString,
    fontSize: UiFontSize
) {
    if (total.isEmpty()) {
        return
    }

    Text(
        text = total.asCompose(),
        style = TextDefaults.defaultTextStyle.copy(
            color = ColorProvider(R.color.black),
            fontSize = fontSize.widgetContent.sp
        ),
        maxLines = 1
    )
}

@SuppressLint("RestrictedApi")
@Composable
private fun ProductsWidgetNotFound(
    modifier: GlanceModifier,
    text: String,
    fontSize: UiFontSize
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
                fontSize = fontSize.widgetContent.sp
            )
        )
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun ProductsWidgetProducts(
    modifier: GlanceModifier,
    name: UiString,
    completed: Boolean,
    pinnedItems: List<ProductWidgetItem>,
    otherItems: List<ProductWidgetItem>,
    displayCompleted: DisplayCompleted,
    fontSize: UiFontSize,
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

@SuppressLint("RestrictedApi")
@Composable
private fun ProductsWidgetItem(
    widgetItem: ProductWidgetItem,
    displayCompleted: DisplayCompleted,
    fontSize: UiFontSize,
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
                fontSize = fontSize.widgetContent.sp
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
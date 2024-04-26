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
import androidx.glance.text.Text
import androidx.glance.text.TextDecoration
import androidx.glance.unit.ColorProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.app.AppAction
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.NightTheme
import ru.sokolovromann.myshopping.ui.activity.MainActivity
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.model.ProductWidgetItem
import ru.sokolovromann.myshopping.ui.model.ProductsWidgetState
import ru.sokolovromann.myshopping.ui.model.UiString
import ru.sokolovromann.myshopping.widget.WidgetKey
import ru.sokolovromann.myshopping.widget.WidgetTypography
import ru.sokolovromann.myshopping.widget.createWidgetTypography

class ProductsWidget : GlanceAppWidget() {

    companion object {
        internal val GreenOpacity75Color = Color(0xBF1B5E20)
        internal val RedOpacity75Color = Color(0xBFFF0000)
        internal val BlackOpacity75Color = Color(0xBF000000)
        internal val WhiteOpacity200Color = Color(0xBFFFFFFF)
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
                entryPoint.shoppingListsRepository().getShoppingListWithConfig(shoppingUid).collectLatest {
                    productsWidgetState.populate(it)
                }
            }
        }

        val typography: WidgetTypography = createWidgetTypography(productsWidgetState.fontSizeOffset)

        Column(modifier = GlanceModifier.fillMaxSize()) {
            if (shoppingUid == null) {
                ProductsWidgetNotFound(
                    modifier = GlanceModifier.defaultWeight(),
                    text = context.getString(R.string.productsWidget_message_loadingError),
                    nightTheme = productsWidgetState.nightTheme,
                    typography = typography
                )
                return@Column
            }

            if (productsWidgetState.isNotFound()) {
                ProductsWidgetName(
                    name = productsWidgetState.nameText,
                    nightTheme = productsWidgetState.nightTheme,
                    typography = typography,
                    completed = productsWidgetState.completed,
                    noSplit = productsWidgetState.displayCompleted == DisplayCompleted.NO_SPLIT
                )

                ProductsWidgetNotFound(
                    modifier = GlanceModifier.defaultWeight(),
                    text = context.getString(R.string.productsWidget_text_productsNotFound),
                    nightTheme = productsWidgetState.nightTheme,
                    typography = typography
                )

                if (productsWidgetState.forceLoad) {
                    LaunchedEffect(Unit) {
                        coroutineScope.launch {
                            entryPoint.shoppingListsRepository().getShoppingListWithConfig(shoppingUid).collectLatest {
                                productsWidgetState.populate(it)
                            }
                        }
                    }
                }
            } else {
                ProductsWidgetProducts(
                    modifier = GlanceModifier.defaultWeight(),
                    name = productsWidgetState.nameText,
                    completed = productsWidgetState.completed,
                    pinnedItems = productsWidgetState.pinnedProducts,
                    otherItems = productsWidgetState.otherProducts,
                    displayCompleted = productsWidgetState.displayCompleted,
                    strikethroughCompletedProducts = productsWidgetState.strikethroughCompletedProducts,
                    nightTheme = productsWidgetState.nightTheme,
                    typography = typography,
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

            val dividerBackgroundColor = if (productsWidgetState.nightTheme.isWidgetNightTheme()) {
                R.color.black_opacity_75
            } else {
                R.color.gray_200_opacity_75
            }
            val rowBackgroundColor = if (productsWidgetState.nightTheme.isWidgetNightTheme()) {
                R.color.black
            } else {
                R.color.gray_200
            }
            Spacer(modifier = GlanceModifier
                .fillMaxWidth()
                .height(ProductsWidgetSpacerHeight)
                .background(ColorProvider(dividerBackgroundColor))
            )
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(
                        vertical = ProductsWidgetMediumSize,
                        horizontal = ProductsWidgetLargeSize
                    )
                    .background(ColorProvider(rowBackgroundColor)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (productsWidgetState.displayMoney) {
                    ProductsWidgetTotal(
                        total = productsWidgetState.totalText,
                        nightTheme = productsWidgetState.nightTheme,
                        typography = typography
                    )
                }
                Spacer(modifier = GlanceModifier.defaultWeight())

                val tintColor = if (productsWidgetState.nightTheme.isWidgetNightTheme()) {
                    R.color.gray_200_opacity_75
                } else {
                    R.color.black_opacity_75
                }
                Image(
                    modifier = GlanceModifier.clickable { startMainActivity(context, shoppingUid) },
                    provider = ImageProvider(R.drawable.ic_all_open),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(ColorProvider(tintColor)),
                )
            }
        }
    }
}

private fun startMainActivity(context: Context, uid: String) {
    val intent = Intent(context, MainActivity::class.java).apply {
        action = AppAction.createWidgetsOpenProducts(uid)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val args = Bundle().apply { putExtra(UiRouteKey.ShoppingUid.key, uid) }
        putExtras(args)
    }
    context.startActivity(intent)
}

@SuppressLint("RestrictedApi")
@Composable
private fun ProductsWidgetName(
    name: UiString,
    nightTheme: NightTheme,
    typography: WidgetTypography,
    completed: Boolean,
    noSplit: Boolean
) {
    if (name.isEmpty()) {
        return
    }

    val backgroundColor = if (nightTheme.isWidgetNightTheme()) {
        if (noSplit) {
            R.color.gray_900
        } else {
            if (completed) R.color.black else R.color.gray_900
        }
    } else {
        if (noSplit) {
            R.color.white
        } else {
            if (completed) R.color.gray_200 else R.color.white
        }
    }

    val textColor = if (nightTheme.isWidgetNightTheme()) R.color.white else R.color.black

    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(
                vertical = ProductsWidgetMediumSize,
                horizontal = ProductsWidgetLargeSize
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = name.asCompose(),
            style = typography.title.copy(
                color = ColorProvider(textColor)
            ),
            maxLines = 1
        )
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun ProductsWidgetTotal(
    total: UiString,
    nightTheme: NightTheme,
    typography: WidgetTypography,
) {
    if (total.isEmpty()) {
        return
    }

    val textColor = if (nightTheme.isWidgetNightTheme()) R.color.white else R.color.black

    Text(
        text = total.asCompose(),
        style = typography.body.copy(
            color = ColorProvider(textColor)
        ),
        maxLines = 1
    )
}

@SuppressLint("RestrictedApi")
@Composable
private fun ProductsWidgetNotFound(
    modifier: GlanceModifier,
    text: String,
    nightTheme: NightTheme,
    typography: WidgetTypography,
) {
    val backgroundColor = if (nightTheme.isWidgetNightTheme()) R.color.black else R.color.white
    val textColor = if (nightTheme.isWidgetNightTheme()) R.color.white else R.color.black

    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(backgroundColor)
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
            style = typography.body.copy(
                color = ColorProvider(textColor)
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
    strikethroughCompletedProducts: Boolean,
    nightTheme: NightTheme,
    typography: WidgetTypography,
    coloredCheckbox: Boolean,
    completedWithCheckbox: Boolean,
    onCheckedChange: (ProductWidgetItem) -> Unit
) {
    val backgroundColor = if (nightTheme.isWidgetNightTheme()) R.color.black else R.color.gray_200

    LazyColumn(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(ColorProvider(backgroundColor))
            .then(modifier)
    ) {
        item {
            ProductsWidgetName(
                name = name,
                nightTheme = nightTheme,
                typography = typography,
                completed = completed,
                noSplit = displayCompleted == DisplayCompleted.NO_SPLIT
            )
        }
        items(pinnedItems) {
            val textDecoration = if (strikethroughCompletedProducts && it.completed) {
                TextDecoration.LineThrough
            } else {
                TextDecoration.None
            }
            ProductsWidgetItem(
                widgetItem = it,
                displayCompleted = displayCompleted,
                textDecoration = textDecoration,
                nightTheme = nightTheme,
                typography = typography,
                coloredCheckbox = coloredCheckbox,
                completedWithCheckbox = completedWithCheckbox,
                onCheckedChange = onCheckedChange
            )
        }
        items(otherItems) {
            val textDecoration = if (strikethroughCompletedProducts && it.completed) {
                TextDecoration.LineThrough
            } else {
                TextDecoration.None
            }
            ProductsWidgetItem(
                widgetItem = it,
                displayCompleted = displayCompleted,
                textDecoration = textDecoration,
                nightTheme = nightTheme,
                typography = typography,
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
    textDecoration: TextDecoration,
    nightTheme: NightTheme,
    typography: WidgetTypography,
    coloredCheckbox: Boolean,
    completedWithCheckbox: Boolean,
    onCheckedChange: (ProductWidgetItem) -> Unit
) {
    val backgroundColor = if (nightTheme.isWidgetNightTheme()) {
        if (displayCompleted == DisplayCompleted.NO_SPLIT) {
            R.color.gray_900
        } else {
            if (widgetItem.completed) R.color.black else R.color.gray_900
        }
    } else {
        if (displayCompleted == DisplayCompleted.NO_SPLIT) {
            R.color.white
        } else {
            if (widgetItem.completed) R.color.gray_200 else R.color.white
        }
    }

    val textColor = if (nightTheme.isWidgetNightTheme()) R.color.white else R.color.black

    val rowModifier = GlanceModifier
        .fillMaxWidth()
        .background(ColorProvider(backgroundColor))
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
            nightTheme = nightTheme,
            checkedWithCheckbox = completedWithCheckbox,
            coloredCheckbox = coloredCheckbox,
            onCheckedChange = { onCheckedChange(widgetItem) }
        )

        Text(
            text = widgetItem.body,
            style = typography.body.copy(
                color = ColorProvider(textColor),
                textDecoration = textDecoration
            )
        )
    }
}

@Composable
private fun ProductsWidgetCheckbox(
    checked: Boolean,
    nightTheme: NightTheme,
    checkedWithCheckbox: Boolean,
    coloredCheckbox: Boolean,
    onCheckedChange: () -> Unit
) {

    val tintColor = if (coloredCheckbox) {
        if (checked) ProductsWidget.GreenOpacity75Color else ProductsWidget.RedOpacity75Color
    } else {
        if (nightTheme.isWidgetNightTheme()) {
            ProductsWidget.WhiteOpacity200Color
        } else {
            ProductsWidget.BlackOpacity75Color
        }
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
        colorFilter = ColorFilter.tint(ColorProvider(tintColor))
    )
}

private val ProductsWidgetSmallSize = 2.dp
private val ProductsWidgetMediumSize = 4.dp
private val ProductsWidgetLargeSize = 8.dp
private val ProductsWidgetSpacerHeight = 1.dp
private val ProductsWidgetCheckedSize = 28.dp
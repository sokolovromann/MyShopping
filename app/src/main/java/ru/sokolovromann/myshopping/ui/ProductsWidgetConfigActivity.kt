package ru.sokolovromann.myshopping.ui

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.AppScaffold
import ru.sokolovromann.myshopping.ui.compose.ShoppingListsGrid
import ru.sokolovromann.myshopping.ui.compose.event.ProductsWidgetConfigScreenEvent
import ru.sokolovromann.myshopping.ui.theme.MyShoppingTheme
import ru.sokolovromann.myshopping.ui.utils.toItemTitle
import ru.sokolovromann.myshopping.ui.utils.updateProductsWidgetState
import ru.sokolovromann.myshopping.ui.viewmodel.ProductsWidgetConfigViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.ProductsWidgetConfigEvent
import ru.sokolovromann.myshopping.widget.products.ProductsWidget

@AndroidEntryPoint
class ProductsWidgetConfigActivity : ComponentActivity() {

    private val viewModel: ProductsWidgetConfigViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val productsWidgetConfigState = viewModel.productsWidgetConfigState

        val widgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID)
        val event = ProductsWidgetConfigEvent.OnCreate(widgetId)
        viewModel.onEvent(event)

        installSplashScreen().apply {
            setKeepOnScreenCondition { productsWidgetConfigState.loading }
        }

        setContent {
            MyShoppingTheme(
                darkTheme = productsWidgetConfigState.nightTheme,
                content = { ProductsWidgetConfigContent() }
            )
        }

        lifecycleScope.launch {
            viewModel.screenEventFlow.collect {
                when (it) {
                    is ProductsWidgetConfigScreenEvent.UpdateWidget -> updateWidget(
                        widgetId = it.widgetId,
                        shoppingUid = it.shoppingUid
                    )

                    ProductsWidgetConfigScreenEvent.FinishApp -> {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                }
            }
        }
    }

    private suspend fun updateWidget(
        widgetId: Int,
        shoppingUid: String
    ) = lifecycleScope.launch(viewModel.dispatchers.io) {
        val glanceId = GlanceAppWidgetManager(applicationContext).getGlanceIdBy(widgetId)
        updateProductsWidgetState(applicationContext, glanceId, shoppingUid)

        withContext(viewModel.dispatchers.main) {
            ProductsWidget().update(applicationContext, glanceId)

            setResult(RESULT_OK)
            finish()
        }
    }

    @Composable
    private fun ProductsWidgetConfigContent() {
        val screenData = viewModel.productsWidgetConfigState.screenData

        AppScaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.productsWidgetConfig_header)) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onEvent(ProductsWidgetConfigEvent.CancelSelectingShoppingList) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.productsWidgetConfig_contentDescription_navigationIcon),
                                tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(
                                    ContentAlpha.medium)
                            )
                        }
                    }
                )
            }
        ) {
            ShoppingListsGrid(
                screenState = screenData.screenState,
                multiColumns = screenData.multiColumns,
                smartphoneScreen = screenData.smartphoneScreen,
                pinnedItems = screenData.pinnedShoppingLists,
                otherItems = screenData.otherShoppingLists,
                displayProducts = screenData.displayProducts,
                highlightCheckbox = screenData.highlightCheckbox,
                fontSize = screenData.fontSize,
                onClick = {
                    val event = ProductsWidgetConfigEvent.SelectShoppingList(it)
                    viewModel.onEvent(event)
                },
                onLongClick = {},
                notFound = {
                    Text(
                        text = stringResource(R.string.productsWidgetConfig_text_shoppingListsNotFound),
                        fontSize = screenData.fontSize.toItemTitle().sp,
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    }
}
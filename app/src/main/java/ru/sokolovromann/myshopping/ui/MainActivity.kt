package ru.sokolovromann.myshopping.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.sokolovromann.myshopping.ui.theme.MyShoppingTheme

@ExperimentalFoundationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyShoppingTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = UiRoute.Purchases.graph,
                    builder = {
                        purchasesGraph(navController)
                        archiveGraph(navController)
                        trashGraph(navController)
                        productsGraph(navController)
                    }
                )
            }
        }
    }
}
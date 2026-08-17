package com.example.paymentsrecording.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.paymentsrecording.ui.AppViewModelFactory
import com.example.paymentsrecording.ui.screens.BudgetScreen
import com.example.paymentsrecording.ui.screens.CategoryManagerScreen
import com.example.paymentsrecording.ui.screens.HomeScreen
import com.example.paymentsrecording.ui.screens.ImportScreen
import com.example.paymentsrecording.ui.screens.MineScreen
import com.example.paymentsrecording.ui.screens.ReviewDetailScreen
import com.example.paymentsrecording.ui.screens.ReviewScreen
import com.example.paymentsrecording.ui.theme.PaymentsTheme

private data class TabItem(val screen: Screen, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun MainScaffold(
    factory: AppViewModelFactory,
    themeMode: Int,
    onThemeChange: (Int) -> Unit
) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    val tabs = listOf(
        TabItem(Screen.Home, "首页", Icons.Outlined.Home),
        TabItem(Screen.Review, "回顾", Icons.Outlined.Assessment),
        TabItem(Screen.Mine, "我的", Icons.Outlined.Person)
    )

    val showBottomBar = currentRoute in tabs.map { it.screen.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.screen.route,
                            onClick = {
                                navController.navigate(tab.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(factory = factory)
            }
            composable(Screen.Review.route) {
                ReviewScreen(factory = factory, navController = navController)
            }
            composable(Screen.Mine.route) {
                MineScreen(
                    factory = factory,
                    themeMode = themeMode,
                    onThemeChange = onThemeChange,
                    navController = navController
                )
            }
            composable(Screen.ReviewDetail.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("reviewId")?.toLongOrNull() ?: 0L
                ReviewDetailScreen(reviewId = id, factory = factory, navController = navController)
            }
            composable(Screen.CategoryManager.route) {
                CategoryManagerScreen(factory = factory, navController = navController)
            }
            composable(Screen.Import.route) {
                ImportScreen(navController = navController)
            }
            composable(Screen.Budget.route) {
                BudgetScreen(factory = factory, navController = navController)
            }
        }
    }
}

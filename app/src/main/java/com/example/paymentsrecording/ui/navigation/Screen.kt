package com.example.paymentsrecording.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Review : Screen("review")
    data object Mine : Screen("mine")
    data object ReviewDetail : Screen("review_detail/{reviewId}") {
        fun create(id: Long) = "review_detail/$id"
    }
    data object CategoryManager : Screen("category_manager")
    data object Import : Screen("import")
    data object Budget : Screen("budget")
}

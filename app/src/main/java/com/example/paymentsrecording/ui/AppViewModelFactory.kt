package com.example.paymentsrecording.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.paymentsrecording.di.AppContainer

/** 通用 ViewModel 工厂，注入 AppContainer。 */
class AppViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(container.transactionRepository, container.categoryRepository, container.budgetRepository) as T
            modelClass.isAssignableFrom(ReviewViewModel::class.java) ->
                ReviewViewModel(container.reviewRepository, container.transactionRepository) as T
            modelClass.isAssignableFrom(MineViewModel::class.java) ->
                MineViewModel(container.transactionRepository, container.budgetRepository) as T
            modelClass.isAssignableFrom(CategoryViewModel::class.java) ->
                CategoryViewModel(container.categoryRepository) as T
            modelClass.isAssignableFrom(BudgetViewModel::class.java) ->
                BudgetViewModel(container.budgetRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}

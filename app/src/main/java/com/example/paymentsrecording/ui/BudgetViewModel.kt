package com.example.paymentsrecording.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paymentsrecording.data.db.entity.Budget
import com.example.paymentsrecording.data.repository.BudgetRepository
import com.example.paymentsrecording.util.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BudgetViewModel(
    private val repo: BudgetRepository
) : ViewModel() {

    val budget: StateFlow<Budget?> = repo.observe().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    fun save(budget: Budget, context: Context) = viewModelScope.launch {
        repo.upsert(budget)
        if (budget.dailyReminderEnabled) {
            ReminderScheduler.enable(context, budget.dailyReminderHour, budget.dailyReminderMinute)
        } else {
            ReminderScheduler.disable(context)
        }
    }
}

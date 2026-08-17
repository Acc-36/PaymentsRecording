package com.example.paymentsrecording.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paymentsrecording.data.db.entity.Budget
import com.example.paymentsrecording.data.db.entity.Transaction
import com.example.paymentsrecording.data.repository.BudgetRepository
import com.example.paymentsrecording.data.repository.CategoryRepository
import com.example.paymentsrecording.data.repository.TransactionRepository
import com.example.paymentsrecording.util.DateUtil
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val balance: Double = 0.0,
    val recent: List<Transaction> = emptyList(),
    val monthTransactions: List<Transaction> = emptyList(),
    val budget: Budget? = null,
    val budgetUsed: Double = 0.0
)

class HomeViewModel(
    private val txRepo: TransactionRepository,
    private val catRepo: CategoryRepository,
    private val budgetRepo: BudgetRepository
) : ViewModel() {

    private val monthStart = DateUtil.monthStart()
    private val monthEnd = DateUtil.monthEnd()

    val uiState: StateFlow<HomeUiState> = combine(
        txRepo.observeMonthRange(monthStart, monthEnd),
        txRepo.observeRecent(50),
        budgetRepo.observe()
    ) { monthTx, recent, budget ->
        val income = monthTx.filter { it.type == 1 }.sumOf { it.amount }
        val expense = monthTx.filter { it.type == 0 }.sumOf { it.amount }
        HomeUiState(
            monthIncome = income,
            monthExpense = expense,
            balance = income - expense,
            recent = recent,
            monthTransactions = monthTx,
            budget = budget,
            budgetUsed = expense
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun addTransaction(t: Transaction) = viewModelScope.launch { txRepo.insert(t) }
    fun updateTransaction(t: Transaction) = viewModelScope.launch { txRepo.update(t) }
    fun deleteTransaction(t: Transaction) = viewModelScope.launch { txRepo.delete(t) }

    /** 提供当前可用分类给记账 Sheet（合并两种类型）。 */
    val categoriesFlow = catRepo.observeAll().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
}

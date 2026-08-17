package com.example.paymentsrecording.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paymentsrecording.data.db.entity.Category
import com.example.paymentsrecording.data.repository.CategoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repo: CategoryRepository
) : ViewModel() {

    val categories: StateFlow<List<Category>> = repo.observeAll().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun add(category: Category) = viewModelScope.launch { repo.insert(category) }
    fun update(category: Category) = viewModelScope.launch { repo.update(category) }
    fun delete(category: Category) = viewModelScope.launch { repo.delete(category) }
}

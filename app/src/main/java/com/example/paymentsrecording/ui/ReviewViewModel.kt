package com.example.paymentsrecording.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paymentsrecording.data.db.entity.Review
import com.example.paymentsrecording.data.repository.ReviewRepository
import com.example.paymentsrecording.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val reviewRepo: ReviewRepository,
    private val txRepo: TransactionRepository
) : ViewModel() {

    val reviews = reviewRepo.observeAll().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _currentStats = MutableStateFlow<ReviewStats?>(null)
    val currentStats: StateFlow<ReviewStats?> = _currentStats.asStateFlow()

    private val _currentReview = MutableStateFlow<Review?>(null)
    val currentReview: StateFlow<Review?> = _currentReview.asStateFlow()

    fun createReview(name: String, start: Long, end: Long) = viewModelScope.launch {
        reviewRepo.insert(Review(name = name, startDate = start, endDate = end))
    }

    fun deleteReview(review: Review) = viewModelScope.launch {
        reviewRepo.delete(review)
    }

    fun loadReview(review: Review) = viewModelScope.launch {
        _currentReview.value = review
        val txs = txRepo.getByRange(review.startDate, review.endDate)
        _currentStats.value = StatsCalculator.compute(txs, review.startDate, review.endDate)
    }

    /** 详情页根据 id 自行加载（独立 ViewModel 实例）。 */
    fun loadById(id: Long) = viewModelScope.launch {
        val review = reviewRepo.getById(id) ?: return@launch
        _currentReview.value = review
        val txs = txRepo.getByRange(review.startDate, review.endDate)
        _currentStats.value = StatsCalculator.compute(txs, review.startDate, review.endDate)
    }

    fun clearCurrent() {
        _currentReview.value = null
        _currentStats.value = null
    }
}

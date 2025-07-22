package com.example.foodietracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodietracker.data.model.Meal
import com.example.foodietracker.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MealViewModel(
    private val repository: MealRepository
) : ViewModel() {

    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadMeals()
    }

    private fun loadMeals() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getMeals()
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { meals ->
                    _meals.value = meals
                    _isLoading.value = false
                }
        }
    }

    fun addMeal(meal: Meal) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.addMeal(meal)
                .onSuccess {
                    loadMeals()
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun deleteMeal(mealId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteMeal(mealId)
                .onSuccess {
                    loadMeals()
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
} 
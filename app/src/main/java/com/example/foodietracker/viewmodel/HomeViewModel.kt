package com.example.foodietracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodietracker.data.model.Meal
import com.example.foodietracker.data.model.MealType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadMeals()
    }

    fun loadMeals() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val userId = auth.currentUser?.uid ?: return@launch
                val mealsRef = db.collection("meals")
                    .whereEqualTo("userId", userId)
                    .orderBy("timestamp")
                
                val snapshot = mealsRef.get().await()
                val mealsList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Meal::class.java)
                }
                
                _meals.value = mealsList
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load meals"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addMeal(type: MealType, name: String, description: String, calories: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val userId = auth.currentUser?.uid ?: return@launch
                val meal = Meal(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    type = type,
                    name = name,
                    description = description,
                    timestamp = Date(),
                    calories = calories
                )
                
                db.collection("meals")
                    .document(meal.id)
                    .set(meal)
                    .await()
                
                loadMeals() // Reload meals after adding
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add meal"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMealsByType(type: MealType): List<Meal> {
        return _meals.value.filter { it.type == type }
    }

    fun getMealsByDateRange(startDate: Date, endDate: Date): List<Meal> {
        return _meals.value.filter { 
            it.timestamp in startDate..endDate 
        }
    }
} 
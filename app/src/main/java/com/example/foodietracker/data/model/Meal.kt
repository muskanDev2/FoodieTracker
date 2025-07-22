package com.example.foodietracker.data.model

import java.util.Date

data class Meal(
    val id: String = "",
    val userId: String = "",
    val type: MealType = MealType.BREAKFAST,
    val name: String = "",
    val description: String = "",
    val timestamp: Date = Date(),
    val calories: Int = 0
)

enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
} 
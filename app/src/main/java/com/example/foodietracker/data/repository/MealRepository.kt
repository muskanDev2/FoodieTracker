package com.example.foodietracker.data.repository

import com.example.foodietracker.data.model.Meal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

interface MealRepository {
    suspend fun addMeal(meal: Meal): Result<Meal>
    fun getMeals(): Flow<List<Meal>>
    suspend fun deleteMeal(mealId: String): Result<Unit>
}

class MealRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : MealRepository {

    override suspend fun addMeal(meal: Meal): Result<Meal> = try {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val mealWithUserId = meal.copy(userId = userId)
        val docRef = firestore.collection("meals").document()
        val mealWithId = mealWithUserId.copy(id = docRef.id)
        docRef.set(mealWithId).await()
        Result.success(mealWithId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun getMeals(): Flow<List<Meal>> = flow {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val snapshot = firestore.collection("meals")
            .whereEqualTo("userId", userId)
            .get()
            .await()
        emit(snapshot.toObjects(Meal::class.java))
    }

    override suspend fun deleteMeal(mealId: String): Result<Unit> = try {
        firestore.collection("meals").document(mealId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
} 
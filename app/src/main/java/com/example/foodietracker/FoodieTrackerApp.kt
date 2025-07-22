package com.example.foodietracker

import android.app.Application
import com.google.firebase.FirebaseApp

class FoodieTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
} 
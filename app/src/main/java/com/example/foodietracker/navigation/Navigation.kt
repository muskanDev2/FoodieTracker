package com.example.foodietracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodietracker.ui.screens.home.HomeScreen
import com.example.foodietracker.ui.screens.login.LoginScreen
import com.example.foodietracker.ui.screens.signup.SignupScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Navigation() {
    val navController = rememberNavController()
    var isAuthenticated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            isAuthenticated = auth.currentUser != null
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) "home" else "login"
    ) {
        composable("login") {
            LoginScreen(navController)
        }
        composable("signup") {
            SignupScreen(navController)
        }
        composable("home") {
            HomeScreen(navController)
        }
    }
} 
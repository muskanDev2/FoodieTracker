package com.example.foodietracker.ui.screens.verification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodietracker.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

@Composable
fun VerificationScreen(
    navController: NavController,
    email: String,
    name: String,
    phoneNumber: String
) {
    var verificationCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var generatedCode by remember { mutableStateOf(generateVerificationCode()) }

    LaunchedEffect(Unit) {
        // In a real app, you would send this code via SMS or email
        // For demo purposes, we'll just show it on screen
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Verify Your Account",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "We've sent a verification code to your email",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // For demo purposes, show the code
        Text(
            text = "Your verification code is: $generatedCode",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = verificationCode,
            onValueChange = { 
                if (it.length <= 6) {
                    verificationCode = it
                }
            },
            label = { Text("Enter 6-digit code") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                if (verificationCode == generatedCode) {
                    isLoading = true
                    error = null
                    
                    // Create user in Firestore
                    val user = User(
                        id = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                        email = email,
                        name = name,
                        phoneNumber = phoneNumber,
                        isVerified = true,
                        verificationCode = generatedCode
                    )

                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.id)
                        .set(user)
                        .addOnSuccessListener {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        .addOnFailureListener { e ->
                            error = e.message ?: "Failed to create user"
                            isLoading = false
                        }
                } else {
                    error = "Invalid verification code"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading && verificationCode.length == 6
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Verify")
            }
        }

        TextButton(
            onClick = { 
                generatedCode = generateVerificationCode()
                error = null
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Resend Code")
        }
    }
}

private fun generateVerificationCode(): String {
    return Random().nextInt(900000).plus(100000).toString()
} 
package com.example.foodietracker.ui.screens.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navController: NavController) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var verificationCode by remember { mutableStateOf("") }
    var showVerificationDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&*._-]).{8,}")
    val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")

    fun validateInputs(): Boolean {
        if (fullName.isBlank()) {
            error = "Please enter your full name"
            return false
        }
        if (!email.matches(emailRegex)) {
            error = "Please enter a valid email"
            return false
        }
        if (!password.matches(passwordRegex)) {
            error = "Password must be at least 8 characters long and contain uppercase, lowercase, number, and special character"
            return false
        }
        if (password != confirmPassword) {
            error = "Passwords do not match"
            return false
        }
        if (phoneNumber.length < 10) {
            error = "Please enter a valid phone number"
            return false
        }
        return true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                    Icon(
                        if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
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
                if (validateInputs()) {
                    isLoading = true
                    error = null
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = task.result.user
                                user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                                    if (verificationTask.isSuccessful) {
                                        showVerificationDialog = true
                                    } else {
                                        error = "Failed to send verification email"
                                        isLoading = false
                                    }
                                }
                            } else {
                                error = task.exception?.message ?: "Signup failed"
                                isLoading = false
                            }
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Sign Up")
            }
        }

        TextButton(
            onClick = { navController.navigate("login") },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Already have an account? Login")
        }
    }

    if (showVerificationDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Email Verification") },
            text = {
                Column {
                    Text("Please check your email for the verification code.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = verificationCode,
                        onValueChange = { verificationCode = it },
                        label = { Text("Verification Code") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isLoading = true
                        FirebaseAuth.getInstance().currentUser?.reload()?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = FirebaseAuth.getInstance().currentUser
                                if (user?.isEmailVerified == true) {
                                    // Save user data to Firestore
                                    val userData = hashMapOf(
                                        "fullName" to fullName,
                                        "email" to email,
                                        "phoneNumber" to phoneNumber,
                                        "createdAt" to com.google.firebase.Timestamp.now()
                                    )
                                    FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .document(user.uid)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            showVerificationDialog = false
                                            showSuccessDialog = true
                                            isLoading = false
                                        }
                                        .addOnFailureListener {
                                            error = "Failed to save user data"
                                            isLoading = false
                                        }
                                } else {
                                    error = "Please verify your email first"
                                    isLoading = false
                                }
                            } else {
                                error = "Failed to reload user"
                                isLoading = false
                            }
                        }
                    }
                ) {
                    Text("Verify")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showVerificationDialog = false
                        isLoading = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSuccessDialog) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(4000)
            showSuccessDialog = false
            navController.navigate("login") {
                popUpTo("signup") { inclusive = true }
            }
        }
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Signup Successful!") },
            text = { Text("Your account has been created. Please login.") },
            confirmButton = {},
            dismissButton = {}
        )
    }
} 
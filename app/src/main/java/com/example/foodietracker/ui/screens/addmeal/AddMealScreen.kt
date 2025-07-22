package com.example.foodietracker.ui.screens.addmeal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ArrowBack
import java.text.SimpleDateFormat
import java.util.*

data class TempMeal(
    val name: String,
    val calories: Int,
    val type: String,
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealScreen(navController: NavController) {
    var mealName by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf("Breakfast") }
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack", "Cheat Meal")
    var expanded by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var caloriesError by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Meal") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = mealName,
                onValueChange = { mealName = it; nameError = false },
                label = { Text("Meal Name") },
                isError = nameError,
                leadingIcon = { Icon(Icons.Default.Restaurant, contentDescription = null) },
                supportingText = if (nameError) { { Text("Meal name cannot be empty") } } else { null },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = calories,
                onValueChange = { newValue ->
                    calories = newValue.filter { it.isDigit() }
                    caloriesError = false
                },
                label = { Text("Calories") },
                isError = caloriesError,
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                supportingText = if (caloriesError) { { Text("Calories must be a number") } } else { null },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedMealType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Meal Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    mealTypes.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                selectedMealType = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    nameError = mealName.isBlank()
                    caloriesError = calories.toIntOrNull() == null

                    if (!nameError && !caloriesError) {
                        // For now, just show a success message. No Firebase connection yet.
                        showSuccessMessage = true
                        // You would typically save this meal to Firebase here:
                        // val newMeal = TempMeal(name = mealName, calories = calories.toInt(), type = selectedMealType)
                        // Save to Firebase...
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Meal")
            }

            if (showSuccessMessage) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000L)
                    showSuccessMessage = false
                    navController.navigateUp() // Go back to HomeScreen
                }
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text("Success!") },
                    text = { Text("Meal saved temporarily.") },
                    confirmButton = {},
                    dismissButton = {}
                )
            }
        }
    }
} 
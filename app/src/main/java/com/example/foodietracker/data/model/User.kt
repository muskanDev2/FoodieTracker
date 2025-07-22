package com.example.foodietracker.data.model

import java.util.Date

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val firstLogin: Date = Date(),
    val isVerified: Boolean = false,
    val verificationCode: String = ""
) 
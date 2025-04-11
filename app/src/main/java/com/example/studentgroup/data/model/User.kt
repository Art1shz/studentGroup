package com.example.studentgroup.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val registrationCode: String = "",
    val role: String = "student"
)

data class RegistrationCode(
    val code: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val isUsed: Boolean = false,
    val usedBy: String = ""
) 
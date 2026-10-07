package com.example.loginapp.model

enum class UserRole {
    ADMIN,
    SUPERVISOR,
    OPERADOR
}

data class User(
    val email: String,
    val role: UserRole,
    val name: String
)
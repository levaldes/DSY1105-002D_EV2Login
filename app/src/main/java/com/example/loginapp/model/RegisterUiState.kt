package com.example.loginapp.model

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val currentUser: User? = null,
    val errores: RegisterErrores = RegisterErrores()
)

data class RegisterErrores(
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val authError: String? = null
)
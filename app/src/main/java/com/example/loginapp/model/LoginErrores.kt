package com.example.loginapp.model

data class LoginErrores(
    val emailError: String? = null,
    val passwordError: String? = null,
    val authError: String? = null
)
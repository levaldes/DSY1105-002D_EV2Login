package com.example.loginapp.model

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errores: LoginErrores = LoginErrores(),
    val currentUser: User? = null
) {
    // El botón se habilita solo si ambos campos tienen texto
    val isLoginEnabled: Boolean
        get() = email.isNotBlank() && password.isNotBlank() && !isLoading
}
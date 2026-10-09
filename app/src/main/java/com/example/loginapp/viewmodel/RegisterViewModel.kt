package com.example.loginapp.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginapp.model.*
import com.example.loginapp.navigation.NavigationEvent
import com.example.loginapp.navigation.Screen
import com.example.loginapp.repository.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

    // ⚠️ Sustituye esta cadena con tu Web Client ID copiado de Firebase Console
    private val webClientId = "682645433329-bo08am7r6grjvimr3041hbck13llt8rt.apps.googleusercontent.com"

    fun onNameChange(newName: String) {
        _uiState.update {
            it.copy(name = newName, errores = it.errores.copy(nameError = null, authError = null))
        }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update {
            it.copy(email = newEmail, errores = it.errores.copy(emailError = null, authError = null))
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update {
            it.copy(password = newPassword, errores = it.errores.copy(passwordError = null, authError = null))
        }
    }

    private fun validarFormulario(): Boolean {
        var isValid = true
        var nameErr: String? = null
        var emailErr: String? = null
        var passErr: String? = null

        if (_uiState.value.name.trim().isEmpty()) {
            nameErr = "El nombre es obligatorio"
            isValid = false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_uiState.value.email).matches()) {
            emailErr = "Formato de correo inválido"
            isValid = false
        }

        if (_uiState.value.password.length < 6) {
            passErr = "La contraseña debe tener al menos 6 caracteres"
            isValid = false
        }

        _uiState.update {
            it.copy(errores = RegisterErrores(nameError = nameErr, emailError = emailErr, passwordError = passErr))
        }
        return isValid
    }

    fun registerWithEmail() {
        if (!validarFormulario()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errores = RegisterErrores()) }

            val result = authRepository.registerWithEmail(
                name = _uiState.value.name,
                email = _uiState.value.email,
                password = _uiState.value.password
            )

            handleAuthResult(result)
        }
    }

    // ✅ Recibe el Context que viene de RegisterScreen
    fun registerWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errores = RegisterErrores()) }
            val result = authRepository.loginWithGoogle(context = context, webClientId = webClientId)
            handleAuthResult(result)
        }
    }

    // ✅ Recibe el Activity que viene de RegisterScreen
    fun registerWithGitHub(activity: Activity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errores = RegisterErrores()) }
            val result = authRepository.loginWithGithub(activity = activity)
            handleAuthResult(result)
        }
    }

    private suspend fun handleAuthResult(result: Result<User>) {
        result.onSuccess { user ->
            _uiState.update { it.copy(isLoading = false, currentUser = user) }

            val route = when (user.role) {
                UserRole.ADMIN -> Screen.HomeAdmin.route
                UserRole.SUPERVISOR -> Screen.HomeSupervisor.route
                UserRole.OPERADOR -> Screen.HomeOperator.route
            }
            _navigationEvent.emit(NavigationEvent.NavigateTo(route))
        }.onFailure { exception ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errores = RegisterErrores(authError = exception.message ?: "Error al registrarse")
                )
            }
        }
    }

    fun navigateToLogin() {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateTo(Screen.Login.route))
        }
    }
}
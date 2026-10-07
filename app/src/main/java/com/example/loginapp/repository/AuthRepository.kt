package com.example.loginapp.repository

import com.example.loginapp.model.User
import com.example.loginapp.model.UserRole
import kotlinx.coroutines.delay

class AuthRepository {

    // 3 usuarios obligatorios solicitados en la pauta
    private val mockUsers = mapOf(
        "admin@guardian.test" to ("123456" to User("admin@guardian.test", UserRole.ADMIN, "Administrador")),
        "supervisor@guardian.test" to ("123456" to User("supervisor@guardian.test", UserRole.SUPERVISOR, "Supervisor")),
        "operador@guardian.test" to ("123456" to User("operador@guardian.test", UserRole.OPERADOR, "Operador"))
    )

    // Simula autenticación con retardo de red
    suspend fun login(email: String, password: String): Result<User> {
        delay(1000) // Simulación de carga/red

        val userData = mockUsers[email.trim().lowercase()]
        return if (userData != null && userData.first == password) {
            Result.success(userData.second)
        } else {
            Result.failure(Exception("Credenciales incorrectas o usuario no registrado"))
        }
    }
}
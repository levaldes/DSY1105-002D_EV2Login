package com.example.loginapp.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.loginapp.model.User
import com.example.loginapp.model.UserRole
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import android.app.Activity
import com.google.firebase.auth.OAuthProvider

class AuthRepository {

    // Declaración de Firebase Auth
    private val firebaseAuth = Firebase.auth

    private val mockUsers = mutableMapOf(
        "admin@guardian.test" to ("123456" to User("admin@guardian.test", UserRole.ADMIN, "Administrador")),
        "supervisor@guardian.test" to ("123456" to User("supervisor@guardian.test", UserRole.SUPERVISOR, "Supervisor")),
        "operador@guardian.test" to ("123456" to User("operador@guardian.test", UserRole.OPERADOR, "Operador"))
    )

    suspend fun login(email: String, password: String): Result<User> {
        delay(1000)
        val userData = mockUsers[email.trim().lowercase()]
        return if (userData != null && userData.first == password) {
            Result.success(userData.second)
        } else {
            Result.failure(Exception("Credenciales incorrectas o usuario no registrado"))
        }
    }

    suspend fun registerWithEmail(name: String, email: String, password: String): Result<User> {
        delay(1000)
        val cleanEmail = email.trim().lowercase()

        if (mockUsers.containsKey(cleanEmail)) {
            return Result.failure(Exception("El correo ya se encuentra registrado"))
        }

        val newUser = User(cleanEmail, UserRole.OPERADOR, name.trim())
        mockUsers[cleanEmail] = password to newUser
        return Result.success(newUser)
    }

    suspend fun loginWithGithub(activity: Activity): Result<User> {
        return try {
            val provider = OAuthProvider.newBuilder("github.com")
            provider.scopes = listOf("user:email")

            val pendingResultTask = firebaseAuth.pendingAuthResult
            val authResult = if (pendingResultTask != null) {
                pendingResultTask.await()
            } else {
                firebaseAuth.startActivityForSignInWithProvider(activity, provider.build()).await()
            }

            val firebaseUser = authResult.user
            val user = User(
                email = firebaseUser?.email ?: "github_user@app.com",
                role = UserRole.OPERADOR,
                name = firebaseUser?.displayName ?: "Usuario GitHub"
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithGoogle(context: Context, webClientId: String): Result<User> {
        return try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

            val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
            val firebaseUser = authResult.user

            val user = User(
                email = firebaseUser?.email ?: "",
                role = UserRole.OPERADOR,
                name = firebaseUser?.displayName ?: "Usuario Google"
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
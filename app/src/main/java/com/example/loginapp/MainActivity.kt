package com.example.loginapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.loginapp.navigation.NavigationEvent
import com.example.loginapp.navigation.Screen
import com.example.loginapp.ui.screens.*
import com.example.loginapp.ui.utils.rememberWindowSizeClass
import com.example.loginapp.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(loginViewModel = loginViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(loginViewModel: LoginViewModel) {
    val navController = rememberNavController()
    val windowSizeClass = rememberWindowSizeClass()

    LaunchedEffect(Unit) {
        loginViewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route) {
                        if (event.route == Screen.Login.route) {
                            popUpTo(0)
                        }
                    }
                }
                is NavigationEvent.NavigateBack -> navController.popBackStack()
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(viewModel = loginViewModel, windowSizeClass = windowSizeClass)
        }
        composable(Screen.HomeAdmin.route) {
            HomeAdminScreen(viewModel = loginViewModel)
        }
        composable(Screen.HomeSupervisor.route) {
            HomeSupervisorScreen(viewModel = loginViewModel)
        }
        composable(Screen.HomeOperator.route) {
            HomeOperadorScreen(viewModel = loginViewModel)
        }
    }
}
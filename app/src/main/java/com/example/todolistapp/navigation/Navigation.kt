package com.example.todolistapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolistapp.ui.LoginScreen
import com.example.todolistapp.ui.RegisterScreen
import com.example.todolistapp.ui.TodoListScreen
import com.example.todolistapp.viewmodel.AuthViewModel
import com.example.todolistapp.viewmodel.LoginViewModel
import com.example.todolistapp.viewmodel.CreateAccountViewModel
import com.example.todolistapp.viewmodel.TodoViewModel
import com.example.todolistapp.data.UserPreferences

@Composable
fun AppNavigation(userPreferences: UserPreferences) {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel { AuthViewModel(userPreferences) }
    val todoViewModel: TodoViewModel = viewModel { TodoViewModel(userPreferences) }
    val loginViewModel: LoginViewModel = viewModel { LoginViewModel(userPreferences) }
    val createAccountViewModel: CreateAccountViewModel = viewModel {
        CreateAccountViewModel(userPreferences, todoViewModel)
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = {
                    authViewModel.setAuthenticated(true)
                    navController.navigate("todoList") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = createAccountViewModel,
                onNavigateToLogin = { navController.navigate("login") },
                onRegisterSuccess = {
                    authViewModel.setAuthenticated(true)
                    navController.navigate("todoList") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }
        composable("todoList") {
            TodoListScreen(
                viewModel = todoViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("todoList") { inclusive = true }
                    }
                }
            )
        }
    }
}
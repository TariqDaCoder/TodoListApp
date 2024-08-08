package com.example.todolistapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.todolistapp.data.UserPreferences
import com.example.todolistapp.api.RetrofitClient
import com.example.todolistapp.data.UserLogin
import com.example.todolistapp.data.ApiKey
import com.example.todolistapp.BuildConfig

class LoginViewModel(private val userPreferences: UserPreferences) : ViewModel() {
    private val apiService = RetrofitClient.todoApiService
    private val apiKey = ApiKey(BuildConfig.API_KEY)

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = apiService.loginUser(apiKey.value, UserLogin(email, password))
                userPreferences.saveUserData(response.token, response.id)
                _loginState.value = LoginState.Success
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login failed", e)
                _loginState.value = LoginState.Error("Login failed: ${e.localizedMessage}")
            }
        }
    }

    fun clearState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
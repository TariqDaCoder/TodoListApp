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
import com.example.todolistapp.data.UserRegistration
import com.example.todolistapp.data.ApiKey
import com.example.todolistapp.BuildConfig
import retrofit2.HttpException

class CreateAccountViewModel(
    private val userPreferences: UserPreferences,
    private val todoViewModel: TodoViewModel
) : ViewModel() {
    private val apiService = RetrofitClient.todoApiService
    private val apiKey = ApiKey(BuildConfig.API_KEY)

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()

    fun createAccount(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                val response = apiService.registerUser(apiKey.value, UserRegistration(name, email, password))
                userPreferences.saveUserData(response.token, response.id)
                _registrationState.value = RegistrationState.Success
                todoViewModel.refreshTodos()
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("CreateAccountViewModel", "Registration failed: $errorBody", e)
                _registrationState.value = RegistrationState.Error("Registration failed: ${e.code()} ${e.message()}\n$errorBody")
            } catch (e: Exception) {
                Log.e("CreateAccountViewModel", "Registration failed", e)
                _registrationState.value = RegistrationState.Error("Registration failed: ${e.localizedMessage}")
            }
        }
    }

    fun clearState() {
        _registrationState.value = RegistrationState.Idle
    }
}

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    object Success : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}
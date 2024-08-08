package com.example.todolistapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.todolistapp.api.RetrofitClient
import com.example.todolistapp.data.Todo
import com.example.todolistapp.data.ApiKey
import com.example.todolistapp.BuildConfig
import com.example.todolistapp.data.UserPreferences

class TodoViewModel(private val userPreferences: UserPreferences) : ViewModel() {
    private val apiService = RetrofitClient.todoApiService
    private val apiKey = ApiKey(BuildConfig.API_KEY)

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        refreshTodos()
    }

    fun refreshTodos() {
        viewModelScope.launch {
            userPreferences.userId.filterNotNull().first()?.let { userId ->
                fetchTodos(userId)
            }
        }
    }

    private fun fetchTodos(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = userPreferences.userToken.first()
                if (token != null) {
                    val fetchedTodos = apiService.getTodos(userId, apiKey.value, "Bearer $token")
                    _todos.value = fetchedTodos
                } else {
                    _error.value = "No authentication token found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to fetch todos: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addTodo(text: String) {
        viewModelScope.launch {
            userPreferences.userId.filterNotNull().first().let { userId ->
                _isLoading.value = true
                _error.value = null
                try {
                    val token = userPreferences.userToken.first()
                    if (token != null) {
                        val newTodo = Todo(id = "", text = text, completed = false)
                        val addedTodo = apiService.createTodo(userId, apiKey.value, "Bearer $token", newTodo)
                        _todos.value += addedTodo
                    } else {
                        _error.value = "No authentication token found"
                    }
                } catch (e: Exception) {
                    _error.value = "Failed to add todo: ${e.localizedMessage}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun toggleTodoCompletion(todoId: String) {
        viewModelScope.launch {
            userPreferences.userId.filterNotNull().first().let { userId ->
                _isLoading.value = true
                _error.value = null
                try {
                    val token = userPreferences.userToken.first()
                    if (token != null) {
                        val todo = _todos.value.find { it.id == todoId } ?: throw Exception("Todo not found")
                        val updatedTodo = todo.copy(completed = !todo.completed)
                        val result = apiService.updateTodo(userId, todoId, apiKey.value, "Bearer $token", updatedTodo)
                        _todos.value = _todos.value.map { if (it.id == result.id) result else it }
                    } else {
                        _error.value = "No authentication token found"
                    }
                } catch (e: Exception) {
                    _error.value = "Failed to update todo: ${e.localizedMessage}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun deleteTodo(todoId: String) {
        viewModelScope.launch {
            userPreferences.userId.filterNotNull().first().let { userId ->
                _isLoading.value = true
                _error.value = null
                try {
                    val token = userPreferences.userToken.first()
                    if (token != null) {
                        apiService.deleteTodo(userId, todoId, apiKey.value, "Bearer $token")
                        _todos.value = _todos.value.filter { it.id != todoId }
                    } else {
                        _error.value = "No authentication token found"
                    }
                } catch (e: Exception) {
                    _error.value = "Failed to delete todo: ${e.localizedMessage}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    class Factory(private val userPreferences: UserPreferences) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TodoViewModel(userPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
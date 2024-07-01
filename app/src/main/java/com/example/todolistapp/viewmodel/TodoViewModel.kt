package com.example.todolistapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.todolistapp.TodoItem

class TodoViewModel : ViewModel() {
    private val _todos = MutableStateFlow<List<TodoItem>>(emptyList())
    val todos: StateFlow<List<TodoItem>> = _todos.asStateFlow()

    fun addTodo(text: String) {
        val newTodo = TodoItem(
            id = _todos.value.size,
            text = text
        )
        _todos.value = _todos.value + newTodo
    }

    fun toggleTodoCompletion(id: Int) {
        _todos.value = _todos.value.map {
            if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
        }
    }

    fun deleteTodo(id: Int) {
        _todos.value = _todos.value.filter { it.id != id }
    }
}
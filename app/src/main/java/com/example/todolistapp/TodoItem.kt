package com.example.todolistapp

data class TodoItem(
    val id: Int,
    val text: String,
    var isCompleted: Boolean = false
)
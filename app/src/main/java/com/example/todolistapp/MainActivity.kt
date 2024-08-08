package com.example.todolistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.todolistapp.ui.theme.TodoListAppTheme
import com.example.todolistapp.navigation.AppNavigation
import com.example.todolistapp.data.UserPreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPreferences = UserPreferences(this)
        setContent {
            TodoListAppTheme {
                AppNavigation(userPreferences)
            }
        }
    }
}
package com.example.todolistapp

import androidx.compose.runtime.collectAsState
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.todolistapp.TodoItem
import com.example.todolistapp.ui.theme.TodoListAppTheme
import com.example.todolistapp.viewmodel.TodoViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TodoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoListAppTheme {
                TodoListApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListApp(viewModel: TodoViewModel) {
    val todos by viewModel.todos.collectAsState()
    var isBottomSheetVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { isBottomSheetVisible = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_todo))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(todos) { todo ->
                TodoItemRow(
                    todo = todo,
                    onCheckedChange = { viewModel.toggleTodoCompletion(todo.id) },
                    onDelete = { viewModel.deleteTodo(todo.id) }
                )
            }
        }
    }

    if (isBottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { isBottomSheetVisible = false }
        ) {
            AddTodoBottomSheet(
                onSave = { newTodoText ->
                    viewModel.addTodo(newTodoText)
                    isBottomSheetVisible = false
                },
                onDismiss = { isBottomSheetVisible = false }
            )
        }
    }
}

@Composable
fun TodoItemRow(
    todo: TodoItem,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = todo.isCompleted,
            onCheckedChange = { onCheckedChange(it) }
        )
        Text(
            text = todo.text,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_todo))
        }
    }
}

@Composable
fun AddTodoBottomSheet(
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var todoText by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = todoText,
            onValueChange = {
                todoText = it
                isError = false
            },
            label = { Text(stringResource(R.string.new_todo)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            trailingIcon = {
                IconButton(onClick = { todoText = "" }) {
                    Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear_text))
                }
            },
            isError = isError
        )
        if (isError) {
            Text(
                text = stringResource(R.string.error_empty_todo),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(20.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
            Button(
                onClick = {
                    if (todoText.isBlank()) {
                        isError = true
                    } else {
                        onSave(todoText)
                        todoText = ""
                    }
                }
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}
//done :)
package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.todoapp.ui.theme.ToDoAppTheme
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.data.InMemoryTodoRepository
import com.example.todoapp.ui.TodoScreen
import com.example.todoapp.viewmodel.TodoViewModelFactory
import com.example.todoapp.viewmodel.TodoViewModelWithRepo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repo = InMemoryTodoRepository()
        val factory = TodoViewModelFactory(repo)

        setContent {
            MaterialTheme {
                val vm: TodoViewModelWithRepo = viewModel(factory = factory)
                TodoScreen(vm = vm)
            }
        }
    }
}

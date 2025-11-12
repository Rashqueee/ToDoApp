package com.example.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.model.Todo
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface TodoRepository {
    suspend fun loadAll(): List<Todo>
    suspend fun insert(title: String)
    suspend fun toggle(id: Int)
    suspend fun delete(id: Int)
}


class TodoViewModelWithRepo(
    private val repo: TodoRepository
) : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos

    init {
        viewModelScope.launch {
            _todos.value = repo.loadAll()
        }
    }

    fun addTask(title: String) {
        viewModelScope.launch {
            repo.insert(title)
            _todos.value = repo.loadAll()
        }
    }

    fun toggleTask(id: Int) {
        viewModelScope.launch {
            repo.toggle(id)
            _todos.value = repo.loadAll()
        }
    }

    fun deleteTask(id: Int) {
        viewModelScope.launch {
            repo.delete(id)
            _todos.value = repo.loadAll()
        }
    }
}

class TodoViewModelFactory(
    private val repo: TodoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModelWithRepo::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModelWithRepo(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

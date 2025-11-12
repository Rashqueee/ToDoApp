package com.example.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.model.Todo
import com.example.todoapp.model.StatusFilter
import kotlinx.coroutines.flow.*
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

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    // StateFlow untuk menyimpan status filter
    private val _statusFilter = MutableStateFlow(StatusFilter.SEMUA)
    val statusFilter: StateFlow<StatusFilter> = _statusFilter

    // StateFlow yang mengkombinasikan todos dengan filter
    private val _filteredTodos = MutableStateFlow<List<Todo>>(emptyList())
    val filteredTodos: StateFlow<List<Todo>> = _filteredTodos


    init {
        viewModelScope.launch {
            _todos.value = repo.loadAll()

            combine(_todos, _statusFilter, _query) { todos, filter, query ->
                val tempTodos = when (filter) {
                    StatusFilter.SEMUA -> todos
                    StatusFilter.AKTIF -> todos.filter { !it.isDone }
                    StatusFilter.SELESAI -> todos.filter { it.isDone }
                }

                if (query.isBlank()) tempTodos
                else tempTodos.filter { it.title.contains(query, ignoreCase = true) }

            }.collect{ filtered -> _filteredTodos.value = filtered}

//            combine(_filteredTodos, _query) { todos, query ->
//                if (query.isBlank()) todos
//                else todos.filter { it.title.contains(query, ignoreCase = true) }
//            }.collect { filtered ->
//                _filteredTodos.value = filtered
//            }
        }
    }

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
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

    // Fungsi untuk mengubah status filter
    fun setStatusFilter(filter: StatusFilter) {
        _statusFilter.value = filter
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
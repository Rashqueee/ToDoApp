package com.example.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import com.example.todoapp.model.Todo
import com.example.todoapp.model.StatusFilter

class TodoViewModel : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos

    // StateFlow untuk menyimpan status filter
    private val _statusFilter = MutableStateFlow(StatusFilter.SEMUA)
    val statusFilter: StateFlow<StatusFilter> = _statusFilter

    // StateFlow yang mengkombinasikan todos dengan filter
    val filteredTodos: StateFlow<List<Todo>> = combine(_todos, _statusFilter) { todos, filter ->
        when (filter) {
            StatusFilter.SEMUA -> todos
            StatusFilter.AKTIF -> todos.filter { !it.isDone }
            StatusFilter.SELESAI -> todos.filter { it.isDone }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addTask(title: String) {
        val nextId = (_todos.value.maxOfOrNull { it.id } ?: 0) + 1
        val newTask = Todo(id = nextId, title = title)
        _todos.value = _todos.value + newTask
    }

    fun toggleTask(id: Int) {
        _todos.value = _todos.value.map { t ->
            if (t.id == id) t.copy(isDone = !t.isDone) else t
        }
    }

    fun deleteTask(id: Int) {
        _todos.value = _todos.value.filterNot { it.id == id }
    }

    // Fungsi untuk mengubah status filter
    fun setStatusFilter(filter: StatusFilter) {
        _statusFilter.value = filter
    }
}
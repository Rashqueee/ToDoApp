package com.example.todoapp.data

import com.example.todoapp.model.Todo
import com.example.todoapp.viewmodel.TodoRepository
import kotlinx.coroutines.delay

class InMemoryTodoRepository : TodoRepository {
    private val todos = mutableListOf<Todo>()
    private var nextId = 1

    override suspend fun loadAll(): List<Todo> {
        delay(100) // simulasi loading dari database
        return todos.toList()
    }

    override suspend fun insert(title: String) {
        todos.add(Todo(id = nextId++, title = title))
    }

    override suspend fun toggle(id: Int) {
        val index = todos.indexOfFirst { it.id == id }
        if (index != -1) {
            val old = todos[index]
            todos[index] = old.copy(isDone = !old.isDone)
        }
    }

    override suspend fun delete(id: Int) {
        todos.removeAll { it.id == id }
    }
}

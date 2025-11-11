package com.example.todoapp.model

data class Todo(
    val id: Int,
    val title: String,
    val isDone: Boolean = false
)
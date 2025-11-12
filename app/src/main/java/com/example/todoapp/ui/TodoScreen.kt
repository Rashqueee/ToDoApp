package com.example.todoapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todoapp.model.StatusFilter
import com.example.todoapp.viewmodel.TodoViewModelWithRepo

@Composable
fun TodoScreen(vm: TodoViewModelWithRepo) {
    val todos by vm.filteredTodos.collectAsState()
    var text by rememberSaveable { mutableStateOf("") }
    val currentFilter by vm.statusFilter.collectAsState()

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Tambah tugas...") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    vm.addTask(text.trim())
                    text = ""
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Tambah")
        }

        HorizontalDivider()

        FilterControls(
            selectedFilter = currentFilter,
            onFilterSelected = { vm.setStatusFilter(it) }
        )

        LazyColumn {
            items(todos) { todo ->
                TodoItem(
                    todo = todo,
                    onToggle = { vm.toggleTask(todo.id) },
                    onDelete = { vm.deleteTask(todo.id) }
                )
            }
        }
    }
}

@Composable
fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        onClick = onClick,
        selected = isSelected,
        label = {
            Text(text = text)
        },
    )
}

@Composable
fun FilterControls(
    selectedFilter: StatusFilter,
    onFilterSelected: (StatusFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilterButton("Semua", selectedFilter == StatusFilter.SEMUA) {
            onFilterSelected(StatusFilter.SEMUA)
        }
        FilterButton("Aktif", selectedFilter == StatusFilter.AKTIF) {
            onFilterSelected(StatusFilter.AKTIF)
        }
        FilterButton("Selesai", selectedFilter == StatusFilter.SELESAI) {
            onFilterSelected(StatusFilter.SELESAI)
        }
    }
}

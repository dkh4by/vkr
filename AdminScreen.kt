package com.example.electronicreception.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.electronicreception.data.Complaint
import com.example.electronicreception.data.FirebaseRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    repository: FirebaseRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var complaints by remember { mutableStateOf<List<Complaint>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorText by remember { mutableStateOf("") }

    fun reload() {
        scope.launch {
            try {
                isLoading = true
                complaints = repository.getAllComplaints()
            } catch (e: Exception) {
                errorText = e.message ?: "Ошибка загрузки обращений"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        reload()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Админ-панель") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {
            when {
                isLoading -> CircularProgressIndicator()

                errorText.isNotBlank() -> Text(errorText, color = MaterialTheme.colorScheme.error)

                complaints.isEmpty() -> Text("Обращений пока нет.")

                else -> {
                    complaints.forEach { complaint ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(complaint.id, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(complaint.subject)
                                Text("Заявитель: ${complaint.fullName}")
                                Text("Статус: ${complaint.status}")

                                Spacer(Modifier.height(10.dp))

                                Row {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                repository.updateComplaintStatus(complaint.id, "В работе")
                                                reload()
                                            }
                                        }
                                    ) {
                                        Text("В работу")
                                    }

                                    Spacer(Modifier.width(8.dp))

                                    Button(
                                        onClick = {
                                            scope.launch {
                                                repository.updateComplaintStatus(complaint.id, "Завершено")
                                                reload()
                                            }
                                        }
                                    ) {
                                        Text("Завершить")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
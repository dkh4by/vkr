package com.example.electronicreception.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.electronicreception.data.Complaint
import com.example.electronicreception.data.FirebaseRepository
import com.example.electronicreception.ui.components.AppCard
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyComplaintsScreen(
    repository: FirebaseRepository,
    onBack: () -> Unit,
    onComplaintClick: (String) -> Unit
){
    val scope = rememberCoroutineScope()

    var complaints by remember { mutableStateOf<List<Complaint>>(emptyList()) }
    var selectedStatus by remember { mutableStateOf("Все") }
    var isLoading by remember { mutableStateOf(true) }
    var errorText by remember { mutableStateOf("") }

    fun reload() {
        scope.launch {
            try {
                isLoading = true
                errorText = ""
                complaints = repository.getMyComplaints()
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

    val statuses = listOf("Все", "Новое", "В работе", "Завершено")

    val filteredComplaints = if (selectedStatus == "Все") {
        complaints
    } else {
        complaints.filter { it.status == selectedStatus }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Мои обращения")
                        Text(
                            text = "История поданных обращений",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { reload() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            AppCard {
                Text(
                    text = "Статистика",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(8.dp))

                Text("Всего обращений: ${complaints.size}")
                Text("Новые: ${complaints.count { it.status == "Новое" }}")
                Text("В работе: ${complaints.count { it.status == "В работе" }}")
                Text("Завершено: ${complaints.count { it.status == "Завершено" }}")
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                statuses.forEach { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { selectedStatus = status },
                        label = { Text(status) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            when {
                isLoading -> {
                    AppCard {
                        Text("Загрузка обращений...")
                    }
                }

                errorText.isNotBlank() -> {
                    AppCard {
                        Text(
                            text = errorText,
                            color = MaterialTheme.colorScheme.error
                        )

                        Spacer(Modifier.height(10.dp))

                        Button(onClick = { reload() }) {
                            Text("Повторить")
                        }
                    }
                }

                filteredComplaints.isEmpty() -> {
                    AppCard {
                        Text(
                            text = if (selectedStatus == "Все") {
                                "У вас пока нет обращений."
                            } else {
                                "Обращений со статусом «$selectedStatus» нет."
                            }
                        )
                    }
                }

                else -> {
                    filteredComplaints.forEach { complaint ->
                        ComplaintCard(
                            complaint = complaint,
                            onClick = {
                                onComplaintClick(complaint.id)
                            }
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ComplaintCard(
    complaint: Complaint,
    onClick: () -> Unit
){
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row {
                Text(
                    text = complaint.id,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                AssistChip(
                    onClick = {},
                    label = { Text(complaint.status) }
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = complaint.subject.ifBlank { "Без темы" },
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(Modifier.height(6.dp))

            Text("Категория: ${complaint.category.ifBlank { "-" }}")
            Text("Дата подачи: ${formatComplaintDate(complaint)}")

            if (complaint.description.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = complaint.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (complaint.locationLat != null && complaint.locationLng != null) {
                Spacer(Modifier.height(10.dp))

                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            "%.5f, %.5f".format(
                                complaint.locationLat,
                                complaint.locationLng
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null
                        )
                    }
                )
            }

            if (complaint.images.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Фотографии:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    complaint.images.forEach { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Фото обращения",
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .size(90.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

private fun formatComplaintDate(complaint: Complaint): String {
    val date = complaint.createdAt?.toDate() ?: return "-"
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru", "RU"))
    return formatter.format(date)
}
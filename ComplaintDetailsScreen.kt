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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.electronicreception.data.Complaint
import com.example.electronicreception.data.FirebaseRepository
import com.example.electronicreception.ui.components.AppCard
import com.example.electronicreception.ui.components.YandexMapPicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDetailsScreen(
    repository: FirebaseRepository,
    complaintId: String,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var complaint by remember { mutableStateOf<Complaint?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorText by remember { mutableStateOf("") }

    fun reload() {
        scope.launch {
            try {
                isLoading = true
                errorText = ""

                complaint = repository.getComplaintById(complaintId)

                if (complaint == null) {
                    errorText = "Обращение не найдено"
                }
            } catch (e: Exception) {
                errorText = e.message ?: "Ошибка загрузки обращения"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(complaintId) {
        reload()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Детали обращения")
                        Text(
                            text = complaintId,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
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
            when {
                isLoading -> {
                    AppCard {
                        Text("Загрузка обращения...")
                    }
                }

                errorText.isNotBlank() -> {
                    AppCard {
                        Text(
                            text = errorText,
                            color = MaterialTheme.colorScheme.error
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(onClick = { reload() }) {
                            Text("Повторить")
                        }
                    }
                }

                complaint != null -> {
                    ComplaintDetailsContent(complaint = complaint!!)
                }
            }
        }
    }
}

@Composable
private fun ComplaintDetailsContent(
    complaint: Complaint
) {
    AppCard {
        Text(
            text = complaint.id,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(8.dp))

        AssistChip(
            onClick = {},
            label = {
                Text(complaint.status.ifBlank { "Статус не указан" })
            }
        )

        Spacer(Modifier.height(12.dp))

        DetailRow(
            title = "Тема",
            value = complaint.subject.ifBlank { "-" }
        )

        DetailRow(
            title = "Категория",
            value = complaint.category.ifBlank { "-" }
        )

        DetailRow(
            title = "Дата подачи",
            value = formatDetailsDate(complaint)
        )

        DetailRow(
            title = "Адрес",
            value = complaint.address.ifBlank { "-" }
        )
    }

    Spacer(Modifier.height(16.dp))

    AppCard {
        Text(
            text = "Описание проблемы",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = complaint.description.ifBlank { "Описание отсутствует" },
            style = MaterialTheme.typography.bodyMedium
        )
    }

    if (complaint.images.isNotEmpty()) {
        Spacer(Modifier.height(16.dp))

        AppCard {
            Text(
                text = "Фотографии",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                complaint.images.forEach { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Фото обращения",
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(130.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }

    if (complaint.locationLat != null && complaint.locationLng != null) {
        Spacer(Modifier.height(16.dp))

        AppCard {
            Text(
                text = "Местоположение",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

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
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                }
            )

            Spacer(Modifier.height(12.dp))

            YandexMapPicker(
                latitude = complaint.locationLat,
                longitude = complaint.locationLng,
                onPointSelected = { _, _ ->
                    // На экране деталей точку не меняем
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DetailRow(
    title: String,
    value: String
) {
    Spacer(Modifier.height(6.dp))

    Text(
        text = title,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.tertiary
    )

    Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium
    )
}

private fun formatDetailsDate(complaint: Complaint): String {
    val date = complaint.createdAt?.toDate() ?: return "-"
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru", "RU"))
    return formatter.format(date)
}
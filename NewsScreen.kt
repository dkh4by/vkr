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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.electronicreception.data.FirebaseRepository
import com.example.electronicreception.data.NewsItem
import com.example.electronicreception.ui.components.AppCard
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    repository: FirebaseRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var news by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("all") }
    var isLoading by remember { mutableStateOf(true) }
    var errorText by remember { mutableStateOf("") }

    fun reload() {
        scope.launch {
            try {
                isLoading = true
                errorText = ""
                news = repository.getNews()
            } catch (e: Exception) {
                errorText = e.message ?: "Ошибка загрузки новостей"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        reload()
    }

    val categories = listOf(
        "all" to "Все",
        "report" to "Обращения",
        "announcement" to "Объявления",
        "housing" to "ЖКХ",
        "roads" to "Дороги",
        "improvement" to "Благоустройство",
        "administration" to "Администрация",
        "general" to "Общее"
    )

    val filteredNews = if (selectedCategory == "all") {
        news
    } else {
        news.filter { item ->
            item.category.equals(selectedCategory, ignoreCase = true)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Новости")
                        Text(
                            text = "Информация администрации",
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
                    text = "Категории новостей",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    categories.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category.first,
                            onClick = {
                                selectedCategory = category.first
                            },
                            label = {
                                Text(category.second)
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            when {
                isLoading -> {
                    AppCard {
                        Text("Загрузка новостей...")
                    }
                }

                errorText.isNotBlank() -> {
                    AppCard {
                        Text(
                            text = errorText,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                news.isEmpty() -> {
                    AppCard {
                        Text("Новостей пока нет.")
                    }
                }

                filteredNews.isEmpty() -> {
                    val selectedCategoryLabel = categories
                        .firstOrNull { it.first == selectedCategory }
                        ?.second
                        ?: "выбранной категории"

                    AppCard {
                        Text("В категории «$selectedCategoryLabel» новостей пока нет.")
                    }
                }

                else -> {
                    filteredNews.forEach { item ->
                        NewsCard(item)
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsCard(
    item: NewsItem
) {
    ElevatedCard(
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
            if (item.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = "Изображение новости",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.height(12.dp))
            }

            AssistChip(
                onClick = {},
                label = {
                    Text(getNewsCategoryLabel(item.category))
                }
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = item.title.ifBlank { "Без заголовка" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = item.text.ifBlank { "Текст новости отсутствует" },
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = formatNewsDate(item),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

private fun formatNewsDate(item: NewsItem): String {
    val date = item.createdAt?.toDate() ?: return "-"
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru", "RU"))
    return formatter.format(date)
}

private fun getNewsCategoryLabel(category: String): String {
    return when (category.lowercase()) {
        "report" -> "Обращения"
        "announcement" -> "Объявления"
        "housing" -> "ЖКХ"
        "roads" -> "Дороги"
        "improvement" -> "Благоустройство"
        "administration" -> "Администрация"
        "general" -> "Общее"
        else -> "Общее"
    }
}
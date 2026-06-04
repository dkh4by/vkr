package com.example.electronicreception.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.electronicreception.data.AppUser
import com.example.electronicreception.data.Complaint
import com.example.electronicreception.data.FirebaseRepository
import com.example.electronicreception.ui.components.AppCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    repository: FirebaseRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var user by remember { mutableStateOf<AppUser?>(null) }
    var complaints by remember { mutableStateOf<List<Complaint>>(emptyList()) }

    var phoneInput by remember { mutableStateOf("") }
    var selectedAvatarUri by remember { mutableStateOf<Uri?>(null) }
    var isPhoneEditMode by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(true) }
    var isSavingPhone by remember { mutableStateOf(false) }
    var isUploadingAvatar by remember { mutableStateOf(false) }

    var errorText by remember { mutableStateOf("") }
    var messageText by remember { mutableStateOf("") }

    val avatarPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedAvatarUri = uri
        }
    }

    fun reload() {
        scope.launch {
            try {
                isLoading = true
                errorText = ""
                messageText = ""

                val profile = repository.getCurrentUserProfile()
                user = profile
                phoneInput = profile?.phone.orEmpty()

                complaints = if (profile?.isAdmin == true) {
                    repository.getAllComplaints()
                } else {
                    repository.getMyComplaints()
                }
            } catch (e: Exception) {
                errorText = e.message ?: "Ошибка загрузки личного кабинета"
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
                title = {
                    Column {
                        Text("Личный кабинет")
                        Text(
                            text = "Профиль пользователя",
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
            when {
                isLoading -> {
                    AppCard {
                        Text("Загрузка профиля...")
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

                user == null -> {
                    AppCard {
                        Text("Профиль пользователя не найден.")
                    }
                }

                else -> {
                    ProfileMainCard(
                        user = user!!,
                        selectedAvatarUri = selectedAvatarUri,
                        isUploadingAvatar = isUploadingAvatar,
                        onPickAvatar = {
                            avatarPickerLauncher.launch("image/*")
                        },
                        onSaveAvatar = {
                            val uri = selectedAvatarUri ?: return@ProfileMainCard

                            scope.launch {
                                try {
                                    isUploadingAvatar = true
                                    messageText = ""
                                    errorText = ""

                                    repository.updateProfileAvatar(
                                        context = context,
                                        imageUri = uri
                                    )

                                    selectedAvatarUri = null
                                    messageText = "Фото профиля обновлено"

                                    reload()
                                } catch (e: Exception) {
                                    errorText = e.message ?: "Ошибка загрузки фото"
                                } finally {
                                    isUploadingAvatar = false
                                }
                            }
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    PhoneEditCard(
                        currentPhone = user!!.phone,
                        phoneInput = phoneInput,
                        isEditMode = isPhoneEditMode,
                        isSaving = isSavingPhone,
                        onStartEdit = {
                            phoneInput = user!!.phone
                            isPhoneEditMode = true
                        },
                        onPhoneChange = { value ->
                            phoneInput = value.filter { it.isDigit() }.take(11)
                        },
                        onCancelEdit = {
                            phoneInput = user!!.phone
                            isPhoneEditMode = false
                        },
                        onSavePhone = {
                            scope.launch {
                                try {
                                    isSavingPhone = true
                                    messageText = ""
                                    errorText = ""

                                    repository.updateProfilePhone(phoneInput.trim())

                                    messageText = "Номер телефона обновлён"
                                    isPhoneEditMode = false

                                    reload()
                                } catch (e: Exception) {
                                    errorText = e.message ?: "Ошибка сохранения телефона"
                                } finally {
                                    isSavingPhone = false
                                }
                            }
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    ProfileStatsCard(
                        isAdmin = user!!.isAdmin,
                        complaints = complaints
                    )

                    Spacer(Modifier.height(16.dp))

                    ProfileInfoCard(user = user!!)

                    if (messageText.isNotBlank()) {
                        Spacer(Modifier.height(16.dp))

                        AppCard {
                            Text(
                                text = messageText,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                    if (errorText.isNotBlank()) {
                        Spacer(Modifier.height(16.dp))

                        AppCard {
                            Text(
                                text = errorText,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileMainCard(
    user: AppUser,
    selectedAvatarUri: Uri?,
    isUploadingAvatar: Boolean,
    onPickAvatar: () -> Unit,
    onSaveAvatar: () -> Unit
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
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val avatarModel: Any? = selectedAvatarUri ?: user.avatarUrl.ifBlank { null }

            Surface(
                modifier = Modifier
                    .size(104.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary
            ) {
                if (avatarModel != null) {
                    AsyncImage(
                        model = avatarModel,
                        contentDescription = "Фото профиля",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = getUserInitial(user),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = user.fullName.ifBlank { "Пользователь" },
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(6.dp))

            AssistChip(
                onClick = {},
                label = {
                    Text(
                        if (user.isAdmin) {
                            "Администратор"
                        } else {
                            "Заявитель"
                        }
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                }
            )

            if (user.isAdmin && user.position.isNotBlank()) {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = user.position,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(Modifier.height(16.dp))

            ElevatedButton(
                onClick = onPickAvatar,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(Modifier.padding(4.dp))
                Text("Выбрать фото профиля")
            }

            if (selectedAvatarUri != null) {
                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = onSaveAvatar,
                    enabled = !isUploadingAvatar,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.padding(4.dp))
                    Text(if (isUploadingAvatar) "Загрузка..." else "Сохранить фото")
                }
            }
        }
    }
}

@Composable
private fun PhoneEditCard(
    currentPhone: String,
    phoneInput: String,
    isEditMode: Boolean,
    isSaving: Boolean,
    onStartEdit: () -> Unit,
    onPhoneChange: (String) -> Unit,
    onCancelEdit: () -> Unit,
    onSavePhone: () -> Unit
) {
    val digitsCount = phoneInput.count { it.isDigit() }
    val canSave = digitsCount == 11

    AppCard {
        Text(
            text = "Номер телефона",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(12.dp))

        if (!isEditMode) {
            ProfileInfoRow(
                icon = Icons.Default.Phone,
                title = "Телефон",
                value = currentPhone.ifBlank { "Не указан" }
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onStartEdit,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Phone, contentDescription = null)
                Spacer(Modifier.padding(4.dp))
                Text("Изменить телефон")
            }
        } else {
            OutlinedTextField(
                value = phoneInput,
                onValueChange = onPhoneChange,
                label = { Text("Телефон, 11 цифр") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                },
                supportingText = {
                    Text("Введено цифр: $digitsCount из 11")
                },
                isError = phoneInput.isNotBlank() && !canSave,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(Modifier.height(12.dp))

            if (canSave) {
                Button(
                    onClick = onSavePhone,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.padding(4.dp))
                    Text(if (isSaving) "Сохранение..." else "Сохранить телефон")
                }

                Spacer(Modifier.height(8.dp))
            }

            ElevatedButton(
                onClick = onCancelEdit,
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Отмена")
            }
        }
    }
}

@Composable
private fun ProfileStatsCard(
    isAdmin: Boolean,
    complaints: List<Complaint>
) {
    AppCard {
        Text(
            text = if (isAdmin) {
                "Статистика обращений в системе"
            } else {
                "Статистика моих обращений"
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatItem(
                title = "Всего",
                value = complaints.size,
                modifier = Modifier.weight(1f)
            )

            StatItem(
                title = "Новые",
                value = complaints.count { it.status == "Новое" },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatItem(
                title = "В работе",
                value = complaints.count { it.status == "В работе" },
                modifier = Modifier.weight(1f)
            )

            StatItem(
                title = "Завершено",
                value = complaints.count { it.status == "Завершено" },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatItem(
    title: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ProfileInfoCard(
    user: AppUser
) {
    AppCard {
        Text(
            text = "Контактные данные",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(12.dp))

        ProfileInfoRow(
            icon = Icons.Default.Email,
            title = "Email",
            value = user.email.ifBlank { "-" }
        )

        Spacer(Modifier.height(10.dp))

        ProfileInfoRow(
            icon = Icons.Default.Phone,
            title = "Телефон",
            value = user.phone.ifBlank { "-" }
        )

        if (user.isAdmin) {
            Spacer(Modifier.height(10.dp))

            ProfileInfoRow(
                icon = Icons.Default.Work,
                title = "Должность",
                value = user.position.ifBlank { "-" }
            )

            Spacer(Modifier.height(10.dp))

            ProfileInfoRow(
                icon = Icons.Default.Work,
                title = "Отдел",
                value = user.department.ifBlank { "-" }
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.padding(6.dp))

        Column {
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
    }
}

private fun getUserInitial(user: AppUser): String {
    val source = user.fullName.ifBlank { user.email.ifBlank { "П" } }
    return source.firstOrNull()?.uppercaseChar()?.toString() ?: "П"
}
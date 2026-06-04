package com.example.electronicreception.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.electronicreception.data.AppUser
import com.example.electronicreception.data.Complaint
import com.example.electronicreception.data.FirebaseRepository
import com.example.electronicreception.ui.components.AppCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: FirebaseRepository,
    onNewComplaint: () -> Unit,
    onMyComplaints: () -> Unit,
    onProfile: () -> Unit,
    onNews: () -> Unit,
    onHelp: () -> Unit,
    onContacts: () -> Unit,
    onAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var user by remember { mutableStateOf<AppUser?>(null) }
    var complaints by remember { mutableStateOf<List<Complaint>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val profile = repository.getCurrentUserProfile()
            user = profile

            complaints = if (profile?.isAdmin == true) {
                repository.getAllComplaints()
            } else {
                repository.getMyComplaints()
            }
        } finally {
            isLoading = false
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader(user = user)

                HorizontalDivider()

                DrawerMenuItem(
                    title = "Главная",
                    icon = Icons.Default.Home,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )

                DrawerMenuItem(
                    title = "Подать обращение",
                    icon = Icons.Default.PostAdd,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNewComplaint()
                    }
                )

                DrawerMenuItem(
                    title = "Мои обращения",
                    icon = Icons.Default.Assignment,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onMyComplaints()
                    }
                )

                DrawerMenuItem(
                    title = "Новости",
                    icon = Icons.Default.Article,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNews()
                    }
                )

                DrawerMenuItem(
                    title = "Помощь",
                    icon = Icons.Default.Help,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onHelp()
                    }
                )

                DrawerMenuItem(
                    title = "Контакты",
                    icon = Icons.Default.ContactPhone,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onContacts()
                    }
                )

                DrawerMenuItem(
                    title = "Личный кабинет",
                    icon = Icons.Default.Person,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onProfile()
                    }
                )

                if (user?.isAdmin == true) {
                    DrawerMenuItem(
                        title = "Админ-панель",
                        icon = Icons.Default.AdminPanelSettings,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onAdmin()
                        }
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                DrawerMenuItem(
                    title = "Выйти",
                    icon = Icons.Default.ExitToApp,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Электронная приёмная")
                            Text(
                                text = "Администрация Кигинского района",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Меню"
                            )
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
                WelcomeCard(user = user)

                Spacer(Modifier.height(16.dp))

                AboutApplicationCard()

                Spacer(Modifier.height(16.dp))

                QuickStatsCard(
                    isLoading = isLoading,
                    complaints = complaints,
                    isAdmin = user?.isAdmin == true
                )

                Spacer(Modifier.height(16.dp))

                MainInfoCard()
            }
        }
    }
}

@Composable
private fun DrawerHeader(
    user: AppUser?
) {
    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Text(
            text = "Электронная приёмная",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = user?.fullName?.ifBlank { user.email } ?: "Пользователь",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = if (user?.isAdmin == true) {
                "Администратор"
            } else {
                "Заявитель"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
private fun DrawerMenuItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Text(title)
        },
        selected = false,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
    )
}

@Composable
private fun WelcomeCard(
    user: AppUser?
) {
    AppCard {
        Text(
            text = "Добро пожаловать!",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = user?.fullName?.ifBlank { user.email } ?: "Пользователь",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = if (user?.isAdmin == true) {
                user.position.ifBlank { "Администратор системы" }
            } else {
                "Заявитель"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
private fun AboutApplicationCard() {
    AppCard {
        Text(
            text = "О приложении",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Мобильное приложение предназначено для быстрой подачи и рассмотрения " +
                    "обращений граждан. Пользователь может отправить обращение, прикрепить " +
                    "фотографии, указать место проблемы на карте и отслеживать статус рассмотрения."
        )
    }
}

@Composable
private fun QuickStatsCard(
    isLoading: Boolean,
    complaints: List<Complaint>,
    isAdmin: Boolean
) {
    AppCard {
        Text(
            text = if (isAdmin) {
                "Обращения в системе"
            } else {
                "Мои обращения"
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(12.dp))

        if (isLoading) {
            Text("Загрузка статистики...")
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HomeStatItem(
                    title = "Всего",
                    value = complaints.size,
                    modifier = Modifier.weight(1f)
                )

                HomeStatItem(
                    title = "В работе",
                    value = complaints.count { it.status == "В работе" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HomeStatItem(
                    title = "Новые",
                    value = complaints.count { it.status == "Новое" },
                    modifier = Modifier.weight(1f)
                )

                HomeStatItem(
                    title = "Завершено",
                    value = complaints.count { it.status == "Завершено" },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HomeStatItem(
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
            modifier = Modifier.padding(14.dp)
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
private fun MainInfoCard() {
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
            Text(
                text = "Возможности приложения",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(10.dp))

            Text("• подача обращений в электронном виде;")
            Text("• прикрепление фотографий к обращению;")
            Text("• выбор места проблемы на Яндекс.Карте;")
            Text("• просмотр статуса в разделе «Мои обращения»;")
            Text("• получение справочной информации и контактов.")
        }
    }
}
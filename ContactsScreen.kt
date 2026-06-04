package com.example.electronicreception.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.electronicreception.ui.components.AppCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val phoneNumber = "+7(34748)3-71-71"
    val email = "adm24@bashkortostan.ru"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Контакты")
                        Text(
                            text = "Связь с администрацией",
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
            AppCard {
                Text(
                    text = "Администрация муниципального района",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(12.dp))

                ContactInfoRow(
                    icon = Icons.Default.LocationOn,
                    title = "Адрес",
                    value = "Республика Башкортостан, Кигинский район, с. Верхние Киги, ул. Салавата, 2"
                )

                Spacer(Modifier.height(12.dp))

                ContactInfoRow(
                    icon = Icons.Default.Phone,
                    title = "Телефон",
                    value = "+7(34748)3-71-71"
                )

                Spacer(Modifier.height(12.dp))

                ContactInfoRow(
                    icon = Icons.Default.Email,
                    title = "Email",
                    value = email
                )

                Spacer(Modifier.height(12.dp))

                ContactInfoRow(
                    icon = Icons.Default.Schedule,
                    title = "Режим работы",
                    value = "Понедельник – пятница: 09:00–18:00\nПерерыв: 13:00–14:00\nСуббота, воскресенье: выходной"
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$phoneNumber")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null)
                    Spacer(Modifier.padding(4.dp))
                    Text("Позвонить")
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:$email")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Email, contentDescription = null)
                    Spacer(Modifier.padding(4.dp))
                    Text("Написать на email")
                }
            }

            Spacer(Modifier.height(16.dp))

            AppCard {
                Text(
                    text = "Ответственные отделы",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(12.dp))

                DepartmentCard(
                    icon = Icons.Default.Groups,
                    title = "Отдел по работе с обращениями граждан",
                    description = "Приём, регистрация и первичная обработка обращений граждан, контроль сроков рассмотрения."
                )

                Spacer(Modifier.height(12.dp))

                DepartmentCard(
                    icon = Icons.Default.Work,
                    title = "Отдел жилищно-коммунального хозяйства",
                    description = "Рассмотрение обращений по вопросам ЖКХ, благоустройства, водоснабжения, отопления и содержания территорий."
                )

                Spacer(Modifier.height(12.dp))

                DepartmentCard(
                    icon = Icons.Default.AccountBalance,
                    title = "Административный отдел",
                    description = "Организационные вопросы, взаимодействие с заявителями и структурными подразделениями администрации."
                )
            }

            Spacer(Modifier.height(16.dp))

            AppCard {
                Text(
                    text = "Порядок обращения",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Для направления обращения используйте раздел «Подать обращение». " +
                            "После отправки обращение будет зарегистрировано в системе, " +
                            "а его статус можно отслеживать в разделе «Мои обращения»."
                )
            }
        }
    }
}

@Composable
private fun ContactInfoRow(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth()
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

@Composable
private fun DepartmentCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp)
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
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
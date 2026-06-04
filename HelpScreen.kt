package com.example.electronicreception.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.electronicreception.ui.components.AppCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBack: () -> Unit
) {
    var expandedIndex by remember { mutableIntStateOf(3) }

    val faqItems = listOf(
        FaqItem(
            question = "Как подать обращение через электронную приемную?",
            answer = "Откройте раздел «Подать обращение», заполните данные заявителя, выберите категорию, опишите проблему, при необходимости прикрепите фотографии и укажите место на карте. После отправки обращение появится в разделе «Мои обращения»."
        ),
        FaqItem(
            question = "Какие обращения не рассматриваются?",
            answer = "Не рассматриваются обращения без достаточного описания проблемы, обращения с некорректными данными, оскорблениями, рекламой, а также сообщения, не относящиеся к компетенции администрации."
        ),
        FaqItem(
            question = "Сколько времени занимает рассмотрение обращения?",
            answer = "Срок рассмотрения зависит от категории и сложности обращения. В системе статус можно отслеживать в разделе «Мои обращения»."
        ),
        FaqItem(
            question = "Как узнать статус моего обращения?",
            answer = "Статус обращения отображается в разделе «Мои обращения». Откройте нужную карточку обращения, чтобы посмотреть подробную информацию, фотографии, координаты и текущий статус."
        ),
        FaqItem(
            question = "Можно ли подать обращение анонимно?",
            answer = "Для подачи обращения необходимо войти в систему. Это нужно для регистрации обращения и последующего отслеживания его статуса."
        ),
        FaqItem(
            question = "Что делать, если проблема не решена после обращения?",
            answer = "Если проблема не решена, можно повторно обратиться в администрацию, указав номер предыдущего обращения и подробно описав текущую ситуацию."
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Помощь")
                        Text(
                            text = "Часто задаваемые вопросы",
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
            Text(
                text = "Помощь и часто задаваемые вопросы",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            faqItems.forEachIndexed { index, item ->
                FaqCard(
                    item = item,
                    isExpanded = expandedIndex == index,
                    onClick = {
                        expandedIndex = if (expandedIndex == index) {
                            -1
                        } else {
                            index
                        }
                    }
                )

                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(8.dp))

            UsefulTipsCard()
        }
    }
}

private data class FaqItem(
    val question: String,
    val answer: String
)

@Composable
private fun FaqCard(
    item: FaqItem,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isExpanded) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(18.dp)
            ) {
                Text(
                    text = item.question,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (isExpanded) {
                        Icons.Default.ExpandLess
                    } else {
                        Icons.Default.ExpandMore
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    HorizontalDivider()

                    Text(
                        text = item.answer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun UsefulTipsCard() {
    AppCard {
        Text(
            text = "Полезные советы при подаче обращения",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(14.dp))

        TipLine(
            icon = Icons.Default.PhotoCamera,
            title = "Прикрепляйте фотографии",
            text = "Фотографии помогают точнее понять суть проблемы. Желательно делать четкие снимки с разных ракурсов."
        )

        Spacer(Modifier.height(14.dp))

        TipLine(
            icon = Icons.Default.LocationOn,
            title = "Указывайте точный адрес",
            text = "Чем точнее указан адрес или точка на карте, тем быстрее специалисты смогут найти и устранить проблему."
        )

        Spacer(Modifier.height(14.dp))

        TipLine(
            icon = Icons.Default.Edit,
            title = "Подробно описывайте проблему",
            text = "Опишите, когда возникла проблема, чем она мешает и какие меры уже предпринимались."
        )

        Spacer(Modifier.height(14.dp))

        TipLine(
            icon = Icons.Default.Schedule,
            title = "Следите за статусом",
            text = "Текущий статус обращения можно посмотреть в разделе «Мои обращения»."
        )
    }
}

@Composable
private fun TipLine(
    icon: ImageVector,
    title: String,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
            shape = MaterialTheme.shapes.small
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(Modifier.padding(6.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
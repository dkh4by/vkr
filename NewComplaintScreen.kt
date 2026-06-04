package com.example.electronicreception.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.electronicreception.data.FirebaseRepository
import com.example.electronicreception.ui.components.AppCard
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.electronicreception.ui.components.YandexMapPicker
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewComplaintScreen(
    repository: FirebaseRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val categories = listOf(
        "Жилищно-коммунальное хозяйство",
        "Дорожное покрытие и тротуары",
        "Уборка территории и вывоз мусора",
        "Уличное освещение",
        "Парковка и организация движения",
        "Озеленение и благоустройство",
        "Другое"
    )

    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories.first()) }
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    var locationLat by remember { mutableStateOf<Double?>(null) }
    var locationLng by remember { mutableStateOf<Double?>(null) }
    var locationText by remember { mutableStateOf("Геолокация не выбрана") }

    val selectedImages = remember { mutableStateListOf<Uri>() }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedImages.clear()
        selectedImages.addAll(uris.take(3))
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            scope.launch {
                try {
                    val location = fusedLocationClient.lastLocation.await()

                    if (location != null) {
                        locationLat = location.latitude
                        locationLng = location.longitude
                        locationText = "Местоположение определено"
                    } else {
                        locationText = "Не удалось определить геолокацию"
                    }
                } catch (e: Exception) {
                    locationText = "Ошибка определения геолокации"
                }
            }
        } else {
            locationText = "Разрешение на геолокацию не выдано"
        }
    }

    LaunchedEffect(Unit) {
        val user = repository.getCurrentUserProfile()
        fullName = user?.fullName.orEmpty()
        phone = user?.phone.orEmpty()
    }

    fun requestLocation() {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            scope.launch {
                try {
                    val location = fusedLocationClient.lastLocation.await()

                    if (location != null) {
                        locationLat = location.latitude
                        locationLng = location.longitude
                        locationText = "Местоположение определено"
                    } else {
                        locationText = "Не удалось определить геолокацию"
                    }
                } catch (e: Exception) {
                    locationText = "Ошибка определения геолокации"
                }
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Подача обращения")
                        Text(
                            text = "Заполните сведения о проблеме",
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
                    text = "Данные заявителя",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("ФИО") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Телефон") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Адрес") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
            }

            Spacer(Modifier.height(16.dp))

            AppCard {
                Text(
                    text = "Содержание обращения",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Категория") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = MaterialTheme.shapes.medium
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    category = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Тема обращения") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание проблемы") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = MaterialTheme.shapes.medium
                )
            }

            Spacer(Modifier.height(16.dp))

            AppCard {
                Text(
                    text = "Фото и геолокация",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(12.dp))

                ElevatedButton(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Добавить фотографии")
                }

                if (selectedImages.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        selectedImages.forEachIndexed { index, uri ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(end = 10.dp)
                            ) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Фото обращения",
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(RoundedCornerShape(14.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                IconButton(
                                    onClick = {
                                        selectedImages.removeAt(index)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Удалить фото",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        requestLocation()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Определить геолокацию")
                }

                Spacer(Modifier.height(8.dp))

                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            if (locationLat != null && locationLng != null) {
                                "$locationText: %.5f, %.5f".format(locationLat, locationLng)
                            } else {
                                locationText
                            }
                        )
                    }
                )


                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Карта обращения",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(8.dp))

                YandexMapPicker(
                    latitude = locationLat,
                    longitude = locationLng,
                    onPointSelected = { lat, lng ->
                        locationLat = lat
                        locationLng = lng
                        locationText = "Точка выбрана на карте"
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }




            Spacer(Modifier.height(18.dp))

            Button(
                onClick = {
                    if (fullName.isBlank() || subject.isBlank() || description.isBlank()) {
                        isSuccess = false
                        message = "Заполните ФИО, тему и описание обращения"
                        return@Button
                    }

                    scope.launch {
                        try {
                            isLoading = true
                            message = ""

                            val id = repository.createComplaint(
                                fullName = fullName,
                                phone = phone,
                                address = address,
                                category = category,
                                subject = subject,
                                description = description,
                                imageUris = selectedImages.toList(),
                                locationLat = locationLat,
                                locationLng = locationLng,
                                context = context
                            )

                            isSuccess = true
                            message = "Обращение отправлено. Номер: $id"

                            address = ""
                            subject = ""
                            description = ""
                            selectedImages.clear()
                            locationLat = null
                            locationLng = null
                            locationText = "Геолокация не выбрана"
                        } catch (e: Exception) {
                            isSuccess = false
                            message = e.message ?: "Ошибка отправки обращения"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Send, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (isLoading) "Отправка..." else "Отправить обращение")
            }

            if (message.isNotBlank()) {
                Spacer(Modifier.height(14.dp))

                AppCard {
                    Text(
                        text = message,
                        color = if (isSuccess) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        }
    }
}
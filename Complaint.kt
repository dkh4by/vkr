package com.example.electronicreception.data

import com.google.firebase.Timestamp

data class Complaint(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val fullName: String = "",
    val phone: String = "",
    val address: String = "",
    val category: String = "",
    val subject: String = "",
    val description: String = "",
    val status: String = "Новое",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,

    val images: List<String> = emptyList(),
    val locationLat: Double? = null,
    val locationLng: Double? = null,

    val assigneeName: String = "",
    val assigneeRole: String = "",
    val assigneeDepartment: String = ""
)
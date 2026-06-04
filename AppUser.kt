package com.example.electronicreception.data

data class AppUser(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val isAdmin: Boolean = false,
    val position: String = "",
    val department: String = "",
    val avatarUrl: String = ""
)
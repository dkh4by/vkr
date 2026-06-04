package com.example.electronicreception.data

import com.google.firebase.Timestamp

data class NewsItem(
    val id: String = "",
    val title: String = "",
    val text: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val createdAt: Timestamp? = null
)
package com.ayybay.app.domain.model

data class ReligionCategory(
    val id: String,
    val name: String,
    val subtitle: String,
    val icon: String,
    val colorHex: Long,
    val books: List<Book>
)

data class Book(
    val id: Long,
    val title: String,
    val author: String = ""
)

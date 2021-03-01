package com.ivanmorgillo.corsoandroid.teama.category

data class Category(
    val name: String,
    val image: String,
    val id: String,
    val recipeAmount: String,
    val categoryArea: List<String>
)

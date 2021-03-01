package com.ivanmorgillo.corsoandroid.teama.favourite

data class FavouriteUI(
    val id: Long,
    val title: String,
    val image: String,
    val notes: String,
    val video: String,
    val ingredients: List<String>,
    val instructions: String,
)

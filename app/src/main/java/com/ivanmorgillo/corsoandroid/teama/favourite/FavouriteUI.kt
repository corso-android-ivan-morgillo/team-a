package com.ivanmorgillo.corsoandroid.teama.favourite

import com.ivanmorgillo.corsoandroid.teama.detail.Ingredient

data class FavouriteUI(
    val id: Long,
    val title: String,
    val image: String,
    val notes: String,
    val video: String,
    val ingredients: List<Ingredient>,
    val instructions: String,
    val area: String
)

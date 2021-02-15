package com.ivanmorgillo.corsoandroid.teama.detail

data class RecipeDetailsUI(
    val id: String,
    val title: String,
    val image: String,
    val ingredients: List<String>,
    val measures: List<String>,
    val instructions: String
)

package com.ivanmorgillo.corsoandroid.teama.detail

data class RecipeDetailsUI(
    val id: String,
    val title: String,
    val image: String,
    val ingredients: List<IngredientUI>,
    val instructions: String
)

package com.ivanmorgillo.corsoandroid.teama.detail

data class RecipeDetailsUI(
    val id: Long,
    val title: String,
    val image: String,
    val video: String,
    val ingredients: List<IngredientUI>,
    val instructions: String,
    val isIngredientsSelected: Boolean,
    val isFavourite: Boolean,
)

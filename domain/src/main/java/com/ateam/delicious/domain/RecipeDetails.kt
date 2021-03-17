package com.ateam.delicious.domain

data class RecipeDetails(
    val name: String,
    val image: String,
    val video: String,
    val idMeal: Long,
    val ingredients: List<Ingredient>,
    val instructions: String,
    val area: String
)

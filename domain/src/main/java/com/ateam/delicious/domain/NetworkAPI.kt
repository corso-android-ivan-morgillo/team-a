package com.ateam.delicious.domain

interface NetworkAPI {

    suspend fun loadRecipeDetails(idMeal: Long): LoadRecipeDetailsResult

    suspend fun loadRecipes(categoryName: String): LoadRecipeResult

    suspend fun loadCategories(): LoadCategoryResult

    suspend fun loadRandomRecipe(): LoadRecipeDetailsResult
}

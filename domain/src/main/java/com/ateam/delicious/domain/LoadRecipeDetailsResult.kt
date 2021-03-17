package com.ateam.delicious.domain

sealed class LoadRecipeDetailsResult {
    data class Success(val details: RecipeDetails) : LoadRecipeDetailsResult()
    data class Failure(val error: LoadRecipeDetailError) : LoadRecipeDetailsResult()
}

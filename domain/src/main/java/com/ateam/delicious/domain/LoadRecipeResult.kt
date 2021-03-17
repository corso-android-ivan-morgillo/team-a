package com.ateam.delicious.domain

sealed class LoadRecipeResult {
    data class Success(val recipes: List<Recipe>) : LoadRecipeResult()
    data class Failure(val error: LoadRecipeError) : LoadRecipeResult()
}

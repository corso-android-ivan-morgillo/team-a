package com.ateam.delicious.domain.result

import com.ateam.delicious.domain.RecipeDetails
import com.ateam.delicious.domain.error.LoadRecipeDetailError

sealed class LoadRecipeDetailsResult {
    data class Success(val details: RecipeDetails) : LoadRecipeDetailsResult()
    data class Failure(val error: LoadRecipeDetailError) : LoadRecipeDetailsResult()
}

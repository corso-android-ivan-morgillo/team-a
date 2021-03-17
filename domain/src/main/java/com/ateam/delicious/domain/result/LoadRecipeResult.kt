package com.ateam.delicious.domain.result

import com.ateam.delicious.domain.Recipe
import com.ateam.delicious.domain.error.LoadRecipeError

sealed class LoadRecipeResult {
    data class Success(val recipes: List<Recipe>) : LoadRecipeResult()
    data class Failure(val error: LoadRecipeError) : LoadRecipeResult()
}

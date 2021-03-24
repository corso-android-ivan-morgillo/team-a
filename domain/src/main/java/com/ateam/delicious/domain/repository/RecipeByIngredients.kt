package com.ateam.delicious.domain.repository

import com.ateam.delicious.domain.NetworkAPI
import com.ateam.delicious.domain.result.LoadRecipeResult

interface RecipeByIngredients {

    suspend fun loadRecipesByIngredients(ingredientName: String): LoadRecipeResult

}

class RecipeByIngredientImpl(private val api: NetworkAPI) : RecipeByIngredients {

    override suspend fun loadRecipesByIngredients(ingredientName: String): LoadRecipeResult {
        return api.loadRecipesByIngredient(ingredientName)
    }
}

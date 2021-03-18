package com.ateam.delicious.domain.repository

import com.ateam.delicious.domain.NetworkAPI
import com.ateam.delicious.domain.result.LoadRecipeDetailsResult

interface RecipeDetailsRepository {
    suspend fun loadRecipeDetails(idMeal: Long): LoadRecipeDetailsResult
}

class RecipeDetailsRepositoryImpl(private val api: NetworkAPI) : RecipeDetailsRepository {
    override suspend fun loadRecipeDetails(idMeal: Long): LoadRecipeDetailsResult {
        return if (idMeal == -1L) {
            api.loadRandomRecipe()
        } else {
            api.loadRecipeDetails(idMeal)
        }
    }
}

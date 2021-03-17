package com.ivanmorgillo.corsoandroid.teama.detail

import com.ateam.delicious.domain.LoadRecipeDetailsResult
import com.ateam.delicious.domain.NetworkAPI

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

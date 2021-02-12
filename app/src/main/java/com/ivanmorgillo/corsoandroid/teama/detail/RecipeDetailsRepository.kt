package com.ivanmorgillo.corsoandroid.teama.detail

import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeResult
import com.ivanmorgillo.corsoandroid.teama.network.RecipeAPI

interface RecipeDetailsRepository {
    suspend fun loadRecipeDetails(idMeal: Long): LoadRecipeResult
}

class RecipeDetailsRepositoryImpl(private val api: RecipeAPI) : RecipeDetailsRepository {
    override suspend fun loadRecipeDetails(idMeal: Long): LoadRecipeResult {
        return api.loadRecipeDetails(idMeal)
    }
}

data class RecipeDetails(val name: String, val image: String, val idMeal: Long)

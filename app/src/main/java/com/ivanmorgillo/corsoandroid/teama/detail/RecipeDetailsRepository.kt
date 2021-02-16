package com.ivanmorgillo.corsoandroid.teama.detail

import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailsResult
import com.ivanmorgillo.corsoandroid.teama.network.RecipeAPI

interface RecipeDetailsRepository {
    suspend fun loadRecipeDetails(idMeal: Long): LoadRecipeDetailsResult
}

class RecipeDetailsRepositoryImpl(private val api: RecipeAPI) : RecipeDetailsRepository {
    override suspend fun loadRecipeDetails(idMeal: Long): LoadRecipeDetailsResult {
        return api.loadRecipeDetails(idMeal)
    }
}

data class RecipeDetails(
    val name: String,
    val image: String,
    val idMeal: String,
    val ingredients: List<Ingredient>,
    val instructions: String
)

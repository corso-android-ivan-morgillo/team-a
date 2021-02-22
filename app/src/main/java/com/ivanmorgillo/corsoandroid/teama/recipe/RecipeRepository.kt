package com.ivanmorgillo.corsoandroid.teama

import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeResult
import com.ivanmorgillo.corsoandroid.teama.network.NetworkAPI

interface RecipesRepository {
    suspend fun loadRecipes(categoryName: String): LoadRecipeResult
}

class RecipeRepositoryImpl(private val api: NetworkAPI) : RecipesRepository {
    override suspend fun loadRecipes(categoryName: String): LoadRecipeResult {
        return api.loadRecipes(categoryName)
    }
}

data class Recipe(val name: String, val image: String, val idMeal: Long)

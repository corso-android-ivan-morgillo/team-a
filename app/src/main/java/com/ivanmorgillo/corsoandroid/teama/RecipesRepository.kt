package com.ivanmorgillo.corsoandroid.teama

import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeResult
import com.ivanmorgillo.corsoandroid.teama.network.RecipeAPI

interface RecipesRepository {
    suspend fun loadRecipes(categoryName: String): LoadRecipeResult
}

class RecipeRepositoryImpl(private val api: RecipeAPI) : RecipesRepository {
    override suspend fun loadRecipes(categoryName: String): LoadRecipeResult {
        return api.loadRecipes(categoryName)
    }
}

data class Recipe(val name: String, val image: String, val idMeal: Long)

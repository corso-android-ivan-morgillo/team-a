package com.ivanmorgillo.corsoandroid.teama

import com.ivanmorgillo.corsoandroid.teama.network.RecipeAPI

interface RecipesRepository {
    suspend fun loadRecipes(): List<Recipe>
}

class RecipeRepositoryImpl(private val api: RecipeAPI) : RecipesRepository {
    override suspend fun loadRecipes(): List<Recipe> {
        return api.loadRecipes()
    }
}

data class Recipe(val name: String, val image: String, val idMeal: String)

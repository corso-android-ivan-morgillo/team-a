package com.ateam.delicious.domain.repository

import com.ateam.delicious.domain.NetworkAPI
import com.ateam.delicious.domain.result.LoadRecipeResult

interface RecipesRepository {
    suspend fun loadRecipes(categoryName: String): LoadRecipeResult
}

class RecipeRepositoryImpl(private val api: NetworkAPI) : RecipesRepository {
    override suspend fun loadRecipes(categoryName: String): LoadRecipeResult {
        return api.loadRecipes(categoryName)
    }
}

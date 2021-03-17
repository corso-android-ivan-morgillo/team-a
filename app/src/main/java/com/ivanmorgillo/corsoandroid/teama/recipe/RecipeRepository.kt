package com.ivanmorgillo.corsoandroid.teama.recipe

import com.ateam.delicious.domain.LoadRecipeResult
import com.ateam.delicious.domain.NetworkAPI

interface RecipesRepository {
    suspend fun loadRecipes(categoryName: String): LoadRecipeResult
}

class RecipeRepositoryImpl(private val api: NetworkAPI) : RecipesRepository {
    override suspend fun loadRecipes(categoryName: String): LoadRecipeResult {
        return api.loadRecipes(categoryName)
    }
}

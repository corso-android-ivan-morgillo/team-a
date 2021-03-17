package com.ateam.delicious.domain

interface RecipesRepository {
    suspend fun loadRecipes(categoryName: String): LoadRecipeResult
}

class RecipeRepositoryImpl(private val api: NetworkAPI) : RecipesRepository {
    override suspend fun loadRecipes(categoryName: String): LoadRecipeResult {
        return api.loadRecipes(categoryName)
    }
}

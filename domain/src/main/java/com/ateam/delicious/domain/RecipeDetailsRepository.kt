package com.ateam.delicious.domain

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

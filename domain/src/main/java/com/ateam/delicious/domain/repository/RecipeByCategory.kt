package com.ateam.delicious.domain.repository

import com.ateam.delicious.domain.NetworkAPI
import com.ateam.delicious.domain.result.LoadRecipeResult

interface RecipeByCategory {

    suspend fun loadRecipesByCategory(categoryName: String): LoadRecipeResult

}

class RecipeByCategoryImpl(private val api: NetworkAPI) : RecipeByCategory {

    override suspend fun loadRecipesByCategory(categoryName: String): LoadRecipeResult {
        return api.loadRecipes(categoryName)
    }

}

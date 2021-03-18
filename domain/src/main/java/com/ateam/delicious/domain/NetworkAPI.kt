package com.ateam.delicious.domain

import com.ateam.delicious.domain.result.LoadCategoryResult
import com.ateam.delicious.domain.result.LoadRecipeDetailsResult
import com.ateam.delicious.domain.result.LoadRecipeResult

interface NetworkAPI {

    suspend fun loadRecipeDetails(idMeal: Long): LoadRecipeDetailsResult

    suspend fun loadRecipes(categoryName: String): LoadRecipeResult

    suspend fun loadCategories(): LoadCategoryResult

    suspend fun loadRandomRecipe(): LoadRecipeDetailsResult
}

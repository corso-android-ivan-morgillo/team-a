package com.ivanmorgillo.corsoandroid.teama.network

import com.ivanmorgillo.corsoandroid.teama.category.CategoryDTO
import com.ivanmorgillo.corsoandroid.teama.detail.RecipeDetailsDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeService {
    @GET("categories.php")
    suspend fun loadCategories(): CategoryDTO

    @GET("filter.php?c=Beef")
    suspend fun loadRecipes(): RecipeDTO

    @GET("lookup.php")
    suspend fun loadRecipeDetails(@Query("i") idMeal: Long): RecipeDetailsDTO
}

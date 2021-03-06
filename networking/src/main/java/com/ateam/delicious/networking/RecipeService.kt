package com.ateam.delicious.networking

import com.ateam.delicious.networking.dto.AreaDTO
import com.ateam.delicious.networking.dto.CategoryDTO
import com.ateam.delicious.networking.dto.RecipeDTO
import com.ateam.delicious.networking.dto.RecipeDetailsDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeService {
    @GET("categories.php")
    suspend fun loadCategories(): CategoryDTO

    @GET("filter.php") // ?c=Beef
    suspend fun loadRecipes(@Query("c") categoryName: String): RecipeDTO

    @GET("filter.php")
    suspend fun loadRecipesByArea(@Query("a") areaName: String): RecipeDTO

    @GET("lookup.php")
    suspend fun loadRecipeDetails(@Query("i") idMeal: Long): RecipeDetailsDTO

    @GET("random.php")
    suspend fun loadRandomRecipe(): RecipeDetailsDTO

    @GET("list.php?a=list")
    suspend fun loadAreas(): AreaDTO

    @GET("filter.php") // ?i=chicken_breast
    suspend fun loadRecipesByIngredient(@Query("i") ingredientName: String): RecipeDTO

}

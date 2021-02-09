package com.ivanmorgillo.corsoandroid.teama.network

import retrofit2.http.GET

interface RecipeService {
    @GET("filter.php?c=Beef")
    suspend fun loadRecipes(): RecipeDTO
}

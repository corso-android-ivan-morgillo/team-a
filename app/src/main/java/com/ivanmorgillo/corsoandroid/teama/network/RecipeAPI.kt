package com.ivanmorgillo.corsoandroid.teama.network

import com.ivanmorgillo.corsoandroid.teama.Recipe
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecipeAPI {
    private val service: RecipeService

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.themealdb.com/api/json/v1/1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        service = retrofit.create(RecipeService::class.java)
    }

    suspend fun loadRecipes(): List<Recipe> {
        val recipesList = service.loadRecipes()
        return recipesList.meals.map {
            Recipe(
                name = it.strMeal,
                image = it.strMealThumb,
                idMeal = it.idMeal
            )
        }
    }
}

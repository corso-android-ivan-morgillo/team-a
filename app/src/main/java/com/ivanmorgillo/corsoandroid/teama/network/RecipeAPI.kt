package com.ivanmorgillo.corsoandroid.teama.network

import android.util.Log
import com.ivanmorgillo.corsoandroid.teama.Recipe
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeError.InterruptedRequest
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeError.NoInternet
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeError.NoRecipeFound
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeError.ServerError
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeError.SlowInternet
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeResult.Failure
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeResult.Success
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

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

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadRecipeDetails(idMeal: Long): LoadRecipeResult {
        try {
            val recipesList = service.loadRecipeDetails(idMeal)
            Log.d("TeamA", recipesList.toString())
        } catch (e: Exception) {
        }
        return Failure(LoadRecipeError.NoRecipeFound)
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadRecipes(): LoadRecipeResult {
        try {
            val recipesList = service.loadRecipes()
            val recipes = recipesList.meals
                .mapNotNull {
                    it.toDomain()
                }
            return if (recipes.isEmpty()) {
                Failure(NoRecipeFound)
            } else {
                Success(recipes)
            }
        } catch (e: IOException) { // no network available
            return Failure(NoInternet)
        } catch (e: ConnectException) { // interrupted network request
            return Failure(InterruptedRequest)
        } catch (e: SocketTimeoutException) { // server timeout error
            return Failure(SlowInternet)
        } catch (e: Exception) { // other generic exception
            Timber.e(e, "Generic Exception on LoadRecipes")
            return Failure(ServerError)
        }
    }

    private fun RecipeDTO.Meal.toDomain(): Recipe? {
        val id = idMeal.toLongOrNull()
        return if (id != null) {
            Recipe(
                name = strMeal,
                image = strMealThumb,
                idMeal = id
            )
        } else {
            null
        }
    }
}

sealed class LoadRecipeError {
    object NoRecipeFound : LoadRecipeError()
    object NoInternet : LoadRecipeError()
    object InterruptedRequest : LoadRecipeError()
    object SlowInternet : LoadRecipeError()
    object ServerError : LoadRecipeError()
}

sealed class LoadRecipeResult {
    data class Success(val recipes: List<Recipe>) : LoadRecipeResult()
    data class Failure(val error: LoadRecipeError) : LoadRecipeResult()
}

package com.ivanmorgillo.corsoandroid.teama.network

import com.ivanmorgillo.corsoandroid.teama.Recipe
import com.ivanmorgillo.corsoandroid.teama.detail.RecipeDetails
import com.ivanmorgillo.corsoandroid.teama.detail.RecipeDetailsDTO
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
    suspend fun loadRecipeDetails(idMeal: Long): LoadRecipeDetailsResult {
        try {
            val recipesDetailsDTO = service.loadRecipeDetails(idMeal)
            if (recipesDetailsDTO.details.isEmpty()) {
                return LoadRecipeDetailsResult.Failure(NoRecipeFound)
            }
            val recipeDetail: RecipeDetailsDTO.Detail = recipesDetailsDTO.details[0]
            val ingredients: MutableList<String> = getIngredientsList(recipeDetail)
            val measures: MutableList<String> = getMeasuresList(recipeDetail)
            val recipeDetails: RecipeDetails = RecipeDetails(
                name = recipeDetail.strMeal,
                image = recipeDetail.strMealThumb,
                idMeal = recipeDetail.idMeal,
                ingredients = ingredients,
                measures = measures,
                instructions = recipeDetail.strInstructions
            )
            return LoadRecipeDetailsResult.Success(recipeDetails)
        } catch (e: IOException) { // no network available
            return LoadRecipeDetailsResult.Failure(NoInternet)
        } catch (e: ConnectException) { // interrupted network request
            return LoadRecipeDetailsResult.Failure(InterruptedRequest)
        } catch (e: SocketTimeoutException) { // server timeout error
            return LoadRecipeDetailsResult.Failure(SlowInternet)
        } catch (e: Exception) { // other generic exception
            Timber.e(e, "Generic Exception on LoadRecipeDetails")
            return LoadRecipeDetailsResult.Failure(ServerError)
        }
    }

    private fun getMeasuresList(it: RecipeDetailsDTO.Detail): MutableList<String> {
        val measures: MutableList<String> = mutableListOf()
        measures.add(it.strMeasure1)
        measures.add(it.strMeasure2)
        measures.add(it.strMeasure3)
        measures.add(it.strMeasure4)
        measures.add(it.strMeasure5)
        measures.add(it.strMeasure6)
        measures.add(it.strMeasure7)
        measures.add(it.strMeasure8)
        measures.add(it.strMeasure9)
        measures.add(it.strMeasure10)
        measures.add(it.strMeasure11)
        measures.add(it.strMeasure12)
        measures.add(it.strMeasure13)
        measures.add(it.strMeasure14)
        measures.add(it.strMeasure15)
        measures.add(it.strMeasure16)
        measures.add(it.strMeasure17)
        measures.add(it.strMeasure18)
        measures.add(it.strMeasure19)
        measures.add(it.strMeasure20)
        return measures
    }

    private fun getIngredientsList(it: RecipeDetailsDTO.Detail): MutableList<String> {
        val ingredients: MutableList<String> = mutableListOf()
        ingredients.add(it.strIngredient1)
        ingredients.add(it.strIngredient2)
        ingredients.add(it.strIngredient3)
        ingredients.add(it.strIngredient4)
        ingredients.add(it.strIngredient5)
        ingredients.add(it.strIngredient6)
        ingredients.add(it.strIngredient7)
        ingredients.add(it.strIngredient8)
        ingredients.add(it.strIngredient9)
        ingredients.add(it.strIngredient10)
        ingredients.add(it.strIngredient11)
        ingredients.add(it.strIngredient12)
        ingredients.add(it.strIngredient13)
        ingredients.add(it.strIngredient14)
        ingredients.add(it.strIngredient15)
        ingredients.add(it.strIngredient16)
        ingredients.add(it.strIngredient17)
        ingredients.add(it.strIngredient18)
        ingredients.add(it.strIngredient19)
        ingredients.add(it.strIngredient20)
        return ingredients
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

sealed class LoadRecipeDetailsResult {
    data class Success(val details: RecipeDetails) : LoadRecipeDetailsResult()
    data class Failure(val error: LoadRecipeError) : LoadRecipeDetailsResult()
}

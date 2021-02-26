package com.ivanmorgillo.corsoandroid.teama.network

import com.ivanmorgillo.corsoandroid.teama.Recipe
import com.ivanmorgillo.corsoandroid.teama.category.Category
import com.ivanmorgillo.corsoandroid.teama.detail.Ingredient
import com.ivanmorgillo.corsoandroid.teama.detail.RecipeDetails
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailError.NoDetailFound
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

class NetworkAPI {
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
            return if (recipesDetailsDTO.details.isEmpty()) {
                LoadRecipeDetailsResult.Failure(NoDetailFound)
            } else {
                val recipeDetail: RecipeDetailsDTO.Detail = recipesDetailsDTO.details[0]
                val ingredients = getIngredients(recipeDetail)
                var video = recipeDetail.strYoutube
                if (video.isNullOrBlank()) {
                    video = ""
                }
                val recipeDetails = RecipeDetails(
                    name = recipeDetail.strMeal,
                    image = recipeDetail.strMealThumb,
                    video = video,
                    idMeal = recipeDetail.idMeal,
                    ingredients = ingredients,
                    instructions = recipeDetail.strInstructions
                )
                LoadRecipeDetailsResult.Success(recipeDetails)
            }
        } catch (e: IOException) { // no network available
            return LoadRecipeDetailsResult.Failure(LoadRecipeDetailError.NoInternet)
        } catch (e: ConnectException) { // interrupted network request
            return LoadRecipeDetailsResult.Failure(LoadRecipeDetailError.InterruptedRequest)
        } catch (e: SocketTimeoutException) { // server timeout error
            return LoadRecipeDetailsResult.Failure(LoadRecipeDetailError.SlowInternet)
        } catch (e: Exception) { // other generic exception
            Timber.e(e, "Generic Exception on LoadRecipeDetails")
            return LoadRecipeDetailsResult.Failure(LoadRecipeDetailError.ServerError)
        }
    }

    private fun getIngredient(strIngredient: String?, strMeasure: String?): Ingredient? {
        return if (!strIngredient.isNullOrBlank()) {
            if (strMeasure.isNullOrBlank()) {
                Ingredient(strIngredient, "qb") // ad esempio il sale
            } else {
                Ingredient(strIngredient, strMeasure)
            }
        } else {
            null
        }
    }

    private fun getIngredients(detail: RecipeDetailsDTO.Detail): List<Ingredient> {
        return listOfNotNull(
            getIngredient(detail.strIngredient1, detail.strMeasure1),
            getIngredient(detail.strIngredient2, detail.strMeasure2),
            getIngredient(detail.strIngredient3, detail.strMeasure3),
            getIngredient(detail.strIngredient4, detail.strMeasure4),
            getIngredient(detail.strIngredient5, detail.strMeasure5),
            getIngredient(detail.strIngredient6, detail.strMeasure6),
            getIngredient(detail.strIngredient7, detail.strMeasure7),
            getIngredient(detail.strIngredient8, detail.strMeasure8),
            getIngredient(detail.strIngredient9, detail.strMeasure9),
            getIngredient(detail.strIngredient10, detail.strMeasure10),
            getIngredient(detail.strIngredient11, detail.strMeasure11),
            getIngredient(detail.strIngredient12, detail.strMeasure12),
            getIngredient(detail.strIngredient13, detail.strMeasure13),
            getIngredient(detail.strIngredient14, detail.strMeasure14),
            getIngredient(detail.strIngredient15, detail.strMeasure15),
            getIngredient(detail.strIngredient16, detail.strMeasure16),
            getIngredient(detail.strIngredient17, detail.strMeasure17),
            getIngredient(detail.strIngredient18, detail.strMeasure18),
            getIngredient(detail.strIngredient19, detail.strMeasure19),
            getIngredient(detail.strIngredient20, detail.strMeasure20)
        )
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadRecipes(categoryName: String): LoadRecipeResult {
        try {
            val recipesList = service.loadRecipes(categoryName)
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

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadCategories(): LoadCategoryResult {
        try {
            val categoriesList = service.loadCategories()
            val categories = categoriesList.categories
                .mapNotNull {
                    it.toDomain()
                }
            return if (categories.isEmpty()) {
                LoadCategoryResult.Failure(LoadCategoryError.NoCategoryFound)
            } else {
                LoadCategoryResult.Success(categories)
            }
        } catch (e: IOException) { // no network available
            return LoadCategoryResult.Failure(LoadCategoryError.NoInternet)
        } catch (e: ConnectException) { // interrupted network request
            return LoadCategoryResult.Failure(LoadCategoryError.InterruptedRequest)
        } catch (e: SocketTimeoutException) { // server timeout error
            return LoadCategoryResult.Failure(LoadCategoryError.SlowInternet)
        } catch (e: Exception) { // other generic exception
            Timber.e(e, "Generic Exception on LoadCategories")
            return LoadCategoryResult.Failure(LoadCategoryError.ServerError)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadRandomRecipe(): LoadRecipeDetailsResult {
        try {
            val recipesDetailsDTO = service.loadRandomRecipe()
            return if (recipesDetailsDTO.details.isEmpty()) {
                LoadRecipeDetailsResult.Failure(NoDetailFound)
            } else {
                val recipeDetail: RecipeDetailsDTO.Detail = recipesDetailsDTO.details[0]
                val ingredients = getIngredients(recipeDetail)
                var video = recipeDetail.strYoutube
                if (video.isNullOrBlank()) {
                    video = ""
                }
                val recipeDetails = RecipeDetails(
                    name = recipeDetail.strMeal,
                    image = recipeDetail.strMealThumb,
                    video = video,
                    idMeal = recipeDetail.idMeal,
                    ingredients = ingredients,
                    instructions = recipeDetail.strInstructions
                )
                LoadRecipeDetailsResult.Success(recipeDetails)
            }
        } catch (e: IOException) { // no network available
            return LoadRecipeDetailsResult.Failure(LoadRecipeDetailError.NoInternet)
        } catch (e: ConnectException) { // interrupted network request
            return LoadRecipeDetailsResult.Failure(LoadRecipeDetailError.InterruptedRequest)
        } catch (e: SocketTimeoutException) { // server timeout error
            return LoadRecipeDetailsResult.Failure(LoadRecipeDetailError.SlowInternet)
        } catch (e: Exception) { // other generic exception
            Timber.e(e, "Generic Exception on LoadRecipeDetails")
            return LoadRecipeDetailsResult.Failure(LoadRecipeDetailError.ServerError)
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

    private fun CategoryDTO.Category.toDomain(): Category? {
        val id = idCategory.toLongOrNull()
        return if (id != null) {
            Category(
                name = strCategory,
                image = strCategoryThumb,
                id = idCategory
            )
            // possibilità di implementare la descrizione
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

sealed class LoadRecipeDetailError {
    object NoDetailFound : LoadRecipeDetailError()
    object NoInternet : LoadRecipeDetailError()
    object InterruptedRequest : LoadRecipeDetailError()
    object SlowInternet : LoadRecipeDetailError()
    object ServerError : LoadRecipeDetailError()
}

sealed class LoadCategoryError {
    object NoCategoryFound : LoadCategoryError()
    object NoInternet : LoadCategoryError()
    object InterruptedRequest : LoadCategoryError()
    object SlowInternet : LoadCategoryError()
    object ServerError : LoadCategoryError()
}

sealed class LoadRecipeResult {
    data class Success(val recipes: List<Recipe>) : LoadRecipeResult()
    data class Failure(val error: LoadRecipeError) : LoadRecipeResult()
}

sealed class LoadRecipeDetailsResult {
    data class Success(val details: RecipeDetails) : LoadRecipeDetailsResult()
    data class Failure(val error: LoadRecipeDetailError) : LoadRecipeDetailsResult()
}

sealed class LoadCategoryResult {
    data class Success(val categories: List<Category>) : LoadCategoryResult()
    data class Failure(val error: LoadCategoryError) : LoadCategoryResult()
}

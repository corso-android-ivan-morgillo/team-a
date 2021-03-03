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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class CacheInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        val cacheControl: CacheControl = CacheControl.Builder()
            //   .maxAge(15, java.util.concurrent.TimeUnit.MINUTES) // 15 minutes cache
            .build()
        return response.newBuilder()
            .removeHeader("Pragma")
            .removeHeader("Cache-Control")
            .header("Cache-Control", cacheControl.toString())
            .build()
    }
}

private const val SIZEONE = 50L
private const val SIZETWO = 1024L

class NetworkAPI(cacheDir: File) {
    private val service: RecipeService

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addNetworkInterceptor(CacheInterceptor())
            .cache(
                Cache(
                    directory = File(cacheDir, "http_cache"),
                    // $0.05 worth of phone storage in 2020
                    maxSize = SIZEONE * SIZETWO * SIZETWO // 50 MiB
                )
            )
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
                    instructions = recipeDetail.strInstructions,
                    area = recipeDetail.strArea
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
    suspend fun loadCategories(): LoadCategoryResult = coroutineScope {
        try {
            val categoriesList = service.loadCategories()
            val categories = categoriesList.categories
                .map {
                    async { it.toDomain() }
                }.awaitAll()
                .filterNotNull()
            if (categories.isEmpty()) {
                LoadCategoryResult.Failure(LoadCategoryError.NoCategoryFound)
            } else {
                LoadCategoryResult.Success(categories)
            }
        } catch (e: IOException) { // no network available
            Timber.d(e.message)
            LoadCategoryResult.Failure(LoadCategoryError.NoInternet)
        } catch (e: ConnectException) { // interrupted network request
            LoadCategoryResult.Failure(LoadCategoryError.InterruptedRequest)
        } catch (e: SocketTimeoutException) { // server timeout error
            LoadCategoryResult.Failure(LoadCategoryError.SlowInternet)
        } catch (e: Exception) { // other generic exception
            Timber.e(e, "Generic Exception on LoadCategories")
            LoadCategoryResult.Failure(LoadCategoryError.ServerError)
        }
    }

    private suspend fun loadCategoryInfo(categoryName: String): CategoryInfo {
        val recipesList: LoadRecipeResult = loadRecipes(categoryName)
        val categoryInfo: CategoryInfo
        if (recipesList is Success) {
            val recipesAmount = recipesList.recipes.size.toString()
            val areaNames = loadCategoriesFlags(recipesList.recipes)
            categoryInfo = CategoryInfo(recipesAmount, areaNames)
            return categoryInfo
        } else {
            TODO()
        }
    }

    data class CategoryInfo(
        var recipesAmount: String,
        var areaNames: List<String>,
    )

    private suspend fun loadCategoriesFlags(recipes: List<Recipe>): List<String> = coroutineScope {

        recipes
            .map {
                async { loadRecipeDetails(it.idMeal) }
            }.awaitAll()
            .filterIsInstance(LoadRecipeDetailsResult.Success::class.java)
            .map {
                reformatFlagName(it.details.area)
            }
            .distinct()
            .sortedDescending()
    }

    private fun reformatFlagName(areaName: String): String {
        return when (areaName) {
            "American" -> "us"
            "British" -> "gb"
            "Canadian" -> "ca"
            "Chinese" -> "cn"
            "Dutch" -> "nl"
            "Egyptian" -> "eg"
            "French" -> "fr"
            "Greek" -> "gr"
            "Indian" -> "in"
            "Irish" -> "ie"
            "Italian" -> "it"
            "Jamaican" -> "jm"
            "Japanese" -> "jp"
            "Kenyan" -> "ke"
            "Malaysian" -> "my"
            "Mexican" -> "mx"
            "Moroccan" -> "ma"
            "Polish" -> "pl"
            "Russian" -> "ru"
            "Spanish" -> "es"
            "Thai" -> "th"
            "Tunisian" -> "tm"
            "Turkish" -> "tr"
            "Vietnamese" -> "vn"
            else -> ""
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
                    instructions = recipeDetail.strInstructions,
                    area = recipeDetail.strArea
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

    private suspend fun CategoryDTO.Category.toDomain(): Category? {
        val id = idCategory.toLongOrNull()
        return if (id != null) {
            val categoryInfo = loadCategoryInfo(strCategory)
            Category(
                name = strCategory,
                image = strCategoryThumb,
                id = idCategory,
                recipeAmount = categoryInfo.recipesAmount,
                categoryArea = categoryInfo.areaNames
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

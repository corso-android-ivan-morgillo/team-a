package com.ateam.delicious.networking

import com.ateam.delicious.domain.Category
import com.ateam.delicious.domain.Ingredient
import com.ateam.delicious.domain.NetworkAPI
import com.ateam.delicious.domain.Recipe
import com.ateam.delicious.domain.RecipeDetails
import com.ateam.delicious.domain.error.LoadCategoryError
import com.ateam.delicious.domain.error.LoadRecipeDetailError
import com.ateam.delicious.domain.error.LoadRecipeDetailError.NoDetailFound
import com.ateam.delicious.domain.error.LoadRecipeError.InterruptedRequest
import com.ateam.delicious.domain.error.LoadRecipeError.NoInternet
import com.ateam.delicious.domain.error.LoadRecipeError.NoRecipeFound
import com.ateam.delicious.domain.error.LoadRecipeError.ServerError
import com.ateam.delicious.domain.error.LoadRecipeError.SlowInternet
import com.ateam.delicious.domain.result.LoadCategoryResult
import com.ateam.delicious.domain.result.LoadRecipeDetailsResult
import com.ateam.delicious.domain.result.LoadRecipeResult
import com.ateam.delicious.domain.result.LoadRecipeResult.Failure
import com.ateam.delicious.domain.result.LoadRecipeResult.Success
import com.ateam.delicious.networking.dto.CategoryDTO
import com.ateam.delicious.networking.dto.RecipeDTO
import com.ateam.delicious.networking.dto.RecipeDetailsDTO
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

class NetworkApiImpl(cacheDir: File) : NetworkAPI {
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
    override suspend fun loadRecipeDetails(idMeal: Long): LoadRecipeDetailsResult {
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
                val reformatFlagName = reformatFlagName(recipeDetail.strArea)
                val recipeDetails = RecipeDetails(
                    name = recipeDetail.strMeal,
                    image = recipeDetail.strMealThumb,
                    video = video,
                    idMeal = recipeDetail.idMeal.toLong(),
                    ingredients = ingredients,
                    instructions = recipeDetail.strInstructions,
                    area = "https://www.themealdb.com/images/icons/flags/big/64/${reformatFlagName}.png"
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
                Ingredient(
                    strIngredient,
                    "qb",
                    "https://www.themealdb.com/images/ingredients/$strIngredient-Small.png"
                ) // ad esempio il sale
            } else {
                Ingredient(
                    strIngredient,
                    strMeasure,
                    "https://www.themealdb.com/images/ingredients/$strIngredient-Small.png"
                )
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
    override suspend fun loadRecipes(categoryName: String): LoadRecipeResult {
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
    override suspend fun loadCategories(): LoadCategoryResult = coroutineScope {
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

        var categoryInfo = CategoryInfo("")
        if (recipesList is Success) {
            val recipesAmount = recipesList.recipes.size.toString()
            categoryInfo = CategoryInfo(recipesAmount)
        }
        return categoryInfo
    }

    data class CategoryInfo(
        var recipesAmount: String
    )

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
    override suspend fun loadRandomRecipe(): LoadRecipeDetailsResult {
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
                    idMeal = recipeDetail.idMeal.toLong(),
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

    override suspend fun loadRecipesByIngredient(ingredientName: String): LoadRecipeResult {
        TODO("Not yet implemented")
    }

    override suspend fun loadRecipesByArea(areaName: String): LoadRecipeResult {
        TODO("Not yet implemented")
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
                recipeAmount = categoryInfo.recipesAmount
            )
        } else {
            null
        }
    }
}

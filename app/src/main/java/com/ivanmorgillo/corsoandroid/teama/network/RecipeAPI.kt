package com.ivanmorgillo.corsoandroid.teama.network

import com.ivanmorgillo.corsoandroid.teama.Recipe
import com.ivanmorgillo.corsoandroid.teama.detail.Ingredient
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
            val ingredients = getIngredients(recipeDetail)
            val recipeDetails: RecipeDetails = RecipeDetails(
                name = recipeDetail.strMeal,
                image = recipeDetail.strMealThumb,
                idMeal = recipeDetail.idMeal,
                ingredients = ingredients,
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

    private fun getIngredient(strIngredient: String, strMeasure: String): Ingredient? {
        return if (!strIngredient.isNullOrEmpty()) {
            if (strMeasure.isNullOrEmpty()) {
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

    /*
    private fun getIngredientsOLD(detail: RecipeDetailsDTO.Detail): List<Ingredient> {
        val ingredients: List<Ingredient> = listOf(
            if (!detail.strIngredient1.isNullOrEmpty()) {
                if (detail.strMeasure1.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient1, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient1, detail.strMeasure1)
                }
            } else {
                null
            },
            if (!detail.strIngredient2.isNullOrEmpty()) {
                if (detail.strMeasure2.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient2, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient2, detail.strMeasure2)
                }
            } else {
                null
            },
            if (!detail.strIngredient3.isNullOrEmpty()) {
                if (detail.strMeasure3.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient3, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient3, detail.strMeasure3)
                }
            } else {
                null
            },
            if (!detail.strIngredient4.isNullOrEmpty()) {
                if (detail.strMeasure4.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient4, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient4, detail.strMeasure4)
                }
            } else {
                null
            },
            if (!detail.strIngredient5.isNullOrEmpty()) {
                if (detail.strMeasure5.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient5, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient5, detail.strMeasure5)
                }
            } else {
                null
            },
            if (!detail.strIngredient6.isNullOrEmpty()) {
                if (detail.strMeasure6.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient6, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient6, detail.strMeasure6)
                }
            } else {
                null
            },
            if (!detail.strIngredient7.isNullOrEmpty()) {
                if (detail.strMeasure7.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient7, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient7, detail.strMeasure7)
                }
            } else {
                null
            },
            if (!detail.strIngredient8.isNullOrEmpty()) {
                if (detail.strMeasure8.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient8, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient8, detail.strMeasure8)
                }
            } else {
                null
            },
            if (!detail.strIngredient9.isNullOrEmpty()) {
                if (detail.strMeasure9.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient9, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient9, detail.strMeasure9)
                }
            } else {
                null
            },
            if (!detail.strIngredient10.isNullOrEmpty()) {
                if (detail.strMeasure10.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient10, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient10, detail.strMeasure10)
                }
            } else {
                null
            },
            if (!detail.strIngredient11.isNullOrEmpty()) {
                if (detail.strMeasure11.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient11, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient11, detail.strMeasure11)
                }
            } else {
                null
            },
            if (!detail.strIngredient12.isNullOrEmpty()) {
                if (detail.strMeasure12.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient12, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient12, detail.strMeasure12)
                }
            } else {
                null
            },
            if (!detail.strIngredient13.isNullOrEmpty()) {
                if (detail.strMeasure13.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient13, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient13, detail.strMeasure13)
                }
            } else {
                null
            },
            if (!detail.strIngredient14.isNullOrEmpty()) {
                if (detail.strMeasure14.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient14, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient14, detail.strMeasure14)
                }
            } else {
                null
            },
            if (!detail.strIngredient15.isNullOrEmpty()) {
                if (detail.strMeasure15.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient15, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient15, detail.strMeasure15)
                }
            } else {
                null
            },
            if (!detail.strIngredient16.isNullOrEmpty()) {
                if (detail.strMeasure16.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient16, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient16, detail.strMeasure16)
                }
            } else {
                null
            },
            if (!detail.strIngredient17.isNullOrEmpty()) {
                if (detail.strMeasure17.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient17, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient17, detail.strMeasure17)
                }
            } else {
                null
            },
            if (!detail.strIngredient18.isNullOrEmpty()) {
                if (detail.strMeasure18.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient18, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient18, detail.strMeasure18)
                }
            } else {
                null
            },
            if (!detail.strIngredient19.isNullOrEmpty()) {
                if (detail.strMeasure19.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient19, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient19, detail.strMeasure19)
                }
            } else {
                null
            },
            if (!detail.strIngredient20.isNullOrEmpty()) {
                if (detail.strMeasure20.isNullOrEmpty()) {
                    Ingredient(detail.strIngredient20, "qb") // ad esempio il sale
                } else {
                    Ingredient(detail.strIngredient20, detail.strMeasure20)
                }
            } else {
                null
            }
        ).filterNotNull()
        return ingredients
    }*/

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

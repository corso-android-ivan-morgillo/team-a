package com.ivanmorgillo.corsoandroid.teama.favourite

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.ivanmorgillo.corsoandroid.teama.detail.Ingredient
import com.ivanmorgillo.corsoandroid.teama.detail.RecipeDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

interface FavouriteRepository {
    suspend fun loadAll(): LoadFavouriteResult
    suspend fun add(favourite: RecipeDetails): Boolean
    suspend fun delete(idMeal: Long): Boolean
    suspend fun isFavourite(idMeal: Long): Boolean
}

class FavouriteRepositoryImpl(private val context: Context, private val gson: Gson) : FavouriteRepository {
    private val storage: SharedPreferences by lazy { context.getSharedPreferences("favourites", Context.MODE_PRIVATE) }
    
    override suspend fun loadAll(): LoadFavouriteResult = withContext(Dispatchers.IO) {
        Timber.d("loading favourites")
        val all = storage.all // <id, stringa json>
            .values
            .map {
                it as String // la singola stringa json (veramente di tipo String), quindi 1 preferito completo
            }
            .map { // it = ogni stringa di Entity (es: name, image, video, ecc)
                gson.fromJson(it, RecipeDetailEntity::class.java)
            }
            .map {
                RecipeDetails(
                    name = it.name,
                    image = it.image,
                    video = it.video,
                    idMeal = it.id,
                    ingredients = it.ingredients,
                    instructions = it.instructions,
                    area = it.area
                )
            }
        LoadFavouriteResult.Success(all)
    }

    override suspend fun isFavourite(idMeal: Long): Boolean = withContext(Dispatchers.IO) {
        val maybeFavourite = storage.getString(idMeal.toString(), null)
        maybeFavourite != null
    }


    @SuppressLint("ApplySharedPref")
    override suspend fun add(favourite: RecipeDetails): Boolean = withContext(Dispatchers.IO) {
        Timber.d("added favourite")
        val recipeDetailEntity = RecipeDetailEntity(
            name = favourite.name,
            image = favourite.image,
            video = favourite.video,
            id = favourite.idMeal,
            ingredients = favourite.ingredients,
            instructions = favourite.instructions,
            area = favourite.area
        )
        val serializedRecipeDetail = gson.toJson(recipeDetailEntity)
        storage.edit().putString(favourite.idMeal.toString(), serializedRecipeDetail).commit()
    }

    @SuppressLint("ApplySharedPref")
    override suspend fun delete(idMeal: Long): Boolean = withContext(Dispatchers.IO) {
        val success = storage.edit().remove(idMeal.toString()).commit()
        if (success) {
            Timber.d("deleted favourite successfully")
        } else {
            Timber.d("error deleting favourite")
        }
        success
    }
}

data class RecipeDetailEntity(
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("video")
    val video: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("ingredients")
    val ingredients: List<Ingredient>,
    @SerializedName("instructions")
    val instructions: String,
    @SerializedName("area")
    val area: String
)

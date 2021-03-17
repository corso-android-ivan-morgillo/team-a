package com.ateam.delicious.networking

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RecipeDTO(
    @SerializedName("meals")
    val meals: List<Meal>
) {
    @Keep
    data class Meal(
        @SerializedName("idMeal")
        val idMeal: String,
        @SerializedName("strMeal")
        val strMeal: String,
        @SerializedName("strMealThumb")
        val strMealThumb: String
    )
}

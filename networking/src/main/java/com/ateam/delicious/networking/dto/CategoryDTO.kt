package com.ateam.delicious.networking.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CategoryDTO(
    @SerializedName("categories")
    val categories: List<Category>,
) {
    @Keep
    data class Category(
        @SerializedName("idCategory")
        val idCategory: String,
        @SerializedName("strCategory")
        val strCategory: String,
        @SerializedName("strCategoryDescription")
        val strCategoryDescription: String,
        @SerializedName("strCategoryThumb")
        val strCategoryThumb: String,
    )
}



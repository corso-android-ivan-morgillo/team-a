package com.ateam.delicious.networking.dto
import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName

@Keep
data class AreaDTO(
    @SerializedName("meals")
    val meals: List<Meal>
) {
    @Keep
    data class Meal(
        @SerializedName("strArea")
        val strArea: String
    )
}

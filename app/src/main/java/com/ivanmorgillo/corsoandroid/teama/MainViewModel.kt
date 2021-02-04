package com.ivanmorgillo.corsoandroid.teama

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    fun getRecipes() = recipes

    private val title = "Pizza1"
    private val image = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg"
    private val recipes = listOf(
            RecipeUI(title = title, image = image),
            RecipeUI(title = title, image = image),
            RecipeUI(title = title, image = image),
            RecipeUI(title = title, image = image),
            RecipeUI(title = title, image = image),
            RecipeUI(title = title, image = image),
            RecipeUI(title = title, image = image),
            RecipeUI(title = title, image = image),
            RecipeUI(title = title, image = image),
            RecipeUI(title = title, image = image),
            RecipeUI(title = title, image = image),
            RecipeUI(title = title, image = image)
    )
}

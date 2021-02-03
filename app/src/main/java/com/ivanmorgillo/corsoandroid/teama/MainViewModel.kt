package com.ivanmorgillo.corsoandroid.teama

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    fun getRecipes() = recipes

    private val recipes = listOf<RecipeUI>(
        RecipeUI(title = "Pizza1", image = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg"),
        RecipeUI(title = "Pizza2", image = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg"),
        RecipeUI(title = "Pizza3", image = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg"),
        RecipeUI(title = "Pizza4", image = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg"),
        RecipeUI(title = "Pizza5", image = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg"),
        RecipeUI(title = "Pizza6", image = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg"),
        RecipeUI(title = "Pizza7", image = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg"),
        RecipeUI(title = "Pizza8", image = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg"),
        RecipeUI(title = "Pizza9", image = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg"),
        RecipeUI(title = "Pizza10", image = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg"),
        RecipeUI(title = "Pizza11", image = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg"),
        RecipeUI(title = "Pizza12", image = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg")
    )

}
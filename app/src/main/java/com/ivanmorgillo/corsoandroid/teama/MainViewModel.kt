package com.ivanmorgillo.corsoandroid.teama

import androidx.lifecycle.ViewModel
private const val MAXRANGE = 10
class MainViewModel : ViewModel() {

    fun getRecipes() = recipes

    private val title = "Pizza1"
    private val image = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg"
    private val recipes = (1..10).map {
        RecipeUI(title = title, image = image)
    }
    /** private val recipes = listOf(
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
     *
     * Metodo alternativo per fare la lista :
     *  private val maxRange = 10
    private val recipes = (1.rangeTo(maxRange)).map {
    RecipeUI(title = title, image = image)
    }
     * */
}

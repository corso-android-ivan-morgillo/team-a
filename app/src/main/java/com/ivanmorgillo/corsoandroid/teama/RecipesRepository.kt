package com.ivanmorgillo.corsoandroid.teama

private const val MAXRANGE = 10

interface RecipesRepository {
    suspend fun loadRecipes(): List<Recipe>
}

class RecipeRepositoryImpl : RecipesRepository {
    override suspend fun loadRecipes(): List<Recipe> {
        val title = "Pizza1"
        val image = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg"
        return (1..MAXRANGE).map {
            Recipe(
                name = title + it,
                image = image,
                idMeal = it.toString(),
            )
        }
    }
}

data class Recipe(val name: String, val image: String, val idMeal: String)

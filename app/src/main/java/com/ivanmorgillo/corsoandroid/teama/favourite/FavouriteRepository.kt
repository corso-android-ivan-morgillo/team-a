package com.ivanmorgillo.corsoandroid.teama.favourite

interface FavouriteRepository {
    suspend fun loadFavourites(): LoadFavouriteResult
}

private const val MAGIC_NUMBER = 10

class FavouriteRepositoryImpl : FavouriteRepository {
    override suspend fun loadFavourites(): LoadFavouriteResult {
        val favourites = (1..MAGIC_NUMBER).map {
            Favourite(
                name = "Preferito $it",
                image = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg",
                idMeal = -1L,
                notes = "my notes",
                video = "https://www.youtube.com/watch?v=SQnr4Z-7rok",
                ingredients = emptyList(),
                instructions = ""
            )
        }
        return LoadFavouriteResult.Success(favourites)
    }
}

data class Favourite(
    val name: String,
    val image: String,
    val idMeal: Long,
    val notes: String,
    val video: String,
    val ingredients: List<String>,
    val instructions: String,
)

package com.ivanmorgillo.corsoandroid.teama.detail

// gli oggetti dentro questa sealed li stiamo aggiungendo a seconda dell'ordine della nostra schermata
// io seguo un pò anche il discorso di ivan perchè la nostra schermata è diversa
sealed class RecipeDetailsUI {
    data class Title(val title: String, val area: String) : RecipeDetailsUI()
    data class Video(val video: String, val image: String) : RecipeDetailsUI()
    data class IngredientsInstructionsList(
        val ingredients: List<IngredientUI>,
        val instruction: String,
        val isIngredientsVisible: Boolean,
    ) : RecipeDetailsUI()

    object TabLayout : RecipeDetailsUI()
}

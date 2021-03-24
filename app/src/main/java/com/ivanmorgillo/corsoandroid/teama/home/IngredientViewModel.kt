package com.ivanmorgillo.corsoandroid.teama.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ateam.delicious.domain.NetworkAPI
import com.ateam.delicious.domain.Recipe
import com.ateam.delicious.domain.result.LoadRecipeResult
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import kotlinx.coroutines.launch

class IngredientViewModel(val api: NetworkAPI) : ViewModel() {

    val states = MutableLiveData<IngredientScreenEvent>()

    fun send(event: IngredientScreenEvent) {

        when (event) {
            IngredientScreenEvent.OnReady -> TODO()
            is IngredientScreenEvent.OnResearch -> {

                //Attenzione, se la ricetta/le ricette non esistono per quell ingrediente
                //Mostrate un alert / qualcosa che dica all'utente "NORESULTS"
                loadRecipesByIngredient(event.ingredientTextSearch)
            }
        }
    }

    private fun loadRecipesByIngredient(ingredient: String) {
        viewModelScope.launch {
            val result = api.loadRecipesByIngredient(ingredient)
            when (result) {
                is LoadRecipeResult.Success -> onSuccess(result.recipes)
                is LoadRecipeResult.Failure -> TODO()
            }.exhaustive
        }


    }

    private fun onSuccess(recipesList: List<Recipe>) {

        val recipesByIngredient = recipesList.map {

            RecipeByIngredientUI(
                title = it.name,
                id = it.idMeal,
                image = it.image

            )
        }
        TODO()

    }

    private fun onFailure() {}

}

data class RecipeByIngredientUI(

    val title: String,
    val image: String,
    val id: Long
)

sealed class IngredientScreenEvent {

    object OnReady : IngredientScreenEvent()
    data class OnResearch(val ingredientTextSearch: String) : IngredientScreenEvent()
}

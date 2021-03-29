package com.ivanmorgillo.corsoandroid.teama.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ateam.delicious.domain.NetworkAPI
import com.ateam.delicious.domain.Recipe
import com.ateam.delicious.domain.error.LoadRecipeError
import com.ateam.delicious.domain.result.LoadRecipeResult
import com.ivanmorgillo.corsoandroid.teama.crashlytics.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.home.IngredientScreenState.Content
import com.ivanmorgillo.corsoandroid.teama.home.IngredientScreenState.Error
import kotlinx.coroutines.launch

class IngredientViewModel(val api: NetworkAPI) : ViewModel() {

    val states = MutableLiveData<IngredientScreenState>()
    val actions = SingleLiveEvent<IngredientScreenAction>()
    fun send(event: IngredientScreenEvent) {

        when (event) {
            IngredientScreenEvent.OnReady -> TODO()
            is IngredientScreenEvent.OnResearch -> {

                //Attenzione, se la ricetta/le ricette non esistono per quell ingrediente
                //Mostrate un alert / qualcosa che dica all'utente "NORESULTS"
                loadRecipesByIngredient(event.ingredientTextSearch)
            }
            is IngredientScreenEvent.OnRecipeByIngredientClick -> {
                actions.postValue(IngredientScreenAction.NavigateToDetail(event.recipeByIngredient))
            }
        }.exhaustive
    }

    private fun loadRecipesByIngredient(ingredient: String) {
        viewModelScope.launch {
            val result = api.loadRecipesByIngredient(ingredient)
            when (result) {
                is LoadRecipeResult.Success -> onSuccess(result.recipes)
                is LoadRecipeResult.Failure -> onFailure(result.error)
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
        states.postValue(Content(recipesByIngredient))

    }

    private fun onFailure(error: LoadRecipeError) {
        states.postValue(Error)
        when (error) {
            LoadRecipeError.InterruptedRequest -> TODO()
            LoadRecipeError.NoInternet -> TODO()
            LoadRecipeError.NoRecipeFound -> actions.postValue(IngredientScreenAction.ShowNoRecipeFound)
            LoadRecipeError.ServerError -> TODO()
            LoadRecipeError.SlowInternet -> TODO()
        }.exhaustive
    }

}

sealed class IngredientScreenAction {
    data class NavigateToDetail(val recipeByIngredient: RecipeByIngredientUI) : IngredientScreenAction()
    object ShowNoRecipeFound : IngredientScreenAction()
}

sealed class IngredientScreenState {

    data class Content(val recipes: List<RecipeByIngredientUI>) : IngredientScreenState()

    object Error : IngredientScreenState()
}

data class RecipeByIngredientUI(

    val title: String,
    val image: String,
    val id: Long
)

sealed class IngredientScreenEvent {

    object OnReady : IngredientScreenEvent()
    data class OnResearch(val ingredientTextSearch: String) : IngredientScreenEvent()
    data class OnRecipeByIngredientClick(val recipeByIngredient: RecipeByIngredientUI) : IngredientScreenEvent()
}

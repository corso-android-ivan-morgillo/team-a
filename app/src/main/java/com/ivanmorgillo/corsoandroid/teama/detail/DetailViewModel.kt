package com.ivanmorgillo.corsoandroid.teama.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teama.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teama.exhaustive
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailsResult
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: RecipeDetailsRepository) : ViewModel() {

    val states = MutableLiveData<DetailScreenStates>()
    val actions = SingleLiveEvent<DetailScreenAction>()
    fun send(event: DetailScreenEvent) {
        when (event) {
            is DetailScreenEvent.OnReady -> {
                loadContent(event.idMeal)
            }
            DetailScreenEvent.OnIngredientsClick -> onIngredientsClick()
            DetailScreenEvent.OnInstructionsClick -> onInstructionsClick()
        }
    }

    private fun loadContent(idMeal: Long) {
        states.postValue(DetailScreenStates.Loading)
        viewModelScope.launch {
            val result = repository.loadRecipeDetails(idMeal)
            when (result) {
                is LoadRecipeDetailsResult.Failure -> TODO()
                is LoadRecipeDetailsResult.Success -> onSuccess(result)
            }.exhaustive
        }
    }

    private fun onSuccess(result: LoadRecipeDetailsResult.Success) {
        val details: RecipeDetails = result.details
        val detailUI = RecipeDetailsUI(
            details.idMeal,
            details.name,
            details.image,
            details.ingredients.map { IngredientUI(it.ingredientName, it.ingredientQuantity) },
            details.instructions
        )
        states.postValue(DetailScreenStates.Content(detailUI))
    }

    private fun onIngredientsClick() {

        actions.postValue(DetailScreenAction.ShowIngredients)
    }

    private fun onInstructionsClick() {

        actions.postValue(DetailScreenAction.ShowInstructions)
    }
}

sealed class DetailScreenStates {
    // questi oggetti rappresentano la nostra schermata inequivocabilmente
    object Loading : DetailScreenStates()
    object Error : DetailScreenStates()

    // se la lista cambia dobbiamo usare una 'data class' quindi non usiamo 'object'
    data class Content(val recipes: RecipeDetailsUI) : DetailScreenStates()
}

sealed class DetailScreenAction {

    object ShowIngredients : DetailScreenAction()
    object ShowInstructions : DetailScreenAction()
}

sealed class DetailScreenEvent {
    data class OnReady(val idMeal: Long) : DetailScreenEvent()
    object OnIngredientsClick : DetailScreenEvent()
    object OnInstructionsClick : DetailScreenEvent()
}

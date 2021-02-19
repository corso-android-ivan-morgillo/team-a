package com.ivanmorgillo.corsoandroid.teama.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teama.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenAction.ShowIngredients
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenAction.ShowInstructions
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenAction.ShowInterruptedRequestMessage
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenAction.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenAction.ShowServerErrorMessage
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenAction.ShowSlowInternetMessage
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenStates.Content
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenStates.Error
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenStates.Loading
import com.ivanmorgillo.corsoandroid.teama.exhaustive
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailError.InterruptedRequest
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailError.NoDetailFound
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailError.NoInternet
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailError.ServerError
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailError.SlowInternet
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailsResult.Failure
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailsResult.Success
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
        states.postValue(Loading)
        viewModelScope.launch {
            val result = repository.loadRecipeDetails(idMeal)
            when (result) {
                is Failure -> onFailure(result)
                is Success -> onSuccess(result)
            }.exhaustive
        }
    }

    private fun onSuccess(result: Success) {
        val details: RecipeDetails = result.details
        val detailUI = RecipeDetailsUI(
            details.idMeal,
            details.name,
            details.image,
            details.ingredients.map { IngredientUI(it.ingredientName, it.ingredientQuantity) },
            details.instructions
        )
        states.postValue(Content(detailUI))
    }

    private fun onFailure(result: Failure) {
        states.postValue(Error)
        when (result.error) {
            NoInternet -> actions.postValue(ShowNoInternetMessage)
            ServerError -> actions.postValue(ShowServerErrorMessage)
            SlowInternet -> actions.postValue(ShowSlowInternetMessage)
            InterruptedRequest -> actions.postValue(ShowInterruptedRequestMessage)
            NoDetailFound -> actions.postValue(DetailScreenAction.ShowNoRecipeDetailFoundMessage)
        }.exhaustive
    }

    private fun onIngredientsClick() {
        actions.postValue(ShowIngredients)
    }

    private fun onInstructionsClick() {
        actions.postValue(ShowInstructions)
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
    object ShowNoInternetMessage : DetailScreenAction()
    object ShowSlowInternetMessage : DetailScreenAction()
    object ShowServerErrorMessage : DetailScreenAction()
    object ShowInterruptedRequestMessage : DetailScreenAction()
    object ShowNoRecipeDetailFoundMessage : DetailScreenAction()
}

sealed class DetailScreenEvent {
    data class OnReady(val idMeal: Long) : DetailScreenEvent()
    object OnIngredientsClick : DetailScreenEvent()
    object OnInstructionsClick : DetailScreenEvent()
}

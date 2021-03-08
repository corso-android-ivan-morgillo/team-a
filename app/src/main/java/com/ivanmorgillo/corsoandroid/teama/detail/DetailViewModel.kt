package com.ivanmorgillo.corsoandroid.teama.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teama.Screens
import com.ivanmorgillo.corsoandroid.teama.Tracking
import com.ivanmorgillo.corsoandroid.teama.crashlytics.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenAction.ShowInterruptedRequestMessage
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenAction.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenAction.ShowServerErrorMessage
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenAction.ShowSlowInternetMessage
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenEvent.OnAddFavouriteClick
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenEvent.OnIngredientsClick
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenEvent.OnInstructionsClick
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenEvent.OnReady
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenStates.Content
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenStates.Error
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenStates.Loading
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.favourite.FavouriteRepository
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailError.InterruptedRequest
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailError.NoDetailFound
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailError.NoInternet
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailError.ServerError
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailError.SlowInternet
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailsResult.Failure
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailsResult.Success
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: RecipeDetailsRepository,
    private val tracking: Tracking,
    private val favouritesRepository: FavouriteRepository,
) : ViewModel() {

    val states = MutableLiveData<DetailScreenStates>()
    val actions = SingleLiveEvent<DetailScreenAction>()
    private var details: RecipeDetails? = null

    init {
        tracking.logScreen(Screens.Details)
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun send(event: DetailScreenEvent) {
        when (event) {
            is OnReady -> loadContent(event.idMeal)
            OnIngredientsClick -> onIngredientsClick()
            OnInstructionsClick -> onInstructionsClick()
            is OnAddFavouriteClick -> viewModelScope.launch { toggleFavourite() }
        }.exhaustive
    }

    private suspend fun toggleFavourite() {
        val currentState = states.value
        if (currentState != null && details != null && currentState is Content) {
            if (currentState.details.isFavourite) {
                favouritesRepository.deleteFavourite(details!!)
                val details = currentState.details.copy(isFavourite = false)
                val content = Content(details)
                states.postValue(content)
            } else {
                favouritesRepository.addFavourite(details!!)
                val details = currentState.details.copy(isFavourite = true)
                val content = Content(details)
                states.postValue(content)
            }
        }
    }

    private fun loadContent(idMeal: Long) {
        states.postValue(Loading)
        viewModelScope.launch {
            val result = repository.loadRecipeDetails(idMeal)
            when (result) {
                is Failure -> onFailure(result)
                is Success -> {
                    val isFavourite: Boolean = favouritesRepository.isFavourite(idMeal)
                    details = result.details
                    onSuccess(result.details, isFavourite)
                }
            }.exhaustive
        }
    }

    private fun onSuccess(details: RecipeDetails, isFavourite: Boolean) {
        val detailUI = RecipeDetailsUI(
            details.idMeal,
            details.name,
            details.image,
            details.video,
            details.ingredients.map { IngredientUI(it.ingredientName, it.ingredientQuantity, it.ingredientImage) },
            details.instructions,
            isIngredientsSelected = true,
            isFavourite
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
        tracking.logEvent("detail_ingredients_clicked")
        val currentState = states.value
        if (currentState != null && currentState is Content) {
            val updatedDetails = currentState.details.copy(isIngredientsSelected = true)
            val content = Content(updatedDetails)
            states.postValue(content)
        }
    }

    private fun onInstructionsClick() {
        tracking.logEvent("detail_instructions_clicked")
        val currentState = states.value
        if (currentState != null && currentState is Content) {
            val updatedDetails = currentState.details.copy(isIngredientsSelected = false)
            val content = Content(updatedDetails)
            states.postValue(content)
        }
    }
}

sealed class DetailScreenStates {
    // questi oggetti rappresentano la nostra schermata inequivocabilmente
    object Loading : DetailScreenStates()
    object Error : DetailScreenStates()

    // se la lista cambia dobbiamo usare una 'data class' quindi non usiamo 'object'
    data class Content(val details: RecipeDetailsUI) : DetailScreenStates()
}

sealed class DetailScreenAction {
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
    object OnAddFavouriteClick : DetailScreenEvent()
}

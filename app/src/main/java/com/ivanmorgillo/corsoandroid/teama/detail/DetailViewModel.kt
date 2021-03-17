package com.ivanmorgillo.corsoandroid.teama.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ateam.delicious.domain.LoadRecipeDetailError.InterruptedRequest
import com.ateam.delicious.domain.LoadRecipeDetailError.NoDetailFound
import com.ateam.delicious.domain.LoadRecipeDetailError.NoInternet
import com.ateam.delicious.domain.LoadRecipeDetailError.ServerError
import com.ateam.delicious.domain.LoadRecipeDetailError.SlowInternet
import com.ateam.delicious.domain.LoadRecipeDetailsResult.Failure
import com.ateam.delicious.domain.LoadRecipeDetailsResult.Success
import com.ateam.delicious.domain.RecipeDetails
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
            if (currentState.isFavourite) {
                favouritesRepository.delete(details!!.idMeal)
                val details = currentState.details
                val content = Content(details, false)
                states.postValue(content)
            } else {
                favouritesRepository.add(details!!)
                val details = currentState.details
                val content = Content(details, true)
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
        val content = createContent(details, true, isFavourite)
        states.postValue(content)
    }

    private fun createContent(details: RecipeDetails, isIngredientsVisible: Boolean, isFavourite: Boolean): Content {
        val detailScreenItems = listOf(
            RecipeDetailsUI.Video(details.video, details.image),
            RecipeDetailsUI.Title(details.name, details.area),
            RecipeDetailsUI.TabLayout,
            RecipeDetailsUI.IngredientsInstructionsList(
                details.ingredients.map {
                    IngredientUI(it.ingredientName, it.ingredientQuantity, it.ingredientImage)
                },
                details.instructions,
                isIngredientsVisible = isIngredientsVisible
            )
        )
        return Content(detailScreenItems, isFavourite)
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
        updateIngredientsVisibility(true)
    }

    private fun onInstructionsClick() {
        tracking.logEvent("detail_instructions_clicked")
        updateIngredientsVisibility(false)
    }

    private fun updateIngredientsVisibility(isIngredientsVisible: Boolean) {
        val currentState = states.value
        if (currentState != null && currentState is Content) {
            val updatedItems = currentState.details
                .map {
                    if (it is RecipeDetailsUI.IngredientsInstructionsList) {
                        it.copy(isIngredientsVisible = isIngredientsVisible)
                    } else {
                        it
                    }
                }
            val content = Content(updatedItems, currentState.isFavourite)
            states.postValue(content)
        }
    }
}

sealed class DetailScreenStates {
    // questi oggetti rappresentano la nostra schermata inequivocabilmente
    object Loading : DetailScreenStates()
    object Error : DetailScreenStates()

    // se la lista cambia dobbiamo usare una 'data class' quindi non usiamo 'object'
    data class Content(val details: List<RecipeDetailsUI>, val isFavourite: Boolean) : DetailScreenStates()
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

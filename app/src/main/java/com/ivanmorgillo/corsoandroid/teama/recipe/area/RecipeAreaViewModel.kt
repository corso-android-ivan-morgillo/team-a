package com.ivanmorgillo.corsoandroid.teama.recipe.area

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ateam.delicious.domain.error.LoadRecipeError
import com.ateam.delicious.domain.repository.RecipesRepository
import com.ateam.delicious.domain.result.LoadRecipeResult
import com.ivanmorgillo.corsoandroid.teama.Screens
import com.ivanmorgillo.corsoandroid.teama.Tracking
import com.ivanmorgillo.corsoandroid.teama.crashlytics.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeUI
import kotlinx.coroutines.launch
import java.util.*

class RecipeAreaViewModel(private val repository: RecipesRepository, private val tracking: Tracking) : ViewModel() {
    private var areaRecipes: List<RecipeUI>? = null
    val states = MutableLiveData<RecipeAreaScreenStates>()
    val actions = SingleLiveEvent<RecipeAreaScreenAction>()

    init {
        tracking.logScreen(Screens.AreaRecipes)
    }

    fun send(event: RecipeAreaScreenEvent) {
        when (event) {
            is RecipeAreaScreenEvent.OnReady -> loadContent(event.areaName)
            is RecipeAreaScreenEvent.OnRecipeAreaClick -> onRecipeAreaClick(event)
            is RecipeAreaScreenEvent.OnRecipeAreaSearch -> onRecipeAreaSearch(event.query)
            is RecipeAreaScreenEvent.OnRefresh -> loadContent(event.areaName)
        }.exhaustive
    }

    private fun loadContent(areaName: String) {
        states.postValue(RecipeAreaScreenStates.Loading)
        viewModelScope.launch {
            val result = repository.loadRecipesByArea(areaName)
            when (result) {
                is LoadRecipeResult.Failure -> onFailure(result)
                is LoadRecipeResult.Success -> onSuccess(result)
            }.exhaustive
        }
    }

    private fun onSuccess(result: LoadRecipeResult.Success) {
        val areaRecipes = result.recipes.map {
            RecipeUI(
                title = it.name,
                image = it.image,
                id = it.idMeal
            )
        }
        this.areaRecipes = areaRecipes
        states.postValue(RecipeAreaScreenStates.Content(areaRecipes))
    }

    private fun onRecipeAreaClick(event: RecipeAreaScreenEvent.OnRecipeAreaClick) {
        tracking.logEvent("areaRecipe_clicked")
        actions.postValue(RecipeAreaScreenAction.NavigateToDetail(event.areaRecipe))
    }

    private fun onRecipeAreaSearch(query: String) {
        tracking.logEvent("recipe_area_search")
        val filteredAreaRecipes = filter(areaRecipes, query)
        states.postValue(RecipeAreaScreenStates.Content(filteredAreaRecipes))
    }

    private fun filter(originalList: List<RecipeUI>?, query: String): List<RecipeUI> {
        return originalList?.filter {
            it.title.toLowerCase(Locale.getDefault())
                .contains(query.toLowerCase(Locale.getDefault()).trim()) || query.isBlank()
        } ?: emptyList()
    }

    private fun onFailure(result: LoadRecipeResult.Failure) {
        states.postValue(RecipeAreaScreenStates.Error)
        when (result.error) {
            LoadRecipeError.InterruptedRequest -> {
                actions.postValue(RecipeAreaScreenAction.ShowInterruptedRequestMessage)
            }
            LoadRecipeError.NoInternet -> actions.postValue(RecipeAreaScreenAction.ShowNoInternetMessage)
            LoadRecipeError.NoRecipeFound -> actions.postValue(RecipeAreaScreenAction.ShowNoRecipeFoundMessage)
            LoadRecipeError.ServerError -> actions.postValue(RecipeAreaScreenAction.ShowServerErrorMessage)
            LoadRecipeError.SlowInternet -> actions.postValue(RecipeAreaScreenAction.ShowSlowInternetMessage)
        }.exhaustive
    }
}

sealed class RecipeAreaScreenAction {
    data class NavigateToDetail(val areaRecipe: RecipeUI) : RecipeAreaScreenAction()
    object ShowNoInternetMessage : RecipeAreaScreenAction()
    object ShowSlowInternetMessage : RecipeAreaScreenAction()
    object ShowServerErrorMessage : RecipeAreaScreenAction()
    object ShowInterruptedRequestMessage : RecipeAreaScreenAction()
    object ShowNoRecipeFoundMessage : RecipeAreaScreenAction()
}

sealed class RecipeAreaScreenStates {

    object Loading : RecipeAreaScreenStates()
    object Error : RecipeAreaScreenStates()
    data class Content(val areaRecipes: List<RecipeUI>) : RecipeAreaScreenStates()
}

sealed class RecipeAreaScreenEvent {

    data class OnRecipeAreaClick(val areaRecipe: RecipeUI) : RecipeAreaScreenEvent()
    data class OnReady(val areaName: String) : RecipeAreaScreenEvent()
    data class OnRefresh(val areaName: String) : RecipeAreaScreenEvent()
    data class OnRecipeAreaSearch(val query: String) : RecipeAreaScreenEvent()
}

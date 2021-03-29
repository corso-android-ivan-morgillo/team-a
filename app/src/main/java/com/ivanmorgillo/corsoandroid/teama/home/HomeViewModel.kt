package com.ivanmorgillo.corsoandroid.teama.home

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.ivanmorgillo.corsoandroid.teama.Tracking
import com.ivanmorgillo.corsoandroid.teama.category.CategoryFragmentDirections
import com.ivanmorgillo.corsoandroid.teama.crashlytics.SingleLiveEvent

class HomeViewModel(private val tracking: Tracking) : ViewModel() {

    val actions = SingleLiveEvent<HomeScreenAction>()

    fun send(event: HomeScreenEvent) {


        when (event) {
            HomeScreenEvent.OnAreaClick -> {
                tracking.logEvent("home_area_click")
                actions.postValue(HomeScreenAction.NavigateToArea)
            }
            HomeScreenEvent.OnCategoryClick -> {
                tracking.logEvent("home_category_click")
                actions.postValue(HomeScreenAction.NavigateToCategory)
            }
            HomeScreenEvent.OnIngredientClick -> {

                tracking.logEvent("home_ingredient_click")
                actions.postValue(HomeScreenAction.NavigateToIngredient)
            }
        }
    }

}

sealed class HomeScreenAction {

    object NavigateToCategory : HomeScreenAction()
    object NavigateToIngredient : HomeScreenAction()
    object NavigateToArea : HomeScreenAction()
}


sealed class HomeScreenEvent {

    object OnCategoryClick : HomeScreenEvent()
    object OnAreaClick : HomeScreenEvent()
    object OnIngredientClick : HomeScreenEvent()

}

package com.ivanmorgillo.corsoandroid.teama

import androidx.lifecycle.ViewModel
import com.ivanmorgillo.corsoandroid.teama.crashlytics.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive

class MainViewModel(private val tracking: Tracking) : ViewModel() {
    val actions = SingleLiveEvent<MainScreenAction>()

    fun send(event: MainScreenEvent) {
        when(event) {
            MainScreenEvent.OnCategoryClick -> {
                tracking.logEvent("category_menu_clicked")
                actions.postValue(MainScreenAction.NavigateToCategory)
            }
            MainScreenEvent.OnFavouritesClick -> {
                tracking.logEvent("favourites_menu_clicked")
                actions.postValue(MainScreenAction.NavigateToFavourites)
            }
            MainScreenEvent.OnFeedbackClick -> {
                tracking.logEvent("feedback_menu_clicked")
                actions.postValue(MainScreenAction.NavigateToFeedback)
            }
            MainScreenEvent.OnRandomRecipeClick -> {
                tracking.logEvent("random_recipe_menu_clicked")
                actions.postValue(MainScreenAction.NavigateToRandomRecipe)
            }
            MainScreenEvent.OnSettingsClick -> {
                tracking.logEvent("settings_menu_clicked")
                actions.postValue(MainScreenAction.NavigateToSettings)
            }
        }.exhaustive
    }
}

sealed class MainScreenAction {
    object NavigateToCategory : MainScreenAction()
    object NavigateToRandomRecipe : MainScreenAction()
    object NavigateToFavourites : MainScreenAction()
    object NavigateToSettings : MainScreenAction()
    object NavigateToFeedback : MainScreenAction()
}

sealed class MainScreenEvent {
    object OnCategoryClick : MainScreenEvent()
    object OnRandomRecipeClick : MainScreenEvent()
    object OnFavouritesClick : MainScreenEvent()
    object OnSettingsClick : MainScreenEvent()
    object OnFeedbackClick : MainScreenEvent()
}

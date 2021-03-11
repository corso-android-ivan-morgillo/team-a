package com.ivanmorgillo.corsoandroid.teama

import android.content.res.Configuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teama.crashlytics.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.settings.SettingsRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: SettingsRepository, private val tracking: Tracking) : ViewModel() {
    val actions = SingleLiveEvent<MainScreenAction>()

    @Suppress("IMPLICIT_CAST_TO_ANY")
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
            MainScreenEvent.OnInitTheme -> {
                viewModelScope.launch {
                    val darkEnabled = repository.isDarkThemeEnabled()
                    actions.postValue(MainScreenAction.ChangeTheme(darkEnabled))
                }
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
    data class ChangeTheme(val darkEnabled: Boolean) : MainScreenAction()
}

sealed class MainScreenEvent {
    object OnCategoryClick : MainScreenEvent()
    object OnRandomRecipeClick : MainScreenEvent()
    object OnFavouritesClick : MainScreenEvent()
    object OnSettingsClick : MainScreenEvent()
    object OnFeedbackClick : MainScreenEvent()
    object OnInitTheme : MainScreenEvent()
}

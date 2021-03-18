package com.ivanmorgillo.corsoandroid.teama

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ateam.delicious.domain.repository.SettingsRepository
import com.google.firebase.auth.FirebaseUser
import com.ivanmorgillo.corsoandroid.teama.crashlytics.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import kotlinx.coroutines.launch

class MainViewModel(private val repository: SettingsRepository, private val tracking: Tracking) : ViewModel() {
    val states = MutableLiveData<MainScreenStates>()
    val actions = SingleLiveEvent<MainScreenAction>()

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun send(event: MainScreenEvent) {
        when (event) {
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
            MainScreenEvent.OnLogin -> actions.postValue(MainScreenAction.ShowLoginDialog)
            MainScreenEvent.OnLogout -> actions.postValue(MainScreenAction.ShowLogout)
            MainScreenEvent.OnLoginFailed -> {
                tracking.logEvent("user_login_failed")
                states.postValue(MainScreenStates.LoginFailure)
            }
            is MainScreenEvent.OnLoginSuccessful -> {
                tracking.logEvent("user_login_successful")
                states.postValue(MainScreenStates.LoggedIn(event.user))
            }
            MainScreenEvent.OnLogoutFailed -> {
                tracking.logEvent("user_logout_failed")
                states.postValue(MainScreenStates.LogoutFailure)
            }
            MainScreenEvent.OnLogoutSuccessful -> {
                tracking.logEvent("user_logout_successful")
                states.postValue(MainScreenStates.LoggedOut)
            }
        }.exhaustive
    }
}

sealed class MainScreenStates {
    data class LoggedIn(val user: FirebaseUser?) : MainScreenStates()
    object LoginFailure : MainScreenStates()
    object LoggedOut : MainScreenStates()
    object LogoutFailure : MainScreenStates()
}

sealed class MainScreenAction {
    object NavigateToCategory : MainScreenAction()
    object NavigateToRandomRecipe : MainScreenAction()
    object NavigateToFavourites : MainScreenAction()
    object NavigateToSettings : MainScreenAction()
    object NavigateToFeedback : MainScreenAction()
    object ShowLoginDialog : MainScreenAction()
    object ShowLogout : MainScreenAction()

    data class ChangeTheme(val darkEnabled: Boolean) : MainScreenAction()
}

sealed class MainScreenEvent {
    object OnCategoryClick : MainScreenEvent()
    object OnRandomRecipeClick : MainScreenEvent()
    object OnFavouritesClick : MainScreenEvent()
    object OnSettingsClick : MainScreenEvent()
    object OnFeedbackClick : MainScreenEvent()
    object OnInitTheme : MainScreenEvent()
    object OnLogin : MainScreenEvent()
    object OnLogout : MainScreenEvent()
    data class OnLoginSuccessful(val user: FirebaseUser?) : MainScreenEvent()
    object OnLoginFailed : MainScreenEvent()
    object OnLogoutSuccessful : MainScreenEvent()
    object OnLogoutFailed : MainScreenEvent()
}

package com.ivanmorgillo.corsoandroid.teama

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ateam.delicious.domain.repository.AuthenticationManager
import com.ateam.delicious.domain.repository.SettingsRepository
import com.google.firebase.auth.FirebaseUser
import com.ivanmorgillo.corsoandroid.teama.crashlytics.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: SettingsRepository,
    private val tracking: Tracking,
    private val authManager: AuthenticationManager
) : ViewModel() {
    val states = MutableLiveData<MainScreenStates>()
    val actions = SingleLiveEvent<MainScreenAction>()

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun send(event: MainScreenEvent) {
        when (event) {
            MainScreenEvent.OnHomeClick -> {
                tracking.logEvent("category_menu_clicked")
                actions.postValue(MainScreenAction.NavigateToHome)
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
            MainScreenEvent.OnInitTheme -> onInitTheme()
            MainScreenEvent.OnLogin -> actions.postValue(MainScreenAction.ShowLoginDialog)
            MainScreenEvent.OnLogout -> actions.postValue(MainScreenAction.ShowLogout)
            MainScreenEvent.OnLoginFailed -> {
                tracking.logEvent("user_login_failed")
                states.postValue(MainScreenStates.LoginFailure)
            }
            is MainScreenEvent.OnLoginSuccessful -> {
                tracking.logEvent("user_login_successful")
                onLoginSuccessful(event.user)
            }
            MainScreenEvent.OnLogoutFailed -> {
                tracking.logEvent("user_logout_failed")
                states.postValue(MainScreenStates.LogoutFailure)
            }
            MainScreenEvent.OnLogoutSuccessful -> {
                tracking.logEvent("user_logout_successful")
                onLogoutSuccessful()
            }
            MainScreenEvent.OnInitUser -> onInitUser()
            MainScreenEvent.OnShoppingListClick -> {
                tracking.logEvent("shopping_list_clicked")
                actions.postValue(MainScreenAction.NavigateToShoppingList)
            }
        }.exhaustive
    }

    private fun onInitUser() {
        actions.postValue(MainScreenAction.UserLogin(authManager.getUser()))
    }

    private fun onLogoutSuccessful() {
        states.postValue(MainScreenStates.LoggedOut)
    }

    private fun onLoginSuccessful(user: FirebaseUser?) {
        states.postValue(MainScreenStates.LoggedIn(user))
    }

    private fun onInitTheme() {
        viewModelScope.launch {
            val darkEnabled = repository.isDarkThemeEnabled()
            actions.postValue(MainScreenAction.ChangeTheme(darkEnabled))
        }
    }
}

sealed class MainScreenStates {
    data class LoggedIn(val user: FirebaseUser?) : MainScreenStates()
    object LoginFailure : MainScreenStates()
    object LoggedOut : MainScreenStates()
    object LogoutFailure : MainScreenStates()
}

sealed class MainScreenAction {
    object NavigateToRandomRecipe : MainScreenAction()
    object NavigateToFavourites : MainScreenAction()
    object NavigateToSettings : MainScreenAction()
    object NavigateToFeedback : MainScreenAction()
    object ShowLoginDialog : MainScreenAction()
    object ShowLogout : MainScreenAction()
    object NavigateToHome : MainScreenAction()
    object NavigateToShoppingList : MainScreenAction()

    data class UserLogin(val user: FirebaseUser?) : MainScreenAction()

    data class ChangeTheme(val darkEnabled: Boolean) : MainScreenAction()
}

sealed class MainScreenEvent {
    object OnRandomRecipeClick : MainScreenEvent()
    object OnFavouritesClick : MainScreenEvent()
    object OnSettingsClick : MainScreenEvent()
    object OnFeedbackClick : MainScreenEvent()
    object OnInitTheme : MainScreenEvent()
    object OnInitUser : MainScreenEvent()
    object OnLogin : MainScreenEvent()
    object OnLogout : MainScreenEvent()
    data class OnLoginSuccessful(val user: FirebaseUser?) : MainScreenEvent()
    object OnLoginFailed : MainScreenEvent()
    object OnLogoutSuccessful : MainScreenEvent()
    object OnLogoutFailed : MainScreenEvent()
    object OnHomeClick : MainScreenEvent()
    object OnShoppingListClick : MainScreenEvent()
}

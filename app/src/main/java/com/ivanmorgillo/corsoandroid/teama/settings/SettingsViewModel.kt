package com.ivanmorgillo.corsoandroid.teama.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teama.Tracking
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository:SettingsRepository, private val tracking:Tracking): ViewModel() {

    val states = MutableLiveData<SettingsScreenStates>()

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun send(event: SettingsScreenEvent){
        when(event){
            is SettingsScreenEvent.OnDarkThemeSwitch -> {
                tracking.logEvent("settings_dark_theme_switch")
                onDarkThemeSwitch(event) }

            SettingsScreenEvent.OnReady -> {
                viewModelScope.launch {
                    val darkThemeEnabled = repository.isDarkThemeEnabled()
                    val content = SettingsScreenStates.Content(darkThemeEnabled)
                    states.postValue(content)
                }
            }
        }.exhaustive
    }


    private fun onDarkThemeSwitch(event: SettingsScreenEvent.OnDarkThemeSwitch) {
      val darkThemeOn = event.enabled
        viewModelScope.launch {
            repository.setDarkTheme(darkThemeOn)
        }
        val currentState = states.value
        if(currentState!=null && currentState is SettingsScreenStates.Content){
            val updatedState = currentState.copy(darkThemeEnabled=darkThemeOn)
            states.postValue(updatedState)
        }

    }

}

sealed class SettingsScreenEvent{
    data class OnDarkThemeSwitch(val enabled: Boolean): SettingsScreenEvent()
    object OnReady : SettingsScreenEvent()
}

sealed class SettingsScreenStates{
    object Loading : SettingsScreenStates()
    object Error : SettingsScreenStates()
    data class Content(val darkThemeEnabled: Boolean) : SettingsScreenStates()
}

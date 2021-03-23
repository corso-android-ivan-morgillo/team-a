package com.ivanmorgillo.corsoandroid.teama.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ateam.delicious.domain.error.LoadAreaError
import com.ateam.delicious.domain.repository.AreaRepository
import com.ateam.delicious.domain.result.LoadAreaResult
import com.ivanmorgillo.corsoandroid.teama.Screens
import com.ivanmorgillo.corsoandroid.teama.Tracking
import com.ivanmorgillo.corsoandroid.teama.crashlytics.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import kotlinx.coroutines.launch

class AreaViewModel(private val repository: AreaRepository, private val tracking: Tracking) : ViewModel() {
    val states = MutableLiveData<AreaScreenStates>()
    val actions = SingleLiveEvent<AreaScreenAction>()

    init {
        tracking.logScreen(Screens.Area)
    }

    fun send(event: AreaScreenEvent) {
        when (event) {
            AreaScreenEvent.OnReady -> loadContent(false)
            is AreaScreenEvent.OnAreaClick -> {
                tracking.logEvent("area_clicked")
                onAreaClick(event)
            }
            AreaScreenEvent.OnRefresh -> {
                tracking.logEvent("area _refresh_clicked")
                loadContent(true)
            }
        }.exhaustive
    }

    private fun loadContent(forced: Boolean) {
        states.postValue(AreaScreenStates.Loading)
        viewModelScope.launch {
            val result = repository.loadAreas(forced)
            when (result) {
                is LoadAreaResult.Failure -> onFailure(result)
                is LoadAreaResult.Success -> onSuccess(result)
            }.exhaustive
        }
    }

    private fun onSuccess(result: LoadAreaResult.Success) {
        val areas = result.areas.map {
            AreaUI(name = it.name)
        }
        states.postValue(AreaScreenStates.Content(areas))
    }

    private fun onAreaClick(event: AreaScreenEvent.OnAreaClick) {
        actions.postValue(AreaScreenAction.NavigateToRecipes(event.area))
    }

    private fun onFailure(result: LoadAreaResult.Failure) {
        states.postValue(AreaScreenStates.Error)
        when (result.error) {
            LoadAreaError.NoInternet -> actions.postValue(AreaScreenAction.ShowNoInternetMessage)
            LoadAreaError.NoAreaFound -> actions.postValue(AreaScreenAction.ShowNoAreaFoundMessage)
            LoadAreaError.ServerError -> actions.postValue(AreaScreenAction.ShowServerErrorMessage)
            LoadAreaError.SlowInternet -> actions.postValue(AreaScreenAction.ShowSlowInternetMessage)
            LoadAreaError.InterruptedRequest -> actions.postValue(AreaScreenAction.ShowInterruptedRequestMessage)
        }.exhaustive
    }

    sealed class AreaScreenAction {
        data class NavigateToRecipes(val area: AreaUI) : AreaScreenAction()
        object ShowNoInternetMessage : AreaScreenAction()
        object ShowSlowInternetMessage : AreaScreenAction()
        object ShowServerErrorMessage : AreaScreenAction()
        object ShowInterruptedRequestMessage : AreaScreenAction()
        object ShowNoAreaFoundMessage : AreaScreenAction()
    }

    sealed class AreaScreenStates {
        object Loading : AreaScreenStates()
        object Error : AreaScreenStates()
        data class Content(val areas: List<AreaUI>) : AreaScreenStates()
    }

    sealed class AreaScreenEvent {
        data class OnAreaClick(val area: AreaUI) : AreaScreenEvent()
        object OnReady : AreaScreenEvent()
        object OnRefresh : AreaScreenEvent()
    }
}

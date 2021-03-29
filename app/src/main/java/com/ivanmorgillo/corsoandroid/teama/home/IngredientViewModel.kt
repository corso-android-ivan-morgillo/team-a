package com.ivanmorgillo.corsoandroid.teama.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IngredientViewModel : ViewModel() {

    val states = MutableLiveData<IngredientScreenEvent>()

    fun send(event: IngredientScreenEvent) {

        when (event) {
            IngredientScreenEvent.OnReady -> TODO()
        }
    }

}


sealed class IngredientScreenEvent {

    object OnReady : IngredientScreenEvent()
}

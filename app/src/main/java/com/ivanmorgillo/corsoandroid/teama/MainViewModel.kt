package com.ivanmorgillo.corsoandroid.teama

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val MAXRANGE = 10

class MainViewModel : ViewModel() {

    fun getRecipes() = recipes

    private val title = "Pizza1"
    private val image = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg"
    private val recipes = (1..MAXRANGE).map {
        RecipeUI(title = title, image = image)
    }

    val states = MutableLiveData<MainScreenStates>()

    fun send(event: MainScreenEvent) {

        when (event) {
            // deve ricevere la lista delle ricette. La view deve ricevere eventi e reagire a stati
            MainScreenEvent.OnReady -> {
//                states.postValue(MainScreenStates.Content(recipes))
                states.postValue(MainScreenStates.Loading)
            }
        }
    }
}

sealed class MainScreenStates {
    // questi oggetti rappresentano la nostra schermata inequivocabilmente
    object Loading : MainScreenStates()
    object Error : MainScreenStates()

    // se la lista cambia dobbiamo usare una 'data class' quindi non usiamo 'object'
    data class Content(val recipes: List<RecipeUI>) : MainScreenStates()
}

sealed class MainScreenEvent {
    object OnReady : MainScreenEvent()
}




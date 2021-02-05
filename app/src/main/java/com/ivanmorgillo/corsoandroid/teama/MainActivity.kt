package com.ivanmorgillo.corsoandroid.teama

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // dobbiamo creare un binding alla UI
        val adapter = RecipesAdapter()
        recipe_list.adapter = adapter
        viewModel.states.observe(this, { state ->
            // riceve l'aggiornamento del nuovo valore
            when (state) {
                is MainScreenStates.Content -> {
                    adapter.setRecipes(state.recipes)
                }
                MainScreenStates.Error -> TODO()
                MainScreenStates.Loading -> TODO()
            }
        })
        viewModel.send(MainScreenEvent.OnReady)
    }
}

data class RecipeUI(
    val title: String,
    val image: String
)

package com.ivanmorgillo.corsoandroid.teama

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
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
                    recipes_list_progressBar.gone()
                    adapter.setRecipes(state.recipes)
                }
                MainScreenStates.Error -> {
                    // non trova le ricette in fase di Loading ad esempio
                    recipes_list_progressBar.gone()
                    Snackbar.make(recipes_list_root, getString(R.string.main_screen_error), Snackbar.LENGTH_SHORT)
                        .show()
                }
                MainScreenStates.Loading -> {
                    recipes_list_progressBar.visible()
                }
            }
        })
        viewModel.send(MainScreenEvent.OnReady)
    }
}

data class RecipeUI(
    val title: String,
    val image: String
)

package com.ivanmorgillo.corsoandroid.teama

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // dobbiamo creare un binding alla UI
        val adapter = RecipesAdapter {
            viewModel.send(MainScreenEvent.OnRecipeClick(it))
        }
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

        viewModel.actions.observe(this, { action ->
            when (action) {
                is MainScreenAction.NavigateToDetail -> {
                    Toast.makeText(this, "Work in progress navigate to detail", Toast.LENGTH_SHORT).show()
                }
                MainScreenAction.ShowNoInternetMessage -> {
                    recipes_list_progressBar.gone()
                    Snackbar.make(recipes_list_root, "No internet connection", Snackbar.LENGTH_LONG).show()
                }
            }.exhaustive
        })
        viewModel.send(MainScreenEvent.OnReady)
        Timber.d("Wow")
    }
}

data class RecipeUI(
    val title: String,
    val image: String
)

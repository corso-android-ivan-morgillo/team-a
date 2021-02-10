package com.ivanmorgillo.corsoandroid.teama

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
                    showNoInternetMessage()
                }
                MainScreenAction.ShowInterruptedRequestMessage -> {
                    Log.d("INTERNET", "ConnectException -- ")
                    Snackbar.make(recipes_list_root, "Disconnected internet while loading", Snackbar.LENGTH_LONG).show()
                }
                MainScreenAction.ShowSlowInternetMessage -> {
                    Log.d("INTERNET", "Internet lento....")
                    Snackbar.make(recipes_list_root, "Slow internet!!", Snackbar.LENGTH_LONG).show()
                }
                MainScreenAction.ShowServerErrorMessage -> {
                    Log.d("INTERNET", "Exception Generica -- ")
                    Snackbar.make(recipes_list_root, "Exception....", Snackbar.LENGTH_LONG).show()
                }
            }.exhaustive
        })
        viewModel.send(MainScreenEvent.OnReady)
        Timber.d("Wow")
    }

    private fun showNoInternetMessage() {
        recipes_list_progressBar.gone()
        Snackbar.make(recipes_list_root, "No internet connection", Snackbar.LENGTH_LONG).show()
        MaterialAlertDialogBuilder(this)
            .setTitle("No internet connection")
            .setMessage("You are not connected to internet")
            .setIcon(R.drawable.ic_wifi_off)
            .setNeutralButton("Retry") { dialogInterface: DialogInterface, i: Int -> }
            .setPositiveButton("Network settings") { dialog, which ->
                startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
            }
            .setCancelable(false)
            .show()
    }
}

data class RecipeUI(
    val title: String,
    val image: String
)

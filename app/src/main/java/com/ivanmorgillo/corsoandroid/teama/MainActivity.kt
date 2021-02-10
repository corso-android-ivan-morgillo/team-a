package com.ivanmorgillo.corsoandroid.teama

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
                    showInterruptedRequestMessage()
                }
                MainScreenAction.ShowSlowInternetMessage -> {
                    showNoInternetMessage()
                }
                MainScreenAction.ShowServerErrorMessage -> {
                    showServerErrorMessage()
                }
            }.exhaustive
        })
        viewModel.send(MainScreenEvent.OnReady)
        Timber.d("Wow")
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.states.value == MainScreenStates.Loading) {
            viewModel.send(MainScreenEvent.OnReady)
        }
        Timber.d("Second wow")
    }

    private fun showServerErrorMessage() {
        recipes_list_progressBar.gone()
        showAlertDialog("Server error",
            "Something went wrong",
            R.drawable.ic_error,
            "Try again",
            { viewModel.send(MainScreenEvent.OnReady) },
            "",
            {}
        )
    }

    private fun showInterruptedRequestMessage() {
        recipes_list_progressBar.gone()
        showAlertDialog("Connection lost",
            "Connection interrupted...",
            R.drawable.ic_wifi_off,
            "Network settings",
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            "Retry",
            { viewModel.send(MainScreenEvent.OnReady) }
        )
    }

    private fun showNoInternetMessage() {
        recipes_list_progressBar.gone()
        showAlertDialog("No internet connection",
            "You are not connected to internet",
            R.drawable.ic_wifi_off,
            "Network settings",
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            "Retry",
            {
                Log.d("INTERNET", "no internet - states ${viewModel.states.value}")
                viewModel.send(MainScreenEvent.OnReady)
            }
        )
    }

    private fun showAlertDialog(
        title: String,
        message: String,
        icon: Int,
        positiveButtonText: String,
        onPositiveButtonClick: () -> Unit,
        neutralButtonText: String,
        onNeutralButtonClick: () -> Unit
    ) {
        recipes_list_progressBar.gone()
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setIcon(icon)
            .setPositiveButton(positiveButtonText) { dialog, which ->
                onPositiveButtonClick()
            }
            .setNeutralButton(neutralButtonText) { dialogInterface: DialogInterface, i: Int ->
                onNeutralButtonClick()
            }
            .setCancelable(false)
            .show()
    }
}

data class RecipeUI(
    val title: String,
    val image: String
)

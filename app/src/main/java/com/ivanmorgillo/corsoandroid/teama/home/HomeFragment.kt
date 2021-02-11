package com.ivanmorgillo.corsoandroid.teama.home

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ivanmorgillo.corsoandroid.teama.MainScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teama.MainScreenAction.ShowInterruptedRequestMessage
import com.ivanmorgillo.corsoandroid.teama.MainScreenAction.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teama.MainScreenAction.ShowServerErrorMessage
import com.ivanmorgillo.corsoandroid.teama.MainScreenAction.ShowSlowInternetMessage
import com.ivanmorgillo.corsoandroid.teama.MainScreenEvent.OnReady
import com.ivanmorgillo.corsoandroid.teama.MainScreenEvent.OnRecipeClick
import com.ivanmorgillo.corsoandroid.teama.MainScreenStates
import com.ivanmorgillo.corsoandroid.teama.MainViewModel
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.RecipesAdapter
import com.ivanmorgillo.corsoandroid.teama.exhaustive
import com.ivanmorgillo.corsoandroid.teama.gone
import com.ivanmorgillo.corsoandroid.teama.visible
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : Fragment() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    // Equivalente alla onCreate di un activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RecipesAdapter {
            viewModel.send(OnRecipeClick(it))
        }
        recipe_list.adapter = adapter
        viewModel.states.observe(viewLifecycleOwner, { state ->
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
        // Questo blocco serve a specificare che per le istruzioni interne il this è "view"
        with(view) {
            viewModel.actions.observe(viewLifecycleOwner, { action ->
                when (action) {
                    is NavigateToDetail -> {
                        val directions = HomeFragmentDirections.actionHomeFragmentToDetailFragment(action.recipe.id)
                        Timber.d("Invio al details RecipeId= ${action.recipe.id}")
                        findNavController().navigate(directions)
                    }
                    ShowNoInternetMessage -> showNoInternetMessage()
                    ShowInterruptedRequestMessage -> showInterruptedRequestMessage()
                    ShowSlowInternetMessage -> showNoInternetMessage()
                    ShowServerErrorMessage -> showServerErrorMessage()
                }.exhaustive
            })
        }
        viewModel.send(OnReady)
        Timber.d("Wow")
    }

    override fun onResume() {
        super.onResume()
        //Log.d("TeamA", viewModel.states.value.toString())
        if (viewModel.states.value == null) {
            viewModel.send(OnReady)
        }
    }

    private fun View.showServerErrorMessage() {
        recipes_list_progressBar.gone()
        showAlertDialog("Server error",
            "Something went wrong",
            R.drawable.ic_error,
            "Try again",
            { viewModel.send(OnReady) },
            "",
            {}
        )
    }

    private fun View.showInterruptedRequestMessage() {
        recipes_list_progressBar.gone()
        showAlertDialog("Connection lost",
            "Connection interrupted...",
            R.drawable.ic_wifi_off,
            "Network settings",
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            "Retry",
            { viewModel.send(OnReady) }
        )
    }

    private fun View.showNoInternetMessage() {
        recipes_list_progressBar.gone()
        showAlertDialog("No internet connection",
            "You are not connected to internet",
            R.drawable.ic_wifi_off,
            "Network settings",
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            "Retry",
            {
                Log.d("INTERNET", "no internet - states ${viewModel.states.value}")
                viewModel.send(OnReady)
            }
        )
    }

    private fun View.showAlertDialog(
        title: String,
        message: String,
        icon: Int,
        positiveButtonText: String,
        onPositiveButtonClick: () -> Unit,
        neutralButtonText: String,
        onNeutralButtonClick: () -> Unit
    ) {
        recipes_list_progressBar.gone()
        MaterialAlertDialogBuilder(this.context)
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

package com.ivanmorgillo.corsoandroid.teama.recipe

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialElevationScale
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.databinding.FragmentRecipeBinding
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.extension.gone
import com.ivanmorgillo.corsoandroid.teama.extension.showAlertDialog
import com.ivanmorgillo.corsoandroid.teama.extension.visible
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeFragmentDirections.Companion.actionRecipeFragmentToDetailFragment
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeScreenAction.ShowInterruptedRequestMessage
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeScreenAction.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeScreenAction.ShowServerErrorMessage
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeScreenAction.ShowSlowInternetMessage
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeScreenEvent.OnReady
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeScreenEvent.OnRecipeClick
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeScreenEvent.OnRefresh
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeScreenStates.Content
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeScreenStates.Error
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeScreenStates.Loading
import com.ivanmorgillo.corsoandroid.teama.utils.Util
import com.ivanmorgillo.corsoandroid.teama.utils.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class RecipeFragment : Fragment(R.layout.fragment_recipe), SearchView.OnQueryTextListener {
    private val viewModel: RecipeViewModel by viewModel()
    private val binding by viewBinding(FragmentRecipeBinding::bind)
    private val args: RecipeFragmentArgs by navArgs()
    private var lastClickedItem: View? = null
    private var categoryName = ""

    // Equivalente alla onCreate di un activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding.recipesRefresh.setOnRefreshListener {
            viewModel.send(OnReady(categoryName))
        }
        val adapter = RecipesAdapter { item, view ->
            lastClickedItem = view
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            viewModel.send(OnRecipeClick(item))
        }
        binding.recipeList.adapter = adapter
        categoryName = args.categoryName
        if (categoryName.isEmpty()) {
            // Torna indietro nella schermata da cui provieni.
            findNavController().popBackStack()
        } else {
            observeStates(adapter)
            // Questo blocco serve a specificare che per le istruzioni interne il this è "view"
            viewModel.actions.observe(viewLifecycleOwner, { action ->
                when (action) {
                    is NavigateToDetail -> {
                        lastClickedItem?.run {
                            val extras = FragmentNavigatorExtras(this to "recipe_transition_item")
                            val directions = actionRecipeFragmentToDetailFragment(action.recipe.id)
                            Timber.d("Invio al details RecipeId= ${action.recipe.id}")
                            findNavController().navigate(directions, extras)
                        }
                    }
                    ShowNoInternetMessage -> showNoInternetMessage()
                    ShowInterruptedRequestMessage -> showInterruptedRequestMessage()
                    ShowSlowInternetMessage -> showNoInternetMessage()
                    ShowServerErrorMessage -> showServerErrorMessage()
                    RecipeScreenAction.ShowNoRecipeFoundMessage -> showNoRecipeFoundMessage()
                }.exhaustive
            })
            viewModel.send(OnReady(categoryName))
        }
    }

    private fun observeStates(adapter: RecipesAdapter) {
        viewModel.states.observe(viewLifecycleOwner, { state ->
            // riceve l'aggiornamento del nuovo valore
            when (state) {
                is Content -> {
                    val recipes = state.recipes
                    adapter.setRecipes(recipes)
                    binding.recipesRefresh.isRefreshing = false
                    if (recipes.isEmpty()) {
                        binding.recipeTextView.visible()
                        binding.recipeList.gone()
                    } else {
                        binding.recipeList.visible()
                        binding.recipeTextView.gone()
                    }
                }
                Error -> binding.recipesRefresh.isRefreshing = false
                Loading -> binding.recipesRefresh.isRefreshing = true
            }
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String): Boolean {
        viewModel.send(RecipeScreenEvent.OnRecipeSearch(query))
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipes_menu, menu)
        val searchMenuItem = menu.findItem(R.id.recipes_search)
        Util().createSearchManager(activity, searchMenuItem, this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.recipes_search) {
            false
        } else super.onOptionsItemSelected(item)
    }

    private fun showServerErrorMessage() {
        binding.recipesRefresh.isRefreshing = false
        binding.root.showAlertDialog(resources.getString(R.string.server_error_title),
            resources.getString(R.string.server_error_message),
            R.drawable.ic_error,
            resources.getString(R.string.retry),
            { viewModel.send(OnRefresh(categoryName)) },
            "",
            {}
        )
    }

    private fun showInterruptedRequestMessage() {
        binding.recipesRefresh.isRefreshing = false
        binding.root.showAlertDialog(resources.getString(R.string.connection_lost_error_title),
            resources.getString(R.string.connection_lost_error_message),
            R.drawable.ic_wifi_off,
            resources.getString(R.string.network_settings),
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            resources.getString(R.string.retry),
            { viewModel.send(OnRefresh(categoryName)) }
        )
    }

    private fun showNoInternetMessage() {
        binding.recipesRefresh.isRefreshing = false
        binding.root.showAlertDialog(resources.getString(R.string.no_internet_error_title),
            resources.getString(R.string.no_internet_error_message),
            R.drawable.ic_wifi_off,
            resources.getString(R.string.network_settings),
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            resources.getString(R.string.retry),
            {
                viewModel.send(OnRefresh(categoryName))
            }
        )
    }

    private fun showNoRecipeFoundMessage() {
        binding.recipesRefresh.isRefreshing = false
        binding.root.showAlertDialog(resources.getString(R.string.no_recipe_found_error_title),
            resources.getString(R.string.no_recipe_found_error_message),
            R.drawable.ic_sad_face,
            resources.getString(R.string.retry),
            { viewModel.send(OnRefresh(categoryName)) },
            "",
            {}
        )
    }
}

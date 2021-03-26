package com.ivanmorgillo.corsoandroid.teama.recipe.area

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
import com.ivanmorgillo.corsoandroid.teama.databinding.FragmentRecipeAreaBinding
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.extension.gone
import com.ivanmorgillo.corsoandroid.teama.extension.showAlertDialog
import com.ivanmorgillo.corsoandroid.teama.extension.visible
import com.ivanmorgillo.corsoandroid.teama.recipe.area.RecipeAreaFragmentDirections.Companion.actionRecipeAreaFragmentToDetailFragment
import com.ivanmorgillo.corsoandroid.teama.utils.Util
import com.ivanmorgillo.corsoandroid.teama.utils.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class RecipeAreaFragment : Fragment(R.layout.fragment_recipe_area), SearchView.OnQueryTextListener {
    private val viewModel: RecipeAreaViewModel by viewModel()
    private val binding by viewBinding(FragmentRecipeAreaBinding::bind)
    private val args: RecipeAreaFragmentArgs by navArgs()
    private var lastClickedItem: View? = null
    private var areaName = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding.recipesAreaRefresh.setOnRefreshListener {
            viewModel.send(RecipeAreaScreenEvent.OnReady(areaName))
        }
        val adapter = RecipesAreaAdapter { item, view ->
            lastClickedItem = view
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            viewModel.send(RecipeAreaScreenEvent.OnRecipeAreaClick(item))
        }
        binding.recipeAreaList.adapter = adapter
        areaName = args.areaName
        if (areaName.isEmpty()) {
            findNavController().popBackStack()
        } else {
            observeStates(adapter)
            viewModel.actions.observe(viewLifecycleOwner, { action ->
                when (action) {
                    is RecipeAreaScreenAction.NavigateToDetail -> {
                        lastClickedItem?.run {
                            val extras = FragmentNavigatorExtras(this to "recipe_transition_item")
                            val directions = actionRecipeAreaFragmentToDetailFragment(action.areaRecipe.id)
                            Timber.d("Invio al details AreaRecipeId= ${action.areaRecipe.id}")
                            findNavController().navigate(directions, extras)
                        }
                    }
                    RecipeAreaScreenAction.ShowInterruptedRequestMessage -> showInterruptedRequestMessage()
                    RecipeAreaScreenAction.ShowNoInternetMessage -> showNoInternetMessage()
                    RecipeAreaScreenAction.ShowNoRecipeFoundMessage -> showNoRecipeFoundMessage()
                    RecipeAreaScreenAction.ShowServerErrorMessage -> showServerErrorMessage()
                    RecipeAreaScreenAction.ShowSlowInternetMessage -> showNoInternetMessage()
                }.exhaustive
            })
            viewModel.send(RecipeAreaScreenEvent.OnReady(areaName))
        }
    }

    private fun observeStates(adapter: RecipesAreaAdapter) {
        viewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                is RecipeAreaScreenStates.Content -> {
                    val areaRecipes = state.areaRecipes
                    adapter.setRecipes(areaRecipes)
                    binding.recipesAreaRefresh.isRefreshing = false
                    if (areaRecipes.isEmpty()) {
                        binding.recipeAreaTextView.visible()
                        binding.recipeAreaList.gone()
                    } else {
                        binding.recipeAreaList.visible()
                        binding.recipeAreaTextView.gone()
                    }
                }
                RecipeAreaScreenStates.Error -> binding.recipesAreaRefresh.isRefreshing = false
                RecipeAreaScreenStates.Loading -> binding.recipesAreaRefresh.isRefreshing = true
            }.exhaustive
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String): Boolean {
        viewModel.send(RecipeAreaScreenEvent.OnRecipeAreaSearch(query))
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipes_area_menu, menu)
        val searchMenuItem = menu.findItem(R.id.recipes_area_search)
        Util().createSearchManager(activity, searchMenuItem, this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.recipes_area_search) {
            false
        } else super.onOptionsItemSelected(item)
    }

    private fun showServerErrorMessage() {
        binding.recipesAreaRefresh.isRefreshing = false
        binding.root.showAlertDialog(resources.getString(R.string.server_error_title),
            resources.getString(R.string.server_error_message),
            R.drawable.ic_error,
            resources.getString(R.string.retry),
            { viewModel.send(RecipeAreaScreenEvent.OnRefresh(areaName)) },
            "",
            {}
        )
    }

    private fun showInterruptedRequestMessage() {
        binding.recipesAreaRefresh.isRefreshing = false
        binding.root.showAlertDialog(resources.getString(R.string.connection_lost_error_title),
            resources.getString(R.string.connection_lost_error_message),
            R.drawable.ic_wifi_off,
            resources.getString(R.string.network_settings),
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            resources.getString(R.string.retry),
            { viewModel.send(RecipeAreaScreenEvent.OnRefresh(areaName)) }
        )
    }

    private fun showNoInternetMessage() {
        binding.recipesAreaRefresh.isRefreshing = false
        binding.root.showAlertDialog(resources.getString(R.string.no_internet_error_title),
            resources.getString(R.string.no_internet_error_message),
            R.drawable.ic_wifi_off,
            resources.getString(R.string.network_settings),
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            resources.getString(R.string.retry),
            {
                viewModel.send(RecipeAreaScreenEvent.OnRefresh(areaName))
            }
        )
    }

    private fun showNoRecipeFoundMessage() {
        binding.recipesAreaRefresh.isRefreshing = false
        binding.root.showAlertDialog(resources.getString(R.string.no_recipe_found_error_title),
            resources.getString(R.string.no_recipe_found_error_message),
            R.drawable.ic_sad_face,
            resources.getString(R.string.retry),
            { viewModel.send(RecipeAreaScreenEvent.OnRefresh(areaName)) },
            "",
            {}
        )
    }
}

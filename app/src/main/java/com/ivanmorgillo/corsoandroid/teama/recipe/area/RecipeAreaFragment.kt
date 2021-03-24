package com.ivanmorgillo.corsoandroid.teama.recipe.area

import android.os.Bundle
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
import com.ivanmorgillo.corsoandroid.teama.extension.visible
import com.ivanmorgillo.corsoandroid.teama.recipe.*
import com.ivanmorgillo.corsoandroid.teama.utils.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class RecipeAreaFragment : Fragment(R.layout.fragment_recipe_area), SearchView.OnQueryTextListener {
    private val viewModel: RecipeAreaViewModel by viewModel()
    private val binding by viewBinding(FragmentRecipeAreaBinding::bind)
    private val args: RecipeFragmentArgs by navArgs()
    private var lastClickedItem: View? = null
    private var areaName = ""

    // Equivalente alla onCreate di un activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding.recipesRefresh.setOnRefreshListener { viewModel.send(RecipeAreaScreenEvent.OnReady(areaName)) }
        val adapter = RecipesAdapter { item, view ->
            lastClickedItem = view
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            viewModel.send(RecipeScreenEvent.OnRecipeClick(item))
        }
        binding.recipeList.adapter = adapter
        areaName = args.areaName
        if (areaName.isEmpty()) {
            findNavController().popBackStack()
        } else {
            viewModel.states.observe(viewLifecycleOwner, { state ->
                // riceve l'aggiornamento del nuovo valore
                when (state) {
                    is RecipeAreaScreenStates.Content -> {
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
                    RecipeAreaScreenStates.Error -> binding.recipesRefresh.isRefreshing = false
                    RecipeAreaScreenStates.Loading -> binding.recipesRefresh.isRefreshing = true
                }
            })
            viewModel.actions.observe(viewLifecycleOwner, { action ->
                when (action) {
                    is RecipeAreaScreenAction.NavigateToDetail -> {
                        lastClickedItem?.run {
                            val extras = FragmentNavigatorExtras(this to "recipe_transition_item")
                            val directions =
                                RecipeAreaFragmentDirections.actionRecipeAreaFragmentToDetailFragment(action.recipe.id)
                            Timber.d("Invio al details RecipeId= ${action.recipe.id}")
                            findNavController().navigate(directions, extras)
                        }
                    }
                    RecipeAreaScreenAction.ShowNoInternetMessage -> showNoInternetMessage()
                    RecipeAreaScreenAction.ShowInterruptedRequestMessage -> showInterruptedRequestMessage()
                    RecipeAreaScreenAction.ShowSlowInternetMessage -> showNoInternetMessage()
                    RecipeAreaScreenAction.ShowServerErrorMessage -> showServerErrorMessage()
                    RecipeAreaScreenAction.ShowNoRecipeFoundMessage -> showNoRecipeFoundMessage()
                }.exhaustive
            })
            viewModel.send(RecipeAreaScreenEvent.OnReady(areaName))
        }
    }


}

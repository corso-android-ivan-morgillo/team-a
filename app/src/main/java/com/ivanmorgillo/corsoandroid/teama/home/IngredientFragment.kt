package com.ivanmorgillo.corsoandroid.teama.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.databinding.FragmentIngredientBinding
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.extension.gone
import com.ivanmorgillo.corsoandroid.teama.extension.visible
import com.ivanmorgillo.corsoandroid.teama.utils.Util
import com.ivanmorgillo.corsoandroid.teama.utils.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class IngredientFragment : Fragment(R.layout.fragment_ingredient), SearchView.OnQueryTextListener {

    val binding by viewBinding(FragmentIngredientBinding::bind)
    val viewModel: IngredientViewModel by viewModel()
    private val recipeByIngredientAdapter = IngredientAdapter { item: RecipeByIngredientUI, _: View ->
        viewModel.send(IngredientScreenEvent.OnRecipeByIngredientClick(item))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.recipeByIngredientList.adapter = recipeByIngredientAdapter

        viewModel.states.observe(viewLifecycleOwner, { state ->

            when (state) {
                is IngredientScreenState.Content -> {
                    binding.ingredientListRoot.visible()
                    binding.containerInfo.gone()
                    val recipes = state.recipes
                    recipeByIngredientAdapter.setRecipesByIngredient(recipes)
                }
            }.exhaustive

        }
        )


        viewModel.actions.observe(viewLifecycleOwner, { action ->
            when (action) {
                is IngredientScreenAction.NavigateToDetail -> {
                    val direction = IngredientFragmentDirections.actionIngredientFragmentToDetailFragment(action.recipeByIngredient.id)
                    findNavController().navigate(direction)
                }
            }.exhaustive
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            viewModel.send(IngredientScreenEvent.OnResearch(query))
        }
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {

        return true
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.ingredient_recipes_menu, menu)
        val searchMenuItem = menu.findItem(R.id.ingredient_recipes_search)
        Util().createSearchManager(activity, searchMenuItem, this)
    }


}

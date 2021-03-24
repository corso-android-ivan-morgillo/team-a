package com.ivanmorgillo.corsoandroid.teama.recipe.area

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialElevationScale
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.databinding.FragmentRecipeAreaBinding
import com.ivanmorgillo.corsoandroid.teama.utils.viewBinding

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
            viewModel.send(OnRecipeAreaClick(item))
        }
        binding.recipeAreaList.adapter = adapter
        areaName = args.areaName
        if (areaName.isEmpty()) {
            findNavController().popBackStack()
        } else {

            viewModel.states.observe(viewLifecycleOwner, { stare ->
                when
            }
            )
        }
        

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        TODO("Not yet implemented")
    }
}

package com.ivanmorgillo.corsoandroid.teama.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.databinding.FragmentAreaBinding
import com.ivanmorgillo.corsoandroid.teama.databinding.FragmentHomeBinding
import com.ivanmorgillo.corsoandroid.teama.utils.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by viewModel()
    private val binding by viewBinding(FragmentHomeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val randomButton = view.findViewById<Button>(R.id.random_button)

        viewModel.actions.observe(viewLifecycleOwner) { action ->
            when (action) {
                HomeScreenAction.NavigateToArea -> {
                    val directions = HomeFragmentDirections.actionHomeFragmentToAreaFragment()
                    findNavController().navigate(directions)
                }
                HomeScreenAction.NavigateToCategory -> {
                    val directions = HomeFragmentDirections.actionHomeFragmentToCategoryFragment()
                    findNavController().navigate(directions)
                }
                HomeScreenAction.NavigateToIngredient -> {
                    val directions = HomeFragmentDirections.actionHomeFragmentToIngredientFragment()
                    findNavController().navigate(directions)
                }
            }
        }

        binding.categoryButton.setOnClickListener {
            viewModel.send(HomeScreenEvent.OnCategoryClick)
        }
        binding.areaButton.setOnClickListener {
            viewModel.send(HomeScreenEvent.OnAreaClick)
        }

        binding.ingredientButton.setOnClickListener {
            viewModel.send(HomeScreenEvent.OnIngredientClick)
        }


    }

}

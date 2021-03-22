package com.ivanmorgillo.corsoandroid.teama.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ivanmorgillo.corsoandroid.teama.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryButton = view.findViewById<Button>(R.id.category_button)
        val areaButton = view.findViewById<Button>(R.id.area_button)
        val ingredientsButton = view.findViewById<Button>(R.id.ingredient_button)
        val randomButton = view.findViewById<Button>(R.id.random_button)

        viewModel.actions.observe(viewLifecycleOwner) { action ->

            when (action) {
                HomeScreenAction.NavigateToArea -> TODO()
                HomeScreenAction.NavigateToCategory -> {
                    val directions = HomeFragmentDirections.actionHomeFragmentToCategoryFragment()
                    findNavController().navigate(directions)
                }
                HomeScreenAction.NavigateToIngredient -> {
                    // bisogna attivare le chiamate di rete e creare le stesse card che abbiamo in category
                }
            }

        }


    }

}

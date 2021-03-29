package com.ivanmorgillo.corsoandroid.teama.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.databinding.FragmentIngredientBinding
import com.ivanmorgillo.corsoandroid.teama.utils.Util
import com.ivanmorgillo.corsoandroid.teama.utils.viewBinding

class IngredientFragment : Fragment(R.layout.fragment_ingredient), SearchView.OnQueryTextListener {

    val binding by viewBinding(FragmentIngredientBinding::bind)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.ingredient_recipes_menu, menu)
        val searchMenuItem = menu.findItem(R.id.ingredient_recipes_search)
        Util().createSearchManager(activity, searchMenuItem, this)
    }


}

package com.ivanmorgillo.corsoandroid.teama.favourite

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.exhaustive
import com.ivanmorgillo.corsoandroid.teama.gone
import com.ivanmorgillo.corsoandroid.teama.visible
import kotlinx.android.synthetic.main.fragment_favourite.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class FavouriteFragment : Fragment(), SearchView.OnQueryTextListener {
    private val viewModel: FavouriteViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true) // necessario per consentire al fragment di avere un menu
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }

    private var favourites: List<FavouriteUI> = emptyList<FavouriteUI>() // necessario salvare qui per la ricerca

    // Equivalente alla onCreate di un activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = FavouriteAdapter({ item, view ->
            viewModel.send(FavouriteScreenEvent.OnFavouriteClick(item))
        }, favourite_list)
        favourite_list.adapter = adapter

        val view = favourites_list_root
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter, context, view))
        itemTouchHelper.attachToRecyclerView(favourite_list)

        viewModel.states.observe(viewLifecycleOwner, { state ->
            // riceve l'aggiornamento del nuovo valore
            when (state) {
                is FavouriteScreenStates.Content -> {
                    favourite_list_progressBar.gone()
                    favourites = state.favourites
                    adapter.setFavourites(favourites)
                }
                FavouriteScreenStates.Error -> {
                    favourite_list_progressBar.gone()
                }
                FavouriteScreenStates.Loading -> favourite_list_progressBar.visible()
            }.exhaustive
        })
        // Questo blocco serve a specificare che per le istruzioni interne il this è "view"
        viewModel.actions.observe(viewLifecycleOwner, { action ->
            when (action) {
                is FavouriteScreenAction.NavigateToDetail -> {
                    val directions =
                        FavouriteFragmentDirections.actionFavouriteFragmentToDetailFragment(action.favourite.id)
                    Timber.d("Invio al details il preferito con id = ${action.favourite.id}")
                    findNavController().navigate(directions)
                }
                FavouriteScreenAction.ShowNoFavouriteFoundMessage -> TODO()
            }.exhaustive
        })
        viewModel.send(FavouriteScreenEvent.OnReady)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String): Boolean {
        val adapter: FavouriteAdapter = favourite_list.adapter as FavouriteAdapter
        val filteredFavouritesList: List<FavouriteUI> = adapter.filter(favourites, query)
        adapter.setFavourites(filteredFavouritesList)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favourites_menu, menu)
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu.findItem(R.id.favourites_search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        searchView.queryHint = resources.getString(R.string.search_favourite_hint)
        searchView.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.favourites_search) {
            false
        } else super.onOptionsItemSelected(item)
    }
}

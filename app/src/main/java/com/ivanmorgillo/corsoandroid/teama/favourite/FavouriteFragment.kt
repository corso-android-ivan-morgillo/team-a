package com.ivanmorgillo.corsoandroid.teama.favourite

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.databinding.FragmentFavouriteBinding
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.extension.gone
import com.ivanmorgillo.corsoandroid.teama.extension.visible
import com.ivanmorgillo.corsoandroid.teama.utils.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class FavouriteFragment : Fragment(R.layout.fragment_favourite), SearchView.OnQueryTextListener {
    private val viewModel: FavouriteViewModel by viewModel()
    private val binding by viewBinding(FragmentFavouriteBinding::bind)

    private var favourites: List<FavouriteUI> = emptyList<FavouriteUI>() // necessario salvare qui per la ricerca

    // Equivalente alla onCreate di un activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true) // necessario per consentire al fragment di avere un menu
        val adapter = FavouriteAdapter { item, view ->
            viewModel.send(FavouriteScreenEvent.OnFavouriteClick(item))
        }
        binding.favouriteList.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(context,
            object : SwipeToDeleteCallback.ItemTouchHelperListener {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
                    viewModel.send(FavouriteScreenEvent.OnFavouriteSwiped(position))
                }
            }))
        itemTouchHelper.attachToRecyclerView(binding.favouriteList)

        viewModel.states.observe(viewLifecycleOwner, { state ->
            // riceve l'aggiornamento del nuovo valore
            when (state) {
                is FavouriteScreenStates.Content -> {
                    binding.favouriteListProgressBar.gone()
                    favourites = state.favourites
                    adapter.setFavourites(favourites)
                    showUndoDeleteSnackbar(state.deletedFavourite) // per rimettere un preferito eliminato
                }
                FavouriteScreenStates.Error -> binding.favouriteListProgressBar.gone()
                FavouriteScreenStates.Loading -> binding.favouriteListProgressBar.visible()
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
                FavouriteScreenAction.ShowNoFavouriteFoundMessage -> {
                    Timber.d("nessun preferito") // nessun preferito salvato
                }
            }.exhaustive
        })
        viewModel.send(FavouriteScreenEvent.OnReady)
    }

    private fun showUndoDeleteSnackbar(deletedFavourite: FavouriteUI?) {
        if (deletedFavourite != null) {
            val snackbar: Snackbar =
                Snackbar.make(binding.root,
                    resources.getString(R.string.favourite_deleted),
                    Snackbar.LENGTH_LONG)
            snackbar.setAction(resources.getString(R.string.undo)) { v ->
                viewModel.send(FavouriteScreenEvent.OnUndoDeleteFavourite(deletedFavourite))
            }
            snackbar.show()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String): Boolean {
        val adapter: FavouriteAdapter = binding.favouriteList.adapter as FavouriteAdapter
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

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
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeScreenEvent
import com.ivanmorgillo.corsoandroid.teama.utils.Util
import com.ivanmorgillo.corsoandroid.teama.utils.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class FavouriteFragment : Fragment(R.layout.fragment_favourite), SearchView.OnQueryTextListener {
    private val viewModel: FavouriteViewModel by viewModel()
    private val binding by viewBinding(FragmentFavouriteBinding::bind)

    // Equivalente alla onCreate di un activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true) // necessario per consentire al fragment di avere un menu
        binding.favouriteRefresh.setOnRefreshListener { viewModel.send(FavouriteScreenEvent.OnRefresh) }
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
                    binding.favouriteRefresh.isRefreshing = false
                    val favourites = state.favourites
                    adapter.setFavourites(favourites)
                    showUndoDeleteSnackbar(state.deletedFavourite) // per rimettere un preferito eliminato
                    if (favourites.isEmpty()) {
                        binding.favouriteTextView.visible()
                        binding.favouriteList.gone()
                    } else {
                        binding.favouriteList.visible()
                        binding.favouriteTextView.gone()
                        showHowToDeleteSnackbar(state.isFavouriteMessageShown)
                    }
                }
                FavouriteScreenStates.Error -> binding.favouriteRefresh.isRefreshing = false
                FavouriteScreenStates.Loading -> binding.favouriteRefresh.isRefreshing = true
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
            snackbar.setAction(resources.getString(R.string.restore)) { v ->
                viewModel.send(FavouriteScreenEvent.OnUndoDeleteFavourite(deletedFavourite))
            }
            snackbar.show()
        }
    }

    private fun showHowToDeleteSnackbar(alreadyShown: Boolean) {
        if (!alreadyShown) {
            val snackbar: Snackbar =
                Snackbar.make(binding.root,
                    getString(R.string.how_delete_favourite_message),
                    Snackbar.LENGTH_LONG)
            snackbar.setAction(getString(R.string.understand)) { v ->
                viewModel.send(FavouriteScreenEvent.OnDeleteMessageRead)
            }
            snackbar.show()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String): Boolean {
        viewModel.send(FavouriteScreenEvent.OnFavouriteSearch(query))
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favourites_menu, menu)
        val searchMenuItem = menu.findItem(R.id.favourites_search)
        Util().createSearchManager(activity, searchMenuItem, this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.favourites_search) {
            false
        } else super.onOptionsItemSelected(item)
    }
}

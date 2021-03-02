package com.ivanmorgillo.corsoandroid.teama.recipe

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.transition.MaterialElevationScale
import com.ivanmorgillo.corsoandroid.teama.MainScreenAction
import com.ivanmorgillo.corsoandroid.teama.MainScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teama.MainScreenAction.ShowInterruptedRequestMessage
import com.ivanmorgillo.corsoandroid.teama.MainScreenAction.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teama.MainScreenAction.ShowServerErrorMessage
import com.ivanmorgillo.corsoandroid.teama.MainScreenAction.ShowSlowInternetMessage
import com.ivanmorgillo.corsoandroid.teama.MainScreenEvent.OnReady
import com.ivanmorgillo.corsoandroid.teama.MainScreenEvent.OnRecipeClick
import com.ivanmorgillo.corsoandroid.teama.MainScreenStates
import com.ivanmorgillo.corsoandroid.teama.MainViewModel
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.exhaustive
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeFragmentDirections.Companion.actionRecipeFragmentToDetailFragment
import com.ivanmorgillo.corsoandroid.teama.showAlertDialog
import kotlinx.android.synthetic.main.fragment_recipe.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class RecipeFragment : Fragment(), SearchView.OnQueryTextListener {
    private val viewModel: MainViewModel by viewModel()
    private val args: RecipeFragmentArgs by navArgs()
    private var lastClickedItem: View? = null
    private var categoryName = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true) // necessario per consentire al fragment di avere un menu
        return inflater.inflate(R.layout.fragment_recipe, container, false)
    }

    private var recipes: List<RecipeUI> = emptyList<RecipeUI>()

    // Equivalente alla onCreate di un activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        val refresh: SwipeRefreshLayout = recipes_refresh // swipe to refresh
        refresh.setOnRefreshListener { viewModel.send(OnReady(categoryName)) }
        val adapter = RecipesAdapter { item, view ->
            lastClickedItem = view
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            viewModel.send(OnRecipeClick(item))
        }
        recipe_list.adapter = adapter
        categoryName = args.categoryName
        if (categoryName.isEmpty()) {
            // Torna indietro nella schermata da cui provieni.
            findNavController().popBackStack()
        } else {
            viewModel.states.observe(viewLifecycleOwner, { state ->
                // riceve l'aggiornamento del nuovo valore
                when (state) {
                    is MainScreenStates.Content -> {

                        recipes = state.recipes
                        adapter.setRecipes(recipes)
                        refresh.isRefreshing = false
                    }
                    MainScreenStates.Error -> {

                        refresh.isRefreshing = false
                    }
                    MainScreenStates.Loading -> {
                        refresh.isRefreshing = true
                    }
                }
            })
            // Questo blocco serve a specificare che per le istruzioni interne il this è "view"
            viewModel.actions.observe(viewLifecycleOwner, { action ->
                when (action) {
                    is NavigateToDetail -> {
                        lastClickedItem?.run {
                            val extras = FragmentNavigatorExtras(this to "recipe_transition_item")
                            val directions = actionRecipeFragmentToDetailFragment(action.recipe.id)
                            Timber.d("Invio al details RecipeId= ${action.recipe.id}")
                            findNavController().navigate(directions, extras)
                        }
                    }
                    ShowNoInternetMessage -> showNoInternetMessage(view)
                    ShowInterruptedRequestMessage -> showInterruptedRequestMessage(view)
                    ShowSlowInternetMessage -> showNoInternetMessage(view)
                    ShowServerErrorMessage -> showServerErrorMessage(view)
                    MainScreenAction.ShowNoRecipeFoundMessage -> showNoRecipeFoundMessage(view)
                }.exhaustive
            })
            viewModel.send(OnReady(categoryName))
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String): Boolean {
        val adapter: RecipesAdapter = recipe_list.adapter as RecipesAdapter
        val filteredRecipesList: List<RecipeUI> = adapter.filter(recipes, query)
        adapter.setRecipes(filteredRecipesList)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipes_menu, menu)
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu.findItem(R.id.recipes_search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        searchView.queryHint = resources.getString(R.string.search_recipe_hint)
        searchView.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.recipes_search) {
            false
        } else super.onOptionsItemSelected(item)
    }

    private fun showServerErrorMessage(view: View) {
        recipes_refresh.isRefreshing = false
        view.showAlertDialog(resources.getString(R.string.server_error_title),
            resources.getString(R.string.server_error_message),
            R.drawable.ic_error,
            resources.getString(R.string.retry),
            { viewModel.send(OnReady(categoryName)) },
            "",
            {}
        )
    }

    private fun showInterruptedRequestMessage(view: View) {
        recipes_refresh.isRefreshing = false
        view.showAlertDialog(resources.getString(R.string.connection_lost_error_title),
            resources.getString(R.string.connection_lost_error_message),
            R.drawable.ic_wifi_off,
            resources.getString(R.string.network_settings),
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            resources.getString(R.string.retry),
            { viewModel.send(OnReady(categoryName)) }
        )
    }

    private fun showNoInternetMessage(view: View) {
        recipes_refresh.isRefreshing = false
        view.showAlertDialog(resources.getString(R.string.no_internet_error_title),
            resources.getString(R.string.no_internet_error_message),
            R.drawable.ic_wifi_off,
            resources.getString(R.string.network_settings),
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            resources.getString(R.string.retry),
            {
                viewModel.send(OnReady(categoryName))
            }
        )
    }

    private fun showNoRecipeFoundMessage(view: View) {
        recipes_refresh.isRefreshing = false
        view.showAlertDialog(resources.getString(R.string.no_recipe_found_error_title),
            resources.getString(R.string.no_recipe_found_error_message),
            R.drawable.ic_sad_face,
            resources.getString(R.string.retry),
            { viewModel.send(OnReady(categoryName)) },
            "",
            {}
        )
    }
}

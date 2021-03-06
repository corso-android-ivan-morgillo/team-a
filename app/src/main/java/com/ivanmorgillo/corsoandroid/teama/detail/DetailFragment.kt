package com.ivanmorgillo.corsoandroid.teama.detail

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import com.ivanmorgillo.corsoandroid.teama.GoogleLoginRequest
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.databinding.FragmentDetailBinding
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.extension.gone
import com.ivanmorgillo.corsoandroid.teama.extension.showAlertDialog
import com.ivanmorgillo.corsoandroid.teama.extension.themeColor
import com.ivanmorgillo.corsoandroid.teama.extension.visible
import com.ivanmorgillo.corsoandroid.teama.utils.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : Fragment(R.layout.fragment_detail) {
    private val viewModel: DetailViewModel by viewModel()
    private val binding by viewBinding(FragmentDetailBinding::bind)
    private val args: DetailFragmentArgs by navArgs()
    private var favouriteToolbarButton: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
        setHasOptionsMenu(true) // necessario per consentire al fragment di avere un menu
    }

    // Equivalente alla onCreate di un activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // La nostra recycler view dovrà accettare in ingresso l'oggetto che conterrà i dettagli.
        val adapter = DetailScreenAdapter(
            { viewModel.send(DetailScreenEvent.OnIngredientsClick) },
            { viewModel.send(DetailScreenEvent.OnInstructionsClick) }
        )
        binding.detailScreenRecyclerview.adapter = adapter
        var recipeId = args.recipeId // ID ottenuto dalla direction da un altro fragment (es: recipeFragment)
        if (arguments != null) { // ID ottenuto dal bundle, cioè dalla MainActivity (menu laterale)
            recipeId = arguments!!.getLong("recipe_id")
        }
        if (recipeId == 0L) {
            // Torna indietro nella schermata da cui provieni.
            findNavController().popBackStack()
        } else {
            viewModel.states.observe(viewLifecycleOwner, { state ->
                when (state) {
                    is DetailScreenStates.Content -> {
                        val isFavourite = state.isFavourite
                        renderToolbarMenu(isFavourite)
                        adapter.items = state.details
                        binding.detailsListProgressBar.gone()
                    } // non trova le ricette in fase di Loading ad esempio
                    DetailScreenStates.Error -> binding.detailsListProgressBar.gone()
                    DetailScreenStates.Loading -> binding.detailsListProgressBar.visible()
                }.exhaustive
            })
            viewModel.actions.observe(viewLifecycleOwner, { action ->
                when (action) {
                    DetailScreenAction.ShowNoInternetMessage -> showNoInternetMessage(recipeId)
                    DetailScreenAction.ShowInterruptedRequestMessage -> showInterruptedRequestMessage(recipeId)
                    DetailScreenAction.ShowSlowInternetMessage -> showNoInternetMessage(recipeId)
                    DetailScreenAction.ShowServerErrorMessage -> showServerErrorMessage(recipeId)
                    DetailScreenAction.ShowNoRecipeDetailFoundMessage -> showNoRecipeDetailFoundMessage(recipeId)
                    DetailScreenAction.ShowLoginMessage -> onShowLoginMessage()
                    DetailScreenAction.RequestGoogleLogin -> (activity as GoogleLoginRequest).onGoogleLogin()
                }.exhaustive
            })
            viewModel.send(DetailScreenEvent.OnReady(recipeId))
        }
    }

    private fun onShowLoginMessage() {
        binding.root.showAlertDialog(getString(R.string.required_login_title),
            getString(R.string.required_login_message),
            R.drawable.ic_account,
            getString(R.string.login),
            { viewModel.send(DetailScreenEvent.OnGoogleLogin) },
            getString(R.string.cancel),
            {}
        )
    }

    private fun renderToolbarMenu(isFavourite: Boolean) {
        if (favouriteToolbarButton != null) { // mostra il menu ora che il Content è arrivato
            if (isFavourite) {
                favouriteToolbarButton?.setIcon(R.drawable.ic_favourite_filled)
            } else {
                favouriteToolbarButton?.setIcon(R.drawable.ic_favourite)
            }
            favouriteToolbarButton!!.visible()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_menu, menu)
        // settato in XML la visibilità a false, per non mostrare il menu subito
        favouriteToolbarButton = menu.findItem(R.id.favourite_button)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.favourite_button) {
            viewModel.send(DetailScreenEvent.OnAddFavouriteClick)
            false
        } else super.onOptionsItemSelected(item)
    }

    private fun showServerErrorMessage(recipeId: Long) {
        binding.detailsListProgressBar.gone()
        binding.root.showAlertDialog(resources.getString(R.string.server_error_title),
            resources.getString(R.string.server_error_message),
            R.drawable.ic_error,
            resources.getString(R.string.retry),
            { viewModel.send(DetailScreenEvent.OnReady(recipeId)) },
            "",
            {}
        )
    }

    private fun showInterruptedRequestMessage(recipeId: Long) {
        binding.detailsListProgressBar.gone()
        binding.root.showAlertDialog(resources.getString(R.string.connection_lost_error_title),
            resources.getString(R.string.connection_lost_error_message),
            R.drawable.ic_wifi_off,
            resources.getString(R.string.network_settings),
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            resources.getString(R.string.retry),
            { viewModel.send(DetailScreenEvent.OnReady(recipeId)) }
        )
    }

    private fun showNoInternetMessage(recipeId: Long) {
        binding.detailsListProgressBar.gone()
        binding.root.showAlertDialog(resources.getString(R.string.no_internet_error_title),
            resources.getString(R.string.no_internet_error_message),
            R.drawable.ic_wifi_off,
            resources.getString(R.string.network_settings),
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            resources.getString(R.string.retry),
            { viewModel.send(DetailScreenEvent.OnReady(recipeId)) }
        )
    }

    private fun showNoRecipeDetailFoundMessage(recipeId: Long) {
        binding.detailsListProgressBar.gone()
        binding.root.showAlertDialog(resources.getString(R.string.no_recipe_detail_found_error_title),
            resources.getString(R.string.no_recipe_detail_found_error_message),
            R.drawable.ic_sad_face,
            resources.getString(R.string.retry),
            { viewModel.send(DetailScreenEvent.OnReady(recipeId)) },
            "",
            {}
        )
    }
}

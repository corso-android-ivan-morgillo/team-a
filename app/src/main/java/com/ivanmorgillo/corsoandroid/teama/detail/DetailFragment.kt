package com.ivanmorgillo.corsoandroid.teama.detail

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.exhaustive
import com.ivanmorgillo.corsoandroid.teama.gone
import com.ivanmorgillo.corsoandroid.teama.showAlertDialog
import com.ivanmorgillo.corsoandroid.teama.themeColor
import com.ivanmorgillo.corsoandroid.teama.visible
import kotlinx.android.synthetic.main.detail_ingredient_instruction.*
import kotlinx.android.synthetic.main.fragment_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : Fragment() {

    private val viewModel: DetailViewModel by viewModel()
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    // Equivalente alla onCreate di un activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // La nostra recycler view dovrà accettare in ingresso l'oggetto che conterrà i dettagli.
        val adapter = DetailScreenAdapter(
            { viewModel.send(DetailScreenEvent.OnIngredientsClick) },
            { viewModel.send(DetailScreenEvent.OnInstructionsClick) }
        )
        detail_screen_recyclerview.adapter = adapter
        val recipeId = args.recipeId
        if (recipeId == 0L) {
            // Torna indietro nella schermata da cui provieni.
            findNavController().popBackStack()
        } else {
            viewModel.states.observe(viewLifecycleOwner, { state ->
                when (state) {
                    is DetailScreenStates.Content -> {
                        details_list_progressBar.gone()
                        // Timber.d("RecipeId= $recipeId")
                        adapter.items = listOf(
                            DetailScreenItems.Image(state.recipes.image),
                            DetailScreenItems.Title(state.recipes.title),
                            DetailScreenItems.TabLayout,
                            DetailScreenItems.IngredientsInstructionsList(
                                state.recipes.ingredients,
                                state.recipes.instructions
                            )
                        )
                    }
                    DetailScreenStates.Error -> {
                        // non trova le ricette in fase di Loading ad esempio
                        details_list_progressBar.gone()
                    }
                    DetailScreenStates.Loading -> {
                        details_list_progressBar.visible()
                    }
                }.exhaustive
            })
            viewModel.actions.observe(viewLifecycleOwner, { action ->
                when (action) {
                    DetailScreenAction.ShowIngredients -> {
                        detail_screen_ingredient_list.visible()
                        detail_screen_instruction.gone()
                    }
                    DetailScreenAction.ShowInstructions -> {
                        detail_screen_instruction.visible()
                        detail_screen_ingredient_list.gone()
                    }
                    DetailScreenAction.ShowNoInternetMessage -> showNoInternetMessage(view, recipeId)
                    DetailScreenAction.ShowInterruptedRequestMessage -> showInterruptedRequestMessage(view, recipeId)
                    DetailScreenAction.ShowSlowInternetMessage -> showNoInternetMessage(view, recipeId)
                    DetailScreenAction.ShowServerErrorMessage -> showServerErrorMessage(view, recipeId)
                    DetailScreenAction.ShowNoRecipeDetailFoundMessage -> showNoRecipeDetailFoundMessage(view, recipeId)
                }.exhaustive
            }
            )
            viewModel.send(DetailScreenEvent.OnReady(recipeId))
        }
    }

    private fun showServerErrorMessage(view: View, recipeId: Long) {
        details_list_progressBar.gone()
        view.showAlertDialog(resources.getString(R.string.server_error_title),
            resources.getString(R.string.server_error_message),
            R.drawable.ic_error,
            resources.getString(R.string.retry),
            { viewModel.send(DetailScreenEvent.OnReady(recipeId)) },
            "",
            {}
        )
    }

    private fun showInterruptedRequestMessage(view: View, recipeId: Long) {
        details_list_progressBar.gone()
        view.showAlertDialog(resources.getString(R.string.connection_lost_error_title),
            resources.getString(R.string.connection_lost_error_message),
            R.drawable.ic_wifi_off,
            resources.getString(R.string.network_settings),
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            resources.getString(R.string.retry),
            { viewModel.send(DetailScreenEvent.OnReady(recipeId)) }
        )
    }

    private fun showNoInternetMessage(view: View, recipeId: Long) {
        details_list_progressBar.gone()
        view.showAlertDialog(resources.getString(R.string.no_internet_error_title),
            resources.getString(R.string.no_internet_error_message),
            R.drawable.ic_wifi_off,
            resources.getString(R.string.network_settings),
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            resources.getString(R.string.retry),
            { viewModel.send(DetailScreenEvent.OnReady(recipeId)) }
        )
    }

    private fun showNoRecipeDetailFoundMessage(view: View, recipeId: Long) {
        details_list_progressBar.gone()
        view.showAlertDialog(resources.getString(R.string.no_recipe_detail_found_error_title),
            resources.getString(R.string.no_recipe_detail_found_error_message),
            R.drawable.ic_sad_face,
            resources.getString(R.string.retry),
            { viewModel.send(DetailScreenEvent.OnReady(recipeId)) },
            "",
            {}
        )
    }
}

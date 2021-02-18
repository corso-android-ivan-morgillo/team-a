package com.ivanmorgillo.corsoandroid.teama.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.exhaustive
import com.ivanmorgillo.corsoandroid.teama.gone
import com.ivanmorgillo.corsoandroid.teama.visible
import kotlinx.android.synthetic.main.detail_ingredient_instruction.*
import kotlinx.android.synthetic.main.fragment_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : Fragment() {

    private val viewModel: DetailViewModel by viewModel()
    private val args: DetailFragmentArgs by navArgs()

    /** Qui ci va la lista da passare all'adapter
    // private val ingredientsList: List<IngredientUI> = loadIngredients() */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_detail, container, false)
        return rootView
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
                // riceve l'aggiornamento del nuovo valore
                when (state) {
                    is DetailScreenStates.Content -> {
                        details_list_progressBar.gone()
                        // Timber.d("RecipeId= $recipeId")
                        adapter.items = listOf(
                            DetailScreenItems.Title(state.recipes.title),
                            DetailScreenItems.Image(state.recipes.image),
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
                }.exhaustive
            }
            )
            viewModel.send(DetailScreenEvent.OnReady(recipeId))
        }
    }
}

package com.ivanmorgillo.corsoandroid.teama.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ivanmorgillo.corsoandroid.teama.DetailScreenEvent
import com.ivanmorgillo.corsoandroid.teama.DetailScreenStates
import com.ivanmorgillo.corsoandroid.teama.DetailViewModel
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.gone
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_home.*
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
        val adapter = DetailScreenAdapter()
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
                        // recipes_list_progressBar.gone() binding con View
                        // Timber.d("RecipeId= $recipeId")
                        adapter.items = listOf(
                            DetailScreenItems.Title(state.recipes.title),
                            DetailScreenItems.Image(state.recipes.image),
                            DetailScreenItems.IngredientsList(state.recipes.ingredients)
                        )
                        Log.d("SHOT", state.recipes.ingredients.toString())
                    }
                    DetailScreenStates.Error -> {
                        // non trova le ricette in fase di Loading ad esempio
                        recipes_list_progressBar.gone()
                    }
                    DetailScreenStates.Loading -> {
                        // recipes_list_progressBar.visible()
                    }
                }
            })
            viewModel.send(DetailScreenEvent.OnReady(recipeId))
        }
    }
}

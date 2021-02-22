package com.ivanmorgillo.corsoandroid.teama.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.gone
import com.ivanmorgillo.corsoandroid.teama.visible
import kotlinx.android.synthetic.main.fragment_category.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoryFragment : Fragment() {
    private val viewModel: CategoryViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = CategoryAdapter { item, _ ->
            viewModel.send(CategoryScreenEvent.OnCategoryClick(item))
        }
        category_list.adapter = adapter
        viewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                is CategoryScreenStates.Content -> {
                    category_list_progressBar.gone()
                    adapter.setCategories(state.categories)
                }
                CategoryScreenStates.Error -> {
                    category_list_progressBar.gone()
                }
                CategoryScreenStates.Loading -> {
                    category_list_progressBar.visible()
                }
            }
        })
        viewModel.actions.observe(viewLifecycleOwner,
            { action ->
                when (action) {
                    is CategoryScreenAction.NavigateToRecipes -> {
                        val directions =
                            CategoryFragmentDirections.actionCategoryFragmentToHomeFragment(action.category.title)
                        findNavController().navigate(directions)
                    }
                    CategoryScreenAction.ShowInterruptedRequestMessage -> TODO()
                    CategoryScreenAction.ShowNoInternetMessage -> TODO()
                    CategoryScreenAction.ShowNoCategoryFoundMessage -> TODO()
                    CategoryScreenAction.ShowServerErrorMessage -> TODO()
                    CategoryScreenAction.ShowSlowInternetMessage -> TODO()
                }
            })
        viewModel.send(CategoryScreenEvent.OnReady)
    }
}

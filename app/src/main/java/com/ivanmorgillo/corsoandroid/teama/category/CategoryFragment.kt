package com.ivanmorgillo.corsoandroid.teama.category

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenAction.NavigateToRecipes
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenAction.ShowInterruptedRequestMessage
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenAction.ShowNoCategoryFoundMessage
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenAction.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenAction.ShowServerErrorMessage
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenAction.ShowSlowInternetMessage
import com.ivanmorgillo.corsoandroid.teama.databinding.FragmentCategoryBinding
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.extension.showAlertDialog
import com.ivanmorgillo.corsoandroid.teama.utils.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoryFragment : Fragment(R.layout.fragment_category) {
    private val viewModel: CategoryViewModel by viewModel()
    private val binding by viewBinding(FragmentCategoryBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.categoryRefresh.setOnRefreshListener { // swipe to refresh
            viewModel.send(CategoryScreenEvent.OnRefresh)
        }
        val categoryCardAdapter = CategoryAdapter { item: CategoryUI, _: View ->
            viewModel.send(CategoryScreenEvent.OnCategoryClick(item))
        }

        binding.categoryList.adapter = categoryCardAdapter

        val fab: FloatingActionButton = binding.fab
        fab.setOnClickListener { view ->
            viewModel.send(CategoryScreenEvent.OnRandomRecipeClick)
        }
        viewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                is CategoryScreenStates.Content -> {
                    categoryCardAdapter.setCategories(state.categories)
                    binding.categoryRefresh.isRefreshing = false
                }
                CategoryScreenStates.Error -> binding.categoryRefresh.isRefreshing = false
                CategoryScreenStates.Loading -> binding.categoryRefresh.isRefreshing = true
            }
        })
        viewModel.actions.observe(viewLifecycleOwner,
            { action ->
                when (action) {
                    is NavigateToRecipes -> {
                        val directions =
                            CategoryFragmentDirections.actionCategoryFragmentToRecipeFragment(action.category.title)
                        findNavController().navigate(directions)
                    }
                    CategoryScreenAction.NavigateToRandomRecipe -> {
                        Toast.makeText(context, getString(R.string.loading_random_recipe), Toast.LENGTH_SHORT).show()
                        val directions = CategoryFragmentDirections.actionCategoryFragmentToDetailFragment(-1L)
                        findNavController().navigate(directions)
                    }
                    ShowNoInternetMessage -> showNoInternetMessage()
                    ShowInterruptedRequestMessage -> showInterruptedRequestMessage()
                    ShowSlowInternetMessage -> showNoInternetMessage()
                    ShowServerErrorMessage -> showServerErrorMessage()
                    ShowNoCategoryFoundMessage -> showNoCategoryFoundMessage()
                }.exhaustive
            })

        // viewModel.send(CategoryScreenEvent.OnReady)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.states.value == null) {
            viewModel.send(CategoryScreenEvent.OnReady)
        }
    }

    private fun showServerErrorMessage() {
        binding.categoryRefresh.isRefreshing = false
        binding.root.showAlertDialog(resources.getString(R.string.server_error_title),
            resources.getString(R.string.server_error_message),
            R.drawable.ic_error,
            resources.getString(R.string.retry),
            { viewModel.send(CategoryScreenEvent.OnRefresh) },
            "",
            {}
        )
    }

    private fun showInterruptedRequestMessage() {
        binding.categoryRefresh.isRefreshing = false
        binding.root.showAlertDialog(resources.getString(R.string.connection_lost_error_title),
            resources.getString(R.string.connection_lost_error_message),
            R.drawable.ic_wifi_off,
            resources.getString(R.string.network_settings),
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            resources.getString(R.string.retry),
            { viewModel.send(CategoryScreenEvent.OnRefresh) }
        )
    }

    private fun showNoInternetMessage() {
        binding.categoryRefresh.isRefreshing = false
        binding.root.showAlertDialog(resources.getString(R.string.no_internet_error_title),
            resources.getString(R.string.no_internet_error_message),
            R.drawable.ic_wifi_off,
            resources.getString(R.string.network_settings),
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            resources.getString(R.string.retry),
            { viewModel.send(CategoryScreenEvent.OnRefresh) }
        )
    }

    private fun showNoCategoryFoundMessage() {
        binding.categoryRefresh.isRefreshing = false
        binding.root.showAlertDialog(resources.getString(R.string.no_category_found_error_title),
            resources.getString(R.string.no_category_found_error_message),
            R.drawable.ic_sad_face,
            resources.getString(R.string.retry),
            { viewModel.send(CategoryScreenEvent.OnRefresh) },
            "",
            {}
        )
    }
}

package com.ivanmorgillo.corsoandroid.teama.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.category.AreaScreenEvent
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive

class AreaFragment : Fragment() {
    private val viewModel: AreaViewModel by viewModel()
    private val binding: by viewBinding(AreaFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.areaRefresh.setOnRefreshListener {
            viewModel.send(AreaViewModel.AreaScreenEvent.OnRefresh)
        }
        val areaCardAdapter = AreaAdapter { item: AreaUI, _: View ->
            viewModel.send(AreaViewModel.AreaScreenEvent.OnAreaClick(item))
        }

        binding.areaList.adapter = areaCardAdapter

        viewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                is AreaViewModel.AreaScreenStates.Content -> {
                    val areas = state.areas
                    areaCardAdapter.setareas(areas)
                    binding.areaRefresh.isRefreshing = false
                    if (areas.isEmpty()) {
                        binding.areaTextView.visible()
                        binding.areaList.gone()
                    } else {
                        binding.areaList.visible()
                        binding.areaTextView.gone()
                    }
                    binding.areaRefresh.isRefreshing = false
                }
                AreaViewModel.AreaScreenStates.Error -> binding.areaRefresh.isRefreshing = false
                AreaViewModel.AreaScreenStates.Loading -> binding.areaRefresh.isRefreshing = true
            }
        })
        viewModel.actions.observe(viewLifecycleOwner,
            { action ->
                when (action) {
                    is AreaViewModel.AreaScreenAction.NavigateToRecipes -> {
                        val directions =
                            AreaFragmentDirections.actionAreaFragmentToRecipeFragment(action.area.name)
                        findNavController().navigate(directions)
                    }
                    AreaViewModel.AreaScreenAction.ShowNoInternetMessage -> showNoInternetMessage()
                    AreaViewModel.AreaScreenAction.ShowInterruptedRequestMessage -> showInterruptedRequestMessage()
                    AreaViewModel.AreaScreenAction.ShowSlowInternetMessage -> showNoInternetMessage()
                    AreaViewModel.AreaScreenAction.ShowServerErrorMessage -> showServerErrorMessage()
                    AreaViewModel.AreaScreenAction.ShowNoAreaFoundMessage -> showNoAreaFoundMessage()
                }.exhaustive
            })
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.states.value == null) {
            viewModel.send(AreaScreenEvent.OnReady)
        }
    }

    private fun showServerErrorMessage() {
        binding.areaRefresh.isRefreshing = false
        binding.root.showAlertDialog(resources.getString(R.string.server_error_title),
            resources.getString(R.string.server_error_message),
            R.drawable.ic_error,
            resources.getString(R.string.retry),
            { viewModel.send(AreaScreenEvent.OnRefresh) },
            "",
            {}
        )
    }

    private fun showInterruptedRequestMessage() {
        binding.areaRefresh.isRefreshing = false
        binding.root.showAlertDialog(resources.getString(R.string.connection_lost_error_title),
            resources.getString(R.string.connection_lost_error_message),
            R.drawable.ic_wifi_off,
            resources.getString(R.string.network_settings),
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            resources.getString(R.string.retry),
            { viewModel.send(AreaScreenEvent.OnRefresh) }
        )
    }

    private fun showNoInternetMessage() {
        binding.areaRefresh.isRefreshing = false
        binding.root.showAlertDialog(resources.getString(R.string.no_internet_error_title),
            resources.getString(R.string.no_internet_error_message),
            R.drawable.ic_wifi_off,
            resources.getString(R.string.network_settings),
            { startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) },
            resources.getString(R.string.retry),
            { viewModel.send(AreaScreenEvent.OnRefresh) }
        )
    }

    private fun showNoAreaFoundMessage() {
        binding.areaRefresh.isRefreshing = false
        binding.root.showAlertDialog(resources.getString(R.string.no_area_found_error_title),
            resources.getString(R.string.no_area_found_error_message),
            R.drawable.ic_sad_face,
            resources.getString(R.string.retry),
            { viewModel.send(AreaScreenEvent.OnRefresh) },
            "",
            {}
        )
    }

}

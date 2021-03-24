package com.ivanmorgillo.corsoandroid.teama.area

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.databinding.FragmentAreaBinding
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.extension.gone
import com.ivanmorgillo.corsoandroid.teama.extension.showAlertDialog
import com.ivanmorgillo.corsoandroid.teama.extension.visible
import com.ivanmorgillo.corsoandroid.teama.utils.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AreaFragment : Fragment(R.layout.fragment_area) {
    private val viewModel: AreaViewModel by viewModel()
    private val binding by viewBinding(FragmentAreaBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.areaRefresh.setOnRefreshListener {
            viewModel.send(AreaScreenEvent.OnRefresh)
        }
        val areaCardAdapter = AreaAdapter { item: AreaUI, _: View ->
            viewModel.send(AreaScreenEvent.OnAreaClick(item))
        }

        binding.areaList.adapter = areaCardAdapter

        viewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                is AreaScreenStates.Content -> {
                    val areas = state.areas
                    areaCardAdapter.setAreas(areas)
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
                AreaScreenStates.Error -> binding.areaRefresh.isRefreshing = false
                AreaScreenStates.Loading -> binding.areaRefresh.isRefreshing = true
            }
        })
        viewModel.actions.observe(viewLifecycleOwner,
            { action ->
                when (action) {
                    is AreaScreenAction.NavigateToRecipes -> {
                        val directions =
                            AreaFragmentDirections.actionAreaFragmentToRecipeFragment(action.area.name)
                        findNavController().navigate(directions)
                    }
                    AreaScreenAction.ShowNoInternetMessage -> showNoInternetMessage()
                    AreaScreenAction.ShowInterruptedRequestMessage -> showInterruptedRequestMessage()
                    AreaScreenAction.ShowSlowInternetMessage -> showNoInternetMessage()
                    AreaScreenAction.ShowServerErrorMessage -> showServerErrorMessage()
                    AreaScreenAction.ShowNoAreaFoundMessage -> showNoAreaFoundMessage()
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

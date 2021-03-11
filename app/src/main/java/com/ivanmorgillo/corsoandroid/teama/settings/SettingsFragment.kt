package com.ivanmorgillo.corsoandroid.teama.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.fragment.app.Fragment
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.databinding.FragmentSettingsBinding
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.utils.viewBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val viewModel: SettingsViewModel by viewModel()
    private val binding by viewBinding(FragmentSettingsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeListeners()
        viewModel.states.observe(viewLifecycleOwner, {state ->
            when(state) {
                is SettingsScreenStates.Content -> {
                    onDarkThemeSwitch(state.darkThemeEnabled)
                }
                SettingsScreenStates.Error -> TODO()
                SettingsScreenStates.Loading -> TODO()
            }.exhaustive
        })
        viewModel.send(SettingsScreenEvent.OnReady)
    }

    private fun initializeListeners() {
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.send(SettingsScreenEvent.OnDarkThemeSwitch(isChecked))
        }
    }


    private fun onDarkThemeSwitch(enabled: Boolean) {
        binding.themeSwitch.isChecked = enabled
        if (enabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}

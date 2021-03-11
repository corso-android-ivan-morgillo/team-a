package com.ivanmorgillo.corsoandroid.teama.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.fragment.app.Fragment
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.databinding.FragmentSettingsBinding
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.extension.showAlertDialog
import com.ivanmorgillo.corsoandroid.teama.utils.viewBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val viewModel: SettingsViewModel by viewModel()
    private val binding by viewBinding(FragmentSettingsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTheme()
        initializeListeners()
        viewModel.states.observe(viewLifecycleOwner, {state ->
            when(state) {
                is SettingsScreenStates.Content -> {
                    onDarkThemeSwitch(state.darkThemeEnabled)
                    when(state.language) {
                        Languages.English -> binding.radioEngLanguage.isChecked = true
                        Languages.Italian -> binding.radioItaLanguage.isChecked = true
                    }.exhaustive
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
        binding.radioItaLanguage.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.send(SettingsScreenEvent.OnLanguageChange(Languages.Italian))
            }
        }
        binding.radioItaLanguage.setOnClickListener { // quando si clicca per cambiare manualmente
            showLanguageChangedDialog()
        }
        binding.radioEngLanguage.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.send(SettingsScreenEvent.OnLanguageChange(Languages.English))
            }
        }
        binding.radioEngLanguage.setOnClickListener { // quando si clicca per cambiare manualmente
            showLanguageChangedDialog()
        }
    }

    private fun showLanguageChangedDialog() {
        binding.root.showAlertDialog(
            title = getString(R.string.language_changed),
            message = getString(R.string.restart_required),
            icon = R.drawable.category_info_button,
            positiveButtonText = getString(R.string.restart),
            onPositiveButtonClick = {
                val intent = activity?.intent
                activity?.finish()
                startActivity(intent)
            },
            neutralButtonText = getString(R.string.not_now),
            onNeutralButtonClick = {}
        )
    }

    private fun initTheme() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES, Configuration.UI_MODE_NIGHT_UNDEFINED -> { // tema notte abilitato o null
                binding.themeSwitch.isChecked = true
            }
            Configuration.UI_MODE_NIGHT_NO -> binding.themeSwitch.isChecked = false
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

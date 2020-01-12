package com.benjaminledet.pokedex.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.benjaminledet.pokedex.R
import com.benjaminledet.pokedex.extensions.getAttributeFromAttr
import com.benjaminledet.pokedex.manager.PreferencesManager
import kotlinx.android.synthetic.main.fragment_account.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountFragment: Fragment() {

    private val preferencesManager by inject<PreferencesManager>()
    private val viewModel by viewModel<AccountViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupThemeView()

        /*
        loginButton.setBackgroundColor(requireContext().getColor(R.color.blue))
        loginButton.setBackgroundColor(requireContext().getAttributeFromAttr(R.attr.colorPrimary))
        */
    }

    private fun setupThemeView() {
        themeRadioGroup.check(
            when (preferencesManager.nightMode) {
                AppCompatDelegate.MODE_NIGHT_NO -> R.id.themeLight
                AppCompatDelegate.MODE_NIGHT_YES -> R.id.themeDark
                else -> R.id.themeDefault
            }
        )
        themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            preferencesManager.nightMode = when (checkedId) {
                R.id.themeLight -> AppCompatDelegate.MODE_NIGHT_NO
                R.id.themeDark -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        }
    }
}
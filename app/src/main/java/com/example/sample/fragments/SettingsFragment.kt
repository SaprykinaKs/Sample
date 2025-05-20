package com.example.sample.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.sample.R
import androidx.core.content.edit
import com.example.sample.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

@Suppress("DEPRECATION")
class SettingsFragment : Fragment() {

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedPrefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notificationsSwitch = view.findViewById<Switch>(R.id.notifications_switch)
        val darkModeSwitch = view.findViewById<Switch>(R.id.dark_mode_switch)
        val languageButton = view.findViewById<Button>(R.id.language_button)
        val logoutButton = view.findViewById<Button>(R.id.logout_button)

        notificationsSwitch.isChecked = sharedPrefs.getBoolean("notifications_enabled", true)
        darkModeSwitch.isChecked = isDarkThemeEnabled()

        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit { putBoolean("notifications_enabled", isChecked) }
            showToast(
                if (isChecked) R.string.notifications_enabled
                else R.string.notifications_disabled
            )
        }

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            setDarkMode(isChecked)
            requireActivity().recreate()
        }

        languageButton.setOnClickListener {
            showLanguageDialog()
        }

        logoutButton.setOnClickListener {
            (activity as? MainActivity)?.logout()
        }
    }

    private fun isDarkThemeEnabled(): Boolean {
        return sharedPrefs.getBoolean("dark_theme_enabled", false)
    }

    private fun setDarkMode(enabled: Boolean) {
        sharedPrefs.edit { putBoolean("dark_theme_enabled", enabled) }
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        showToast(
            if (enabled) R.string.dark_theme_enabled
            else R.string.dark_theme_disabled
        )
    }

    private fun showToast(@StringRes messageRes: Int) {
        Toast.makeText(
            requireContext(),
            getString(messageRes),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showLanguageDialog() {
        val languages = arrayOf(
            getString(R.string.russian),
            getString(R.string.english)
        )

        val currentLang = sharedPrefs.getString("app_language", "en") ?: "en"
        val checkedItem = if (currentLang == "ru") 0 else 1

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.select_language)
            .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                val langCode = if (which == 0) "ru" else "en"
                if (currentLang != langCode) {
                    sharedPrefs.edit { putString("app_language", langCode) }
                    setAppLocale(langCode)
                    restartApp()
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun setAppLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun restartApp() {
        requireActivity().finish()
        startActivity(Intent(requireActivity(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}
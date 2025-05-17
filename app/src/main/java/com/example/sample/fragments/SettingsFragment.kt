package com.example.sample.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.sample.R
import com.example.sample.utils.AuthManager
import androidx.core.content.edit
import com.example.sample.MainActivity

class SettingsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notificationsSwitch = view.findViewById<Switch>(R.id.notifications_switch)
        val darkModeSwitch = view.findViewById<Switch>(R.id.dark_mode_switch)
        val logoutButton = view.findViewById<Button>(R.id.logout_button)

        // Загружаем сохраненные настройки
        notificationsSwitch.isChecked = sharedPreferences.getBoolean("notifications_enabled", true)
        darkModeSwitch.isChecked = isDarkThemeEnabled()

        // Обработчик уведомлений
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit { putBoolean("notifications_enabled", isChecked) }
            showToast(if (isChecked) "Уведомления включены" else "Уведомления выключены")
        }

        // Обработчик темы
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            setDarkMode(isChecked)
            showToast(if (isChecked) "Темная тема включена" else "Темная тема выключена")
            requireActivity().recreate()
        }

        // Обработчик выхода
        logoutButton.setOnClickListener {
            (activity as? MainActivity)?.logout()
        }
    }

    private fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean("dark_theme_enabled", false)
    }

    private fun setDarkMode(enabled: Boolean) {
        sharedPreferences.edit { putBoolean("dark_theme_enabled", enabled) }
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
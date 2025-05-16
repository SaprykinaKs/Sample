package com.example.sample.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.sample.R

class SettingsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализируем SharedPreferences
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

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
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply()
            // Здесь можно добавить логику включения/выключения уведомлений
        }

        // Обработчик темы
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            setDarkMode(isChecked)
            requireActivity().recreate() // Перезагружаем активити для применения темы
        }

        // Обработчик выхода
        logoutButton.setOnClickListener {
            // Реализуйте логику выхода
            // Например: (activity as? MainActivity)?.logout()
        }
    }

    private fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean("dark_theme_enabled", false)
    }

    private fun setDarkMode(enabled: Boolean) {
        // Сохраняем настройку
        sharedPreferences.edit().putBoolean("dark_theme_enabled", enabled).apply()

        // Устанавливаем тему
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
// LoginActivity.kt
package com.example.sample

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.sample.databinding.ActivityLoginBinding
import com.example.sample.utils.AuthManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.error_fill_all_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (AuthManager.login(this, username, password)) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, R.string.error_wrong_credentials, Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Добавляем кнопку выбора языка в layout или используем существующую
        binding.root.findViewById<Button>(R.id.language_button)?.setOnClickListener {
            showLanguageDialog()
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf(
            getString(R.string.russian),
            getString(R.string.english)
        )

        val currentLang = sharedPrefs.getString("app_language", "en") ?: "en"
        val checkedItem = if (currentLang == "ru") 0 else 1

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.select_language)
            .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                val langCode = if (which == 0) "ru" else "en"
                sharedPrefs.edit { putString("app_language", langCode) }
                setAppLocale(langCode)
                dialog.dismiss()
                recreate() // Пересоздаем активити для применения языка
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

    override fun attachBaseContext(newBase: Context) {
        val sharedPrefs = newBase.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val lang = sharedPrefs.getString("app_language", "en") ?: "en"
        super.attachBaseContext(wrapContext(newBase, lang))
    }

    private fun wrapContext(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}
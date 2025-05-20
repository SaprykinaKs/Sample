package com.example.sample

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sample.databinding.ActivityRegisterBinding
import com.example.sample.utils.AuthManager
import com.example.sample.utils.LocaleUtils

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleUtils.getSavedLanguage(newBase)
        super.attachBaseContext(LocaleUtils.wrapContext(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, R.string.error_fill_all_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, R.string.error_password_mismatch, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (AuthManager.register(this, username, password)) {
                Toast.makeText(this, R.string.success_account_created, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, R.string.error_user_exists, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
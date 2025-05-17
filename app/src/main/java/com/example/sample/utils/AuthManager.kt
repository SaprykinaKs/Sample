package com.example.sample.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

object AuthManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_ACCOUNTS = "user_accounts"
    private const val KEY_LOGGED_IN = "is_logged_in"

    private fun getSharedPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun login(context: Context, username: String, password: String): Boolean {
        val accounts = getAccounts(context)
        val isValid = accounts.any { it.username == username && it.password == password }

        if (isValid) {
            getSharedPrefs(context).edit { putBoolean(KEY_LOGGED_IN, true) }
        }
        return isValid
    }

    fun register(context: Context, username: String, password: String): Boolean {
        val accounts = getAccounts(context).toMutableList()

        if (accounts.any { it.username == username }) {
            return false
        }

        accounts.add(UserAccount(username, password))
        saveAccounts(context, accounts)
        return true
    }

    fun isLoggedIn(context: Context): Boolean {
        return getSharedPrefs(context).getBoolean(KEY_LOGGED_IN, false)
    }

    fun logout(context: Context) {
        getSharedPrefs(context).edit { putBoolean(KEY_LOGGED_IN, false) }
    }

    private fun getAccounts(context: Context): List<UserAccount> {
        val json = getSharedPrefs(context).getString(KEY_ACCOUNTS, "[]") ?: "[]"
        val type = object : TypeToken<List<UserAccount>>() {}.type
        return Gson().fromJson(json, type)
    }

    private fun saveAccounts(context: Context, accounts: List<UserAccount>) {
        val json = Gson().toJson(accounts)
        getSharedPrefs(context).edit { putString(KEY_ACCOUNTS, json) }
    }

    data class UserAccount(val username: String, val password: String)
}
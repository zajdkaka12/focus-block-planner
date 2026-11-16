package com.focusblock.planner.data.local

import android.content.Context

class PreferencesStorage(context: Context) {

    private val preferences =
        context.getSharedPreferences("focus_block_planner", Context.MODE_PRIVATE)

    fun readString(key: String, fallback: String): String =
        preferences.getString(key, fallback) ?: fallback

    fun writeString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun readInt(key: String, fallback: Int): Int = preferences.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean): Boolean =
        preferences.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun contains(key: String): Boolean = preferences.contains(key)

    fun remove(key: String) {
        preferences.edit().remove(key).apply()
    }
}

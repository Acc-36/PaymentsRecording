package com.example.paymentsrecording.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

/**
 * 简易主题偏好存储。使用 SharedPreferences 以避免额外依赖。
 * themeMode: 0=跟随系统, 1=浅色, 2=深色
 */
object ThemePref {
    private const val FILE = "theme_pref"
    private const val KEY_MODE = "theme_mode"

    fun getMode(context: Context): Int =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getInt(KEY_MODE, 0)

    fun setMode(context: Context, mode: Int) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putInt(KEY_MODE, mode).apply()
    }
}

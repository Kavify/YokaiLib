package ru.feryafox.yokailib.preferences

import android.content.Context

object PreferencesManagerInitializer {
    fun init(context: Context) {
        PreferencesManagerFactory.init(context)
        SecurePreferencesManagerFactory.init(context)
    }
}
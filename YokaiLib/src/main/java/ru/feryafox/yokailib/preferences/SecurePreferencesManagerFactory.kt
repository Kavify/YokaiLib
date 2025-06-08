package ru.feryafox.yokailib.preferences

import android.content.Context
import dev.spght.encryptedprefs.EncryptedSharedPreferences
import dev.spght.encryptedprefs.MasterKey

object SecurePreferencesManagerFactory {

    private lateinit var appContext: Context
    private val cache = mutableMapOf<String, PreferencesManager>()

    fun init(context: Context) { appContext = context.applicationContext }

    fun create(id: String): PreferencesManager {
        check(::appContext.isInitialized) {
            "SecurePreferencesManagerFactory is not initialized. Call init(context) first."
        }

        return cache.getOrPut(id) {
            val masterKey = MasterKey.Builder(appContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val prefs = EncryptedSharedPreferences.create(
                appContext,
                "secure_prefs_$id",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            PreferencesManager(prefs, id)
        }
    }
}

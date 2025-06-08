package ru.feryafox.yokailib.storages.defaultstorages.secure

import ru.feryafox.yokailib.preferences.SecurePreferencesManagerFactory
import ru.feryafox.yokailib.storages.base.StorageField

abstract class DefaultSecurePreferencesStorageField<T>(
    override val id: String,
    override val key: String,
    override val initValue: T
) : StorageField<T> {

    private val prefs by lazy { SecurePreferencesManagerFactory.create(id) }
    private var _value: T? = null

    override var field: T
        get() {
            if (_value == null) _value = load()
            return _value!!
        }
        set(value) {
            _value = value
            prefs.setString(key, serialize(value))
        }

    private fun load(): T =
        if (prefs.contains(key)) deserialize(prefs.getString(key))
        else {
            prefs.setString(key, serialize(initValue))
            initValue
        }
}

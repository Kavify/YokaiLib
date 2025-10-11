package ru.feryafox.yokailib.storages.defaultstorages.secure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class JsonSecurePreferencesStorageField<T>(
    override val id: String,
    override val key: String,
    override val initValue: T,
    private val serializer: KSerializer<T>
) : DefaultSecurePreferencesStorageField<T>(id, key, initValue) {

    override fun serialize(value: T): String {
        return Json.encodeToString(serializer, value)
    }

    override fun deserialize(value: String): T {
        return Json.decodeFromString(serializer, value)
    }
}

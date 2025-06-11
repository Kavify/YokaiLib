package ru.feryafox.yokailib.storages.defaultstorages.preferences

class BooleanStorageField(
    override val id: String,
    override val key: String,
    override val initValue: Boolean = false
): DefaultPreferencesStorageField<Boolean>(id, key, initValue) {
    override fun serialize(value: Boolean): String = value.toString()

    override fun deserialize(value: String): Boolean = value.toBooleanStrictOrNull()
        ?: throw IllegalArgumentException("Cannot deserialize '$value' to Boolean")
}
package ru.feryafox.yokailib.storages.defaultstorages.secure

class StringSecureStorageField(
    override val id: String,
    override val key: String,
    override val initValue: String = ""
) : DefaultSecurePreferencesStorageField<String>(id, key, initValue) {

    override fun serialize(value: String): String = value
    override fun deserialize(value: String): String = value
}

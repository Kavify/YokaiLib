package ru.feryafox.yokailib.settings.base

// TODO возможность поддержки нескольких языков
abstract class CategorySettings(
    val id: String,
    val title: String?,
    val onSaved: () -> Unit = {}
) {

    abstract val fields: List<SettingField<*>>

    @Suppress("UNCHECKED_CAST")
    fun save() {
        fields.forEach {
            if (it.isUpdated || it.isOnUpdateBehavior == OnUpdateBehavior.ON_SAVED) (it.onUpdate as (Any?) -> Unit).invoke(it.field.field)
            onSaved()
        }
    }

    fun get(key: String): Any? = fields.firstOrNull { it.field.key == key }?.field?.field
}

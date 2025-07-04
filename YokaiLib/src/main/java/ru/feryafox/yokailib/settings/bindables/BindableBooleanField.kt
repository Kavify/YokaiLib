package ru.feryafox.yokailib.settings.bindables

import ru.feryafox.yokailib.settings.base.Disableable
import ru.feryafox.yokailib.settings.base.OnUpdateBehavior
import ru.feryafox.yokailib.settings.defaults.BooleanField
import ru.feryafox.yokailib.storages.base.StorageField

class BindableBooleanField(
    override val title: String,
    override val field: StorageField<Boolean>,
    override val required: Boolean = false,
    override val isOnUpdateBehavior: OnUpdateBehavior,
    override val immediatelyOnUpdate: (Boolean) -> Unit = {},
    val bindScope: BoolBindScope,
    override val onUpdate: (Boolean) -> Unit = {}
) : BooleanField(
    title = title,
    field = field,
    isOnUpdateBehavior = isOnUpdateBehavior,
    immediatelyOnUpdate = immediatelyOnUpdate,
    onUpdate = onUpdate
) {
    init {
        bindScope.process(field.field)
    }
    override fun immediatelyUpdate(value: Boolean) {
        super.immediatelyUpdate(value)
        bindScope.process(value)
    }
}

class BoolBindScope {
    private val disableableBindings = mutableMapOf<Disableable, Boolean>()

    infix fun Disableable.disabledWhen(isDisabled: Boolean) {
        disableableBindings[this] = isDisabled
    }

    fun process(isDisabled: Boolean) {
        disableableBindings.forEach { (disableable, whenDisable) ->
            disableable.isDisabled = if (whenDisable) isDisabled else !isDisabled
        }
    }

}

fun bind(init: BoolBindScope.() -> Unit): BoolBindScope {
    val bindScope = BoolBindScope()
    bindScope.init()
    return bindScope
}

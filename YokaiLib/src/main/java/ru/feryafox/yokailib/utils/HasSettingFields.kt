package ru.feryafox.yokailib.utils

import ru.feryafox.yokailib.categories.Category
import ru.feryafox.yokailib.categories.CategoryItem
import ru.feryafox.yokailib.settings.base.CategorySettings
import ru.feryafox.yokailib.settings.base.SettingField

interface HasSettingFields { val fieldsForValidation: List<SettingField<*>> }

val Category.settingsFields: List<SettingField<*>>
    get() = (this as? HasSettingFields)?.fieldsForValidation.orEmpty()

val CategoryItem.settingsFields: List<SettingField<*>>
    get() = (this as? HasSettingFields)?.fieldsForValidation.orEmpty()

val CategorySettings.settingsFields: List<SettingField<*>>
    get() = fields

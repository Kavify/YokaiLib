package ru.feryafox.yokailib.utils

/**
 * Режимы выполнения валидации
 */
enum class ValidationMode {
    /**
     * Валидация выполняется при каждом изменении экрана
     */
    ON_SCREEN_CHANGE,

    /**
     * Валидация выполняется при открытии/закрытии Drawer
     */
    ON_DRAWER_TOGGLE,

    /**
     * Валидация выполняется только при возврате из настроек
     */
    ON_SETTINGS_EXIT,

    /**
     * Валидация выполняется при открытии drawer и возврате из настроек
     */
    ON_DRAWER_AND_SETTINGS,

    /**
     * Валидация не выполняется автоматически
     */
    MANUAL
}


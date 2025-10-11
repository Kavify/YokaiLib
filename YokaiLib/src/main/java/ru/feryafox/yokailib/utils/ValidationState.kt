package ru.feryafox.yokailib.utils

/**
 * Состояние валидации с поддержкой асинхронности
 */
sealed class ValidationState {
    /**
     * Валидация не запущена
     */
    object Idle : ValidationState()

    /**
     * Валидация выполняется
     */
    object Loading : ValidationState()

    /**
     * Валидация завершена с результатом
     */
    data class Completed(val result: ValidationResult) : ValidationState()
}


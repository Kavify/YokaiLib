package ru.feryafox.yokailib.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Интерфейс для объектов, которые могут быть валидированы асинхронно
 */
interface AsyncValidatable : Validatable {
    /**
     * Проверяет, прошла ли валидация успешно (асинхронная версия)
     * @return ValidationResult с результатом валидации
     */
    suspend fun validateAsync(): ValidationResult {
        return withContext(Dispatchers.Default) {
            validate()
        }
    }
}


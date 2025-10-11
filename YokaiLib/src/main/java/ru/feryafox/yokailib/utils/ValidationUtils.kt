package ru.feryafox.yokailib.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import ru.feryafox.yokailib.categories.Category
import ru.feryafox.yokailib.categories.CategoryItem
import ru.feryafox.yokailib.root.ui.components.popup.LocalPopupHost
import ru.feryafox.yokailib.root.ui.components.popup.PopupConfig
import ru.feryafox.yokailib.root.ui.components.popup.needAttention
import ru.feryafox.yokailib.settings.base.SettingField

/**
 * Проверяет валидацию объектов, реализующих интерфейс Validatable.
 * Если валидация не прошла, показывает всплывающее окно с ошибками.
 *
 * @return `true`, если валидация прошла успешно
 * @return `false`, если валидация не прошла (показано всплывающее окно)
 */
@Composable
fun checkValidation(
    vararg validatables: Validatable,
    title: String = "Требуются данные",
): Boolean {
    val host = LocalPopupHost.current

    // Собираем все ошибки валидации
    val allFailures = mutableListOf<ValidationFailure>()
    validatables.forEach { validatable ->
        val result = validatable.validate()
        if (result is ValidationResult.Failure) {
            allFailures.addAll(result.failures)
        }
    }

    if (allFailures.isNotEmpty()) {
        val errorMessages = allFailures.map { it.message }
        host.show(
            PopupConfig(
                title = title,
                message = errorMessages.joinToString("\n"),
                tint = MaterialTheme.colorScheme.errorContainer,
                dismissible = false,
                dimBackground = true,
                inline = true,
                validationResult = ValidationResult.Failure(allFailures)
            )
        )
        return false
    }
    return true
}

/**
 * Проверяет валидацию категории и её элемента.
 * Показывает всплывающее окно, если валидация не прошла.
 *
 * @return `true`, если валидация прошла успешно
 * @return `false`, если валидация не прошла
 */
@Composable
fun checkCategoryItemValidation(
    category: Category,
    item: CategoryItem,
    title: String = "Требуются данные"
): Boolean {
    val host = LocalPopupHost.current

    val categoryValidation = category.validate()
    val itemValidation = item.validate()

    // Объединяем результаты валидации
    val combinedResult = when {
        !categoryValidation.isValid -> categoryValidation
        !itemValidation.isValid -> itemValidation
        else -> ValidationResult.Success
    }

    if (combinedResult is ValidationResult.Failure) {
        val errorMessages = combinedResult.getAllMessages()
        host.show(
            PopupConfig(
                title = title,
                message = errorMessages.joinToString("\n"),
                tint = MaterialTheme.colorScheme.errorContainer,
                dismissible = false,
                dimBackground = true,
                inline = true,
                validationResult = combinedResult
            )
        )
        return false
    }
    return true
}

/**
 * Проверяет список [fields] на «пустые» обязательные значения.
 * (Старая система для обратной совместимости)
 *
 * @return `true`,   если все ОК (можно продолжать работу)
 * @return `false`,  если найдено хотя-бы одно поле-ошибка (показана всплывашка)
 *
 * **skipDisabled** ― игнорировать ли выключенные (Disableable) поля.
 */
@Composable
fun checkRequiredFields(
    fields: List<SettingField<*>>,
    skipDisabled: Boolean = true,
    title: String = "Требуются данные",
    message: String = "Заполните следующие поля",
): Boolean {
    val host = LocalPopupHost.current
    val invalid = fields.filter { it.needAttention(skipDisabled) }

    if (invalid.isNotEmpty()) {
        host.show(
            PopupConfig(
                title = title,
                message = message,
                tint = MaterialTheme.colorScheme.errorContainer,
                dismissible = false,
                dimBackground = true,
                requiredFields = invalid,
                skipDisabled = skipDisabled
            )
        )
        return false
    }
    return true
}

package ru.feryafox.yokailib.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.feryafox.yokailib.categories.Category
import ru.feryafox.yokailib.categories.CategoryItem
import ru.feryafox.yokailib.settings.base.CategorySettings
import ru.feryafox.yokailib.settings.base.Disableable
import ru.feryafox.yokailib.settings.base.SettingField

/**
 * Extension-функции для валидации категорий, элементов и настроек
 */

/**
 * Возвращает правила валидации для всех обязательных полей в настройках
 * @param messageTemplate шаблон сообщения об ошибке, может содержать {fieldTitle}
 * @param checkDisabled если false, то отключенные поля не будут проверяться
 * @param predicate дополнительное условие для фильтрации полей (по умолчанию проверяются все)
 */
fun CategorySettings.requiredFieldsValidation(
    messageTemplate: String = "Поле '{fieldTitle}' обязательно для заполнения",
    checkDisabled: Boolean = true,
    predicate: (SettingField<*>) -> Boolean = { true }
): List<ValidationRule> {
    return FieldValidationRules.allRequiredFieldsFilled(this, messageTemplate, checkDisabled, predicate)
}

/**
 * Возвращает правила валидации только для включенных обязательных полей
 * (поля, которые реализуют Disableable и имеют isDisabled == false)
 * @param messageTemplate шаблон сообщения об ошибке, может содержать {fieldTitle}
 */
fun CategorySettings.requiredEnabledFieldsValidation(
    messageTemplate: String = "Поле '{fieldTitle}' обязательно для заполнения"
): List<ValidationRule> {
    return FieldValidationRules.allRequiredFieldsFilled(
        settings = this,
        messageTemplate = messageTemplate,
        checkDisabled = false,
        predicate = { field ->
            // Проверяем только включенные поля
            (field as? Disableable)?.isDisabled == false
        }
    )
}

/**
 * Проверяет валидность категории
 */
fun Category.validate(): ValidationResult {
    return (this as? Validatable)?.validate() ?: ValidationResult.Success
}

/**
 * Проверяет, заблокирована ли категория
 */
val Category.isBlocked: Boolean
    get() = (this as? Validatable)?.isBlocked ?: false

/**
 * Проверяет валидность элемента категории
 */
fun CategoryItem.validate(): ValidationResult {
    return (this as? Validatable)?.validate() ?: ValidationResult.Success
}

/**
 * Проверяет, заблокирован ли элемент категории
 */
val CategoryItem.isBlocked: Boolean
    get() = (this as? Validatable)?.isBlocked ?: false

/**
 * Проверяет валидность настроек категории
 */
fun CategorySettings.validate(): ValidationResult {
    return (this as? Validatable)?.validate() ?: ValidationResult.Success
}

/**
 * Проверяет, заблокированы ли настройки категории
 */
val CategorySettings.isBlocked: Boolean
    get() = (this as? Validatable)?.isBlocked ?: false

/**
 * Асинхронная проверка валидности категории
 */
suspend fun Category.validateAsync(): ValidationResult {
    return withContext(Dispatchers.Default) {
        when (this@validateAsync) {
            is AsyncValidatable -> (this@validateAsync as AsyncValidatable).validateAsync()
            is Validatable -> (this@validateAsync as Validatable).validate()
            else -> ValidationResult.Success
        }
    }
}

/**
 * Асинхронная проверка валидности элемента категории
 */
suspend fun CategoryItem.validateAsync(): ValidationResult {
    return withContext(Dispatchers.Default) {
        when (this@validateAsync) {
            is AsyncValidatable -> (this@validateAsync as AsyncValidatable).validateAsync()
            is Validatable -> (this@validateAsync as Validatable).validate()
            else -> ValidationResult.Success
        }
    }
}

/**
 * Асинхронная проверка валидности настроек категории
 */
suspend fun CategorySettings.validateAsync(): ValidationResult {
    return withContext(Dispatchers.Default) {
        when (this@validateAsync) {
            is AsyncValidatable -> (this@validateAsync as AsyncValidatable).validateAsync()
            is Validatable -> (this@validateAsync as Validatable).validate()
            else -> ValidationResult.Success
        }
    }
}

package ru.feryafox.yokailib.utils

import ru.feryafox.yokailib.settings.base.CategorySettings
import ru.feryafox.yokailib.settings.base.SettingField

/**
 * Интерфейс для объектов, которые могут быть валидированы
 */
interface Validatable {
    /**
     * Список правил валидации
     */
    val validationRules: List<ValidationRule>

    /**
     * Проверяет, прошла ли валидация успешно
     * Сначала проверяет заполненность полей, потом кастомную валидацию
     * @return ValidationResult с результатом валидации
     */
    fun validate(): ValidationResult {
        val failures = mutableListOf<ValidationFailure>()

        // Сортируем правила по приоритету: сначала проверка заполненности (приоритет 0), потом остальные
        val sortedRules = validationRules.sortedBy { it.priority }

        for (rule in sortedRules) {
            val result = rule.validate()
            if (!result.isValid) {
                failures.add(ValidationFailure(rule.message, rule.fieldId))

                // Если это проверка заполненности и она не прошла, останавливаемся
                // чтобы сначала показать только ошибки заполненности
                if (rule.priority == ValidationPriority.REQUIRED) {
                    // Продолжаем собирать только ошибки заполненности
                    continue
                } else if (failures.any { it.fieldId != null && sortedRules.any { r ->
                        r.priority == ValidationPriority.REQUIRED && r.fieldId == it.fieldId
                    }}) {
                    // Если есть хоть одна ошибка заполненности, прерываем дальнейшую проверку
                    break
                }
            }
        }

        // Если есть ошибки заполненности, возвращаем только их
        val requiredFailures = failures.filter { failure ->
            sortedRules.any { it.fieldId == failure.fieldId && it.priority == ValidationPriority.REQUIRED }
        }

        if (requiredFailures.isNotEmpty()) {
            return ValidationResult.Failure(requiredFailures)
        }

        return if (failures.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(failures)
        }
    }

    /**
     * Проверяет, заблокирован ли объект из-за неуспешной валидации
     */
    val isBlocked: Boolean
        get() = !validate().isValid
}

/**
 * Приоритеты валидации
 */
object ValidationPriority {
    const val REQUIRED = 0  // Проверка заполненности - самый высокий приоритет
    const val CUSTOM = 100   // Кастомная валидация - выполняется после
}

/**
 * Правило валидации
 */
interface ValidationRule {
    /**
     * Сообщение об ошибке при невыполнении правила
     */
    val message: String

    /**
     * ID поля, к которому относится правило (опционально)
     */
    val fieldId: String?
        get() = null

    /**
     * Приоритет выполнения правила (меньше = выше приоритет)
     */
    val priority: Int
        get() = ValidationPriority.CUSTOM

    /**
     * Проверяет правило
     * @return true, если правило выполнено
     */
    fun validate(): ValidationRuleResult
}

/**
 * Результат проверки одного правила
 */
data class ValidationRuleResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

/**
 * Результат валидации
 */
sealed class ValidationResult {
    abstract val isValid: Boolean

    /**
     * Валидация прошла успешно
     */
    object Success : ValidationResult() {
        override val isValid: Boolean = true
    }

    /**
     * Валидация не прошла
     */
    data class Failure(val failures: List<ValidationFailure>) : ValidationResult() {
        override val isValid: Boolean = false

        /**
         * Получить все сообщения об ошибках
         */
        fun getAllMessages(): List<String> = failures.map { it.message }

        /**
         * Получить сообщения об ошибках для конкретного поля
         */
        fun getMessagesForField(fieldId: String): List<String> =
            failures.filter { it.fieldId == fieldId }.map { it.message }
    }
}

/**
 * Описание неуспешной валидации
 */
data class ValidationFailure(
    val message: String,
    val fieldId: String? = null
)

/**
 * Готовые правила валидации для SettingField
 */
object FieldValidationRules {
    /**
     * Проверяет, что все обязательные поля в CategorySettings заполнены
     * @param settings настройки категории
     * @param messageTemplate шаблон сообщения об ошибке, может содержать {fieldTitle}
     * @param checkDisabled если false, то отключенные поля не будут проверяться
     * @param predicate дополнительное условие для фильтрации полей (по умолчанию проверяются все)
     */
    fun allRequiredFieldsFilled(
        settings: CategorySettings,
        messageTemplate: String = "Поле '{fieldTitle}' обязательно для заполнения",
        checkDisabled: Boolean = true,
        predicate: (SettingField<*>) -> Boolean = { true }
    ): List<ValidationRule> {
        return settings.fields
            .filter { it.required }
            .filter { predicate(it) }
            .map { field ->
                object : ValidationRule {
                    override val message: String = messageTemplate.replace("{fieldTitle}", field.title)
                    override val fieldId: String = field.field.key
                    override val priority: Int = ValidationPriority.REQUIRED

                    override fun validate(): ValidationRuleResult {
                        // Если checkDisabled == false, то нужно проверить, включено ли поле
                        // Используем predicate для проверки
                        if (!checkDisabled && !predicate(field)) {
                            return ValidationRuleResult(true, null)
                        }

                        val value = field.field.field
                        val isValid = when {
                            value == null -> false
                            value is String -> value.isNotBlank()
                            value is Collection<*> -> value.isNotEmpty()
                            else -> true
                        }
                        return ValidationRuleResult(isValid, if (!isValid) message else null)
                    }
                }
            }
    }

    /**
     * Проверяет, что обязательное поле заполнено
     */
    fun <T> required(
        field: SettingField<T>,
        message: String = "Поле '${field.title}' обязательно для заполнения"
    ): ValidationRule = object : ValidationRule {
        override val message: String = message
        override val fieldId: String = field.field.key
        override val priority: Int = ValidationPriority.REQUIRED

        override fun validate(): ValidationRuleResult {
            val value = field.field.field
            val isValid = when {
                !field.required -> true
                value == null -> false
                value is String -> value.isNotBlank()
                value is Collection<*> -> value.isNotEmpty()
                else -> true
            }
            return ValidationRuleResult(isValid, if (!isValid) message else null)
        }
    }

    /**
     * Проверяет строковое поле с помощью кастомной функции
     */
    fun <T> custom(
        field: SettingField<T>,
        message: String,
        predicate: (T?) -> Boolean
    ): ValidationRule = object : ValidationRule {
        override val message: String = message
        override val fieldId: String = field.field.key
        override val priority: Int = ValidationPriority.CUSTOM

        override fun validate(): ValidationRuleResult {
            @Suppress("UNCHECKED_CAST")
            val value = field.field.field as? T
            val isValid = predicate(value)
            return ValidationRuleResult(isValid, if (!isValid) message else null)
        }
    }

    /**
     * Проверяет минимальную длину строки
     */
    fun minLength(
        field: SettingField<String>,
        minLength: Int,
        message: String = "Поле '${field.title}' должно содержать минимум $minLength символов"
    ): ValidationRule = custom(field, message) { value ->
        (value?.length ?: 0) >= minLength
    }

    /**
     * Проверяет максимальную длину строки
     */
    fun maxLength(
        field: SettingField<String>,
        maxLength: Int,
        message: String = "Поле '${field.title}' должно содержать максимум $maxLength символов"
    ): ValidationRule = custom(field, message) { value ->
        (value?.length ?: 0) <= maxLength
    }

    /**
     * Проверяет диапазон числового значения
     */
    fun <T : Comparable<T>> range(
        field: SettingField<T>,
        min: T,
        max: T,
        message: String = "Значение поля '${field.title}' должно быть в диапазоне от $min до $max"
    ): ValidationRule = custom(field, message) { value ->
        value?.let { it >= min && it <= max } ?: false
    }
}

/**
 * Кастомное правило валидации
 */
class CustomValidationRule(
    override val message: String,
    override val fieldId: String? = null,
    override val priority: Int = ValidationPriority.CUSTOM,
    private val predicate: () -> Boolean
) : ValidationRule {
    override fun validate(): ValidationRuleResult {
        val isValid = predicate()
        return ValidationRuleResult(isValid, if (!isValid) message else null)
    }
}

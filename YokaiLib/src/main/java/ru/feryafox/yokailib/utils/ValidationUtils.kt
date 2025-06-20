package ru.feryafox.yokailib.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import ru.feryafox.yokailib.root.ui.components.popup.LocalPopupHost
import ru.feryafox.yokailib.root.ui.components.popup.PopupConfig
import ru.feryafox.yokailib.root.ui.components.popup.needAttention
import ru.feryafox.yokailib.settings.base.SettingField

/**
 * Проверяет список [fields] на «пустые» обязательные значения.
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

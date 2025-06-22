package ru.feryafox.yokailib.root.ui.components.popup

import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.dp
import ru.feryafox.yokailib.settings.base.Disableable
import ru.feryafox.yokailib.settings.base.SettingField
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex

/**
 * Конфигурация всплывающего уведомления, которое отображается поверх текущего
 * контента приложения (но при `inline = true` — под выезжающим Drawer-меню).
 *
 * ### Базовые поля
 * | Параметр | Назначение | Значение по-умолчанию |
 * |----------|------------|-----------------------|
 * | **title** | Заголовок карточки. Если `null` или пусто, заголовок не рисуется. | `null` |
 * | **message** | Основной текст уведомления. Поддерживает многострочный текст. | — |
 * | **tint** | Цвет фона карточки. Используйте для выделения ошибки, успеха и т. п.<br>Если `Color.Unspecified`, берётся `MaterialTheme.colorScheme.surfaceVariant`. | `Color.Unspecified` |
 *
 * ### Управление закрытием
 * | Параметр | Что делает | По-умолчанию |
 * |----------|------------|--------------|
 * | **dismissible** | • `true` — появляется крестик, можно закрыть Back-ом или тапом снаружи (если `inline = false`).<br>• `false` — пользователь закрыть не может, карточка пропадёт только программно или когда исчезнут **requiredFields**. | `true` |
 *
 * ### Фон и блокировка ввода
 * | Параметр | Что делает | По-умолчанию |
 * |----------|------------|--------------|
 * | **dimBackground** | Затемняет фон полупрозрачным чёрным цветом (`alpha ≈ 0.45`).<br>Не блокирует клики, если `inline = true`; <br>при `inline = false` блокировка зависит от `dismissible`. | `false` |
 *
 * ### Поддержка «обязательных» полей
 * | Параметр | Что делает | По-умолчанию |
 * |----------|------------|--------------|
 * | **requiredFields** | Список `SettingField`, которые нужно заполнить.<br>Если список **не пуст** и `dismissible == false`, карточка будет сама проверять поля на каждом recomposition и скроется, когда все поля станут валидными. | `emptyList()` |
 * | **skipDisabled** | Игнорировать ли `SettingField`, унаследованные от `Disableable` и помеченные `isDisabled = true`.<br>Удобно при биндингах: выключенные поля не считаются «обязательными». | `true` |
 *
 * ### Режим отображения
 * | Параметр | Что делает | По-умолчанию |
 * |----------|------------|--------------|
 * | **inline** | • `true` — карточка рисуется **внутри** Compose-иерархии → оказывается *под* Drawer-меню, жесты открытия/закрытия работают.<br>• `false` — отображается через классический `Popup` Android → поверх всех окон (полный модальный диалог). | `true` |
 *
 * ---
 * #### Пример использования
 * ```kotlin
 * // показать уведомление об обязательных полях, не блокируя Drawer
 * LocalPopupHost.current.show(
 *     PopupConfig(
 *         title          = "Требуются данные",
 *         message        = "Заполните следующие поля",
 *         tint           = MaterialTheme.colorScheme.errorContainer,
 *         dismissible    = false,      // крестика нет, Back не закрывает
 *         dimBackground  = true,       // затемняем фон
 *         requiredFields = invalidList, // список SettingField с ошибками
 *         inline         = true        // важно: под Drawer
 *     )
 * )
 * ```
 */
@Immutable
data class PopupConfig(
    val title: String? = null,
    val message: String,
    val tint: Color = Color.Unspecified,
    val dismissible: Boolean = true,
    val dimBackground: Boolean = false,
    val requiredFields: List<SettingField<*>> = emptyList(),
    val skipDisabled: Boolean = true,
    /** `true` — уведомление рисуется внутри Compose-иерархии (под Drawer).
     *  `false` — используется Android Popup и отображается поверх всего UI. */
    val inline: Boolean = true
)

class PopupHostController internal constructor(
    private val state: MutableState<PopupConfig?>
) {
    fun show(cfg: PopupConfig) { state.value = cfg }
    fun dismiss()              { state.value = null }
}

val LocalPopupHost = staticCompositionLocalOf<PopupHostController> {
    error("PopupHost не инициализирован. Оберните экран в <PopupHost>.")
}

@Composable
fun PopupHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val popupState = remember { mutableStateOf<PopupConfig?>(null) }
    val controller = remember { PopupHostController(popupState) }

    CompositionLocalProvider(LocalPopupHost provides controller) {
        Box(modifier.fillMaxSize()) {

            content()

            popupState.value?.let { cfg ->

                val card: @Composable () -> Unit = {
                    DefaultPopupCard(config = cfg) { controller.dismiss() }
                }

                if (cfg.inline) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        if (cfg.dimBackground) {
                            Box(
                                Modifier
                                    .matchParentSize()
                                    .alpha(0.45f)
                                    .background(cfg.tint.takeOrElse { Color.Black })
                            )
                        }

                        Box(
                            modifier = Modifier
                                .padding(top = 32.dp)
                                .align(Alignment.TopCenter)
                        ) {
                            card()
                        }
                    }
                } else {
                    Popup(
                        alignment = Alignment.TopCenter,
                        properties = PopupProperties(
                            focusable = !cfg.dismissible,
                            dismissOnBackPress = cfg.dismissible,
                            dismissOnClickOutside = cfg.dismissible
                        ),
                        onDismissRequest = {
                            if (cfg.dismissible) controller.dismiss()
                        }
                    ) { card() }

                    if (cfg.dimBackground) {
                        Popup(properties = PopupProperties(focusable = false)) {
                            Box(
                                Modifier
                                    .matchParentSize()
                                    .alpha(0.45f)
                                    .background(cfg.tint.takeOrElse { Color.Black })
                            )
                        }
                    }
                }

                if (cfg.requiredFields.isNotEmpty() && !cfg.dismissible) {
                    val stillInvalid by remember {
                        derivedStateOf {
                            cfg.requiredFields.any { it.needAttention(cfg.skipDisabled) }
                        }
                    }
                    if (!stillInvalid) controller.dismiss()
                }
            }
        }
    }
}

internal fun SettingField<*>.needAttention(skipDisabled: Boolean): Boolean {
    if (skipDisabled && (this is Disableable && this.isDisabled)) return false
    if (!required) return false

    return when (val v = field.field) {
        null      -> true
        is String -> v.isBlank()
        is Int    -> v == 0
        is Long   -> v == 0L
        is Float  -> v == 0f
        else      -> false
    }
}

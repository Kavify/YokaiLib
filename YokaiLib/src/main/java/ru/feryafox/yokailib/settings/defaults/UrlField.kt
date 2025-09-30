package ru.feryafox.yokailib.settings.defaults

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import ru.feryafox.yokailib.settings.base.Disableable
import ru.feryafox.yokailib.settings.base.OnUpdateBehavior
import ru.feryafox.yokailib.settings.base.SettingField
import ru.feryafox.yokailib.storages.base.StorageField

class UrlField(
    override val title: String = "",
    override val field: StorageField<String>,
    override val required: Boolean = false,
    override val isOnUpdateBehavior: OnUpdateBehavior = OnUpdateBehavior.ON_CHANGED,
    private val validateUrl: Boolean = true,
    override val onUpdate: (String) -> Unit
) : SettingField<String>, Disableable {
    private var _isUpdated = false

    override val isUpdated: Boolean
        get() = _isUpdated

    private var _isDisabled by mutableStateOf(false)
    override var isDisabled: Boolean
        get() = _isDisabled
        set(value) { _isDisabled = value }

    private fun isValidUrl(url: String): Boolean {
        if (!validateUrl || url.isBlank()) return true
        return url.toHttpUrlOrNull() != null
    }

    override val component: @Composable (String) -> Unit = { value ->
        var text by remember { mutableStateOf(value) }
        var isError by remember { mutableStateOf(false) }
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(text) {
            isError = text.isNotEmpty() && !isValidUrl(text)
        }

        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            OutlinedTextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                    field.field = newText
                    _isUpdated = true
                },
                label = {
                    Text(
                        text = buildAnnotatedString {
                            append(title)
                            if (required) {
                                append(" ")
                                withStyle(style = SpanStyle(color = Color.Red)) {
                                    append("*")
                                }
                            }
                        }
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
                enabled = !_isDisabled,
                isError = isError,
                supportingText = if (isError) {
                    { Text("Введите корректную ссылку", color = MaterialTheme.colorScheme.error) }
                } else null,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = "URL",
                        tint = if (_isDisabled)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        else if (isError)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                text = ""
                                field.field = ""
                                _isUpdated = true
                            },
                            enabled = !_isDisabled
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear URL",
                                tint = if (_isDisabled)
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        }
    }
}

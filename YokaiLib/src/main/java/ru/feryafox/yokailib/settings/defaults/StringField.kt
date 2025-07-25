package ru.feryafox.yokailib.settings.defaults

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ru.feryafox.yokailib.settings.base.Disableable
import ru.feryafox.yokailib.settings.base.OnUpdateBehavior
import ru.feryafox.yokailib.settings.base.SettingField
import ru.feryafox.yokailib.storages.base.StorageField

class StringField(
    override val title: String = "",
    override val field: StorageField<String>,
    override val required: Boolean = false,
    override val isOnUpdateBehavior: OnUpdateBehavior,
    override val onUpdate: (String) -> Unit
) : SettingField<String>, Disableable {
    private var _isUpdated = false

    override val isUpdated: Boolean
        get() = _isUpdated

    private var _isDisabled by mutableStateOf(false)
    override var isDisabled: Boolean
        get() = _isDisabled
        set(value) { _isDisabled = value }

    override val component: @Composable (String) -> Unit = { value ->
        var text by remember { mutableStateOf(value) }
        val keyboardController = LocalSoftwareKeyboardController.current

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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
                enabled = !_isDisabled,
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        IconButton(onClick = {
                            text = ""
                            field.field = ""
                            _isUpdated = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear text"
                            )
                        }
                    }
                }
            )
        }
    }
}

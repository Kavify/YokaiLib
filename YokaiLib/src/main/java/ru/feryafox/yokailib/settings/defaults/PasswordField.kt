package ru.feryafox.yokailib.settings.defaults

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import ru.feryafox.yokailib.settings.base.OnUpdateBehavior
import ru.feryafox.yokailib.settings.base.SettingField
import ru.feryafox.yokailib.storages.base.StorageField

class PasswordField(
    override val title: String = "",
    override val field: StorageField<String>,
    override val isOnUpdateBehavior: OnUpdateBehavior = OnUpdateBehavior.ON_CHANGED,
    override val onUpdate: (String) -> Unit
) : SettingField<String> {

    private var _isUpdated = false
    override val isUpdated: Boolean get() = _isUpdated

    override val component: @Composable (String) -> Unit = { value ->
        var text by remember { mutableStateOf(value) }
        var isVisible by remember { mutableStateOf(false) }
        val keyboardController = LocalSoftwareKeyboardController.current

        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            OutlinedTextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                    field.field = newText
                    _isUpdated = true
                },
                label = { Text(title) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                visualTransformation = if (isVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Row {
                        if (text.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    text = ""
                                    field.field = ""
                                    _isUpdated = true
                                }
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                        IconButton(onClick = { isVisible = !isVisible }) {
                            Icon(
                                imageVector = if (isVisible)
                                    Icons.Default.VisibilityOff
                                else
                                    Icons.Default.Visibility,
                                contentDescription = if (isVisible) "Hide password" else "Show password"
                            )
                        }
                    }
                }
            )
        }
    }
}

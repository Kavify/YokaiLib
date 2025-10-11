package ru.feryafox.yokailib.settings.defaults

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ru.feryafox.yokailib.settings.base.Disableable
import ru.feryafox.yokailib.settings.base.SettingField
import ru.feryafox.yokailib.settings.base.OnUpdateBehavior
import ru.feryafox.yokailib.storages.base.StorageField

open class ButtonField(
    override val title: String = "",
    val buttonText: String = "Click",
    override val required: Boolean = false,
    private val onClick: () -> Unit = {},
    override val onUpdate: (Unit) -> Unit = {},
    override val isOnUpdateBehavior: OnUpdateBehavior = OnUpdateBehavior.ON_SAVED
) : SettingField<Unit>, Disableable {
    private var _isDisabled by mutableStateOf(false)

    override var isDisabled: Boolean
        get() = _isDisabled
        set(value) { _isDisabled = value }

    override val isUpdated: Boolean
        get() = false

    override val field: StorageField<*> = object : StorageField<Unit> {
        override val id: String = ""
        override val key: String = ""
        override var field: Unit = Unit
        override val initValue: Unit = Unit
        override fun serialize(value: Unit): String = ""
        override fun deserialize(value: String): Unit = Unit
    }

    override val component: @Composable (Unit) -> Unit = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (title.isNotEmpty()) {
                Text(
                    text = buildAnnotatedString {
                        append(title)
                        if (required) {
                            append(" ")
                            withStyle(style = SpanStyle(color = Color.Red)) {
                                append("*")
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                )
            }

            Button(
                onClick = onClick,
                enabled = !_isDisabled,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(text = buttonText)
            }
        }
    }
}

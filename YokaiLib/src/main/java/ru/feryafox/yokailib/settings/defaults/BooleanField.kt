package ru.feryafox.yokailib.settings.defaults

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.feryafox.yokailib.settings.base.Disableable
import ru.feryafox.yokailib.settings.base.ImmediatelyOnUpdate
import ru.feryafox.yokailib.settings.base.OnUpdateBehavior
import ru.feryafox.yokailib.settings.base.SettingField
import ru.feryafox.yokailib.storages.base.StorageField

open class BooleanField(
    override val title: String = "",
    override val field: StorageField<Boolean>,
    override val isOnUpdateBehavior: OnUpdateBehavior = OnUpdateBehavior.ON_SAVED,
    override val immediatelyOnUpdate: (Boolean) -> Unit = {},
    override val onUpdate: (Boolean) -> Unit = {},
) : SettingField<Boolean>, ImmediatelyOnUpdate<Boolean>, Disableable {
    private var _isUpdated = false

    private var _isDisabled by mutableStateOf(false)

    override var isDisabled: Boolean
        get() = _isDisabled
        set(value) { _isDisabled = value }

    override val isUpdated: Boolean
        get() = _isUpdated

    override fun immediatelyUpdate(value: Boolean) {
        immediatelyOnUpdate(value)
    }

    override val component: @Composable (Boolean) -> Unit = { value ->
        var checked by remember { mutableStateOf(value) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
                    .alignByBaseline()
            )

            Switch(
                checked = checked,
                onCheckedChange = { newValue ->
                    checked = newValue
                    field.field = newValue
                    _isUpdated = true
                    immediatelyUpdate(newValue)
                },
                enabled = !_isDisabled,
                modifier = Modifier.alignByBaseline()
            )
        }
    }
}

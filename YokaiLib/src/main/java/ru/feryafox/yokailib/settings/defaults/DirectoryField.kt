package ru.feryafox.yokailib.settings.defaults

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ru.feryafox.yokailib.settings.base.*
import ru.feryafox.yokailib.storages.base.StorageField

class DirectoryField(
    override val title: String,
    override val field: StorageField<String>,
    override val required: Boolean = false,
    override val isOnUpdateBehavior: OnUpdateBehavior = OnUpdateBehavior.ON_CHANGED,
    private val texts: DirectoryFieldTexts = DirectoryFieldTexts.DEFAULT,
    override val onUpdate: (String) -> Unit
) : SettingField<String> {

    private var _isUpdated = false
    override val isUpdated: Boolean get() = _isUpdated

    override val component: @Composable (String) -> Unit = { currentValue ->
        val context = LocalContext.current
        var text by remember { mutableStateOf(currentValue) }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocumentTree(),
            onResult = { uri: Uri? ->
                uri?.let {
                    val newPath = it.toString()
                    text = newPath
                    field.field = newPath
                    _isUpdated = true
                }
            }
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = buildAnnotatedString {
                    append(title)
                    if (required) {
                        append(" ")
                        withStyle(SpanStyle(color = Color.Red)) {
                            append("*")
                        }
                    }
                },
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text.ifBlank { texts.emptyText },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Button(onClick = { launcher.launch(null) }) {
                Text(texts.buttonText)
            }
        }
    }
}

data class DirectoryFieldTexts(
    val buttonText: String = "Выбрать директорию",
    val emptyText: String = "Директория не выбрана"
) {
    companion object {
        val DEFAULT = DirectoryFieldTexts()
    }
}
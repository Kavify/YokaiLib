package ru.feryafox.yokailib.root.ui.components.popup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun DefaultPopupCard(
    config: PopupConfig,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(8.dp))
            .fillMaxWidth(0.92f),
        colors = CardDefaults.cardColors(
            containerColor = if (config.tint == Color.Unspecified)
                MaterialTheme.colorScheme.surfaceVariant
            else config.tint
        )
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    config.title?.let {
                        Text(it, style = MaterialTheme.typography.titleMedium)
                    }
                    if (config.isLoading) {
                        Spacer(Modifier.width(12.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
                if (config.dismissible) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(config.message, style = MaterialTheme.typography.bodyMedium)

            if (config.requiredFields.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Column {
                    config.requiredFields.forEach { field ->
                        if (field.needAttention(config.skipDisabled)) {
                            Text("• ${field.title}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

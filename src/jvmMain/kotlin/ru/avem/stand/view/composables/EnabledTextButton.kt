package ru.avem.stand.view.composables

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun EnabledTextButton(text: String, enabled: Boolean = true, color: Color? = null, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(backgroundColor = color ?: MaterialTheme.colors.primary)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.h4 //TODO вынести отдельным параметром (css)
        )
    }
}

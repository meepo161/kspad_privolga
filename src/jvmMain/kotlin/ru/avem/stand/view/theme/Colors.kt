package ru.avem.stand.view.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val colors: Colors
    @Composable
    get() = MaterialTheme.colors.copy(
        primary = Color(0x3F, 0x3F, 0x3F, 0xFF),
        secondary = Color(0x7F, 0x7F, 0x7F, 0x7F)
    )

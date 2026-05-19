package com.panoplia.caminosanctus.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CaminoDarkColors = darkColorScheme(
    primary        = Color(0xFFD4AF37),   // gold
    onPrimary      = Color(0xFF0A0A14),
    background     = Color(0xFF0A0A14),   // near-black
    onBackground   = Color(0xFFE8E0D0),   // warm white
    surface        = Color(0xFF12121E),
    onSurface      = Color(0xFFE8E0D0),
    secondary      = Color(0xFF9A8A70),
    onSecondary    = Color(0xFF0A0A14),
)

@Composable
fun CaminoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CaminoDarkColors,
        content     = content
    )
}

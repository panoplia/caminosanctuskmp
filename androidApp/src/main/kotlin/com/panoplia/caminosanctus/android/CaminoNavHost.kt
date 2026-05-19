package com.panoplia.caminosanctus.android

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun CaminoNavHost() {
    // Phase 1 placeholder — replaced by full NavController graph in Phase 2
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Camino Sanctus", color = Color(0xFFD4AF37))
    }
}

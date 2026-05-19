package com.panoplia.caminosanctus.android.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EncuentroOverlayScreen(
    viewModel: EncuentroViewModel,
    onUnlock: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A14)),
        contentAlignment = Alignment.Center
    ) {
        when (val currentState = state) {
            is EncuentroState.Loading -> {
                CircularProgressIndicator(color = Color(0xFFD4AF37))
            }

            is EncuentroState.Active -> {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "YOU ARE SEEKING DISTRACTION.",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = currentState.card.body,
                        color = Color(0xFFE0E0E0),
                        fontSize = 22.sp,
                        lineHeight = 32.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = currentState.card.source?.let { "— $it" } ?: "",
                        color = Color(0xFFD4AF37),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(64.dp))

                    AnimatedVisibility(
                        visible = currentState.remainingSeconds == 0,
                        enter = fadeIn(animationSpec = tween(1_000)),
                        exit = fadeOut()
                    ) {
                        Button(
                            onClick = {
                                viewModel.grantAccess()
                                onUnlock()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("ENTER APP", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (currentState.remainingSeconds > 0) {
                        Text(
                            text = "BREATHE. WAIT ${currentState.remainingSeconds}s",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            is EncuentroState.Error -> {
                LaunchedEffect(Unit) { onUnlock() }
            }

            is EncuentroState.Unlocked -> {
                LaunchedEffect(Unit) { onUnlock() }
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VoiceVisualizer(
    isListening: Boolean,
    isSpeaking: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "voice_wave")

    val bar1 by transition.animateFloat(
        initialValue = 12f,
        targetValue = if (isListening || isSpeaking) 48f else 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar1"
    )

    val bar2 by transition.animateFloat(
        initialValue = 18f,
        targetValue = if (isListening || isSpeaking) 64f else 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, delayMillis = 100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar2"
    )

    val bar3 by transition.animateFloat(
        initialValue = 24f,
        targetValue = if (isListening || isSpeaking) 80f else 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, delayMillis = 200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar3"
    )

    val bar4 by transition.animateFloat(
        initialValue = 16f,
        targetValue = if (isListening || isSpeaking) 56f else 16f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, delayMillis = 150, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar4"
    )

    val bar5 by transition.animateFloat(
        initialValue = 12f,
        targetValue = if (isListening || isSpeaking) 40f else 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, delayMillis = 50, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar5"
    )

    val glowGradient = Brush.verticalGradient(
        listOf(
            Color(0xFF00E5FF),
            Color(0xFFA855F7),
            Color(0xFF6366F1)
        )
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val heights = listOf(bar1, bar2, bar3, bar4, bar5)
        heights.forEach { heightVal ->
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(heightVal.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(glowGradient)
            )
        }
    }
}

@Composable
fun OrbPulsar(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "orb_pulse")
    val scale by transition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isActive) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        // Outer glow
        Box(
            modifier = Modifier
                .size((110 * scale).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x3300E5FF),
                            Color(0x22A855F7),
                            Color.Transparent
                        )
                    )
                )
        )
        // Mid ring
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF00E5FF),
                            Color(0xFFA855F7)
                        )
                    )
                )
        )
    }
}

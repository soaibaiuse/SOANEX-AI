package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.MainViewModel
import com.example.ui.components.OrbPulsar
import com.example.ui.components.VoiceVisualizer

@Composable
fun VoiceAssistantScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isListening by viewModel.isListening.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val transcript by viewModel.voiceTranscript.collectAsState()
    val currentMessages by viewModel.currentMessages.collectAsState()

    val lastAiMessage = currentMessages.lastOrNull { it.role.equals("ASSISTANT", true) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startListening()
        } else {
            Toast.makeText(context, "Microphone permission is needed for speech recognition", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top status
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Voice Assistant",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "SOANEX Neural Speech Engine",
                fontSize = 12.sp,
                color = Color(0xFF00E5FF),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // State Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        when {
                            isListening -> Color(0xFF00E5FF).copy(alpha = 0.2f)
                            isSpeaking -> Color(0xFFA855F7).copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = when {
                        isListening -> "Listening to your voice..."
                        isSpeaking -> "Speaking response..."
                        else -> "Tap microphone to speak"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = when {
                        isListening -> Color(0xFF00E5FF)
                        isSpeaking -> Color(0xFFA855F7)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Center Pulsing Visualizer
        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            OrbPulsar(isActive = isListening || isSpeaking)
            VoiceVisualizer(
                isListening = isListening,
                isSpeaking = isSpeaking,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Live Transcript / AI Output Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (transcript.isNotBlank()) {
                    Text(
                        text = "You said:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                    Text(
                        text = "\"$transcript\"",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (lastAiMessage != null) {
                    Text(
                        text = "SOANEX AI Response:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA855F7)
                    )
                    Text(
                        text = lastAiMessage.content.take(240) + if (lastAiMessage.content.length > 240) "..." else "",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (isSpeaking) {
                            OutlinedButton(
                                onClick = { viewModel.stopSpeaking() },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Red)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Stop Audio", fontSize = 12.sp)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { viewModel.speakText(lastAiMessage.content) },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF00E5FF))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Listen Audio", fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Speak naturally. For example:\n\"SOANEX, explain the Indian Constitution.\"",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mic Action Button
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        if (isListening) listOf(Color(0xFFF43F5E), Color(0xFFE11D48))
                        else listOf(Color(0xFF00E5FF), Color(0xFFA855F7))
                    )
                )
                .testTag("voice_mic_toggle"),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    if (isListening) {
                        viewModel.stopListening()
                    } else {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasPermission) {
                            viewModel.startListening()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Microphone",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick speech queries
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    viewModel.sendVoiceQuery("SOANEX, explain the Indian Constitution.")
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("📜 Indian Constitution", fontSize = 11.sp)
            }
            OutlinedButton(
                onClick = {
                    viewModel.sendVoiceQuery("Give me a quick productivity tip.")
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("⚡ Productivity Tip", fontSize = 11.sp)
            }
        }
    }
}

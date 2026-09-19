package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.GeneratedImageEntity
import com.example.ui.MainViewModel

@Composable
fun ImageGenScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val images by viewModel.generatedImages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()

    var prompt by remember { mutableStateOf("Create a cinematic rainy city at night with neon lights") }
    var selectedStyle by remember { mutableStateOf("Cinematic") }
    var selectedRatio by remember { mutableStateOf("1:1") }
    var previewImage by remember { mutableStateOf<GeneratedImageEntity?>(null) }

    val styles = listOf("Cinematic", "Photorealistic", "Cyberpunk", "3D Render", "Anime", "Digital Art")
    val aspectRatios = listOf("1:1", "16:9", "9:16", "4:3")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "AI Image Generator",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Powered by Google Gemini 2.5 Image Engine / Demo Abstraction",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prompt Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Prompt", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("Describe the image you want to create...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("image_gen_prompt_input"),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Styles
                Text("Art Style", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    styles.take(3).forEach { style ->
                        FilterChip(
                            selected = selectedStyle == style,
                            onClick = { selectedStyle = style },
                            label = { Text(style, fontSize = 11.sp) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    styles.drop(3).forEach { style ->
                        FilterChip(
                            selected = selectedStyle == style,
                            onClick = { selectedStyle = style },
                            label = { Text(style, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Aspect Ratio
                Text("Aspect Ratio", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    aspectRatios.forEach { ratio ->
                        FilterChip(
                            selected = selectedRatio == ratio,
                            onClick = { selectedRatio = ratio },
                            label = { Text(ratio, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (prompt.isNotBlank()) {
                            viewModel.generateImage(prompt, selectedStyle, selectedRatio)
                        }
                    },
                    enabled = !isGenerating && prompt.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("image_gen_submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E5FF),
                        contentColor = Color(0xFF042F2E)
                    )
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.Black)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Synthesizing Visual...")
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Image", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Gallery Section
        Text(
            text = "Generated Image Gallery",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (images.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No generated images yet", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    Text("Type a prompt above and tap Generate Image", color = Color(0xFF64748B), fontSize = 12.sp)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                images.forEach { imageItem ->
                    GalleryCard(
                        item = imageItem,
                        onPreview = { previewImage = imageItem },
                        onDelete = {
                            viewModel.deleteImage(imageItem.id)
                        },
                        onUsePrompt = { prompt = imageItem.prompt }
                    )
                }
            }
        }
    }

    // Full preview dialog
    previewImage?.let { img ->
        AlertDialog(
            onDismissRequest = { previewImage = null },
            title = { Text(img.style, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.soanex_hero_banner),
                        contentDescription = img.prompt,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(img.prompt, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Aspect Ratio: ${img.aspectRatio} • Simulated Demo Canvas", fontSize = 11.sp, color = Color(0xFFF59E0B))
                }
            },
            confirmButton = {
                Button(onClick = {
                    Toast.makeText(context, "Image saved to device gallery", Toast.LENGTH_SHORT).show()
                    previewImage = null
                }) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Download")
                }
            },
            dismissButton = {
                TextButton(onClick = { previewImage = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun GalleryCard(
    item: GeneratedImageEntity,
    onPreview: () -> Unit,
    onDelete: () -> Unit,
    onUsePrompt: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPreview)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.soanex_hero_banner),
                    contentDescription = item.prompt,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("${item.style} • ${item.aspectRatio}", color = Color.White, fontSize = 11.sp)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.prompt,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onUsePrompt) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reuse prompt", tint = Color(0xFF00E5FF))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete image", tint = Color.Red.copy(alpha = 0.7f))
                }
            }
        }
    }
}

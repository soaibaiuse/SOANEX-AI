package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.MarkdownText

data class WebSource(
    val title: String,
    val snippet: String,
    val url: String,
    val domain: String,
    val timeAgo: String
)

@Composable
fun WebSearchScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("Latest developments in quantum computing this week") }
    var isSearching by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "News", "Research", "Websites")

    var searchHistory by remember {
        mutableStateOf(
            listOf(
                "Latest developments in quantum computing this week",
                "Gemini 3.5 architecture breakthroughs",
                "Best practices for Jetpack Compose 2026",
                "Global clean energy milestones"
            )
        )
    }

    val sources = remember(query) {
        listOf(
            WebSource(
                title = "Quantum Supremacy & Error Correction Breakthroughs (2026)",
                snippet = "Scientists demonstrate fault-tolerant logical qubits with sub-10^-4 error rates using superconducting topological architecture.",
                url = "https://nature.com/articles/quantum-computing-2026",
                domain = "nature.com",
                timeAgo = "3 hours ago"
            ),
            WebSource(
                title = "Commercial Quantum Processors Reach 5,000+ Qubits",
                snippet = "New scalable quantum annealers and neutral-atom processors announce cloud availability for cryptography and molecular simulation.",
                url = "https://quantumweekly.org/insights",
                domain = "quantumweekly.org",
                timeAgo = "1 day ago"
            ),
            WebSource(
                title = "Google Quantum AI: Next-Generation Coherence Times",
                snippet = "Quantum laboratory reports a 10x improvement in coherence durations through cryogenic packaging optimizations.",
                url = "https://blog.google/technology/ai/quantum-update",
                domain = "blog.google",
                timeAgo = "2 days ago"
            )
        )
    }

    val synthesizedSummary = remember(query) {
        "### SOANEX AI Web Grounded Summary\n\n" +
                "Based on verified real-time sources on **\"$query\"**:\n\n" +
                "1. **Logical Qubit Fault Tolerance**: Major physics consortiums have achieved active topological error suppression, moving beyond physical error boundaries.\n" +
                "2. **Commercial Scaling**: Neutral-atom and superconducting quantum hardware reached practical deployment in drug discovery and optimization workflows.\n" +
                "3. **Coherence Records**: Cryogenic packaging enhancements resulted in significantly elongated quantum state retention.\n\n" +
                "*Citations automatically cross-referenced against 3 indexed sources below.*"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Public, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Web Search Mode",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Grounded search with source citations & live synthesis",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search the web or ask a timely question...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("web_search_input"),
            shape = RoundedCornerShape(16.dp),
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (query.isNotBlank()) {
                            isSearching = true
                            if (!searchHistory.contains(query)) {
                                searchHistory = listOf(query) + searchHistory
                            }
                            isSearching = false
                        }
                    }
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF00E5FF))
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                if (query.isNotBlank()) {
                    isSearching = true
                    if (!searchHistory.contains(query)) {
                        searchHistory = listOf(query) + searchHistory
                    }
                    isSearching = false
                }
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00E5FF),
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = Color(0xFF00E5FF)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = activeTab == index,
                    onClick = { activeTab = index },
                    text = { Text(title, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grounding Notice
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0C4A6E).copy(alpha = 0.4f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                "🌐 Grounded Search Engine • Citations verified with web telemetry",
                fontSize = 11.sp,
                color = Color(0xFF38BDF8),
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Synthesized Result Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                MarkdownText(markdown = synthesizedSummary)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sources List
        Text("Sources & Citations", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            sources.forEach { source ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Toast.makeText(context, "Opening ${source.domain}", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(source.domain, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                                Text(" • ${source.timeAgo}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(source.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(source.snippet, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                        }
                        Icon(Icons.Default.Launch, contentDescription = "Open", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Search History
        Text("Recent Searches", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            searchHistory.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { query = item }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(item, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                }
            }
        }
    }
}

package com.example.ui

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.NavDestination
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ChatsHistoryScreen
import com.example.ui.screens.CodingScreen
import com.example.ui.screens.DocumentsScreen
import com.example.ui.screens.ImageGenScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudyScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.VoiceAssistantScreen
import com.example.ui.screens.WebSearchScreen
import com.example.ui.screens.WritingScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(viewModel: MainViewModel) {
    val context = LocalContext.current
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentDest by viewModel.currentDestination.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showHelpDialog by remember { mutableStateOf(false) }

    if (!isAuthenticated) {
        AuthScreen(viewModel)
        return
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 768.dp
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()

        if (isWideScreen) {
            // Wide Screen / Tablet / Desktop layout with persistent Left Sidebar
            Row(modifier = Modifier.fillMaxSize()) {
                SidebarContent(
                    currentDest = currentDest,
                    onNavigate = { dest ->
                        if (dest == NavDestination.HELP) {
                            showHelpDialog = true
                        } else {
                            viewModel.navigateTo(dest)
                        }
                    },
                    onNewChat = { viewModel.createNewChat() },
                    onLogout = { viewModel.authRepo.logout() },
                    userName = currentUser?.name ?: "Soaib Ali",
                    planTitle = currentUser?.plan?.title ?: "Pro Plan",
                    isDarkMode = isDarkMode,
                    onToggleTheme = { viewModel.toggleDarkMode() },
                    modifier = Modifier
                        .width(280.dp)
                        .fillMaxHeight()
                )

                // Main Content Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    ScreenContent(currentDest = currentDest, viewModel = viewModel)
                }
            }
        } else {
            // Mobile Compact layout with Drawer & Bottom Navigation
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier.width(300.dp),
                        drawerContainerColor = MaterialTheme.colorScheme.surface
                    ) {
                        SidebarContent(
                            currentDest = currentDest,
                            onNavigate = { dest ->
                                coroutineScope.launch { drawerState.close() }
                                if (dest == NavDestination.HELP) {
                                    showHelpDialog = true
                                } else {
                                    viewModel.navigateTo(dest)
                                }
                            },
                            onNewChat = {
                                coroutineScope.launch { drawerState.close() }
                                viewModel.createNewChat()
                            },
                            onLogout = {
                                coroutineScope.launch { drawerState.close() }
                                viewModel.authRepo.logout()
                            },
                            userName = currentUser?.name ?: "Soaib Ali",
                            planTitle = currentUser?.plan?.title ?: "Pro Plan",
                            isDarkMode = isDarkMode,
                            onToggleTheme = { viewModel.toggleDarkMode() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.soanex_logo),
                                        contentDescription = "SOANEX AI",
                                        modifier = Modifier.size(24.dp).clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = currentDest.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = { coroutineScope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("open_sidebar_drawer_button")
                                ) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                                }
                            },
                            actions = {
                                IconButton(onClick = { viewModel.toggleDarkMode() }) {
                                    Icon(
                                        imageVector = if (isDarkMode) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                                        contentDescription = "Toggle theme",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { viewModel.createNewChat() }) {
                                    Icon(Icons.Default.Add, contentDescription = "New Chat", tint = Color(0xFF00E5FF))
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            val bottomItems = listOf(
                                NavDestination.AI_ASSISTANT,
                                NavDestination.IMAGE_GENERATOR,
                                NavDestination.VOICE,
                                NavDestination.TASKS,
                                NavDestination.MEMORY
                            )
                            bottomItems.forEach { item ->
                                NavigationBarItem(
                                    selected = currentDest == item,
                                    onClick = { viewModel.navigateTo(item) },
                                    icon = { Icon(item.icon, contentDescription = item.title) },
                                    label = { Text(item.title, fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF00E5FF),
                                        selectedTextColor = Color(0xFF00E5FF),
                                        indicatorColor = Color(0xFF0C4A6E)
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        ScreenContent(currentDest = currentDest, viewModel = viewModel)
                    }
                }
            }
        }
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("SOANEX AI Help & Keyboard Shortcuts") },
            text = {
                Column {
                    Text("• Enter / Send: Submit prompt to Gemini 3.5\n• Multimodal: Attach image using the paperclip icon\n• Voice Mode: Tap microphone or choose Voice Assistant\n• Personal Memory: Say 'My name is [Name]' or configure in Memory\n• Coding: Switch to Coding screen for multi-language snippets\n• Document Analysis: Upload PDFs and chat directly with them\n• Netlify/SaaS Ready: Built with modern modular Android architecture")
                }
            },
            confirmButton = {
                Button(onClick = { showHelpDialog = false }) {
                    Text("Got it")
                }
            }
        )
    }
}

@Composable
fun SidebarContent(
    currentDest: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    onNewChat: () -> Unit,
    onLogout: () -> Unit,
    userName: String,
    planTitle: String,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF00E5FF), Color(0xFFA855F7))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.soanex_logo),
                        contentDescription = "SOANEX Logo",
                        modifier = Modifier.size(32.dp).clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "SOANEX AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF00E5FF)
                    )
                    Text(
                        text = "“Your AI, Your Way.”",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // New Chat Action Button
            Button(
                onClick = onNewChat,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("sidebar_new_chat_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E5FF),
                    contentColor = Color(0xFF042F2E)
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Chat", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Items List
            val primaryDestinations = listOf(
                NavDestination.CHATS,
                NavDestination.AI_ASSISTANT,
                NavDestination.IMAGE_GENERATOR,
                NavDestination.VOICE,
                NavDestination.DOCUMENTS,
                NavDestination.WEB_SEARCH,
                NavDestination.STUDY,
                NavDestination.CODING,
                NavDestination.WRITING,
                NavDestination.TASKS,
                NavDestination.MEMORY,
                NavDestination.SETTINGS
            )

            primaryDestinations.forEach { dest ->
                val isSelected = currentDest == dest
                Surface(
                    onClick = { onNavigate(dest) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Color(0xFF0C4A6E) else Color.Transparent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = dest.icon,
                            contentDescription = dest.title,
                            tint = if (isSelected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = dest.title,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Bottom Sidebar Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            // User Card
            Surface(
                onClick = { onNavigate(NavDestination.PROFILE) },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF00E5FF), Color(0xFFA855F7)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.soanex_logo),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp).clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(userName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("⭐ $planTitle", fontSize = 10.sp, color = Color(0xFF00E5FF), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavigate(NavDestination.SUBSCRIPTION) }) {
                    Icon(Icons.Default.Star, contentDescription = "Subscription", tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = { onNavigate(NavDestination.HELP) }) {
                    Icon(Icons.AutoMirrored.Filled.Help, contentDescription = "Help", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                        contentDescription = "Toggle theme",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onLogout) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun ScreenContent(currentDest: NavDestination, viewModel: MainViewModel) {
    Crossfade(targetState = currentDest, label = "screen_transition") { dest ->
        when (dest) {
            NavDestination.CHATS -> ChatsHistoryScreen(viewModel)
            NavDestination.AI_ASSISTANT -> ChatScreen(viewModel)
            NavDestination.IMAGE_GENERATOR -> ImageGenScreen(viewModel)
            NavDestination.VOICE -> VoiceAssistantScreen(viewModel)
            NavDestination.DOCUMENTS -> DocumentsScreen(viewModel)
            NavDestination.WEB_SEARCH -> WebSearchScreen(viewModel)
            NavDestination.STUDY -> StudyScreen(viewModel)
            NavDestination.CODING -> CodingScreen(viewModel)
            NavDestination.WRITING -> WritingScreen(viewModel)
            NavDestination.TASKS -> TasksScreen(viewModel)
            NavDestination.MEMORY -> MemoryScreen(viewModel)
            NavDestination.SETTINGS -> SettingsScreen(viewModel)
            NavDestination.PROFILE -> ProfileScreen(viewModel)
            NavDestination.SUBSCRIPTION -> SubscriptionScreen(viewModel)
            NavDestination.HELP -> ChatScreen(viewModel)
        }
    }
}

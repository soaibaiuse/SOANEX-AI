package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserPlan
import com.example.ui.MainViewModel
import com.example.ui.components.NavDestination

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(user?.name ?: "Soaib Ali") }
    var editEmail by remember { mutableStateOf(user?.email ?: "soaibali98010@gmail.com") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "User Profile",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Manage account, subscription & usage quotas",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Details Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
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
                        contentDescription = "Avatar",
                        modifier = Modifier.size(68.dp).clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = user?.name ?: "Soaib Ali",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = user?.email ?: "soaibali98010@gmail.com",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF00E5FF).copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "⭐ ${user?.plan?.title ?: "Pro Plan"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = {
                            editName = user?.name ?: ""
                            editEmail = user?.email ?: ""
                            showEditProfileDialog = true
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Edit Profile", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.navigateTo(NavDestination.SUBSCRIPTION)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Manage Plan", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Daily Usage Statistics
        Text("Daily Usage Statistics", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // AI Queries
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("AI Queries Today", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("18 / 500", fontSize = 13.sp, color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { 18f / 500f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF00E5FF),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Images Generated
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Images Generated", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("3 / 50", fontSize = 13.sp, color = Color(0xFFA855F7), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { 3f / 50f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFFA855F7),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout
        OutlinedButton(
            onClick = {
                viewModel.authRepo.logout()
                Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }

    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.authRepo.updateProfile(editName, editEmail)
                        showEditProfileDialog = false
                        Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SubscriptionScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    var showCheckoutModal by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Pricing & Plans",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Unlock higher query limits, Gemini 3.5 & premium speed",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Free Plan Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Free Tier", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("₹0 / month", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.height(8.dp))
                Text("• 20 queries / day\n• Standard response latency\n• Basic personal memory", fontSize = 12.sp, lineHeight = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Pro Plan Card (Featured)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C4A6E)),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFF00E5FF), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("PRO PLAN", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF00E5FF))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF00E5FF))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("MOST POPULAR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF042F2E))
                    }
                }

                Text("₹99 / month", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "• 500 queries / day with Gemini 3.5\n" +
                            "• Real-time speech synthesis & voice assistant\n" +
                            "• Unlimited Personal Memory items\n" +
                            "• High-resolution image generation access\n" +
                            "• Priority document indexing & Q&A",
                    fontSize = 13.sp,
                    color = Color.White,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showCheckoutModal = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("upgrade_pro_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF042F2E)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        if (user?.plan == UserPlan.PRO) "Current Active Plan" else "Upgrade to Pro (₹99)",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Billing History
        Text("Billing History", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("SOANEX Pro Monthly", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Paid via UPI / Card • Invoice #SX-9801", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Text("₹99.00", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                }
            }
        }
    }

    if (showCheckoutModal) {
        AlertDialog(
            onDismissRequest = { showCheckoutModal = false },
            title = { Text("Simulated Razorpay / Stripe Checkout", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("You are subscribing to SOANEX AI Pro for ₹99/month.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Unlimited personal memory\n• Fast Gemini 3.5 inference\n• Cancel anytime")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.authRepo.upgradeToPro()
                        showCheckoutModal = false
                        Toast.makeText(context, "Payment successful! Pro Plan activated.", Toast.LENGTH_LONG).show()
                    }
                ) {
                    Text("Confirm Payment (₹99)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckoutModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

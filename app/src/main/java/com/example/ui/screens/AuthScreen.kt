package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MainViewModel

enum class AuthMode {
    LOGIN,
    SIGNUP,
    FORGOT_PASSWORD
}

@Composable
fun AuthScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }

    var name by remember { mutableStateOf("Soaib Ali") }
    var email by remember { mutableStateOf("soaibali98010@gmail.com") }
    var password by remember { mutableStateOf("soanex123456") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF080C14),
                        Color(0xFF0F172A),
                        Color(0xFF090D16)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF111827).copy(alpha = 0.95f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // SOANEX AI Logo
                Box(
                    modifier = Modifier
                        .size(68.dp)
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
                        contentDescription = "SOANEX AI Logo",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "SOANEX AI",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF00E5FF)
                )

                Text(
                    text = "“Your AI, Your Way.”",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = when (authMode) {
                        AuthMode.LOGIN -> "Sign In to Your Account"
                        AuthMode.SIGNUP -> "Create Your Account"
                        AuthMode.FORGOT_PASSWORD -> "Reset Password"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (authMode == AuthMode.SIGNUP) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF38BDF8))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF38BDF8))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_email_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                if (authMode != AuthMode.FORGOT_PASSWORD) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF38BDF8))
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        when (authMode) {
                            AuthMode.LOGIN -> {
                                if (viewModel.authRepo.login(email, password)) {
                                    Toast.makeText(context, "Welcome back, $email!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Please enter a valid email and password", Toast.LENGTH_SHORT).show()
                                }
                            }
                            AuthMode.SIGNUP -> {
                                if (viewModel.authRepo.signup(name, email, password)) {
                                    Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Please complete all fields (password min 6 chars)", Toast.LENGTH_SHORT).show()
                                }
                            }
                            AuthMode.FORGOT_PASSWORD -> {
                                Toast.makeText(context, "Password reset instructions sent to $email", Toast.LENGTH_LONG).show()
                                authMode = AuthMode.LOGIN
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("auth_submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E5FF),
                        contentColor = Color(0xFF042F2E)
                    )
                ) {
                    Text(
                        text = when (authMode) {
                            AuthMode.LOGIN -> "Log In"
                            AuthMode.SIGNUP -> "Sign Up"
                            AuthMode.FORGOT_PASSWORD -> "Send Reset Link"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (authMode == AuthMode.LOGIN) {
                        TextButton(onClick = { authMode = AuthMode.FORGOT_PASSWORD }) {
                            Text("Forgot password?", color = Color(0xFF38BDF8), fontSize = 13.sp)
                        }
                        TextButton(onClick = { authMode = AuthMode.SIGNUP }) {
                            Text("Create account", color = Color(0xFFA855F7), fontSize = 13.sp)
                        }
                    } else {
                        TextButton(onClick = { authMode = AuthMode.LOGIN }) {
                            Text("Already have an account? Sign In", color = Color(0xFF38BDF8), fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

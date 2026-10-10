package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.BilimViewModel
import com.example.ui.components.BilimCard
import com.example.ui.theme.BlueGeography
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.DarkNavyInputBg
import com.example.ui.theme.GoldHistory
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusAbsentContainer
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite

@Composable
fun LoginScreen(
    viewModel: BilimViewModel,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showSetupDialog by remember { mutableStateOf(false) }

    val isLoggingIn by viewModel.isLoggingIn.collectAsStateWithLifecycle(initialValue = false)
    val loginError by viewModel.loginError.collectAsStateWithLifecycle(initialValue = null)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Branding
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(GoldHistory.copy(alpha = 0.25f), DarkNavyBg)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_logo),
                    contentDescription = "Bilim Markazi logotipi",
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Bilim Markazi",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Text(
                text = "Tizimga kirish uchun hisobingizni tanlang",
                fontSize = 13.sp,
                color = TextMutedGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Card
            BilimCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "Shaxsiy kabinetga kirish",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Error banner
                    AnimatedVisibility(visible = loginError != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = StatusAbsentContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusAbsent.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = loginError ?: "",
                                color = StatusAbsent,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Username Input
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input"),
                        label = { Text("Foydalanuvchi nomi", color = TextMutedGray) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = GoldHistory)
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldHistory,
                            unfocusedBorderColor = DarkNavyCardBorder,
                            focusedContainerColor = DarkNavyInputBg,
                            unfocusedContainerColor = DarkNavyInputBg,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        label = { Text("Parol", color = TextMutedGray) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = GoldHistory)
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (isPasswordVisible) "Parolni berkitish" else "Parolni ko'rsatish",
                                    tint = TextMutedGray
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldHistory,
                            unfocusedBorderColor = DarkNavyCardBorder,
                            focusedContainerColor = DarkNavyInputBg,
                            unfocusedContainerColor = DarkNavyInputBg,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if (!isLoggingIn) viewModel.login(username, password)
                        })
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Submit Button
                    Button(
                        onClick = { viewModel.login(username, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_button"),
                        enabled = !isLoggingIn && username.isNotBlank() && password.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldHistory,
                            contentColor = DarkNavyBg,
                            disabledContainerColor = GoldHistory.copy(alpha = 0.3f),
                            disabledContentColor = DarkNavyBg.copy(alpha = 0.5f)
                        )
                    ) {
                        if (isLoggingIn) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = DarkNavyBg,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Tizimga kirish",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Demo Role Switcher
            Text(
                text = "Sinov uchun tezkor rollar:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMutedGray
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DemoRoleChip(
                        title = "Administrator",
                        subtitle = "admin",
                        modifier = Modifier.weight(1f),
                        accentColor = GoldHistory
                    ) {
                        username = "admin"
                        password = "Admin@2026!"
                        viewModel.login("admin", "Admin@2026!")
                    }

                    DemoRoleChip(
                        title = "Ota-ona",
                        subtitle = "dilnoza.karimova",
                        modifier = Modifier.weight(1f),
                        accentColor = BlueGeography
                    ) {
                        username = "dilnoza.karimova"
                        password = "Otaona@2026!"
                        viewModel.login("dilnoza.karimova", "Otaona@2026!")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DemoRoleChip(
                        title = "Ustoz (Tarix)",
                        subtitle = "ustoz.tarix",
                        modifier = Modifier.weight(1f),
                        accentColor = GoldHistory
                    ) {
                        username = "ustoz.tarix"
                        password = "Ustoz@2026!"
                        viewModel.login("ustoz.tarix", "Ustoz@2026!")
                    }

                    DemoRoleChip(
                        title = "Ustoz (Geografiya)",
                        subtitle = "ustoz.geo",
                        modifier = Modifier.weight(1f),
                        accentColor = BlueGeography
                    ) {
                        username = "ustoz.geo"
                        password = "Ustoz@2026!"
                        viewModel.login("ustoz.geo", "Ustoz@2026!")
                    }
                }

                DemoRoleChip(
                    title = "O'quvchi (9-A)",
                    subtitle = "sardor.karimov (Sardor)",
                    modifier = Modifier.fillMaxWidth(),
                    accentColor = GoldHistory
                ) {
                    username = "sardor.karimov"
                    password = "Talaba@2026!"
                    viewModel.login("sardor.karimov", "Talaba@2026!")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // First-time setup / Security Info
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showSetupDialog = true }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = TextMutedGray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Xavfsizlik va dastlabki sozlash haqida",
                    fontSize = 12.sp,
                    color = TextMutedGray
                )
            }
        }

        if (showSetupDialog) {
            AlertDialog(
                onDismissRequest = { showSetupDialog = false },
                title = {
                    Text("Xavfsiz Tizim Sozlamalari", color = TextWhite, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text(
                            text = "• Parollar ochiq holda saqlanmaydi, SHA-256 + 128-bitli tasodifiy tuz (salt) bilan shifrlangan.\n\n" +
                                    "• Ruxsatlar va rollar bazaviy qatlamda (Repository) tekshiriladi.\n\n" +
                                    "• Administrator barcha foydalanuvchilar, dars jurnallari va tizim auditini to'liq boshqaradi.",
                            color = TextMutedGray,
                            fontSize = 14.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSetupDialog = false }) {
                        Text("Tushundim", color = GoldHistory)
                    }
                },
                containerColor = com.example.ui.theme.DarkNavySurface
            )
        }
    }
}

@Composable
private fun DemoRoleChip(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    accentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
        color = DarkNavyInputBg
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextMutedGray
            )
        }
    }
}

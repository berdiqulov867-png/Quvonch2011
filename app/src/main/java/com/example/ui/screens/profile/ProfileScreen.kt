package com.example.ui.screens.profile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserRole
import com.example.ui.BilimViewModel
import com.example.ui.components.BilimCard
import com.example.ui.theme.BlueGeography
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.DarkNavyInputBg
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldHistory
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite

@Composable
fun ProfileScreen(
    viewModel: BilimViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var showChangePassDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Shaxsiy Profil va Xavfsizlik",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Text(
                text = "Foydalanuvchi ma'lumotlari va tizim sozlamalari",
                fontSize = 12.sp,
                color = TextMutedGray
            )
        }

        // Profile Avatar Card
        item {
            BilimCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(GoldHistory, BlueGeography)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.fullName?.take(1)?.uppercase() ?: "U",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = currentUser?.fullName ?: "Foydalanuvchi",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "@${currentUser?.username}",
                            fontSize = 13.sp,
                            color = GoldHistory
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DarkNavyInputBg,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            val roleUz = when (currentUser?.role) {
                                UserRole.ADMIN.name -> "Administrator (Direktor)"
                                UserRole.TEACHER.name -> "O'qituvchi (${currentUser?.assignedSubject?.uppercase()})"
                                UserRole.STUDENT.name -> "${currentUser?.className} sinf o'quvchisi"
                                UserRole.PARENT.name -> "Ota-ona kabineti"
                                else -> "Foydalanuvchi"
                            }
                            Text(
                                text = roleUz,
                                fontSize = 11.sp,
                                color = TextMutedGray,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Contact & Account Details
        item {
            BilimCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Hisob ma'lumotlari",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    ProfileInfoRow(label = "Telefon raqam", value = currentUser?.phone?.ifBlank { "+998 -- --- -- --" } ?: "+998 -- --- -- --")
                    ProfileInfoRow(label = "Tizimdagi ID", value = currentUser?.id ?: "—")
                    ProfileInfoRow(label = "Holati", value = if (currentUser?.isArchived == true) "Arxivlangan" else "Faol hisob")
                    ProfileInfoRow(label = "Xavfsizlik shifrlash", value = "SHA-256 + 128-bit Salt")
                }
            }
        }

        // Actions: Change Password, Logout
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { showChangePassDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkNavySurface,
                        contentColor = GoldHistory
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldHistory.copy(alpha = 0.4f))
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Parolni o'zgartirish", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusAbsent.copy(alpha = 0.15f),
                        contentColor = StatusAbsent
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusAbsent.copy(alpha = 0.4f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tizimdan chiqish", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // System Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bilim Markazi v1.0",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldHistory
                )
                Text(
                    text = "eMaktab uslubidagi mustaqil ta'lim boshqaruv platformasi",
                    fontSize = 11.sp,
                    color = TextMutedGray
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showChangePassDialog) {
        var newPass by remember { mutableStateOf("") }
        var confirmPass by remember { mutableStateOf("") }
        var errorMsg by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showChangePassDialog = false },
            title = { Text("Parolni o'zgartirish", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (errorMsg != null) {
                        Text(text = errorMsg ?: "", color = StatusAbsent, fontSize = 12.sp)
                    }
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("Yangi parol", color = TextMutedGray) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                    OutlinedTextField(
                        value = confirmPass,
                        onValueChange = { confirmPass = it },
                        label = { Text("Yangi parolni tasdiqlang", color = TextMutedGray) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPass.length < 6) {
                            errorMsg = "Parol kamida 6 belgidan iborat bo'lishi kerak"
                            return@Button
                        }
                        if (newPass != confirmPass) {
                            errorMsg = "Parollar bir xil emas"
                            return@Button
                        }
                        currentUser?.let { user ->
                            viewModel.resetPassword(user.id, newPass)
                            showChangePassDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldHistory, contentColor = Color.Black)
                ) {
                    Text("O'zgartirish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePassDialog = false }) { Text("Bekor qilish", color = TextMutedGray) }
            },
            containerColor = DarkNavySurface
        )
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextMutedGray, fontSize = 13.sp)
        Text(text = value, color = TextWhite, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    }
}

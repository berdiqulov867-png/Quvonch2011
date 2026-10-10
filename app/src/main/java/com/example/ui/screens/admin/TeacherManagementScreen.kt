package com.example.ui.screens.admin

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.BilimViewModel
import com.example.ui.components.BilimCard
import com.example.ui.components.SubjectBadge
import com.example.ui.theme.BlueGeography
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldHistory
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite

@Composable
fun TeacherManagementScreen(
    viewModel: BilimViewModel,
    modifier: Modifier = Modifier
) {
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val teachers = users.filter { it.role == UserRole.TEACHER.name }

    var showAddDialog by remember { mutableStateOf(false) }
    var editingPermissionsTeacher by remember { mutableStateOf<UserEntity?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "O'qituvchilar va Fan Biriktirmalari",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = "Tarix va Geografiya o'qituvchilari hisoblari va huquqlari nazorati",
                    fontSize = 12.sp,
                    color = TextMutedGray
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            items(teachers) { teacher ->
                BilimCard {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val isTarix = teacher.assignedSubject == "tarix"
                            val subjectColor = if (isTarix) GoldHistory else BlueGeography

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(subjectColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = subjectColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = teacher.fullName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    SubjectBadge(subjectId = teacher.assignedSubject)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "@${teacher.username}",
                                        fontSize = 11.sp,
                                        color = TextMutedGray
                                    )
                                }
                            }

                            IconButton(onClick = { editingPermissionsTeacher = teacher }) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Huquqlar",
                                    tint = GoldHistory
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Permissions Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Baho qo'yish: ${if (teacher.canGrade) "Ruxsat etilgan" else "Taqiqlangan"}",
                                fontSize = 12.sp,
                                color = if (teacher.canGrade) GoldHistory else TextMutedGray
                            )
                            Text(
                                text = "Test yaratish: ${if (teacher.canCreateTests) "Ruxsat etilgan" else "Taqiqlangan"}",
                                fontSize = 12.sp,
                                color = if (teacher.canCreateTests) BlueGeography else TextMutedGray
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = GoldHistory,
            contentColor = Color.Black
        ) {
            Icon(Icons.Default.Add, contentDescription = "O'qituvchi qo'shish")
        }
    }

    if (showAddDialog) {
        AddTeacherDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, username, subj, phone, pass ->
                viewModel.addTeacher(name, username, subj, phone, pass)
                showAddDialog = false
            }
        )
    }

    editingPermissionsTeacher?.let { teacher ->
        TeacherPermissionsDialog(
            teacher = teacher,
            onDismiss = { editingPermissionsTeacher = null },
            onConfirm = { canGrade, canTests ->
                viewModel.updateTeacherPermissions(teacher.id, canGrade, canTests)
                editingPermissionsTeacher = null
            }
        )
    }
}

@Composable
private fun AddTeacherDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("tarix") }
    var phone by remember { mutableStateOf("+998 ") }
    var pass by remember { mutableStateOf("Ustoz@2026!") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yangi o'qituvchi qo'shish", color = TextWhite, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("F.I.SH", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Foydalanuvchi nomi", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                Text("Biriktiriladigan fan:", color = TextMutedGray, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedSubject == "tarix",
                        onClick = { selectedSubject = "tarix" },
                        label = { Text("Tarix (Oltin)") }
                    )
                    FilterChip(
                        selected = selectedSubject == "geografiya",
                        onClick = { selectedSubject = "geografiya" },
                        label = { Text("Geografiya (Moviy)") }
                    )
                }
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Telefon raqami", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = { Text("Boshlang'ich parol", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && username.isNotBlank()) {
                        onConfirm(name, username, selectedSubject, phone, pass)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldHistory, contentColor = Color.Black)
            ) {
                Text("Qo'shish")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Bekor qilish", color = TextMutedGray)
            }
        },
        containerColor = DarkNavySurface
    )
}

@Composable
private fun TeacherPermissionsDialog(
    teacher: UserEntity,
    onDismiss: () -> Unit,
    onConfirm: (Boolean, Boolean) -> Unit
) {
    var canGrade by remember { mutableStateOf(teacher.canGrade) }
    var canCreateTests by remember { mutableStateOf(teacher.canCreateTests) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${teacher.fullName} huquqlari", color = TextWhite, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Baho qo'yish huquqi", color = TextWhite, fontSize = 14.sp)
                        Text("Jurnalga baho kiritish va o'zgartirish", color = TextMutedGray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = canGrade,
                        onCheckedChange = { canGrade = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldHistory, checkedTrackColor = GoldHistory.copy(alpha = 0.5f))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Test yaratish huquqi", color = TextWhite, fontSize = 14.sp)
                        Text("O'quvchilar uchun testlar tuzish", color = TextMutedGray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = canCreateTests,
                        onCheckedChange = { canCreateTests = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = BlueGeography, checkedTrackColor = BlueGeography.copy(alpha = 0.5f))
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(canGrade, canCreateTests) },
                colors = ButtonDefaults.buttonColors(containerColor = GoldHistory, contentColor = Color.Black)
            ) {
                Text("Saqlash")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Bekor qilish", color = TextMutedGray)
            }
        },
        containerColor = DarkNavySurface
    )
}

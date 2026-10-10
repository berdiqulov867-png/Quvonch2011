package com.example.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
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
import com.example.ui.components.GradeBadge
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.DarkNavyInputBg
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldHistory
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusPresent
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite

@Composable
fun StudentManagementScreen(
    viewModel: BilimViewModel,
    modifier: Modifier = Modifier
) {
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val classes by viewModel.classes.collectAsStateWithLifecycle()
    val grades by viewModel.grades.collectAsStateWithLifecycle()
    val attendance by viewModel.attendanceList.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedClassFilter by remember { mutableStateOf("BARCHASI") }

    var showAddDialog by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<UserEntity?>(null) }
    var viewingStudent by remember { mutableStateOf<UserEntity?>(null) }
    var resetPassStudent by remember { mutableStateOf<UserEntity?>(null) }

    val allStudents = users.filter { it.role == UserRole.STUDENT.name }
    val filteredStudents = allStudents.filter { student ->
        val matchesClass = if (selectedClassFilter == "BARCHASI") true else student.className == selectedClassFilter
        val matchesSearch = student.fullName.contains(searchQuery, ignoreCase = true) ||
                student.username.contains(searchQuery, ignoreCase = true)
        matchesClass && matchesSearch
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ism yoki login bo'yicha qidirish...", color = TextMutedGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldHistory) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = TextMutedGray)
                        }
                    }
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
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Class Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedClassFilter == "BARCHASI",
                        onClick = { selectedClassFilter = "BARCHASI" },
                        label = { Text("Barchasi (${allStudents.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldHistory,
                            selectedLabelColor = Color.Black,
                            containerColor = DarkNavySurface,
                            labelColor = TextWhite
                        )
                    )
                }
                items(classes) { cl ->
                    val count = allStudents.count { it.className == cl.id }
                    FilterChip(
                        selected = selectedClassFilter == cl.id,
                        onClick = { selectedClassFilter = cl.id },
                        label = { Text("${cl.name} ($count)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldHistory,
                            selectedLabelColor = Color.Black,
                            containerColor = DarkNavySurface,
                            labelColor = TextWhite
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Students List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredStudents) { student ->
                    BilimCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (student.isArchived) TextMutedGray.copy(alpha = 0.2f) else GoldHistory.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = student.fullName.take(1).uppercase(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (student.isArchived) TextMutedGray else GoldHistory
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = student.fullName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    if (student.isArchived) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = StatusAbsent.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "Arxiv",
                                                fontSize = 10.sp,
                                                color = StatusAbsent,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "Sinf: ${student.className} • @${student.username}",
                                    fontSize = 12.sp,
                                    color = TextMutedGray
                                )
                                if (student.phone.isNotBlank()) {
                                    Text(
                                        text = student.phone,
                                        fontSize = 11.sp,
                                        color = GoldHistory
                                    )
                                }
                            }

                            // Actions
                            Row {
                                IconButton(onClick = { viewingStudent = student }) {
                                    Icon(Icons.Default.Visibility, contentDescription = "Profil", tint = TextMutedGray)
                                }
                                IconButton(onClick = { editingStudent = student }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Tahrirlash", tint = GoldHistory)
                                }
                                IconButton(onClick = { resetPassStudent = student }) {
                                    Icon(Icons.Default.LockReset, contentDescription = "Parolni tiklash", tint = TextMutedGray)
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Add Floating Action Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = GoldHistory,
            contentColor = Color.Black
        ) {
            Icon(Icons.Default.Add, contentDescription = "O'quvchi qo'shish")
        }
    }

    // Add Student Dialog
    if (showAddDialog) {
        AddStudentDialog(
            classes = classes.map { it.id },
            onDismiss = { showAddDialog = false },
            onConfirm = { name, username, cls, phone, pass ->
                viewModel.addStudent(name, username, cls, phone, pass)
                showAddDialog = false
            }
        )
    }

    // Edit Student Dialog
    editingStudent?.let { student ->
        EditStudentDialog(
            student = student,
            classes = classes.map { it.id },
            onDismiss = { editingStudent = null },
            onConfirm = { updated ->
                viewModel.updateStudent(updated)
                editingStudent = null
            }
        )
    }

    // Reset Password Dialog
    resetPassStudent?.let { student ->
        ResetPasswordDialog(
            student = student,
            onDismiss = { resetPassStudent = null },
            onConfirm = { newPass ->
                viewModel.resetPassword(student.id, newPass)
                resetPassStudent = null
            }
        )
    }

    // View Student Profile Modal
    viewingStudent?.let { student ->
        val studentGrades = grades.filter { it.studentId == student.id }
        val studentAtt = attendance.filter { it.studentId == student.id }

        AlertDialog(
            onDismissRequest = { viewingStudent = null },
            title = {
                Text(text = student.fullName, color = TextWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    val phoneDisplay = if (student.phone.isNotBlank()) student.phone else "Noma'lum"
                    Text(text = "Sinf: ${student.className} | Foydalanuvchi: ${student.username}", color = TextMutedGray, fontSize = 13.sp)
                    Text(text = "Telefon: $phoneDisplay", color = GoldHistory, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Oxirgi baholari:", fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    if (studentGrades.isEmpty()) {
                        Text(text = "Hozircha baholar mavjud emas", color = TextMutedGray, fontSize = 12.sp)
                    } else {
                        studentGrades.take(4).forEach { g ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = g.topic, fontSize = 12.sp, color = TextWhite, maxLines = 1)
                                    Text(text = "${g.subjectId.uppercase()} • ${g.lessonDate}", fontSize = 11.sp, color = TextMutedGray)
                                }
                                GradeBadge(score = g.score)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Davomat yozuvlari: ${studentAtt.size} ta dars qayd etilgan", color = TextMutedGray, fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { viewingStudent = null }) {
                    Text("Yopish", color = GoldHistory)
                }
            },
            containerColor = DarkNavySurface
        )
    }
}

@Composable
private fun AddStudentDialog(
    classes: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, user: String, cls: String, phone: String, pass: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf(classes.firstOrNull() ?: "9-A") }
    var phone by remember { mutableStateOf("+998 ") }
    var pass by remember { mutableStateOf("Talaba@2026!") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yangi o'quvchi qo'shish", color = TextWhite, fontWeight = FontWeight.Bold) },
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
                    label = { Text("Foydalanuvchi nomi (login)", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                Text("Biriktiriladigan sinf:", color = TextMutedGray, fontSize = 12.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(classes) { cls ->
                        FilterChip(
                            selected = selectedClass == cls,
                            onClick = { selectedClass = cls },
                            label = { Text(cls) }
                        )
                    }
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
                        onConfirm(name, username, selectedClass, phone, pass)
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
private fun EditStudentDialog(
    student: UserEntity,
    classes: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (UserEntity) -> Unit
) {
    var name by remember { mutableStateOf(student.fullName) }
    var selectedClass by remember { mutableStateOf(student.className) }
    var phone by remember { mutableStateOf(student.phone) }
    var isArchived by remember { mutableStateOf(student.isArchived) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("O'quvchini tahrirlash", color = TextWhite, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("F.I.SH", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                Text("Sinfni tanlang:", color = TextMutedGray, fontSize = 12.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(classes) { cls ->
                        FilterChip(
                            selected = selectedClass == cls,
                            onClick = { selectedClass = cls },
                            label = { Text(cls) }
                        )
                    }
                }
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Telefon raqami", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isArchived = !isArchived }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isArchived) Icons.Default.Archive else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isArchived) StatusAbsent else StatusPresent
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArchived) "Holat: Arxivlangan" else "Holat: Faol o'quvchi",
                        color = TextWhite,
                        fontSize = 14.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(student.copy(fullName = name, className = selectedClass, phone = phone, isArchived = isArchived))
                },
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

@Composable
private fun ResetPasswordDialog(
    student: UserEntity,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newPass by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Parolni tiklash", color = TextWhite, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "${student.fullName} (@${student.username}) uchun yangi xavfsiz parol kiriting:",
                    color = TextMutedGray,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    label = { Text("Yangi parol", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPass.isNotBlank()) onConfirm(newPass)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldHistory, contentColor = Color.Black)
            ) {
                Text("O'zgartirish")
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

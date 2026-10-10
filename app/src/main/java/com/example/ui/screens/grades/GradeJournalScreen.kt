package com.example.ui.screens.grades

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.GradeAuditLogEntity
import com.example.data.model.GradeEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.BilimViewModel
import com.example.ui.components.BilimCard
import com.example.ui.components.GradeBadge
import com.example.ui.theme.BlueGeography
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.DarkNavyInputBg
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldHistory
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusPresent
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GradeJournalScreen(
    viewModel: BilimViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allGrades by viewModel.grades.collectAsStateWithLifecycle()
    val classes by viewModel.classes.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val auditLogs by viewModel.gradeAuditLogs.collectAsStateWithLifecycle()

    var selectedSubject by remember { mutableStateOf("tarix") }
    var selectedClass by remember { mutableStateOf("9-A") }
    var selectedQuarter by remember { mutableIntStateOf(1) }

    var showAddGradeDialog by remember { mutableStateOf(false) }
    var editingGrade by remember { mutableStateOf<GradeEntity?>(null) }
    var showAuditHistoryDialog by remember { mutableStateOf(false) }

    val isTarix = selectedSubject == "tarix"
    val themeColor = if (isTarix) GoldHistory else BlueGeography

    val canUserGrade = currentUser?.role == UserRole.ADMIN.name ||
            (currentUser?.role == UserRole.TEACHER.name && currentUser?.canGrade == true &&
                    (currentUser?.assignedSubject.isNullOrBlank() || currentUser?.assignedSubject == selectedSubject))

    val studentsInClass = allUsers.filter { it.role == UserRole.STUDENT.name && it.className == selectedClass }

    val filteredGrades = allGrades.filter {
        it.subjectId == selectedSubject && it.className == selectedClass && it.quarter == selectedQuarter
    }

    // Calculate class average
    val classAvg = if (filteredGrades.isNotEmpty()) filteredGrades.map { it.score }.average() else 0.0

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Subject Tabs: Tarix (Gold) vs Geografiya (Blue)
            TabRow(
                selectedTabIndex = if (isTarix) 0 else 1,
                containerColor = DarkNavySurface,
                contentColor = themeColor,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[if (isTarix) 0 else 1]),
                        color = themeColor
                    )
                }
            ) {
                Tab(
                    selected = isTarix,
                    onClick = { selectedSubject = "tarix" },
                    text = {
                        Text(
                            text = "Tarix Jurnali",
                            fontWeight = if (isTarix) FontWeight.Bold else FontWeight.Normal,
                            color = if (isTarix) GoldHistory else TextMutedGray
                        )
                    }
                )
                Tab(
                    selected = !isTarix,
                    onClick = { selectedSubject = "geografiya" },
                    text = {
                        Text(
                            text = "Geografiya Jurnali",
                            fontWeight = if (!isTarix) FontWeight.Bold else FontWeight.Normal,
                            color = if (!isTarix) BlueGeography else TextMutedGray
                        )
                    }
                )
            }

            // Filters & Actions Row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Class Selector
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(classes) { cl ->
                            FilterChip(
                                selected = selectedClass == cl.id,
                                onClick = { selectedClass = cl.id },
                                label = { Text(cl.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = themeColor,
                                    selectedLabelColor = Color.Black,
                                    containerColor = DarkNavySurface,
                                    labelColor = TextWhite
                                )
                            )
                        }
                    }

                    // Audit history button
                    IconButton(onClick = { showAuditHistoryDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "O'zgarishlar tarixi",
                            tint = themeColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Quarter Selector (1..4)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        (1..4).forEach { q ->
                            FilterChip(
                                selected = selectedQuarter == q,
                                onClick = { selectedQuarter = q },
                                label = { Text("$q-chorak") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = themeColor.copy(alpha = 0.25f),
                                    selectedLabelColor = themeColor,
                                    containerColor = DarkNavySurface,
                                    labelColor = TextMutedGray
                                )
                            )
                        }
                    }

                    // Average Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = themeColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = String.format(Locale.US, "O'rtacha: %.1f", classAvg),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Journal List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (studentsInClass.isEmpty()) {
                    item {
                        BilimCard {
                            Text(
                                text = "Ushbu sinfda o'quvchilar ro'yxati topilmadi",
                                color = TextMutedGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                items(studentsInClass) { student ->
                    val studentGrades = filteredGrades.filter { it.studentId == student.id }
                    val studentAvg = if (studentGrades.isNotEmpty()) studentGrades.map { it.score }.average() else 0.0

                    BilimCard {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(themeColor.copy(alpha = 0.18f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = student.fullName.take(1).uppercase(),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = themeColor
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = student.fullName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                        Text(
                                            text = "@${student.username}",
                                            fontSize = 11.sp,
                                            color = TextMutedGray
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkNavyInputBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, themeColor.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = if (studentAvg > 0) String.format(Locale.US, "%.1f", studentAvg) else "—",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (studentAvg >= 86) StatusPresent else themeColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Grades History for this student
                            if (studentGrades.isEmpty()) {
                                Text(
                                    text = "Ushbu chorakda hali baholanmagan",
                                    fontSize = 12.sp,
                                    color = TextMutedGray
                                )
                            } else {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(studentGrades) { gr ->
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable(enabled = canUserGrade) {
                                                    if (canUserGrade) editingGrade = gr
                                                },
                                            shape = RoundedCornerShape(8.dp),
                                            color = DarkNavyInputBg,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkNavyCardBorder)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                GradeBadge(score = gr.score)
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = gr.lessonDate.takeLast(5),
                                                    fontSize = 10.sp,
                                                    color = TextMutedGray
                                                )
                                            }
                                        }
                                    }
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

        // Floating Action Button to Add Grade
        if (canUserGrade) {
            FloatingActionButton(
                onClick = { showAddGradeDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                containerColor = themeColor,
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Baho qo'yish")
            }
        }
    }

    // Add Grade Dialog
    if (showAddGradeDialog) {
        GradeInputDialog(
            title = "Yangi baho qo'yish",
            students = studentsInClass,
            subjectId = selectedSubject,
            className = selectedClass,
            quarter = selectedQuarter,
            initialGrade = null,
            themeColor = themeColor,
            onDismiss = { showAddGradeDialog = false },
            onConfirm = { student, score, date, topic, note, reason ->
                viewModel.saveGrade(
                    id = null,
                    studentId = student.id,
                    studentName = student.fullName,
                    className = selectedClass,
                    subjectId = selectedSubject,
                    score = score,
                    quarter = selectedQuarter,
                    lessonDate = date,
                    topic = topic,
                    note = note,
                    reason = reason
                )
                showAddGradeDialog = false
            }
        )
    }

    // Edit Grade Dialog
    editingGrade?.let { grade ->
        val student = studentsInClass.find { it.id == grade.studentId } ?: UserEntity(
            id = grade.studentId,
            username = "",
            passwordHash = "",
            salt = "",
            role = "STUDENT",
            fullName = grade.studentName
        )

        GradeInputDialog(
            title = "Bahoni tahrirlash",
            students = listOf(student),
            subjectId = grade.subjectId,
            className = grade.className,
            quarter = grade.quarter,
            initialGrade = grade,
            themeColor = themeColor,
            onDismiss = { editingGrade = null },
            onConfirm = { _, score, date, topic, note, reason ->
                viewModel.saveGrade(
                    id = grade.id,
                    studentId = grade.studentId,
                    studentName = grade.studentName,
                    className = grade.className,
                    subjectId = grade.subjectId,
                    score = score,
                    quarter = grade.quarter,
                    lessonDate = date,
                    topic = topic,
                    note = note,
                    reason = reason
                )
                editingGrade = null
            }
        )
    }

    // Audit History Dialog
    if (showAuditHistoryDialog) {
        val filteredLogs = auditLogs.filter { it.subjectId == selectedSubject }
        AlertDialog(
            onDismissRequest = { showAuditHistoryDialog = false },
            title = {
                Text(
                    text = "${if (isTarix) "Tarix" else "Geografiya"} — Baholar o'zgarishi jurnali",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.height(350.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (filteredLogs.isEmpty()) {
                        item {
                            Text("Hozircha baho o'zgartirishlar tarixi mavjud emas", color = TextMutedGray, fontSize = 13.sp)
                        }
                    }
                    items(filteredLogs) { log ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkNavyInputBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkNavyCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = log.studentName, fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 13.sp)
                                    Text(text = "${log.oldScore} → ${log.newScore} ball", fontWeight = FontWeight.Bold, color = themeColor, fontSize = 13.sp)
                                }
                                Text(text = "O'zgartirdi: ${log.changedBy} (${log.changedByRole})", fontSize = 11.sp, color = TextMutedGray)
                                Text(text = "Sabab: ${log.reason}", fontSize = 11.sp, color = GoldHistory)
                                Text(text = formatDate(log.timestamp), fontSize = 10.sp, color = TextMutedGray)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAuditHistoryDialog = false }) {
                    Text("Yopish", color = themeColor)
                }
            },
            containerColor = DarkNavySurface
        )
    }
}

@Composable
private fun GradeInputDialog(
    title: String,
    students: List<UserEntity>,
    subjectId: String,
    className: String,
    quarter: Int,
    initialGrade: GradeEntity?,
    themeColor: Color,
    onDismiss: () -> Unit,
    onConfirm: (student: UserEntity, score: Int, date: String, topic: String, note: String, reason: String) -> Unit
) {
    var selectedStudent by remember { mutableStateOf(students.firstOrNull()) }
    var score by remember { mutableIntStateOf(initialGrade?.score ?: 85) }
    var topic by remember { mutableStateOf(initialGrade?.topic ?: "") }
    var note by remember { mutableStateOf(initialGrade?.note ?: "") }
    var date by remember { mutableStateOf(initialGrade?.lessonDate ?: "2026-10-10") }
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, color = TextWhite, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (initialGrade == null) {
                    Text("O'quvchini tanlang:", color = TextMutedGray, fontSize = 12.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(students) { st ->
                            FilterChip(
                                selected = selectedStudent?.id == st.id,
                                onClick = { selectedStudent = st },
                                label = { Text(st.fullName) }
                            )
                        }
                    }
                } else {
                    Text("O'quvchi: ${initialGrade.studentName}", color = TextWhite, fontWeight = FontWeight.Bold)
                }

                // 100-ball slider and direct score display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Baho (0 - 100 ball):", color = TextMutedGray, fontSize = 13.sp)
                    GradeBadge(score = score)
                }

                Slider(
                    value = score.toFloat(),
                    onValueChange = { score = it.toInt() },
                    valueRange = 0f..100f,
                    steps = 99,
                    colors = SliderDefaults.colors(thumbColor = themeColor, activeTrackColor = themeColor)
                )

                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Dars mavzusi", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Sana (YYYY-MM-DD)", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Izoh (ixtiyoriy)", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )

                if (initialGrade != null) {
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("O'zgartirish sababi (Audit uchun)", color = GoldHistory) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedStudent?.let { st ->
                        onConfirm(st, score, date, topic, note, reason)
                    }
                },
                enabled = (initialGrade != null || selectedStudent != null) && topic.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor, contentColor = Color.Black)
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

private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(millis))
}

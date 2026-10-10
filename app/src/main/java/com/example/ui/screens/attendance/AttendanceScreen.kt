package com.example.ui.screens.attendance

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.AttendanceEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.BilimViewModel
import com.example.ui.components.AttendanceBadge
import com.example.ui.components.BilimCard
import com.example.ui.components.SubjectBadge
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.DarkNavyInputBg
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldHistory
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusExcused
import com.example.ui.theme.StatusLate
import com.example.ui.theme.StatusPresent
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite

@Composable
fun AttendanceScreen(
    viewModel: BilimViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val attendanceList by viewModel.attendanceList.collectAsStateWithLifecycle()
    val classes by viewModel.classes.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()

    var selectedClass by remember { mutableStateOf("9-A") }
    var selectedSubject by remember { mutableStateOf("tarix") }
    var selectedDate by remember { mutableStateOf("2026-10-10") }
    var selectedLessonNumber by remember { mutableIntStateOf(1) }

    var studentForNoteDialog by remember { mutableStateOf<Pair<UserEntity, String>?>(null) }

    val canRecord = currentUser?.role == UserRole.ADMIN.name || currentUser?.role == UserRole.TEACHER.name
    val studentsInClass = allUsers.filter { it.role == UserRole.STUDENT.name && it.className == selectedClass }

    // Summary stats for the class
    val classAttendance = attendanceList.filter { it.className == selectedClass }
    val totalRecords = classAttendance.size.coerceAtLeast(1)
    val presentCount = classAttendance.count { it.status == "KELDI" }
    val lateCount = classAttendance.count { it.status == "KECHIKDI" }
    val excusedCount = classAttendance.count { it.status == "SABABLI" }
    val absentCount = classAttendance.count { it.status == "SABABSIZ" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Elektron Davomat Jurnali",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Text(
            text = "Darsga qatnashish nazorati va oylik hisobot",
            fontSize = 12.sp,
            color = TextMutedGray
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Class & Subject Selectors
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(classes) { cl ->
                FilterChip(
                    selected = selectedClass == cl.id,
                    onClick = { selectedClass = cl.id },
                    label = { Text(cl.name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldHistory,
                        selectedLabelColor = Color.Black,
                        containerColor = DarkNavySurface,
                        labelColor = TextWhite
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Subject filter & Lesson number
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedSubject == "tarix",
                    onClick = { selectedSubject = "tarix" },
                    label = { Text("Tarix") }
                )
                FilterChip(
                    selected = selectedSubject == "geografiya",
                    onClick = { selectedSubject = "geografiya" },
                    label = { Text("Geografiya") }
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkNavyInputBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkNavyCardBorder)
            ) {
                Text(
                    text = "$selectedDate • $selectedLessonNumber-dars",
                    fontSize = 12.sp,
                    color = GoldHistory,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Monthly / Class Overall Attendance Bar
        BilimCard {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$selectedClass sinf umumiy davomati",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                    )
                    Text(
                        text = "${(presentCount * 100) / totalRecords}% qatnashish",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusPresent
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AttStatSmall("Keldi", presentCount, StatusPresent, Modifier.weight(1f))
                    AttStatSmall("Kechikdi", lateCount, StatusLate, Modifier.weight(1f))
                    AttStatSmall("Sababli", excusedCount, StatusExcused, Modifier.weight(1f))
                    AttStatSmall("Sababsiz", absentCount, StatusAbsent, Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Mark All "Keldi" button
        if (canRecord) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = {
                        studentsInClass.forEach { student ->
                            viewModel.recordAttendance(
                                studentId = student.id,
                                studentName = student.fullName,
                                className = selectedClass,
                                subjectId = selectedSubject,
                                date = selectedDate,
                                status = "KELDI",
                                lessonNumber = selectedLessonNumber,
                                note = ""
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = StatusPresent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Barchani 'Keldi' deb belgilash", fontSize = 12.sp, color = StatusPresent)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Students Attendance List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(studentsInClass) { student ->
                val currentRecord = attendanceList.find {
                    it.studentId == student.id && it.date == selectedDate && it.lessonNumber == selectedLessonNumber
                }
                val currentStatus = currentRecord?.status ?: "KELDI"

                BilimCard {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = student.fullName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                if (currentRecord?.note?.isNotBlank() == true) {
                                    Text(
                                        text = "Izoh: ${currentRecord.note}",
                                        fontSize = 11.sp,
                                        color = GoldHistory
                                    )
                                }
                            }
                            AttendanceBadge(status = currentStatus)
                        }

                        if (canRecord) {
                            Spacer(modifier = Modifier.height(10.dp))
                            // Status choice chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                StatusOptionPill(
                                    label = "Keldi",
                                    color = StatusPresent,
                                    isSelected = currentStatus == "KELDI",
                                    modifier = Modifier.weight(1f)
                                ) {
                                    viewModel.recordAttendance(
                                        student.id, student.fullName, selectedClass, selectedSubject,
                                        selectedDate, "KELDI", selectedLessonNumber, ""
                                    )
                                }

                                StatusOptionPill(
                                    label = "Kechikdi",
                                    color = StatusLate,
                                    isSelected = currentStatus == "KECHIKDI",
                                    modifier = Modifier.weight(1f)
                                ) {
                                    studentForNoteDialog = Pair(student, "KECHIKDI")
                                }

                                StatusOptionPill(
                                    label = "Sababli",
                                    color = StatusExcused,
                                    isSelected = currentStatus == "SABABLI",
                                    modifier = Modifier.weight(1f)
                                ) {
                                    studentForNoteDialog = Pair(student, "SABABLI")
                                }

                                StatusOptionPill(
                                    label = "Sababsiz",
                                    color = StatusAbsent,
                                    isSelected = currentStatus == "SABABSIZ",
                                    modifier = Modifier.weight(1f)
                                ) {
                                    viewModel.recordAttendance(
                                        student.id, student.fullName, selectedClass, selectedSubject,
                                        selectedDate, "SABABSIZ", selectedLessonNumber, "Darsga sababsiz kelmadi"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for adding late/excused note
    studentForNoteDialog?.let { (student, status) ->
        var note by remember { mutableStateOf(if (status == "KECHIKDI") "10 daqiqa kechikdi" else "Shifokor ma'lumotnomasi mavjud") }
        AlertDialog(
            onDismissRequest = { studentForNoteDialog = null },
            title = { Text("$status izohi", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("${student.fullName} uchun sabab yoki izoh kiriting:", color = TextMutedGray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Izoh", color = TextMutedGray) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.recordAttendance(
                            student.id, student.fullName, selectedClass, selectedSubject,
                            selectedDate, status, selectedLessonNumber, note
                        )
                        studentForNoteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldHistory, contentColor = Color.Black)
                ) {
                    Text("Tasdiqlash")
                }
            },
            dismissButton = {
                TextButton(onClick = { studentForNoteDialog = null }) {
                    Text("Bekor qilish", color = TextMutedGray)
                }
            },
            containerColor = DarkNavySurface
        )
    }
}

@Composable
private fun AttStatSmall(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = TextMutedGray
            )
        }
    }
}

@Composable
private fun StatusOptionPill(
    label: String,
    color: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) color.copy(alpha = 0.28f) else DarkNavyInputBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) color else DarkNavyCardBorder)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) color else TextMutedGray
            )
        }
    }
}

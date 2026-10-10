package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserRole
import com.example.ui.BilimViewModel
import com.example.ui.components.BilimCard
import com.example.ui.components.StatCard
import com.example.ui.theme.BlueGeography
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    viewModel: BilimViewModel,
    onNavigateToStudents: () -> Unit,
    onNavigateToTeachers: () -> Unit,
    onNavigateToGrades: () -> Unit,
    onNavigateToTimetable: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val classes by viewModel.classes.collectAsStateWithLifecycle()
    val grades by viewModel.grades.collectAsStateWithLifecycle()
    val attendance by viewModel.attendanceList.collectAsStateWithLifecycle()
    val auditLogs by viewModel.adminAuditLogs.collectAsStateWithLifecycle()

    val studentCount = users.count { it.role == UserRole.STUDENT.name && !it.isArchived }
    val teacherCount = users.count { it.role == UserRole.TEACHER.name && !it.isArchived }
    val classCount = classes.size

    // Calculate Subject Averages
    val tarixGrades = grades.filter { it.subjectId == "tarix" }
    val tarixAvg = if (tarixGrades.isNotEmpty()) tarixGrades.map { it.score }.average() else 0.0

    val geoGrades = grades.filter { it.subjectId == "geografiya" }
    val geoAvg = if (geoGrades.isNotEmpty()) geoGrades.map { it.score }.average() else 0.0

    // Attendance stats
    val totalAtt = attendance.size.coerceAtLeast(1)
    val presentCount = attendance.count { it.status == "KELDI" }
    val lateCount = attendance.count { it.status == "KECHIKDI" }
    val excusedCount = attendance.count { it.status == "SABABLI" }
    val absentCount = attendance.count { it.status == "SABABSIZ" }

    val presentPct = (presentCount * 100) / totalAtt
    val latePct = (lateCount * 100) / totalAtt
    val excusedPct = (excusedCount * 100) / totalAtt
    val absentPct = (absentCount * 100) / totalAtt

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Banner
        item {
            BilimCard {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Boshqaruv Paneli",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "eMaktab yagona monitoring va nazorat tizimi",
                                fontSize = 12.sp,
                                color = TextMutedGray
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldHistory.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "2026 O'quv yili",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GoldHistory,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4 Key Stats (Students, Teachers, Classes, Total Grades)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "O'quvchilar",
                        value = "$studentCount nafar",
                        subtitle = "Faol ro'yxatda",
                        icon = Icons.Default.School,
                        accentColor = GoldHistory,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToStudents
                    )
                    StatCard(
                        title = "O'qituvchilar",
                        value = "$teacherCount nafar",
                        subtitle = "Tarix & Geografiya",
                        icon = Icons.Default.People,
                        accentColor = BlueGeography,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToTeachers
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Jami Sinflar",
                        value = "$classCount ta",
                        subtitle = "8-11 sinflar",
                        icon = Icons.Default.Class,
                        accentColor = StatusPresent,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Baholar jurnali",
                        value = "${grades.size} ta baho",
                        subtitle = "100 ballik tizim",
                        icon = Icons.Default.Grade,
                        accentColor = StatusExcused,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToGrades
                    )
                }
            }
        }

        // Subject Academic Performance
        item {
            BilimCard {
                Column {
                    Text(
                        text = "Fanlar bo'yicha o'zlashtirish (O'rtacha ball)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Tarix
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(GoldHistory)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tarix (O'zbekiston va Jahon)",
                                    fontSize = 13.sp,
                                    color = TextWhite
                                )
                            }
                            Text(
                                text = String.format(Locale.US, "%.1f ball", tarixAvg),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldHistory
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (tarixAvg / 100.0).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = GoldHistory,
                            trackColor = DarkNavyInputBg,
                            strokeCap = StrokeCap.Round
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Geografiya
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(BlueGeography)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Geografiya (Umumiy va Iqtisodiy)",
                                    fontSize = 13.sp,
                                    color = TextWhite
                                )
                            }
                            Text(
                                text = String.format(Locale.US, "%.1f ball", geoAvg),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BlueGeography
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (geoAvg / 100.0).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = BlueGeography,
                            trackColor = DarkNavyInputBg,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }
            }
        }

        // Attendance Statistics Section
        item {
            BilimCard(onClick = onNavigateToAttendance) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Davomat ko'rsatkichlari",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Batafsil →",
                            fontSize = 12.sp,
                            color = BlueGeography
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AttStatPill("Keldi", "$presentPct%", StatusPresent, Modifier.weight(1f))
                        AttStatPill("Kechikdi", "$latePct%", StatusLate, Modifier.weight(1f))
                        AttStatPill("Sababli", "$excusedPct%", StatusExcused, Modifier.weight(1f))
                        AttStatPill("Sababsiz", "$absentPct%", StatusAbsent, Modifier.weight(1f))
                    }
                }
            }
        }

        // Quick Action Buttons
        item {
            Text(
                text = "Tezkor Amallar",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    title = "O'quvchilar",
                    icon = Icons.Default.School,
                    accentColor = GoldHistory,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToStudents
                )
                QuickActionButton(
                    title = "Ustozlar",
                    icon = Icons.Default.Person,
                    accentColor = BlueGeography,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTeachers
                )
                QuickActionButton(
                    title = "Baholash",
                    icon = Icons.Default.Grade,
                    accentColor = StatusPresent,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToGrades
                )
                QuickActionButton(
                    title = "Jadval",
                    icon = Icons.Default.EventNote,
                    accentColor = StatusExcused,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTimetable
                )
            }
        }

        // Recent Activity / Audit Log
        item {
            Text(
                text = "Oxirgi Amallar va Xavfsizlik Jurnali",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        items(auditLogs.take(5)) { log ->
            BilimCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(GoldHistory.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = GoldHistory,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = log.targetDetails,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite
                        )
                        Text(
                            text = "${log.performedBy} • ${formatDate(log.timestamp)}",
                            fontSize = 11.sp,
                            color = TextMutedGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AttStatPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextMutedGray
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
        color = DarkNavySurface
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextWhite
            )
        }
    }
}

private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("HH:mm, dd-MM", Locale.getDefault())
    return sdf.format(Date(millis))
}

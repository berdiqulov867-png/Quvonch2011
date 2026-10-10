package com.example.ui.screens.rankings

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserRole
import com.example.ui.BilimViewModel
import com.example.ui.components.BilimCard
import com.example.ui.components.GradeBadge
import com.example.ui.theme.BlueGeography
import com.example.ui.theme.DarkNavyInputBg
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldHistory
import com.example.ui.theme.StatusPresent
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite
import java.util.Locale

data class StudentRankItem(
    val studentId: String,
    val studentName: String,
    val className: String,
    val averageScore: Double,
    val totalGrades: Int,
    val attendanceRate: Int
)

@Composable
fun RankingsAndReportsScreen(
    viewModel: BilimViewModel,
    modifier: Modifier = Modifier
) {
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val grades by viewModel.grades.collectAsStateWithLifecycle()
    val attendance by viewModel.attendanceList.collectAsStateWithLifecycle()

    var selectedRankingTab by remember { mutableIntStateOf(0) } // 0: O'quvchilar reytingi, 1: Sinflararo hisobot
    var selectedSubjectFilter by remember { mutableStateOf("BARCHASI") }

    val students = users.filter { it.role == UserRole.STUDENT.name && !it.isArchived }

    val rankedStudents = students.map { st ->
        val stGrades = grades.filter { g ->
            g.studentId == st.id && (selectedSubjectFilter == "BARCHASI" || g.subjectId == selectedSubjectFilter)
        }
        val avg = if (stGrades.isNotEmpty()) stGrades.map { it.score }.average() else 0.0

        val stAtt = attendance.filter { it.studentId == st.id }
        val attRate = if (stAtt.isNotEmpty()) (stAtt.count { it.status == "KELDI" } * 100) / stAtt.size else 100

        StudentRankItem(
            studentId = st.id,
            studentName = st.fullName,
            className = st.className,
            averageScore = avg,
            totalGrades = stGrades.size,
            attendanceRate = attRate
        )
    }.sortedByDescending { it.averageScore }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Reyting va Hisobotlar",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Text(
            text = "Maktab peshqadamlari va akademik monitoring",
            fontSize = 12.sp,
            color = TextMutedGray
        )

        Spacer(modifier = Modifier.height(10.dp))

        TabRow(
            selectedTabIndex = selectedRankingTab,
            containerColor = DarkNavySurface,
            contentColor = GoldHistory
        ) {
            Tab(
                selected = selectedRankingTab == 0,
                onClick = { selectedRankingTab = 0 },
                text = {
                    Text(
                        text = "O'quvchilar reytingi",
                        fontWeight = if (selectedRankingTab == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedRankingTab == 0) GoldHistory else TextMutedGray
                    )
                }
            )
            Tab(
                selected = selectedRankingTab == 1,
                onClick = { selectedRankingTab = 1 },
                text = {
                    Text(
                        text = "Sinflar hisoboti",
                        fontWeight = if (selectedRankingTab == 1) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedRankingTab == 1) BlueGeography else TextMutedGray
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedSubjectFilter == "BARCHASI",
                onClick = { selectedSubjectFilter = "BARCHASI" },
                label = { Text("Umumiy ball") }
            )
            FilterChip(
                selected = selectedSubjectFilter == "tarix",
                onClick = { selectedSubjectFilter = "tarix" },
                label = { Text("Tarix bo'yicha") }
            )
            FilterChip(
                selected = selectedSubjectFilter == "geografiya",
                onClick = { selectedSubjectFilter = "geografiya" },
                label = { Text("Geografiya bo'yicha") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedRankingTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(rankedStudents) { index, item ->
                    val medalColor = when (index) {
                        0 -> GoldHistory
                        1 -> Color(0xFFC0C0C0) // Kumush
                        2 -> Color(0xFFCD7F32) // Bronza
                        else -> DarkNavyInputBg
                    }

                    BilimCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (index < 3) medalColor else DarkNavyInputBg),
                                contentAlignment = Alignment.Center
                            ) {
                                if (index < 3) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMutedGray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.studentName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = "Sinf: ${item.className} • Davomat: ${item.attendanceRate}%",
                                    fontSize = 11.sp,
                                    color = TextMutedGray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { (item.averageScore / 100.0).toFloat().coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (index == 0) GoldHistory else BlueGeography,
                                    trackColor = DarkNavyInputBg,
                                    strokeCap = StrokeCap.Round
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (index == 0) GoldHistory.copy(alpha = 0.2f) else DarkNavyInputBg
                            ) {
                                Text(
                                    text = String.format(Locale.US, "%.1f", item.averageScore),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (index == 0) GoldHistory else TextWhite,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        } else {
            // Class by Class Report
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val classList = listOf("8-A", "9-A", "10-A", "11-B")
                itemsIndexed(classList) { _, cls ->
                    val clsGrades = grades.filter { it.className == cls }
                    val clsAvg = if (clsGrades.isNotEmpty()) clsGrades.map { it.score }.average() else 85.0
                    val clsAtt = attendance.filter { it.className == cls }
                    val attPct = if (clsAtt.isNotEmpty()) (clsAtt.count { it.status == "KELDI" } * 100) / clsAtt.size else 92

                    BilimCard {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$cls sinf natijalari",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = BlueGeography.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "O'rtacha: %.1f", clsAvg),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BlueGeography,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Davomat ko'rsatkichi:", color = TextMutedGray, fontSize = 12.sp)
                                Text(text = "$attPct%", color = StatusPresent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { (clsAvg / 100.0).toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = GoldHistory,
                                trackColor = DarkNavyInputBg,
                                strokeCap = StrokeCap.Round
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

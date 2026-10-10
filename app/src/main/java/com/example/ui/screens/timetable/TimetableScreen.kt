package com.example.ui.screens.timetable

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
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.BilimViewModel
import com.example.ui.components.BilimCard
import com.example.ui.components.SubjectBadge
import com.example.ui.theme.BlueGeography
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.DarkNavyInputBg
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldHistory
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite

@Composable
fun TimetableScreen(
    viewModel: BilimViewModel,
    modifier: Modifier = Modifier
) {
    val timetableList by viewModel.timetable.collectAsStateWithLifecycle()
    val classes by viewModel.classes.collectAsStateWithLifecycle()

    val daysOfWeek = listOf("Dushanba", "Seshanba", "Chorshanba", "Payshanba", "Juma", "Shanba")
    var selectedDayIndex by remember { mutableIntStateOf(0) }
    var selectedClass by remember { mutableStateOf("9-A") }

    val currentDay = daysOfWeek[selectedDayIndex]
    val filteredLessons = timetableList.filter {
        it.className == selectedClass && it.dayOfWeek.equals(currentDay, ignoreCase = true)
    }.sortedBy { it.lessonNumber }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Dars Jadvali",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Text(
            text = "Haftalik dars soatlari va xonalar taqsimoti",
            fontSize = 12.sp,
            color = TextMutedGray
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Class Filter Chips
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

        Spacer(modifier = Modifier.height(10.dp))

        // Day of Week Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedDayIndex,
            containerColor = DarkNavySurface,
            contentColor = GoldHistory,
            edgePadding = 0.dp
        ) {
            daysOfWeek.forEachIndexed { index, day ->
                val isSelected = selectedDayIndex == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedDayIndex = index },
                    text = {
                        Text(
                            text = day,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) GoldHistory else TextMutedGray
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Lessons List for selected day
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (filteredLessons.isEmpty()) {
                item {
                    BilimCard {
                        Text(
                            text = "$selectedClass sinf uchun $currentDay kuni darslar rejalashtirilmagan.",
                            color = TextMutedGray,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            items(filteredLessons) { lesson ->
                val isTarix = lesson.subjectId == "tarix"
                val accentColor = if (isTarix) GoldHistory else BlueGeography

                BilimCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Lesson Number Circle
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${lesson.lessonNumber}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SubjectBadge(subjectId = lesson.subjectId)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = TextMutedGray,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = lesson.timeRange,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextWhite
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = lesson.subjectName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = TextMutedGray,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = lesson.teacherName,
                                        fontSize = 12.sp,
                                        color = TextMutedGray
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MeetingRoom,
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = lesson.roomNumber,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = accentColor
                                    )
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
}

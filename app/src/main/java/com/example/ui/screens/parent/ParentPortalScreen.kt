package com.example.ui.screens.parent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import com.example.data.model.PaymentEntity
import com.example.ui.BilimViewModel
import com.example.ui.components.AttendanceBadge
import com.example.ui.components.BilimCard
import com.example.ui.components.GradeBadge
import com.example.ui.components.SubjectBadge
import com.example.ui.theme.BlueGeography
import com.example.ui.theme.ClickBrandColor
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.DarkNavyInputBg
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldHistory
import com.example.ui.theme.PaymeBrandColor
import com.example.ui.theme.StatusPresent
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite
import com.example.ui.theme.UzumBrandColor
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ParentPortalScreen(
    viewModel: BilimViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val grades by viewModel.grades.collectAsStateWithLifecycle()
    val attendance by viewModel.attendanceList.collectAsStateWithLifecycle()
    val timetable by viewModel.timetable.collectAsStateWithLifecycle()
    val announcements by viewModel.announcements.collectAsStateWithLifecycle()
    val payments by viewModel.payments.collectAsStateWithLifecycle()

    var selectedParentTab by remember { mutableIntStateOf(0) }
    // 0: Baholar, 1: Davomat, 2: To'lovlar, 3: Xabarnomalar, 4: Dars jadvali

    var showPaymentModal by remember { mutableStateOf(false) }
    var selectedPaymentItem by remember { mutableStateOf<PaymentEntity?>(null) }

    // Parent sees only linked child
    val childId = currentUser?.parentOfStudentId ?: "student_sardor"
    val childUser = allUsers.find { it.id == childId } ?: allUsers.find { it.role == "STUDENT" }

    val childGrades = grades.filter { it.studentId == childUser?.id }
    val childAttendance = attendance.filter { it.studentId == childUser?.id }
    val childTimetable = timetable.filter { it.className == (childUser?.className ?: "9-A") }
    val childPayments = payments.filter { it.parentId == (currentUser?.id ?: "parent_dilnoza") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Child Header Card
        BilimCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(GoldHistory.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = GoldHistory,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Farzandingiz: ${childUser?.fullName ?: "Sardor Karimov"}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "Sinf: ${childUser?.className ?: "9-A"} • Ota-ona: ${currentUser?.fullName}",
                        fontSize = 12.sp,
                        color = TextMutedGray
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StatusPresent.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Faol",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusPresent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        val tabs = listOf("Baholar", "Davomat", "To'lovlar", "E'lonlar", "Jadval")
        ScrollableTabRow(
            selectedTabIndex = selectedParentTab,
            containerColor = DarkNavySurface,
            contentColor = GoldHistory,
            edgePadding = 0.dp
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedParentTab == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedParentTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) GoldHistory else TextMutedGray
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Content
        when (selectedParentTab) {
            0 -> {
                // Grades
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (childGrades.isEmpty()) {
                        item { Text("Baholar mavjud emas", color = TextMutedGray) }
                    }
                    items(childGrades) { g ->
                        BilimCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        SubjectBadge(subjectId = g.subjectId)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = g.lessonDate,
                                            fontSize = 11.sp,
                                            color = TextMutedGray
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = g.topic,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    if (g.note.isNotBlank()) {
                                        Text(
                                            text = "Ustoz izohi: ${g.note}",
                                            fontSize = 11.sp,
                                            color = GoldHistory
                                        )
                                    }
                                }
                                GradeBadge(score = g.score)
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }

            1 -> {
                // Attendance
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (childAttendance.isEmpty()) {
                        item { Text("Davomat ma'lumotlari mavjud emas", color = TextMutedGray) }
                    }
                    items(childAttendance) { att ->
                        BilimCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${att.date} • ${att.lessonNumber}-para",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Fan: ${att.subjectId.uppercase()} • O'qituvchi: ${att.recordedBy}",
                                        fontSize = 11.sp,
                                        color = TextMutedGray
                                    )
                                    if (att.note.isNotBlank()) {
                                        Text(
                                            text = "Sabab: ${att.note}",
                                            fontSize = 11.sp,
                                            color = GoldHistory
                                        )
                                    }
                                }
                                AttendanceBadge(status = att.status)
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }

            2 -> {
                // Payments (Click, Payme, Uzum)
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "To'lovlar va To'garaklar",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Button(
                            onClick = { showPaymentModal = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldHistory, contentColor = Color.Black)
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Yangi to'lov", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(childPayments) { p ->
                            val providerColor = when (p.provider) {
                                "CLICK" -> ClickBrandColor
                                "PAYME" -> PaymeBrandColor
                                else -> UzumBrandColor
                            }

                            BilimCard(onClick = { selectedPaymentItem = p }) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = p.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                        Text(
                                            text = "${p.date} • Chek: ${p.transactionCode}",
                                            fontSize = 11.sp,
                                            color = TextMutedGray
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = providerColor.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = p.provider,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = providerColor,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${formatSum(p.amountSum)} so'm",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                        Text(
                                            text = p.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (p.status == "TO'LANDI") StatusPresent else GoldHistory
                                        )
                                    }
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }

            3 -> {
                // Teacher Announcements
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(announcements) { ann ->
                        BilimCard {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = ann.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    if (ann.priority == "MUHIM") {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = GoldHistory.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "Muhim",
                                                fontSize = 10.sp,
                                                color = GoldHistory,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = ann.content,
                                    fontSize = 12.sp,
                                    color = TextMutedGray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Yuboruvchi: ${ann.authorName} • ${ann.date}",
                                    fontSize = 10.sp,
                                    color = TextMutedGray
                                )
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }

            4 -> {
                // Child Timetable
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(childTimetable) { tt ->
                        BilimCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${tt.dayOfWeek} • ${tt.lessonNumber}-para (${tt.timeRange})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GoldHistory
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = tt.subjectName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Ustoz: ${tt.teacherName} • Xona: ${tt.roomNumber}",
                                        fontSize = 11.sp,
                                        color = TextMutedGray
                                    )
                                }
                                SubjectBadge(subjectId = tt.subjectId)
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    // New Payment Dialog (Click, Payme, Uzum)
    if (showPaymentModal) {
        var selectedService by remember { mutableStateOf("Tarix fanidan individual to'garak") }
        var amountSum by remember { mutableStateOf("150000") }
        var selectedProvider by remember { mutableStateOf("CLICK") }

        AlertDialog(
            onDismissRequest = { showPaymentModal = false },
            title = { Text("To'lovni amalga oshirish", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("To'lov xizmatini tanlang:", color = TextMutedGray, fontSize = 12.sp)
                    val services = listOf(
                        "Tarix fanidan individual to'garak" to 150000L,
                        "Geografiya atlasi va o'quv qo'llanmasi" to 75000L,
                        "Maktab kutubxonasi yillik a'zolik" to 40000L
                    )
                    services.forEach { (srv, cost) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedService == srv) DarkNavyInputBg else Color.Transparent)
                                .clickable {
                                    selectedService = srv
                                    amountSum = cost.toString()
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = srv,
                                fontSize = 13.sp,
                                color = if (selectedService == srv) GoldHistory else TextWhite,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${formatSum(cost)} so'm",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldHistory
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Milliy to'lov tizimini tanlang:", color = TextMutedGray, fontSize = 12.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PaymentProviderCard(
                            name = "Click",
                            color = ClickBrandColor,
                            isSelected = selectedProvider == "CLICK",
                            modifier = Modifier.weight(1f)
                        ) { selectedProvider = "CLICK" }

                        PaymentProviderCard(
                            name = "Payme",
                            color = PaymeBrandColor,
                            isSelected = selectedProvider == "PAYME",
                            modifier = Modifier.weight(1f)
                        ) { selectedProvider = "PAYME" }

                        PaymentProviderCard(
                            name = "Uzum",
                            color = UzumBrandColor,
                            isSelected = selectedProvider == "UZUM",
                            modifier = Modifier.weight(1f)
                        ) { selectedProvider = "UZUM" }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountSum.toLongOrNull() ?: 100000L
                        viewModel.processPayment(selectedService, amount, selectedProvider)
                        showPaymentModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldHistory, contentColor = Color.Black)
                ) {
                    Text("To'lash")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPaymentModal = false }) { Text("Bekor qilish", color = TextMutedGray) }
            },
            containerColor = DarkNavySurface
        )
    }

    // Receipt details dialog
    selectedPaymentItem?.let { p ->
        AlertDialog(
            onDismissRequest = { selectedPaymentItem = null },
            title = { Text("Elektron To'lov Cheki", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Tranzaksiya: ${p.transactionCode}", color = GoldHistory, fontWeight = FontWeight.Bold)
                    Text(text = "Xizmat: ${p.title}", color = TextWhite)
                    Text(text = "O'quvchi: ${p.studentName}", color = TextMutedGray)
                    Text(text = "Summa: ${formatSum(p.amountSum)} so'm", color = TextWhite, fontWeight = FontWeight.Bold)
                    Text(text = "To'lov tizimi: ${p.provider}", color = BlueGeography)
                    Text(text = "Sana: ${p.date}", color = TextMutedGray)
                    Text(text = "Holati: ${p.status}", color = StatusPresent, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedPaymentItem = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldHistory, contentColor = Color.Black)
                ) {
                    Text("Yopish")
                }
            },
            containerColor = DarkNavySurface
        )
    }
}

@Composable
private fun PaymentProviderCard(
    name: String,
    color: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) color.copy(alpha = 0.2f) else DarkNavyInputBg,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSelected) color else DarkNavyCardBorder)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) color else TextWhite
            )
        }
    }
}

private fun formatSum(amount: Long): String {
    return NumberFormat.getNumberInstance(Locale.US).format(amount)
}

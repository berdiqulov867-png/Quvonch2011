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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.model.SubjectEntity
import com.example.data.model.UserRole
import com.example.ui.BilimViewModel
import com.example.ui.components.BilimCard
import com.example.ui.theme.BlueGeography
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldHistory
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite

@Composable
fun SubjectManagementScreen(
    viewModel: BilimViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()

    var editingSubject by remember { mutableStateOf<SubjectEntity?>(null) }
    val isAdmin = currentUser?.role == UserRole.ADMIN.name

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "O'quv Fanlari Boshqaruvi",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Text(
            text = "Tarix (Oltin mavzu) va Geografiya (Moviy mavzu) bo'limlari",
            fontSize = 12.sp,
            color = TextMutedGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(subjects) { subject ->
                val isTarix = subject.id == "tarix"
                val themeColor = if (isTarix) GoldHistory else BlueGeography
                val icon = if (isTarix) Icons.Default.Book else Icons.Default.Public

                BilimCard(borderColor = themeColor) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(themeColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = themeColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = subject.nameUz,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = "Kod: ${subject.code} • ${subject.hoursPerWeek} soat/hafta",
                                    fontSize = 12.sp,
                                    color = themeColor
                                )
                            }

                            if (isAdmin) {
                                IconButton(onClick = { editingSubject = subject }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Tahrirlash",
                                        tint = themeColor
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = subject.description,
                            fontSize = 13.sp,
                            color = TextMutedGray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = themeColor.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, themeColor.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = themeColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Biriktirilgan o'qituvchi:",
                                        fontSize = 12.sp,
                                        color = TextMutedGray
                                    )
                                }
                                Text(
                                    text = subject.teacherName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    editingSubject?.let { subj ->
        var name by remember { mutableStateOf(subj.nameUz) }
        var teacher by remember { mutableStateOf(subj.teacherName) }
        var hours by remember { mutableStateOf(subj.hoursPerWeek.toString()) }
        var desc by remember { mutableStateOf(subj.description) }

        val themeColor = if (subj.id == "tarix") GoldHistory else BlueGeography

        AlertDialog(
            onDismissRequest = { editingSubject = null },
            title = { Text("${subj.nameUz} ni tahrirlash", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Fan to'liq nomi", color = TextMutedGray) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                    OutlinedTextField(
                        value = teacher,
                        onValueChange = { teacher = it },
                        label = { Text("Biriktirilgan o'qituvchi", color = TextMutedGray) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                    OutlinedTextField(
                        value = hours,
                        onValueChange = { hours = it },
                        label = { Text("Haftalik dars soati", color = TextMutedGray) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Tavsif va o'quv dasturi", color = TextMutedGray) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = subj.copy(
                            nameUz = name,
                            teacherName = teacher,
                            hoursPerWeek = hours.toIntOrNull() ?: subj.hoursPerWeek,
                            description = desc
                        )
                        viewModel.updateSubject(updated)
                        editingSubject = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor, contentColor = Color.Black)
                ) {
                    Text("Saqlash")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSubject = null }) { Text("Bekor qilish", color = TextMutedGray) }
            },
            containerColor = DarkNavySurface
        )
    }
}

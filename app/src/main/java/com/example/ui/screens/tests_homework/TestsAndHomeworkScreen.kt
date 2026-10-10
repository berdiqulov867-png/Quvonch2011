package com.example.ui.screens.tests_homework

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.data.model.HomeworkEntity
import com.example.data.model.TestQuizEntity
import com.example.data.model.UserRole
import com.example.ui.BilimViewModel
import com.example.ui.components.BilimCard
import com.example.ui.components.GradeBadge
import com.example.ui.components.SubjectBadge
import com.example.ui.theme.BlueGeography
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.DarkNavyInputBg
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldHistory
import com.example.ui.theme.StatusPresent
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val points: Int
)

@Composable
fun TestsAndHomeworkScreen(
    viewModel: BilimViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val tests by viewModel.tests.collectAsStateWithLifecycle()
    val homeworkList by viewModel.homeworkList.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Testlar, 1: Uy vazifalari
    var showCreateTestDialog by remember { mutableStateOf(false) }
    var showCreateHwDialog by remember { mutableStateOf(false) }

    var takingTestQuiz by remember { mutableStateOf<TestQuizEntity?>(null) }
    var submittingHomework by remember { mutableStateOf<HomeworkEntity?>(null) }

    val canTeacherManage = currentUser?.role == UserRole.ADMIN.name ||
            (currentUser?.role == UserRole.TEACHER.name && currentUser?.canCreateTests == true)

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkNavySurface,
                contentColor = GoldHistory
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Testlar (${tests.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) GoldHistory else TextMutedGray
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Uy Vazifalari (${homeworkList.size})",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) BlueGeography else TextMutedGray
                        )
                    }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (selectedTab == 0) {
                    // Tests Tab
                    items(tests) { test ->
                        BilimCard {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SubjectBadge(subjectId = test.subjectId)
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DarkNavyInputBg
                                    ) {
                                        Text(
                                            text = "Muddati: ${test.deadlineDate}",
                                            fontSize = 11.sp,
                                            color = TextMutedGray,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = test.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )

                                Text(
                                    text = test.description,
                                    fontSize = 12.sp,
                                    color = TextMutedGray,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Vaqt: ${test.durationMinutes} daqiqa • Sinf: ${test.className}",
                                        fontSize = 11.sp,
                                        color = GoldHistory
                                    )

                                    if (currentUser?.role == UserRole.STUDENT.name) {
                                        Button(
                                            onClick = { takingTestQuiz = test },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = GoldHistory,
                                                contentColor = Color.Black
                                            )
                                        ) {
                                            Text("Testni yechish", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Homework Tab
                    items(homeworkList) { hw ->
                        BilimCard {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SubjectBadge(subjectId = hw.subjectId)
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DarkNavyInputBg
                                    ) {
                                        Text(
                                            text = "Muddati: ${hw.deadline}",
                                            fontSize = 11.sp,
                                            color = BlueGeography,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = hw.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )

                                Text(
                                    text = hw.description,
                                    fontSize = 12.sp,
                                    color = TextMutedGray,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Ustoz: ${hw.teacherName} • ${hw.className}",
                                        fontSize = 11.sp,
                                        color = TextMutedGray
                                    )

                                    if (currentUser?.role == UserRole.STUDENT.name) {
                                        Button(
                                            onClick = { submittingHomework = hw },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = BlueGeography,
                                                contentColor = Color.Black
                                            )
                                        ) {
                                            Text("Topshirish", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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

        if (canTeacherManage) {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showCreateTestDialog = true else showCreateHwDialog = true
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                containerColor = if (selectedTab == 0) GoldHistory else BlueGeography,
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Yangi qo'shish")
            }
        }
    }

    // Interactive Quiz Dialog for Student
    takingTestQuiz?.let { test ->
        TakeQuizDialog(
            test = test,
            studentName = currentUser?.fullName ?: "O'quvchi",
            studentId = currentUser?.id ?: "student_curr",
            onDismiss = { takingTestQuiz = null },
            onSubmit = { score, total, correct, feedback ->
                viewModel.submitTest(
                    testId = test.id,
                    studentId = currentUser?.id ?: "student_curr",
                    studentName = currentUser?.fullName ?: "O'quvchi",
                    score = score,
                    totalQuestions = total,
                    correctCount = correct,
                    feedback = feedback
                )
                takingTestQuiz = null
            }
        )
    }

    // Submit Homework Dialog
    submittingHomework?.let { hw ->
        var submissionText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { submittingHomework = null },
            title = { Text("Uy vazifasini topshirish", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(text = hw.title, color = GoldHistory, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = submissionText,
                        onValueChange = { submissionText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        label = { Text("Javobingiz yoki konspektingiz...", color = TextMutedGray) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitHomework(
                            homeworkId = hw.id,
                            studentId = currentUser?.id ?: "student_curr",
                            studentName = currentUser?.fullName ?: "O'quvchi",
                            text = submissionText
                        )
                        submittingHomework = null
                    },
                    enabled = submissionText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueGeography, contentColor = Color.Black)
                ) {
                    Text("Yuborish")
                }
            },
            dismissButton = {
                TextButton(onClick = { submittingHomework = null }) {
                    Text("Bekor qilish", color = TextMutedGray)
                }
            },
            containerColor = DarkNavySurface
        )
    }

    // Create Test Dialog
    if (showCreateTestDialog) {
        CreateTestDialog(
            onDismiss = { showCreateTestDialog = false },
            onConfirm = { newTest ->
                viewModel.createTest(newTest)
                showCreateTestDialog = false
            }
        )
    }

    // Create Homework Dialog
    if (showCreateHwDialog) {
        CreateHomeworkDialog(
            teacherName = currentUser?.fullName ?: "Ustoz",
            onDismiss = { showCreateHwDialog = false },
            onConfirm = { newHw ->
                viewModel.createHomework(newHw)
                showCreateHwDialog = false
            }
        )
    }
}

@Composable
private fun TakeQuizDialog(
    test: TestQuizEntity,
    studentName: String,
    studentId: String,
    onDismiss: () -> Unit,
    onSubmit: (score: Int, total: Int, correct: Int, feedback: String) -> Unit
) {
    val questions = remember(test.questionsJson) {
        parseQuestions(test.questionsJson)
    }
    val selectedAnswers = remember { mutableStateMapOf<Int, Int>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = test.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            LazyColumn(
                modifier = Modifier.height(380.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(questions) { q ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkNavyInputBg, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "${q.id}. ${q.question}",
                            color = TextWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        q.options.forEachIndexed { optIndex, optText ->
                            val isSelected = selectedAnswers[q.id] == optIndex
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { selectedAnswers[q.id] = optIndex }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedAnswers[q.id] = optIndex },
                                    colors = RadioButtonDefaults.colors(selectedColor = GoldHistory)
                                )
                                Text(
                                    text = optText,
                                    fontSize = 12.sp,
                                    color = if (isSelected) GoldHistory else TextMutedGray
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var correctCount = 0
                    questions.forEach { q ->
                        if (selectedAnswers[q.id] == q.correctIndex) {
                            correctCount++
                        }
                    }
                    val total = questions.size.coerceAtLeast(1)
                    val score = (correctCount * 100) / total
                    val feedback = when {
                        score >= 86 -> "A'lo natija! Barcha asosiy mavzularni puxta egallagansiz."
                        score >= 71 -> "Yaxshi natija! Ayrim savollarda e'tiborliroq bo'ling."
                        else -> "Qoniqarli. Mavzuni darslikdan qayta takrorlash tavsiya etiladi."
                    }
                    onSubmit(score, total, correctCount, feedback)
                },
                enabled = selectedAnswers.size == questions.size,
                colors = ButtonDefaults.buttonColors(containerColor = GoldHistory, contentColor = Color.Black)
            ) {
                Text("Natijani tekshirish")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Chiqish", color = TextMutedGray)
            }
        },
        containerColor = DarkNavySurface
    )
}

@Composable
private fun CreateTestDialog(
    onDismiss: () -> Unit,
    onConfirm: (TestQuizEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subjectId by remember { mutableStateOf("tarix") }
    var className by remember { mutableStateOf("9-A") }
    var questionText by remember { mutableStateOf("") }
    var optA by remember { mutableStateOf("") }
    var optB by remember { mutableStateOf("") }
    var optC by remember { mutableStateOf("") }
    var optD by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yangi Test Tuzish", color = TextWhite, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.height(360.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Test nomi", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Tavsif", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("1-Savol matni", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                OutlinedTextField(
                    value = optA,
                    onValueChange = { optA = it },
                    label = { Text("A varianti (To'g'ri javob)", color = GoldHistory) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                OutlinedTextField(
                    value = optB,
                    onValueChange = { optB = it },
                    label = { Text("B varianti", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                OutlinedTextField(
                    value = optC,
                    onValueChange = { optC = it },
                    label = { Text("C varianti", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                OutlinedTextField(
                    value = optD,
                    onValueChange = { optD = it },
                    label = { Text("D varianti", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qJson = """
                        [
                          {
                            "id": 1,
                            "question": "$questionText",
                            "options": ["$optA", "$optB", "$optC", "$optD"],
                            "correctIndex": 0,
                            "points": 100
                          }
                        ]
                    """.trimIndent()
                    val newTest = TestQuizEntity(
                        id = "test_${UUID.randomUUID().toString().take(8)}",
                        subjectId = subjectId,
                        title = title,
                        description = description,
                        className = className,
                        durationMinutes = 15,
                        deadlineDate = "2026-10-20",
                        teacherName = "Fan o'qituvchisi",
                        questionsJson = qJson
                    )
                    onConfirm(newTest)
                },
                enabled = title.isNotBlank() && questionText.isNotBlank() && optA.isNotBlank() && optB.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldHistory, contentColor = Color.Black)
            ) {
                Text("Yaratish")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Bekor qilish", color = TextMutedGray) }
        },
        containerColor = DarkNavySurface
    )
}

@Composable
private fun CreateHomeworkDialog(
    teacherName: String,
    onDismiss: () -> Unit,
    onConfirm: (HomeworkEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subjectId by remember { mutableStateOf("tarix") }
    var className by remember { mutableStateOf("9-A") }
    var deadline by remember { mutableStateOf("2026-10-18") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yangi Uy Vazifasi", color = TextWhite, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Vazifa sarlavhasi", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Vazifa tavsifi va talablari", color = TextMutedGray) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Topshirish muddati", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val hw = HomeworkEntity(
                        id = "hw_${UUID.randomUUID().toString().take(8)}",
                        subjectId = subjectId,
                        className = className,
                        title = title,
                        description = description,
                        deadline = deadline,
                        teacherName = teacherName
                    )
                    onConfirm(hw)
                },
                enabled = title.isNotBlank() && description.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = BlueGeography, contentColor = Color.Black)
            ) {
                Text("E'lon qilish")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Bekor qilish", color = TextMutedGray) }
        },
        containerColor = DarkNavySurface
    )
}

private fun parseQuestions(jsonString: String): List<QuizQuestion> {
    val list = mutableListOf<QuizQuestion>()
    try {
        val array = JSONArray(jsonString)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val optsArray = obj.getJSONArray("options")
            val opts = mutableListOf<String>()
            for (j in 0 until optsArray.length()) {
                opts.add(optsArray.getString(j))
            }
            list.add(
                QuizQuestion(
                    id = obj.optInt("id", i + 1),
                    question = obj.optString("question"),
                    options = opts,
                    correctIndex = obj.optInt("correctIndex", 0),
                    points = obj.optInt("points", 25)
                )
            )
        }
    } catch (_: Exception) {}
    return list
}

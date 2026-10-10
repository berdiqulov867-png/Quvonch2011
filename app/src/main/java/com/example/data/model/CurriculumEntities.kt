package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tests")
data class TestQuizEntity(
    @PrimaryKey val id: String,
    val subjectId: String, // "tarix" or "geografiya"
    val title: String,
    val description: String,
    val className: String,
    val durationMinutes: Int,
    val deadlineDate: String,
    val teacherName: String,
    val questionsJson: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "test_submissions")
data class TestSubmissionEntity(
    @PrimaryKey val id: String,
    val testId: String,
    val studentId: String,
    val studentName: String,
    val score: Int, // 0 - 100
    val totalQuestions: Int,
    val correctCount: Int,
    val feedback: String,
    val submittedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "homework")
data class HomeworkEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val className: String,
    val title: String,
    val description: String,
    val deadline: String,
    val teacherName: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "homework_submissions")
data class HomeworkSubmissionEntity(
    @PrimaryKey val id: String,
    val homeworkId: String,
    val studentId: String,
    val studentName: String,
    val submissionText: String,
    val score: Int? = null,
    val teacherFeedback: String? = null,
    val submittedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "library_resources")
data class LibraryResourceEntity(
    @PrimaryKey val id: String,
    val subjectId: String, // "tarix" or "geografiya"
    val title: String,
    val author: String,
    val category: String, // "Darslik", "Qo'llanma", "Atlas", "Manba"
    val description: String,
    val fileSize: String,
    val pageCount: Int,
    val chaptersJson: String,
    val uploaderName: String
)

@Entity(tableName = "timetable")
data class TimetableEntity(
    @PrimaryKey val id: String,
    val className: String,
    val dayOfWeek: String, // "Dushanba", "Seshanba", "Chorshanba", "Payshanba", "Juma", "Shanba"
    val lessonNumber: Int,
    val timeRange: String,
    val subjectId: String,
    val subjectName: String,
    val teacherName: String,
    val roomNumber: String
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val targetRole: String, // "ALL", "STUDENT", "PARENT", "TEACHER"
    val authorName: String,
    val date: String,
    val priority: String // "MUHIM", "ODDIY"
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val parentId: String,
    val studentName: String,
    val title: String,
    val amountSum: Long,
    val provider: String, // "CLICK", "PAYME", "UZUM"
    val status: String, // "TO'LANDI", "KUTILMOQDA"
    val date: String,
    val transactionCode: String
)

@Entity(tableName = "admin_audit_logs")
data class AdminAuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val targetDetails: String,
    val performedBy: String,
    val timestamp: Long = System.currentTimeMillis()
)

package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val passwordHash: String,
    val salt: String,
    val role: String, // ADMIN, TEACHER, STUDENT, PARENT
    val fullName: String,
    val phone: String = "",
    val className: String = "", // e.g. "9-A"
    val assignedSubject: String = "", // "tarix" or "geografiya"
    val parentOfStudentId: String = "",
    val isArchived: Boolean = false,
    val canGrade: Boolean = true,
    val canCreateTests: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "classes")
data class SchoolClassEntity(
    @PrimaryKey val id: String, // e.g. "9-A"
    val name: String,
    val gradeLevel: Int,
    val room: String,
    val headTeacherName: String,
    val studentCount: Int = 0
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String, // "tarix", "geografiya"
    val nameUz: String,
    val code: String,
    val colorHex: String,
    val teacherName: String,
    val hoursPerWeek: Int,
    val description: String
)

@Entity(tableName = "grades")
data class GradeEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val studentName: String,
    val className: String,
    val subjectId: String, // "tarix" or "geografiya"
    val score: Int, // 0 - 100
    val quarter: Int, // 1, 2, 3, 4
    val lessonDate: String, // YYYY-MM-DD
    val topic: String,
    val teacherName: String,
    val note: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "grade_audit_logs")
data class GradeAuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gradeId: String,
    val studentName: String,
    val subjectId: String,
    val oldScore: Int,
    val newScore: Int,
    val changedBy: String,
    val changedByRole: String,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val studentName: String,
    val className: String,
    val subjectId: String,
    val date: String, // YYYY-MM-DD
    val status: String, // "KELDI", "KECHIKDI", "SABABLI", "SABABSIZ"
    val lessonNumber: Int,
    val recordedBy: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

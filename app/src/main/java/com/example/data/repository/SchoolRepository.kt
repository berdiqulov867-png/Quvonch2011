package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.AdminAuditLogEntity
import com.example.data.model.AnnouncementEntity
import com.example.data.model.AttendanceEntity
import com.example.data.model.GradeAuditLogEntity
import com.example.data.model.GradeEntity
import com.example.data.model.HomeworkEntity
import com.example.data.model.HomeworkSubmissionEntity
import com.example.data.model.LibraryResourceEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.SchoolClassEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.TestQuizEntity
import com.example.data.model.TestSubmissionEntity
import com.example.data.model.TimetableEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.util.PasswordSecurity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class SchoolRepository(private val db: AppDatabase) {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // 1. Authentication
    suspend fun login(username: String, password: String): Result<UserEntity> {
        if (username.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Iltimos, foydalanuvchi nomi va parolni kiriting"))
        }
        val user = db.userDao().getUserByUsername(username.trim().lowercase())
            ?: return Result.failure(IllegalArgumentException("Foydalanuvchi nomi yoki parol noto'g'ri"))

        if (user.isArchived) {
            return Result.failure(IllegalStateException("Ushbu hisob arxivlangan. Administratorga murojaat qiling."))
        }

        val isValid = PasswordSecurity.verifyPassword(password, user.salt, user.passwordHash)
        if (!isValid) {
            return Result.failure(IllegalArgumentException("Foydalanuvchi nomi yoki parol noto'g'ri"))
        }

        _currentUser.value = user
        db.adminAuditDao().insertAuditLog(
            AdminAuditLogEntity(
                action = "TIZIMGA_KIRILDI",
                targetDetails = "${user.fullName} (${user.role}) tizimga kirdi",
                performedBy = user.username
            )
        )
        return Result.success(user)
    }

    suspend fun quickLogin(username: String): Result<UserEntity> {
        val user = db.userDao().getUserByUsername(username)
            ?: return Result.failure(IllegalArgumentException("Foydalanuvchi topilmadi"))
        _currentUser.value = user
        return Result.success(user)
    }

    fun logout() {
        val user = _currentUser.value
        _currentUser.value = null
    }

    // 2. Users Management (Admin)
    fun getAllUsers(): Flow<List<UserEntity>> = db.userDao().getAllUsers()
    fun getUsersByRole(role: String): Flow<List<UserEntity>> = db.userDao().getUsersByRole(role)
    fun getStudentsByClass(className: String): Flow<List<UserEntity>> = db.userDao().getStudentsByClass(className)
    fun countUsersByRole(role: String): Flow<Int> = db.userDao().countUsersByRole(role)

    suspend fun addStudent(
        fullName: String,
        username: String,
        className: String,
        phone: String,
        initialPass: String
    ): Result<Unit> {
        val active = _currentUser.value
        if (active?.role != UserRole.ADMIN.name) {
            return Result.failure(SecurityException("Faqat administrator o'quvchi qo'sha oladi"))
        }
        val existing = db.userDao().getUserByUsername(username.trim().lowercase())
        if (existing != null) {
            return Result.failure(IllegalArgumentException("Ushbu foydalanuvchi nomi band"))
        }
        val salt = PasswordSecurity.generateSalt()
        val hash = PasswordSecurity.hashPassword(initialPass, salt)
        val id = "student_${UUID.randomUUID().toString().take(8)}"
        val user = UserEntity(
            id = id,
            username = username.trim().lowercase(),
            passwordHash = hash,
            salt = salt,
            role = UserRole.STUDENT.name,
            fullName = fullName.trim(),
            phone = phone.trim(),
            className = className
        )
        db.userDao().insertUser(user)
        db.adminAuditDao().insertAuditLog(
            AdminAuditLogEntity(
                action = "O'QUVCHI_QO'SHILDI",
                targetDetails = "$fullName ($className) ro'yxatga olindi",
                performedBy = active.username
            )
        )
        return Result.success(Unit)
    }

    suspend fun updateStudent(user: UserEntity): Result<Unit> {
        val active = _currentUser.value
        if (active?.role != UserRole.ADMIN.name) {
            return Result.failure(SecurityException("Ruxsat berilmagan"))
        }
        db.userDao().updateUser(user)
        db.adminAuditDao().insertAuditLog(
            AdminAuditLogEntity(
                action = "O'QUVCHI_YANGILANDI",
                targetDetails = "${user.fullName} ma'lumotlari yangilandi",
                performedBy = active.username
            )
        )
        return Result.success(Unit)
    }

    suspend fun resetUserPassword(userId: String, newPass: String): Result<Unit> {
        val active = _currentUser.value
        if (active?.role != UserRole.ADMIN.name) {
            return Result.failure(SecurityException("Faqat administrator parolni tiklay oladi"))
        }
        val user = db.userDao().getUserById(userId)
            ?: return Result.failure(IllegalArgumentException("Foydalanuvchi topilmadi"))
        val salt = PasswordSecurity.generateSalt()
        val hash = PasswordSecurity.hashPassword(newPass, salt)
        val updated = user.copy(passwordHash = hash, salt = salt)
        db.userDao().updateUser(updated)
        db.adminAuditDao().insertAuditLog(
            AdminAuditLogEntity(
                action = "PAROL_TIKLASH",
                targetDetails = "${user.fullName} ning paroli yangilandi",
                performedBy = active.username
            )
        )
        return Result.success(Unit)
    }

    suspend fun addTeacher(
        fullName: String,
        username: String,
        assignedSubject: String,
        phone: String,
        initialPass: String
    ): Result<Unit> {
        val active = _currentUser.value
        if (active?.role != UserRole.ADMIN.name) {
            return Result.failure(SecurityException("Faqat administrator o'qituvchi qo'sha oladi"))
        }
        val existing = db.userDao().getUserByUsername(username.trim().lowercase())
        if (existing != null) {
            return Result.failure(IllegalArgumentException("Ushbu foydalanuvchi nomi band"))
        }
        val salt = PasswordSecurity.generateSalt()
        val hash = PasswordSecurity.hashPassword(initialPass, salt)
        val id = "teacher_${UUID.randomUUID().toString().take(8)}"
        val user = UserEntity(
            id = id,
            username = username.trim().lowercase(),
            passwordHash = hash,
            salt = salt,
            role = UserRole.TEACHER.name,
            fullName = fullName.trim(),
            phone = phone.trim(),
            assignedSubject = assignedSubject
        )
        db.userDao().insertUser(user)
        db.adminAuditDao().insertAuditLog(
            AdminAuditLogEntity(
                action = "O'QITUVCHI_QO'SHILDI",
                targetDetails = "$fullName ($assignedSubject) qo'shildi",
                performedBy = active.username
            )
        )
        return Result.success(Unit)
    }

    suspend fun updateTeacherPermissions(userId: String, canGrade: Boolean, canCreateTests: Boolean): Result<Unit> {
        val active = _currentUser.value
        if (active?.role != UserRole.ADMIN.name) {
            return Result.failure(SecurityException("Ruxsat berilmagan"))
        }
        val teacher = db.userDao().getUserById(userId)
            ?: return Result.failure(IllegalArgumentException("O'qituvchi topilmadi"))
        val updated = teacher.copy(canGrade = canGrade, canCreateTests = canCreateTests)
        db.userDao().updateUser(updated)
        db.adminAuditDao().insertAuditLog(
            AdminAuditLogEntity(
                action = "HUQUQLAR_O'ZGARTIRILDI",
                targetDetails = "${teacher.fullName}: Baholash=$canGrade, Testlar=$canCreateTests",
                performedBy = active.username
            )
        )
        return Result.success(Unit)
    }

    // 3. Classes and Subjects
    fun getAllClasses(): Flow<List<SchoolClassEntity>> = db.schoolDao().getAllClasses()
    fun getAllSubjects(): Flow<List<SubjectEntity>> = db.schoolDao().getAllSubjects()

    suspend fun updateSubject(subject: SubjectEntity): Result<Unit> {
        val active = _currentUser.value
        if (active?.role != UserRole.ADMIN.name) {
            return Result.failure(SecurityException("Faqat administrator fanlarni o'zgartira oladi"))
        }
        db.schoolDao().updateSubject(subject)
        return Result.success(Unit)
    }

    // 4. Grades
    fun getAllGrades(): Flow<List<GradeEntity>> = db.gradeDao().getAllGrades()
    fun getGradesByStudent(studentId: String): Flow<List<GradeEntity>> = db.gradeDao().getGradesByStudent(studentId)
    fun getGradesByClassAndSubject(className: String, subjectId: String): Flow<List<GradeEntity>> =
        db.gradeDao().getGradesByClassAndSubject(className, subjectId)
    fun getGradeAuditLogs(): Flow<List<GradeAuditLogEntity>> = db.gradeDao().getGradeAuditLogs()

    suspend fun saveGrade(
        id: String?,
        studentId: String,
        studentName: String,
        className: String,
        subjectId: String,
        score: Int,
        quarter: Int,
        lessonDate: String,
        topic: String,
        note: String,
        reasonForChange: String = ""
    ): Result<Unit> {
        val active = _currentUser.value ?: return Result.failure(SecurityException("Tizimga kirmagansiz"))
        if (active.role != UserRole.ADMIN.name && active.role != UserRole.TEACHER.name) {
            return Result.failure(SecurityException("Baho qo'yish huquqi faqat o'qituvchi va administratorga berilgan"))
        }
        if (active.role == UserRole.TEACHER.name) {
            if (!active.canGrade) {
                return Result.failure(SecurityException("Sizga baho qo'yish huquqi cheklangan"))
            }
            if (active.assignedSubject.isNotBlank() && active.assignedSubject != subjectId) {
                return Result.failure(SecurityException("Siz faqat o'z faningiz (${active.assignedSubject}) bo'yicha baholay olasiz"))
            }
        }

        if (score !in 0..100) {
            return Result.failure(IllegalArgumentException("Baho 0 dan 100 ballgacha bo'lishi kerak"))
        }

        val gradeId = id ?: "gr_${UUID.randomUUID().toString().take(8)}"
        val oldGrade = if (id != null) db.gradeDao().getGradeById(id) else null

        val grade = GradeEntity(
            id = gradeId,
            studentId = studentId,
            studentName = studentName,
            className = className,
            subjectId = subjectId,
            score = score,
            quarter = quarter,
            lessonDate = lessonDate,
            topic = topic,
            teacherName = active.fullName,
            note = note,
            updatedAt = System.currentTimeMillis()
        )

        db.gradeDao().insertGrade(grade)

        // Audit log for grade change
        val oldScore = oldGrade?.score ?: 0
        db.gradeDao().insertGradeAuditLog(
            GradeAuditLogEntity(
                gradeId = gradeId,
                studentName = studentName,
                subjectId = subjectId,
                oldScore = oldScore,
                newScore = score,
                changedBy = active.fullName,
                changedByRole = active.role,
                reason = if (reasonForChange.isNotBlank()) reasonForChange else if (oldGrade != null) "Baho tahrirlandi" else "Yangi baho qo'yildi"
            )
        )
        return Result.success(Unit)
    }

    // 5. Attendance
    fun getAllAttendance(): Flow<List<AttendanceEntity>> = db.attendanceDao().getAllAttendance()
    fun getAttendanceByClassAndDate(className: String, date: String): Flow<List<AttendanceEntity>> =
        db.attendanceDao().getAttendanceByClassAndDate(className, date)
    fun getAttendanceByStudent(studentId: String): Flow<List<AttendanceEntity>> =
        db.attendanceDao().getAttendanceByStudent(studentId)

    suspend fun recordAttendance(
        studentId: String,
        studentName: String,
        className: String,
        subjectId: String,
        date: String,
        status: String,
        lessonNumber: Int,
        note: String
    ): Result<Unit> {
        val active = _currentUser.value ?: return Result.failure(SecurityException("Tizimga kirmagansiz"))
        if (active.role != UserRole.ADMIN.name && active.role != UserRole.TEACHER.name) {
            return Result.failure(SecurityException("Davomat kiritish huquqiga ega emassiz"))
        }

        val id = "att_${studentId}_${date}_$lessonNumber"
        val att = AttendanceEntity(
            id = id,
            studentId = studentId,
            studentName = studentName,
            className = className,
            subjectId = subjectId,
            date = date,
            status = status,
            lessonNumber = lessonNumber,
            recordedBy = active.fullName,
            note = note
        )
        db.attendanceDao().insertAttendance(att)
        return Result.success(Unit)
    }

    // 6. Tests & Homework
    fun getAllTests(): Flow<List<TestQuizEntity>> = db.testDao().getAllTests()
    fun getTestsByClass(className: String): Flow<List<TestQuizEntity>> = db.testDao().getTestsByClass(className)
    fun getTestsBySubject(subjectId: String): Flow<List<TestQuizEntity>> = db.testDao().getTestsBySubject(subjectId)
    suspend fun getTestById(id: String): TestQuizEntity? = db.testDao().getTestById(id)
    fun getTestSubmissionsByStudent(studentId: String): Flow<List<TestSubmissionEntity>> =
        db.testDao().getSubmissionsByStudent(studentId)

    suspend fun createTest(test: TestQuizEntity): Result<Unit> {
        val active = _currentUser.value ?: return Result.failure(SecurityException("Tizimga kirmagansiz"))
        if (active.role != UserRole.ADMIN.name && active.role != UserRole.TEACHER.name) {
            return Result.failure(SecurityException("Test yaratish huquqiga ega emassiz"))
        }
        db.testDao().insertTest(test)
        return Result.success(Unit)
    }

    suspend fun submitTest(
        testId: String,
        studentId: String,
        studentName: String,
        score: Int,
        totalQuestions: Int,
        correctCount: Int,
        feedback: String
    ): Result<Unit> {
        val sub = TestSubmissionEntity(
            id = "sub_${UUID.randomUUID().toString().take(8)}",
            testId = testId,
            studentId = studentId,
            studentName = studentName,
            score = score,
            totalQuestions = totalQuestions,
            correctCount = correctCount,
            feedback = feedback
        )
        db.testDao().insertSubmission(sub)
        return Result.success(Unit)
    }

    // Homework
    fun getAllHomework(): Flow<List<HomeworkEntity>> = db.homeworkDao().getAllHomework()
    fun getHomeworkByClass(className: String): Flow<List<HomeworkEntity>> = db.homeworkDao().getHomeworkByClass(className)
    fun getHomeworkSubmissions(studentId: String): Flow<List<HomeworkSubmissionEntity>> =
        db.homeworkDao().getSubmissionsByStudent(studentId)

    suspend fun createHomework(hw: HomeworkEntity): Result<Unit> {
        val active = _currentUser.value ?: return Result.failure(SecurityException("Tizimga kirmagansiz"))
        if (active.role != UserRole.ADMIN.name && active.role != UserRole.TEACHER.name) {
            return Result.failure(SecurityException("Vazifa yaratish huquqiga ega emassiz"))
        }
        db.homeworkDao().insertHomework(hw)
        return Result.success(Unit)
    }

    suspend fun submitHomework(
        homeworkId: String,
        studentId: String,
        studentName: String,
        text: String
    ): Result<Unit> {
        val sub = HomeworkSubmissionEntity(
            id = "hwsub_${UUID.randomUUID().toString().take(8)}",
            homeworkId = homeworkId,
            studentId = studentId,
            studentName = studentName,
            submissionText = text
        )
        db.homeworkDao().insertSubmission(sub)
        return Result.success(Unit)
    }

    suspend fun gradeHomeworkSubmission(sub: HomeworkSubmissionEntity, score: Int, feedback: String): Result<Unit> {
        val active = _currentUser.value ?: return Result.failure(SecurityException("Tizimga kirmagansiz"))
        if (active.role != UserRole.ADMIN.name && active.role != UserRole.TEACHER.name) {
            return Result.failure(SecurityException("Ruxsat berilmagan"))
        }
        val updated = sub.copy(score = score, teacherFeedback = feedback)
        db.homeworkDao().updateSubmission(updated)
        return Result.success(Unit)
    }

    // 7. Library
    fun getAllLibraryResources(): Flow<List<LibraryResourceEntity>> = db.libraryDao().getAllResources()
    fun getLibraryResourcesBySubject(subjectId: String): Flow<List<LibraryResourceEntity>> =
        db.libraryDao().getResourcesBySubject(subjectId)

    suspend fun addLibraryResource(resource: LibraryResourceEntity): Result<Unit> {
        val active = _currentUser.value ?: return Result.failure(SecurityException("Tizimga kirmagansiz"))
        if (active.role != UserRole.ADMIN.name && active.role != UserRole.TEACHER.name) {
            return Result.failure(SecurityException("Kutubxonaga kitob yuklash huquqiga ega emassiz"))
        }
        db.libraryDao().insertResource(resource)
        return Result.success(Unit)
    }

    // 8. Timetable
    fun getAllTimetable(): Flow<List<TimetableEntity>> = db.timetableDao().getAllTimetable()
    fun getTimetableByClass(className: String): Flow<List<TimetableEntity>> = db.timetableDao().getTimetableByClass(className)

    // 9. Announcements
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>> = db.announcementDao().getAllAnnouncements()
    suspend fun postAnnouncement(title: String, content: String, targetRole: String, priority: String): Result<Unit> {
        val active = _currentUser.value ?: return Result.failure(SecurityException("Tizimga kirmagansiz"))
        val ann = AnnouncementEntity(
            id = "ann_${UUID.randomUUID().toString().take(8)}",
            title = title,
            content = content,
            targetRole = targetRole,
            authorName = active.fullName,
            date = "2026-10-10",
            priority = priority
        )
        db.announcementDao().insertAnnouncement(ann)
        return Result.success(Unit)
    }

    // 10. Payments (Click, Payme, Uzum)
    fun getAllPayments(): Flow<List<PaymentEntity>> = db.paymentDao().getAllPayments()
    fun getPaymentsByParent(parentId: String): Flow<List<PaymentEntity>> = db.paymentDao().getPaymentsByParent(parentId)

    suspend fun processPayment(
        parentId: String,
        studentName: String,
        title: String,
        amountSum: Long,
        provider: String
    ): Result<PaymentEntity> {
        val prefix = when (provider) {
            "CLICK" -> "CLK"
            "PAYME" -> "PAY"
            else -> "UZM"
        }
        val txCode = "$prefix-${(100000..999999).random()}"
        val payment = PaymentEntity(
            id = "pay_${UUID.randomUUID().toString().take(8)}",
            parentId = parentId,
            studentName = studentName,
            title = title,
            amountSum = amountSum,
            provider = provider,
            status = "TO'LANDI",
            date = "2026-10-10",
            transactionCode = txCode
        )
        db.paymentDao().insertPayment(payment)
        return Result.success(payment)
    }

    // 11. Admin Audit Logs
    fun getAllAuditLogs(): Flow<List<AdminAuditLogEntity>> = db.adminAuditDao().getAllAuditLogs()
}

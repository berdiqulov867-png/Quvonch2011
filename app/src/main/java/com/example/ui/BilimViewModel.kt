package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
import com.example.data.repository.SchoolRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BilimViewModel(private val repository: SchoolRepository) : ViewModel() {

    val currentUser: StateFlow<UserEntity?> = repository.currentUser

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val classes: StateFlow<List<SchoolClassEntity>> = repository.getAllClasses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subjects: StateFlow<List<SubjectEntity>> = repository.getAllSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val grades: StateFlow<List<GradeEntity>> = repository.getAllGrades()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gradeAuditLogs: StateFlow<List<GradeAuditLogEntity>> = repository.getGradeAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendanceList: StateFlow<List<AttendanceEntity>> = repository.getAllAttendance()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tests: StateFlow<List<TestQuizEntity>> = repository.getAllTests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val homeworkList: StateFlow<List<HomeworkEntity>> = repository.getAllHomework()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val libraryResources: StateFlow<List<LibraryResourceEntity>> = repository.getAllLibraryResources()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val timetable: StateFlow<List<TimetableEntity>> = repository.getAllTimetable()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcements: StateFlow<List<AnnouncementEntity>> = repository.getAllAnnouncements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payments: StateFlow<List<PaymentEntity>> = repository.getAllPayments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminAuditLogs: StateFlow<List<AdminAuditLogEntity>> = repository.getAllAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI state
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _isLoggingIn = MutableStateFlow(false)
    val isLoggingIn: StateFlow<Boolean> = _isLoggingIn.asStateFlow()

    private val _actionMessage = MutableSharedFlow<String>()
    val actionMessage: SharedFlow<String> = _actionMessage.asSharedFlow()

    fun login(username: String, pass: String) {
        viewModelScope.launch {
            _isLoggingIn.value = true
            _loginError.value = null
            val result = repository.login(username, pass)
            _isLoggingIn.value = false
            if (result.isFailure) {
                _loginError.value = result.exceptionOrNull()?.message ?: "Xatolik yuz berdi"
            }
        }
    }

    fun quickLogin(username: String) {
        viewModelScope.launch {
            _isLoggingIn.value = true
            _loginError.value = null
            val result = repository.quickLogin(username)
            _isLoggingIn.value = false
            if (result.isFailure) {
                _loginError.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun logout() {
        repository.logout()
    }

    fun addStudent(fullName: String, username: String, className: String, phone: String, pass: String) {
        viewModelScope.launch {
            val res = repository.addStudent(fullName, username, className, phone, pass)
            if (res.isSuccess) {
                _actionMessage.emit("O'quvchi $fullName muvaffaqiyatli qo'shildi")
            } else {
                _actionMessage.emit("Xatolik: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun updateStudent(user: UserEntity) {
        viewModelScope.launch {
            val res = repository.updateStudent(user)
            if (res.isSuccess) {
                _actionMessage.emit("O'quvchi ma'lumotlari saqlandi")
            } else {
                _actionMessage.emit("Xatolik: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun resetPassword(userId: String, newPass: String) {
        viewModelScope.launch {
            val res = repository.resetUserPassword(userId, newPass)
            if (res.isSuccess) {
                _actionMessage.emit("Parol muvaffaqiyatli yangilandi")
            } else {
                _actionMessage.emit("Xatolik: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun addTeacher(fullName: String, username: String, subject: String, phone: String, pass: String) {
        viewModelScope.launch {
            val res = repository.addTeacher(fullName, username, subject, phone, pass)
            if (res.isSuccess) {
                _actionMessage.emit("O'qituvchi $fullName qo'shildi")
            } else {
                _actionMessage.emit("Xatolik: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun updateTeacherPermissions(userId: String, canGrade: Boolean, canCreateTests: Boolean) {
        viewModelScope.launch {
            val res = repository.updateTeacherPermissions(userId, canGrade, canCreateTests)
            if (res.isSuccess) {
                _actionMessage.emit("O'qituvchi huquqlari yangilandi")
            } else {
                _actionMessage.emit("Xatolik: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun saveGrade(
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
        reason: String
    ) {
        viewModelScope.launch {
            val res = repository.saveGrade(id, studentId, studentName, className, subjectId, score, quarter, lessonDate, topic, note, reason)
            if (res.isSuccess) {
                _actionMessage.emit("$studentName ga $score ball baho kiritildi")
            } else {
                _actionMessage.emit("Baho saqlanmadi: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun recordAttendance(
        studentId: String,
        studentName: String,
        className: String,
        subjectId: String,
        date: String,
        status: String,
        lessonNumber: Int,
        note: String
    ) {
        viewModelScope.launch {
            val res = repository.recordAttendance(studentId, studentName, className, subjectId, date, status, lessonNumber, note)
            if (res.isSuccess) {
                _actionMessage.emit("Davomat qayd etildi ($status)")
            } else {
                _actionMessage.emit("Xatolik: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun createTest(test: TestQuizEntity) {
        viewModelScope.launch {
            val res = repository.createTest(test)
            if (res.isSuccess) {
                _actionMessage.emit("Test muvaffaqiyatli yaratildi")
            } else {
                _actionMessage.emit("Xatolik: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun submitTest(
        testId: String,
        studentId: String,
        studentName: String,
        score: Int,
        totalQuestions: Int,
        correctCount: Int,
        feedback: String
    ) {
        viewModelScope.launch {
            val res = repository.submitTest(testId, studentId, studentName, score, totalQuestions, correctCount, feedback)
            if (res.isSuccess) {
                _actionMessage.emit("Test topshirildi! Sizning natijangiz: $score ball ($correctCount/$totalQuestions)")
            } else {
                _actionMessage.emit("Xatolik: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun createHomework(hw: HomeworkEntity) {
        viewModelScope.launch {
            val res = repository.createHomework(hw)
            if (res.isSuccess) {
                _actionMessage.emit("Uy vazifasi e'lon qilindi")
            } else {
                _actionMessage.emit("Xatolik: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun submitHomework(homeworkId: String, studentId: String, studentName: String, text: String) {
        viewModelScope.launch {
            val res = repository.submitHomework(homeworkId, studentId, studentName, text)
            if (res.isSuccess) {
                _actionMessage.emit("Uy vazifasi topshirildi!")
            } else {
                _actionMessage.emit("Xatolik: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun addLibraryResource(res: LibraryResourceEntity) {
        viewModelScope.launch {
            val r = repository.addLibraryResource(res)
            if (r.isSuccess) {
                _actionMessage.emit("Resurs kutubxonaga qo'shildi")
            } else {
                _actionMessage.emit("Xatolik: ${r.exceptionOrNull()?.message}")
            }
        }
    }

    fun processPayment(title: String, amount: Long, provider: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val res = repository.processPayment(user.id, user.fullName, title, amount, provider)
            if (res.isSuccess) {
                _actionMessage.emit("$provider orqali to'lov muvaffaqiyatli amalga oshirildi! Chek: ${res.getOrNull()?.transactionCode}")
            }
        }
    }

    fun postAnnouncement(title: String, content: String, targetRole: String, priority: String) {
        viewModelScope.launch {
            val res = repository.postAnnouncement(title, content, targetRole, priority)
            if (res.isSuccess) {
                _actionMessage.emit("E'lon barchaga yetkazildi")
            }
        }
    }

    fun updateSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            val res = repository.updateSubject(subject)
            if (res.isSuccess) {
                _actionMessage.emit("Fan ma'lumotlari yangilandi")
            }
        }
    }
}

class BilimViewModelFactory(private val repository: SchoolRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BilimViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BilimViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

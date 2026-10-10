package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username AND isArchived = 0 LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY fullName ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = :role ORDER BY fullName ASC")
    fun getUsersByRole(role: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'STUDENT' AND className = :className AND isArchived = 0 ORDER BY fullName ASC")
    fun getStudentsByClass(className: String): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("SELECT COUNT(*) FROM users WHERE role = :role AND isArchived = 0")
    fun countUsersByRole(role: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface SchoolDao {
    @Query("SELECT * FROM classes ORDER BY gradeLevel ASC, name ASC")
    fun getAllClasses(): Flow<List<SchoolClassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClasses(classes: List<SchoolClassEntity>)

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Update
    suspend fun updateSubject(subject: SubjectEntity)
}

@Dao
interface GradeDao {
    @Query("SELECT * FROM grades ORDER BY lessonDate DESC")
    fun getAllGrades(): Flow<List<GradeEntity>>

    @Query("SELECT * FROM grades WHERE studentId = :studentId ORDER BY lessonDate DESC")
    fun getGradesByStudent(studentId: String): Flow<List<GradeEntity>>

    @Query("SELECT * FROM grades WHERE className = :className AND subjectId = :subjectId ORDER BY lessonDate DESC")
    fun getGradesByClassAndSubject(className: String, subjectId: String): Flow<List<GradeEntity>>

    @Query("SELECT * FROM grades WHERE id = :id LIMIT 1")
    suspend fun getGradeById(id: String): GradeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grade: GradeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrades(grades: List<GradeEntity>)

    @Update
    suspend fun updateGrade(grade: GradeEntity)

    @Query("DELETE FROM grades WHERE id = :id")
    suspend fun deleteGrade(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGradeAuditLog(log: GradeAuditLogEntity)

    @Query("SELECT * FROM grade_audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getGradeAuditLogs(): Flow<List<GradeAuditLogEntity>>
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance ORDER BY date DESC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE className = :className AND date = :date")
    fun getAttendanceByClassAndDate(className: String, date: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceByStudent(studentId: String): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(list: List<AttendanceEntity>)

    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)
}

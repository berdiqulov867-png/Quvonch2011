package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdminAuditLogEntity
import com.example.data.model.AnnouncementEntity
import com.example.data.model.HomeworkEntity
import com.example.data.model.HomeworkSubmissionEntity
import com.example.data.model.LibraryResourceEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.TestQuizEntity
import com.example.data.model.TestSubmissionEntity
import com.example.data.model.TimetableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TestDao {
    @Query("SELECT * FROM tests ORDER BY createdAt DESC")
    fun getAllTests(): Flow<List<TestQuizEntity>>

    @Query("SELECT * FROM tests WHERE className = :className ORDER BY createdAt DESC")
    fun getTestsByClass(className: String): Flow<List<TestQuizEntity>>

    @Query("SELECT * FROM tests WHERE subjectId = :subjectId ORDER BY createdAt DESC")
    fun getTestsBySubject(subjectId: String): Flow<List<TestQuizEntity>>

    @Query("SELECT * FROM tests WHERE id = :id LIMIT 1")
    suspend fun getTestById(id: String): TestQuizEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: TestQuizEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTests(tests: List<TestQuizEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: TestSubmissionEntity)

    @Query("SELECT * FROM test_submissions WHERE studentId = :studentId ORDER BY submittedAt DESC")
    fun getSubmissionsByStudent(studentId: String): Flow<List<TestSubmissionEntity>>

    @Query("SELECT * FROM test_submissions WHERE testId = :testId")
    fun getSubmissionsByTest(testId: String): Flow<List<TestSubmissionEntity>>
}

@Dao
interface HomeworkDao {
    @Query("SELECT * FROM homework ORDER BY createdAt DESC")
    fun getAllHomework(): Flow<List<HomeworkEntity>>

    @Query("SELECT * FROM homework WHERE className = :className ORDER BY createdAt DESC")
    fun getHomeworkByClass(className: String): Flow<List<HomeworkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomework(homework: HomeworkEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeworkList(list: List<HomeworkEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(sub: HomeworkSubmissionEntity)

    @Update
    suspend fun updateSubmission(sub: HomeworkSubmissionEntity)

    @Query("SELECT * FROM homework_submissions WHERE studentId = :studentId")
    fun getSubmissionsByStudent(studentId: String): Flow<List<HomeworkSubmissionEntity>>

    @Query("SELECT * FROM homework_submissions WHERE homeworkId = :homeworkId")
    fun getSubmissionsByHomework(homeworkId: String): Flow<List<HomeworkSubmissionEntity>>
}

@Dao
interface LibraryDao {
    @Query("SELECT * FROM library_resources ORDER BY title ASC")
    fun getAllResources(): Flow<List<LibraryResourceEntity>>

    @Query("SELECT * FROM library_resources WHERE subjectId = :subjectId ORDER BY title ASC")
    fun getResourcesBySubject(subjectId: String): Flow<List<LibraryResourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(resource: LibraryResourceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResources(resources: List<LibraryResourceEntity>)
}

@Dao
interface TimetableDao {
    @Query("SELECT * FROM timetable ORDER BY lessonNumber ASC")
    fun getAllTimetable(): Flow<List<TimetableEntity>>

    @Query("SELECT * FROM timetable WHERE className = :className ORDER BY lessonNumber ASC")
    fun getTimetableByClass(className: String): Flow<List<TimetableEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetable(entries: List<TimetableEntity>)
}

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY date DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncements(list: List<AnnouncementEntity>)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY date DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE parentId = :parentId ORDER BY date DESC")
    fun getPaymentsByParent(parentId: String): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(list: List<PaymentEntity>)

    @Update
    suspend fun updatePayment(payment: PaymentEntity)
}

@Dao
interface AdminAuditDao {
    @Query("SELECT * FROM admin_audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllAuditLogs(): Flow<List<AdminAuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AdminAuditLogEntity)
}

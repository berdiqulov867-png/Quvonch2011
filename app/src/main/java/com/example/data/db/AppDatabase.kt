package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.AdminAuditDao
import com.example.data.dao.AnnouncementDao
import com.example.data.dao.AttendanceDao
import com.example.data.dao.GradeDao
import com.example.data.dao.HomeworkDao
import com.example.data.dao.LibraryDao
import com.example.data.dao.PaymentDao
import com.example.data.dao.SchoolDao
import com.example.data.dao.TestDao
import com.example.data.dao.TimetableDao
import com.example.data.dao.UserDao
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

@Database(
    entities = [
        UserEntity::class,
        SchoolClassEntity::class,
        SubjectEntity::class,
        GradeEntity::class,
        GradeAuditLogEntity::class,
        AttendanceEntity::class,
        TestQuizEntity::class,
        TestSubmissionEntity::class,
        HomeworkEntity::class,
        HomeworkSubmissionEntity::class,
        LibraryResourceEntity::class,
        TimetableEntity::class,
        AnnouncementEntity::class,
        PaymentEntity::class,
        AdminAuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun schoolDao(): SchoolDao
    abstract fun gradeDao(): GradeDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun testDao(): TestDao
    abstract fun homeworkDao(): HomeworkDao
    abstract fun libraryDao(): LibraryDao
    abstract fun timetableDao(): TimetableDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun paymentDao(): PaymentDao
    abstract fun adminAuditDao(): AdminAuditDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bilim_markazi_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

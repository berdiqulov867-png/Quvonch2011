package com.example.data.db

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
import com.example.util.PasswordSecurity

object DatabaseSeeder {

    suspend fun seedIfNeeded(db: AppDatabase) {
        val userCount = db.userDao().getUserCount()
        if (userCount > 0) return

        // 1. Initial Users
        val adminSalt = PasswordSecurity.generateSalt()
        val teacherTarixSalt = PasswordSecurity.generateSalt()
        val teacherGeoSalt = PasswordSecurity.generateSalt()
        val studentSalt = PasswordSecurity.generateSalt()
        val parentSalt = PasswordSecurity.generateSalt()
        val defSalt = PasswordSecurity.generateSalt()

        val users = listOf(
            UserEntity(
                id = "user_admin",
                username = "admin",
                passwordHash = PasswordSecurity.hashPassword("Admin@2026!", adminSalt),
                salt = adminSalt,
                role = "ADMIN",
                fullName = "Akrom Qodirov (Direktor)",
                phone = "+998 90 123 45 67"
            ),
            UserEntity(
                id = "teacher_tarix",
                username = "ustoz.tarix",
                passwordHash = PasswordSecurity.hashPassword("Ustoz@2026!", teacherTarixSalt),
                salt = teacherTarixSalt,
                role = "TEACHER",
                fullName = "Temur Po'latov",
                phone = "+998 91 234 56 78",
                assignedSubject = "tarix",
                canGrade = true,
                canCreateTests = true
            ),
            UserEntity(
                id = "teacher_geo",
                username = "ustoz.geo",
                passwordHash = PasswordSecurity.hashPassword("Ustoz@2026!", teacherGeoSalt),
                salt = teacherGeoSalt,
                role = "TEACHER",
                fullName = "Nodira Rahimova",
                phone = "+998 93 345 67 89",
                assignedSubject = "geografiya",
                canGrade = true,
                canCreateTests = true
            ),
            UserEntity(
                id = "student_sardor",
                username = "sardor.karimov",
                passwordHash = PasswordSecurity.hashPassword("Talaba@2026!", studentSalt),
                salt = studentSalt,
                role = "STUDENT",
                fullName = "Sardor Karimov",
                phone = "+998 97 456 78 90",
                className = "9-A"
            ),
            UserEntity(
                id = "parent_dilnoza",
                username = "dilnoza.karimova",
                passwordHash = PasswordSecurity.hashPassword("Otaona@2026!", parentSalt),
                salt = parentSalt,
                role = "PARENT",
                fullName = "Dilnoza Karimova",
                phone = "+998 99 567 89 01",
                parentOfStudentId = "student_sardor"
            ),
            UserEntity(
                id = "student_madina",
                username = "madina.rahimova",
                passwordHash = PasswordSecurity.hashPassword("Talaba@2026!", defSalt),
                salt = defSalt,
                role = "STUDENT",
                fullName = "Madina Rahimova",
                phone = "+998 90 987 65 43",
                className = "9-A"
            ),
            UserEntity(
                id = "student_jasur",
                username = "jasur.alimov",
                passwordHash = PasswordSecurity.hashPassword("Talaba@2026!", defSalt),
                salt = defSalt,
                role = "STUDENT",
                fullName = "Jasur Alimov",
                phone = "+998 91 876 54 32",
                className = "9-A"
            ),
            UserEntity(
                id = "student_nilufar",
                username = "nilufar.saidova",
                passwordHash = PasswordSecurity.hashPassword("Talaba@2026!", defSalt),
                salt = defSalt,
                role = "STUDENT",
                fullName = "Nilufar Saidova",
                phone = "+998 93 765 43 21",
                className = "9-A"
            ),
            UserEntity(
                id = "student_bobur",
                username = "bobur.omonov",
                passwordHash = PasswordSecurity.hashPassword("Talaba@2026!", defSalt),
                salt = defSalt,
                role = "STUDENT",
                fullName = "Bobur Omonov",
                phone = "+998 94 654 32 10",
                className = "9-A"
            )
        )
        db.userDao().insertUsers(users)

        // 2. Classes
        val classes = listOf(
            SchoolClassEntity("8-A", "8-A sinf", 8, "102-xona", "Dilshod Fayziyev", 28),
            SchoolClassEntity("9-A", "9-A sinf", 9, "204-xona", "Nodira Rahimova", 30),
            SchoolClassEntity("10-A", "10-A sinf", 10, "305-xona", "Temur Po'latov", 26),
            SchoolClassEntity("11-B", "11-B sinf", 11, "401-xona", "Gulnoza Toirova", 24)
        )
        db.schoolDao().insertClasses(classes)

        // 3. Subjects
        val subjects = listOf(
            SubjectEntity(
                id = "tarix",
                nameUz = "O'zbekiston va Jahon Tarixi",
                code = "TARIX",
                colorHex = "#F5B942",
                teacherName = "Temur Po'latov",
                hoursPerWeek = 4,
                description = "Qadimgi sivilizatsiyalar, Amir Temur va Temuriylar davri, yangi davr tarixi."
            ),
            SubjectEntity(
                id = "geografiya",
                nameUz = "Umumiy va Iqtisodiy Geografiya",
                code = "GEO",
                colorHex = "#4DA3FF",
                teacherName = "Nodira Rahimova",
                hoursPerWeek = 3,
                description = "O'zbekiston tabiiy boyliklari, iqlim mintaqalari, jahon iqtisodiyoti va xaritalar."
            )
        )
        db.schoolDao().insertSubjects(subjects)

        // 4. Grades (100-point scale)
        val grades = listOf(
            // Sardor Karimov (Tarix & Geo)
            GradeEntity("g_1", "student_sardor", "Sardor Karimov", "9-A", "tarix", 92, 1, "2026-10-02", "Amir Temur davlati tuzilishi", "Temur Po'latov", "Mavzuni a'lo darajada tahlil qildi"),
            GradeEntity("g_2", "student_sardor", "Sardor Karimov", "9-A", "tarix", 88, 1, "2026-10-05", "Buyuk Ipak Yo'li va savdo karvonlari", "Temur Po'latov", "Yaxshi javob berdi"),
            GradeEntity("g_3", "student_sardor", "Sardor Karimov", "9-A", "geografiya", 95, 1, "2026-10-03", "O'zbekiston relyefi va tog' tizmalari", "Nodira Rahimova", "Xaritada mustaqil ko'rsatib berdi"),
            GradeEntity("g_4", "student_sardor", "Sardor Karimov", "9-A", "geografiya", 90, 1, "2026-10-07", "Amudaryo va Sirdaryo havzasi", "Nodira Rahimova", "A'lo"),

            // Madina Rahimova
            GradeEntity("g_5", "student_madina", "Madina Rahimova", "9-A", "tarix", 98, 1, "2026-10-05", "Buyuk Ipak Yo'li va savdo karvonlari", "Temur Po'latov", "Benuqson tahlil"),
            GradeEntity("g_6", "student_madina", "Madina Rahimova", "9-A", "geografiya", 96, 1, "2026-10-07", "Amudaryo va Sirdaryo havzasi", "Nodira Rahimova", "A'lo"),

            // Jasur Alimov
            GradeEntity("g_7", "student_jasur", "Jasur Alimov", "9-A", "tarix", 78, 1, "2026-10-05", "Buyuk Ipak Yo'li va savdo karvonlari", "Temur Po'latov", "Qoniqarli"),
            GradeEntity("g_8", "student_jasur", "Jasur Alimov", "9-A", "geografiya", 82, 1, "2026-10-07", "Amudaryo va Sirdaryo havzasi", "Nodira Rahimova", "Yaxshi"),

            // Nilufar Saidova
            GradeEntity("g_9", "student_nilufar", "Nilufar Saidova", "9-A", "tarix", 94, 1, "2026-10-05", "Buyuk Ipak Yo'li va savdo karvonlari", "Temur Po'latov", "A'lo"),
            GradeEntity("g_10", "student_nilufar", "Nilufar Saidova", "9-A", "geografiya", 91, 1, "2026-10-07", "Amudaryo va Sirdaryo havzasi", "Nodira Rahimova", "A'lo"),

            // Bobur Omonov
            GradeEntity("g_11", "student_bobur", "Bobur Omonov", "9-A", "tarix", 72, 1, "2026-10-05", "Buyuk Ipak Yo'li va savdo karvonlari", "Temur Po'latov", "Qayta tayyorgarlik kerak"),
            GradeEntity("g_12", "student_bobur", "Bobur Omonov", "9-A", "geografiya", 75, 1, "2026-10-07", "Amudaryo va Sirdaryo havzasi", "Nodira Rahimova", "Yaxshi")
        )
        db.gradeDao().insertGrades(grades)

        // 5. Attendance
        val attendanceList = listOf(
            AttendanceEntity("att_1", "student_sardor", "Sardor Karimov", "9-A", "tarix", "2026-10-08", "KELDI", 1, "Temur Po'latov"),
            AttendanceEntity("att_2", "student_sardor", "Sardor Karimov", "9-A", "geografiya", "2026-10-08", "KELDI", 2, "Nodira Rahimova"),
            AttendanceEntity("att_3", "student_sardor", "Sardor Karimov", "9-A", "tarix", "2026-10-07", "KECHIKDI", 1, "Temur Po'latov", "5 daqiqa kechikdi"),
            AttendanceEntity("att_4", "student_sardor", "Sardor Karimov", "9-A", "geografiya", "2026-10-06", "KELDI", 3, "Nodira Rahimova"),

            AttendanceEntity("att_5", "student_madina", "Madina Rahimova", "9-A", "tarix", "2026-10-08", "KELDI", 1, "Temur Po'latov"),
            AttendanceEntity("att_6", "student_jasur", "Jasur Alimov", "9-A", "tarix", "2026-10-08", "SABABLI", 1, "Temur Po'latov", "Shifokor ma'lumotnomasi"),
            AttendanceEntity("att_7", "student_bobur", "Bobur Omonov", "9-A", "tarix", "2026-10-08", "SABABSIZ", 1, "Temur Po'latov", "Kelmaganligi sababi aniqlanmoqda"),
            AttendanceEntity("att_8", "student_nilufar", "Nilufar Saidova", "9-A", "tarix", "2026-10-08", "KELDI", 1, "Temur Po'latov")
        )
        db.attendanceDao().insertAttendanceList(attendanceList)

        // 6. Tests & Multiple-Choice Questions
        val tarixQuestionsJson = """
            [
              {
                "id": 1,
                "question": "Amir Temur davlatining poytaxti qaysi shahar bo'lgan?",
                "options": ["Buxoro", "Samarqand", "Xiva", "Toshkent"],
                "correctIndex": 1,
                "points": 25
              },
              {
                "id": 2,
                "question": "Amir Temur qaysi yili tavallud topgan?",
                "options": ["1336-yil", "1370-yil", "1380-yil", "1405-yil"],
                "correctIndex": 0,
                "points": 25
              },
              {
                "id": 3,
                "question": "'Kuch - adolatdadir' shiori kimga tegishli?",
                "options": ["Mirzo Ulug'bek", "Alisher Navoiy", "Amir Temur", "Zahiriddin Bobur"],
                "correctIndex": 2,
                "points": 25
              },
              {
                "id": 4,
                "question": "Buyuk Ipak Yo'lining asosiy markaziy tarmoqlari qayerdan o'tgan?",
                "options": ["Shimoliy Yevropa", "O'rta Osiyo va Movarounnahr", "Janubiy Afrika", "Skandinaviya"],
                "correctIndex": 1,
                "points": 25
              }
            ]
        """.trimIndent()

        val geoQuestionsJson = """
            [
              {
                "id": 1,
                "question": "O'zbekistonning eng uzun daryosi qaysi?",
                "options": ["Zarafshon", "Amudaryo", "Sirdaryo", "Chirchiq"],
                "correctIndex": 1,
                "points": 25
              },
              {
                "id": 2,
                "question": "O'zbekiston iqlimi qaysi tipga mansub?",
                "options": ["Tropik nam", "Keskin kontinental", "Ekvatorial", "Dengiz iqlimi"],
                "correctIndex": 1,
                "points": 25
              },
              {
                "id": 3,
                "question": "Dunyo okeanlarining eng kattasi qaysi?",
                "options": ["Atlantika okeani", "Hind okeani", "Tinch okeani", "Shimoliy Muz okeani"],
                "correctIndex": 2,
                "points": 25
              },
              {
                "id": 4,
                "question": "O'zbekistondagi eng baland cho'qqi qaysi?",
                "options": ["Hazrati Sulton (4643 m)", "Katta Chimyon", "Bobotog'", "Qoratepa"],
                "correctIndex": 0,
                "points": 25
              }
            ]
        """.trimIndent()

        val tests = listOf(
            TestQuizEntity(
                id = "test_tarix_1",
                subjectId = "tarix",
                title = "Amir Temur davlati va harbiy san'ati",
                description = "9-sinf o'quvchilari uchun Amir Temur davri bo'yicha oraliq nazorat testi.",
                className = "9-A",
                durationMinutes = 20,
                deadlineDate = "2026-10-15",
                teacherName = "Temur Po'latov",
                questionsJson = tarixQuestionsJson
            ),
            TestQuizEntity(
                id = "test_geo_1",
                subjectId = "geografiya",
                title = "O'zbekiston tabiiy geografiyasi va daryolari",
                description = "O'zbekiston gidrografiyasi va iqlimiy sharoitlari yuzasidan 4 savolli test.",
                className = "9-A",
                durationMinutes = 15,
                deadlineDate = "2026-10-16",
                teacherName = "Nodira Rahimova",
                questionsJson = geoQuestionsJson
            )
        )
        db.testDao().insertTests(tests)

        // Seed sample completed submission for Sardor in Tarix
        db.testDao().insertSubmission(
            TestSubmissionEntity(
                id = "sub_sardor_1",
                testId = "test_tarix_1",
                studentId = "student_sardor",
                studentName = "Sardor Karimov",
                score = 100,
                totalQuestions = 4,
                correctCount = 4,
                feedback = "A'lo natija! Barcha savollarga to'g'ri javob berildi."
            )
        )

        // 7. Homework
        val homeworkList = listOf(
            HomeworkEntity(
                id = "hw_tarix_1",
                subjectId = "tarix",
                className = "9-A",
                title = "Temuriylar renessansi va me'morchiligi haqida insho",
                description = "Samarqanddagi Registon ansambli va Bibixonim masjidining qurilish tarixi bo'yicha 1 varaq konspekt tayyorlash.",
                deadline = "2026-10-12",
                teacherName = "Temur Po'latov"
            ),
            HomeworkEntity(
                id = "hw_geo_1",
                subjectId = "geografiya",
                className = "9-A",
                title = "Orol dengizi havzasi ekologiyasi",
                description = "Orol dengizining qurishi oqibatlari va Orolbo'yi hududida amalga oshirilayotgan ekologik loyihalar tahlili.",
                deadline = "2026-10-14",
                teacherName = "Nodira Rahimova"
            )
        )
        db.homeworkDao().insertHomeworkList(homeworkList)

        db.homeworkDao().insertSubmission(
            HomeworkSubmissionEntity(
                id = "hwsub_1",
                homeworkId = "hw_tarix_1",
                studentId = "student_sardor",
                studentName = "Sardor Karimov",
                submissionText = "Temuriylar davrida Samarqand jahon madaniyati va ilm-fan markaziga aylandi. Registon maydoni, Go'ri Amir maqbarasi me'morchilik durdonalari hisoblanadi.",
                score = 95,
                teacherFeedback = "Juda chuqur va chiroyli yoritilgan! Barakalla."
            )
        )

        // 8. Digital Library
        val libraryBooks = listOf(
            LibraryResourceEntity(
                id = "lib_1",
                subjectId = "tarix",
                title = "O'zbekiston Tarixi (9-sinf darsligi)",
                author = "M. Is'hoqov, A. Muhammadjonov",
                category = "Darslik",
                description = "9-sinf umumta'lim maktablari uchun davlat ta'lim standarti asosidagi to'liq darslik.",
                fileSize = "18.4 MB",
                pageCount = 224,
                chaptersJson = "1-Bob: Qadimgi Baqtriya va So'g'diyona\n2-Bob: Kushonlar podsholigi\n3-Bob: Amir Temur saltanati\n4-Bob: Temuriylar renessansi",
                uploaderName = "Temur Po'latov"
            ),
            LibraryResourceEntity(
                id = "lib_2",
                subjectId = "tarix",
                title = "Temur Tuzuklari — Tarixiy Manba",
                author = "Amir Temur Ko'ragon",
                category = "Manba",
                description = "Davlat boshqaruvi, harbiy yurishlar va adolat qonunlari to'g'risidagi qomusiy asar.",
                fileSize = "9.2 MB",
                pageCount = 160,
                chaptersJson = "1-Qism: Saltanat qoidalari va vazirlar tayinlash\n2-Qism: Qo'shin tuzilishi va harbiy hiylalar\n3-Qism: Mamlakat obodonchiligi",
                uploaderName = "Temur Po'latov"
            ),
            LibraryResourceEntity(
                id = "lib_3",
                subjectId = "geografiya",
                title = "O'zbekiston Tabiiy Geografiyasi (9-sinf)",
                author = "P. G'ulomov, H. Vahobov",
                category = "Darslik",
                description = "O'zbekiston hududining tabiiy sharoiti, yer usti boyliklari, daryolari va ko'llari.",
                fileSize = "22.1 MB",
                pageCount = 196,
                chaptersJson = "1-Bob: Geografik o'rni va chegaralari\n2-Bob: Relyef va foydali qazilmalar\n3-Bob: Iqlim va ichki suvlar\n4-Bob: Tuproq va o'simlik qoplami",
                uploaderName = "Nodira Rahimova"
            ),
            LibraryResourceEntity(
                id = "lib_4",
                subjectId = "geografiya",
                title = "O'zbekiston va Jahon Geografik Atlasi",
                author = "O'zbekiston Geografiya Jamiyati",
                category = "Atlas",
                description = "Yuqori aniqlikdagi siyosiy, tabiiy, iqlimiy va iqtisodiy xaritalar to'plami.",
                fileSize = "34.5 MB",
                pageCount = 120,
                chaptersJson = "Xarita 1: O'zbekiston tabiiy xaritasi\nXarita 2: Viloyatlar ma'muriy xaritasi\nXarita 3: Jahon iqtisodiy mintaqalari",
                uploaderName = "Nodira Rahimova"
            )
        )
        db.libraryDao().insertResources(libraryBooks)

        // 9. Timetable (Weekly Schedule for 9-A)
        val timetableEntries = listOf(
            TimetableEntity("tt_1", "9-A", "Dushanba", 1, "08:30 - 09:15", "tarix", "O'zbekiston va Jahon Tarixi", "Temur Po'latov", "305-xona"),
            TimetableEntity("tt_2", "9-A", "Dushanba", 2, "09:25 - 10:10", "geografiya", "Umumiy va Iqtisodiy Geografiya", "Nodira Rahimova", "204-xona"),
            TimetableEntity("tt_3", "9-A", "Seshanba", 1, "08:30 - 09:15", "geografiya", "Umumiy va Iqtisodiy Geografiya", "Nodira Rahimova", "204-xona"),
            TimetableEntity("tt_4", "9-A", "Chorshanba", 1, "08:30 - 09:15", "tarix", "O'zbekiston va Jahon Tarixi", "Temur Po'latov", "305-xona"),
            TimetableEntity("tt_5", "9-A", "Payshanba", 2, "09:25 - 10:10", "geografiya", "Umumiy va Iqtisodiy Geografiya", "Nodira Rahimova", "204-xona"),
            TimetableEntity("tt_6", "9-A", "Juma", 1, "08:30 - 09:15", "tarix", "O'zbekiston va Jahon Tarixi", "Temur Po'latov", "305-xona"),
            TimetableEntity("tt_7", "9-A", "Shanba", 1, "08:30 - 09:15", "tarix", "O'zbekiston va Jahon Tarixi", "Temur Po'latov", "305-xona")
        )
        db.timetableDao().insertTimetable(timetableEntries)

        // 10. Announcements
        val announcements = listOf(
            AnnouncementEntity(
                id = "ann_1",
                title = "Tarix fani bo'yicha maktab olimpiadasi",
                content = "Hurmatli o'quvchilar! Kelasi dushanba kuni soat 14:00 da 8-11 sinflar o'rtasida Tarix fani olimpiadasining 1-bosqichi bo'lib o'tadi.",
                targetRole = "ALL",
                authorName = "Akrom Qodirov (Direktor)",
                date = "2026-10-09",
                priority = "MUHIM"
            ),
            AnnouncementEntity(
                id = "ann_2",
                title = "Ota-onalar majlisi e'loni",
                content = "9-A sinf ota-onalari diqqatiga: 1-chorak yakunlari va yangi o'quv rejalari bo'yicha onlayn yig'ilish o'tkaziladi.",
                targetRole = "PARENT",
                authorName = "Nodira Rahimova (Sinf rahbari)",
                date = "2026-10-08",
                priority = "MUHIM"
            ),
            AnnouncementEntity(
                id = "ann_3",
                title = "Geografik xaritalar ko'rgazmasi",
                content = "Maktab kutubxonasida 'Sayyoramiz mo'jizalari' nomli geografik atlaslar va interaktiv xaritalar ko'rgazmasi ochildi.",
                targetRole = "STUDENT",
                authorName = "Nodira Rahimova",
                date = "2026-10-06",
                priority = "ODDIY"
            )
        )
        db.announcementDao().insertAnnouncements(announcements)

        // 11. Payments for Parent
        val payments = listOf(
            PaymentEntity(
                id = "pay_1",
                parentId = "parent_dilnoza",
                studentName = "Sardor Karimov",
                title = "Tarix to'garagi (Oktabr oyi)",
                amountSum = 180000,
                provider = "CLICK",
                status = "TO'LANDI",
                date = "2026-10-01",
                transactionCode = "CLK-9481920"
            ),
            PaymentEntity(
                id = "pay_2",
                parentId = "parent_dilnoza",
                studentName = "Sardor Karimov",
                title = "Geografik atlas va metodik qo'llanma",
                amountSum = 65000,
                provider = "PAYME",
                status = "TO'LANDI",
                date = "2026-10-03",
                transactionCode = "PAY-552019"
            ),
            PaymentEntity(
                id = "pay_3",
                parentId = "parent_dilnoza",
                studentName = "Sardor Karimov",
                title = "Tarix fani olimpiada badali",
                amountSum = 50000,
                provider = "UZUM",
                status = "KUTILMOQDA",
                date = "2026-10-09",
                transactionCode = "UZM-112349"
            )
        )
        db.paymentDao().insertPayments(payments)

        // 12. Audit Logs
        val auditLogs = listOf(
            AdminAuditLogEntity(
                action = "TIZIM_ISHGA_TUSHIRILDI",
                targetDetails = "Bilim Markazi maktab bazasi muvaffaqiyatli ishga tushirildi",
                performedBy = "admin"
            ),
            AdminAuditLogEntity(
                action = "O'QUVCHI_QO'SHILDI",
                targetDetails = "Sardor Karimov 9-A sinfiga biriktirildi",
                performedBy = "admin"
            ),
            AdminAuditLogEntity(
                action = "FAN_BIRIKTIRILDI",
                targetDetails = "Temur Po'latovga Tarix fani biriktirildi",
                performedBy = "admin"
            )
        )
        for (log in auditLogs) {
            db.adminAuditDao().insertAuditLog(log)
        }
    }
}

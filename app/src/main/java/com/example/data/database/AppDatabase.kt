package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AppDao
import com.example.data.model.DailyActivityEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.ExamEntity
import com.example.data.model.GpaRecordEntity
import com.example.data.model.MemoryEntity
import com.example.data.model.NoteEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.StudyTaskEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        SubjectEntity::class,
        NoteEntity::class,
        DocumentEntity::class,
        StudyTaskEntity::class,
        DailyActivityEntity::class,
        ExamEntity::class,
        MemoryEntity::class,
        StudySessionEntity::class,
        GpaRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "always_with_student.db"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.appDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: AppDao) {
            // Seed Profile
            dao.saveUserProfile(
                UserProfile(
                    id = 1,
                    fullName = "Rayhan Ahmed",
                    studentId = "STU-2026-4482",
                    institution = "City Science & Technology College",
                    classOrSemester = "Class 12 / HSC 2nd Year",
                    department = "Science & ICT",
                    academicLevel = "HSC"
                )
            )

            // Seed Default Subjects
            val defaultSubjects = listOf(
                SubjectEntity(name = "Bangla", code = "BAN-101", iconName = "MenuBook", colorHex = "#0284C7"),
                SubjectEntity(name = "English", code = "ENG-102", iconName = "Translate", colorHex = "#7C3AED"),
                SubjectEntity(name = "Mathematics", code = "MATH-201", iconName = "Calculate", colorHex = "#2563EB"),
                SubjectEntity(name = "Physics", code = "PHY-202", iconName = "Science", colorHex = "#059669"),
                SubjectEntity(name = "Chemistry", code = "CHEM-203", iconName = "Biotech", colorHex = "#EA580C"),
                SubjectEntity(name = "Biology", code = "BIO-204", iconName = "Spa", colorHex = "#16A34A"),
                SubjectEntity(name = "ICT", code = "ICT-301", iconName = "Devices", colorHex = "#4F46E5"),
                SubjectEntity(name = "Programming", code = "CSE-110", iconName = "Terminal", colorHex = "#9333EA")
            )
            for (sub in defaultSubjects) {
                dao.insertSubject(sub)
            }

            // Seed Sample Notes
            dao.insertNote(
                NoteEntity(
                    title = "Calculus & Derivatives Quick Formula Sheet",
                    content = "Fundamental Differentiation Rules:\n1. d/dx(x^n) = n*x^(n-1)\n2. d/dx(sin x) = cos x\n3. d/dx(cos x) = -sin x\n4. Product Rule: (u*v)' = u'*v + u*v'\n5. Quotient Rule: (u/v)' = (u'*v - u*v') / v^2\n\nRemember to practice integration substitution techniques before Monday!",
                    subjectName = "Mathematics",
                    isPinned = true,
                    isFavorite = true
                )
            )
            dao.insertNote(
                NoteEntity(
                    title = "Newtonian Mechanics & Momentum Conservation",
                    content = "Key Principles:\n- F = dp/dt = m*a (Constant mass)\n- Impulse J = Integral F dt = delta p\n- In elastic collisions: Both Kinetic Energy and Momentum are conserved.\n- In inelastic collisions: Only Momentum is conserved.",
                    subjectName = "Physics",
                    isPinned = false,
                    isFavorite = true
                )
            )
            dao.insertNote(
                NoteEntity(
                    title = "Database Normalization & SQL Queries",
                    content = "1NF: Atomic values, no repeating groups.\n2NF: 1NF + No partial functional dependencies on PK.\n3NF: 2NF + No transitive dependencies (Non-key attributes depend only on PK).\nBCNF: For every functional dependency X -> Y, X is a super key.",
                    subjectName = "ICT",
                    isPinned = false,
                    isFavorite = false
                )
            )

            // Seed Sample Documents
            dao.insertDocument(
                DocumentEntity(
                    title = "HSC Physics Chapter 4 Motion - Class Slides.pdf",
                    fileUri = "content://docs/physics_ch4.pdf",
                    fileType = "PDF",
                    subjectName = "Physics",
                    fileSizeFormatted = "3.8 MB",
                    isFavorite = true,
                    isOfflineAvailable = true
                )
            )
            dao.insertDocument(
                DocumentEntity(
                    title = "English Grammar & Prepositions Guide.pdf",
                    fileUri = "content://docs/english_prep.pdf",
                    fileType = "PDF",
                    subjectName = "English",
                    fileSizeFormatted = "1.4 MB",
                    isFavorite = false,
                    isOfflineAvailable = true
                )
            )

            // Seed Sample Tasks
            dao.insertTask(
                StudyTaskEntity(
                    title = "Solve 20 Calculus integration problems",
                    description = "Exercise 7.2 from textbook page 142",
                    subjectName = "Mathematics",
                    dueDate = System.currentTimeMillis() + 86400000L,
                    priority = "HIGH",
                    reminderTimeFormatted = "07:30 PM"
                )
            )
            dao.insertTask(
                StudyTaskEntity(
                    title = "Review Chemistry Organic Reactions Mechanism",
                    description = "Electrophilic addition & benzene substitution",
                    subjectName = "Chemistry",
                    dueDate = System.currentTimeMillis() + 2 * 86400000L,
                    priority = "MEDIUM",
                    reminderTimeFormatted = "09:00 PM"
                )
            )

            // Seed Daily Habits
            dao.insertDailyActivity(
                DailyActivityEntity(
                    title = "Solve 5 Math Problems Daily",
                    category = "Practice",
                    streakCount = 7,
                    targetDaysPerWeek = 7,
                    isCompletedToday = true
                )
            )
            dao.insertDailyActivity(
                DailyActivityEntity(
                    title = "Learn 10 English Vocabulary Words",
                    category = "Reading",
                    streakCount = 14,
                    targetDaysPerWeek = 7,
                    isCompletedToday = true
                )
            )
            dao.insertDailyActivity(
                DailyActivityEntity(
                    title = "Study Focus 2 Hours",
                    category = "Study",
                    streakCount = 5,
                    targetDaysPerWeek = 6,
                    isCompletedToday = false
                )
            )
            dao.insertDailyActivity(
                DailyActivityEntity(
                    title = "Morning Physical Exercise & Walk",
                    category = "Health",
                    streakCount = 3,
                    targetDaysPerWeek = 5,
                    isCompletedToday = true
                )
            )

            // Seed Exams
            dao.insertExam(
                ExamEntity(
                    title = "Midterm Examination - Higher Math 1st Paper",
                    subjectName = "Mathematics",
                    examDate = System.currentTimeMillis() + 5 * 86400000L,
                    timeFormatted = "10:00 AM",
                    roomNumber = "Room 302",
                    totalMarks = 100,
                    syllabusNotes = "Matrix, Determinants, Vectors & Trigonometry Chapters 1 to 4"
                )
            )
            dao.insertExam(
                ExamEntity(
                    title = "Physics Lab Practical Assessment",
                    subjectName = "Physics",
                    examDate = System.currentTimeMillis() + 12 * 86400000L,
                    timeFormatted = "02:00 PM",
                    roomNumber = "Physics Lab B",
                    totalMarks = 50,
                    syllabusNotes = "Simple Pendulum, Sonometer, Surface Tension experiments"
                )
            )

            // Seed Memories
            dao.insertMemory(
                MemoryEntity(
                    title = "First Day of College Campus Orientation",
                    description = "Met amazing batchmates and professors. The library and main auditorium look magnificent!",
                    dateFormatted = "Aug 15, 2026",
                    moodOrTag = "Campus Life",
                    isFavorite = true
                )
            )
            dao.insertMemory(
                MemoryEntity(
                    title = "Champions at Inter-College Science Olympiad",
                    description = "Our robotics and physics project won 1st prize in the regional science expo!",
                    dateFormatted = "Aug 24, 2026",
                    moodOrTag = "Achievement",
                    isFavorite = true
                )
            )
        }
    }
}

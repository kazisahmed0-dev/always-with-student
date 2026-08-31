package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "Student Scholar",
    val studentId: String = "STU-2026-001",
    val institution: String = "Dhaka University / Ideal College",
    val classOrSemester: String = "1st Semester / HSC 2nd Year",
    val department: String = "Science / Computer Science",
    val academicLevel: String = AcademicLevel.HSC.name,
    val targetGpa: Double = 5.0,
    val profilePhotoUri: String? = null,
    val isSynced: Boolean = false
) {
    val institutionName: String get() = institution
    val departmentOrGroup: String get() = department
}

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val code: String = "",
    val iconName: String = "MenuBook",
    val colorHex: String = "#1E3A8A",
    val isDeleted: Boolean = false,
    val deletedAt: Long = 0L,
    val syncStatus: String = SyncStatus.LOCAL_ONLY.name
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val subjectName: String = "General",
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val imageUri: String? = null,
    val documentUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long = 0L,
    val syncStatus: String = SyncStatus.LOCAL_ONLY.name
)

@Entity(tableName = "study_documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val fileUri: String,
    val fileType: String = DocumentType.PDF.name,
    val subjectName: String = "General",
    val fileSizeFormatted: String = "1.2 MB",
    val isFavorite: Boolean = false,
    val isOfflineAvailable: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long = 0L,
    val syncStatus: String = SyncStatus.LOCAL_ONLY.name
)

@Entity(tableName = "study_tasks")
data class StudyTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val subjectName: String = "General",
    val dueDate: Long = System.currentTimeMillis() + 86400000L,
    val priority: String = TaskPriority.MEDIUM.name,
    val isCompleted: Boolean = false,
    val reminderTimeFormatted: String = "08:00 PM",
    val isDeleted: Boolean = false,
    val deletedAt: Long = 0L,
    val syncStatus: String = SyncStatus.LOCAL_ONLY.name
)

@Entity(tableName = "daily_activities")
data class DailyActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String = "Study", // Study, Revision, Exercise, Reading, Practice
    val streakCount: Int = 0,
    val targetDaysPerWeek: Int = 7,
    val isCompletedToday: Boolean = false,
    val lastCompletedDate: String = "",
    val isDeleted: Boolean = false,
    val deletedAt: Long = 0L,
    val syncStatus: String = SyncStatus.LOCAL_ONLY.name
)

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subjectName: String,
    val examDate: Long = System.currentTimeMillis() + 7 * 86400000L,
    val timeFormatted: String = "10:00 AM",
    val roomNumber: String = "Room 304",
    val totalMarks: Int = 100,
    val syllabusNotes: String = "",
    val isDeleted: Boolean = false,
    val deletedAt: Long = 0L,
    val syncStatus: String = SyncStatus.LOCAL_ONLY.name
)

@Entity(tableName = "student_memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val dateFormatted: String = "",
    val imageUri: String? = null,
    val moodOrTag: String = "Campus Life", // Campus, Friends, Lab, Event, Achievement
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long = 0L,
    val syncStatus: String = SyncStatus.LOCAL_ONLY.name
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectName: String,
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "gpa_records")
data class GpaRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val scaleType: String, // "SSC/HSC 5.0" or "University 4.0"
    val scoreGpa: Double,
    val totalCreditsOrSubjects: String,
    val gradeLetter: String,
    val timestamp: Long = System.currentTimeMillis(),
    val detailsSummary: String = ""
)

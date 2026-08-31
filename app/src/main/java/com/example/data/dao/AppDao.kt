package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // --- Profile ---
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    // --- Subjects ---
    @Query("SELECT * FROM subjects WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Query("UPDATE subjects SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDeleteSubject(id: Int, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE subjects SET isDeleted = 0, deletedAt = 0 WHERE id = :id")
    suspend fun restoreSubject(id: Int)

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun permanentlyDeleteSubject(id: Int)

    // --- Notes ---
    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND subjectName = :subject ORDER BY isPinned DESC, updatedAt DESC")
    fun getNotesBySubject(subject: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoriteNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("UPDATE notes SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDeleteNote(id: Int, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET isDeleted = 0, deletedAt = 0 WHERE id = :id")
    suspend fun restoreNote(id: Int)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun permanentlyDeleteNote(id: Int)

    // --- Documents / PDFs ---
    @Query("SELECT * FROM study_documents WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DocumentEntity): Long

    @Update
    suspend fun updateDocument(doc: DocumentEntity)

    @Query("UPDATE study_documents SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDeleteDocument(id: Int, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE study_documents SET isDeleted = 0, deletedAt = 0 WHERE id = :id")
    suspend fun restoreDocument(id: Int)

    @Query("DELETE FROM study_documents WHERE id = :id")
    suspend fun permanentlyDeleteDocument(id: Int)

    // --- Study Tasks ---
    @Query("SELECT * FROM study_tasks WHERE isDeleted = 0 ORDER BY isCompleted ASC, dueDate ASC")
    fun getAllTasks(): Flow<List<StudyTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: StudyTaskEntity): Long

    @Update
    suspend fun updateTask(task: StudyTaskEntity)

    @Query("UPDATE study_tasks SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDeleteTask(id: Int, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE study_tasks SET isDeleted = 0, deletedAt = 0 WHERE id = :id")
    suspend fun restoreTask(id: Int)

    @Query("DELETE FROM study_tasks WHERE id = :id")
    suspend fun permanentlyDeleteTask(id: Int)

    // --- Daily Activities / Habits ---
    @Query("SELECT * FROM daily_activities WHERE isDeleted = 0 ORDER BY id ASC")
    fun getAllDailyActivities(): Flow<List<DailyActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyActivity(activity: DailyActivityEntity): Long

    @Update
    suspend fun updateDailyActivity(activity: DailyActivityEntity)

    @Query("UPDATE daily_activities SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDeleteDailyActivity(id: Int, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE daily_activities SET isDeleted = 0, deletedAt = 0 WHERE id = :id")
    suspend fun restoreDailyActivity(id: Int)

    @Query("DELETE FROM daily_activities WHERE id = :id")
    suspend fun permanentlyDeleteDailyActivity(id: Int)

    // --- Exams ---
    @Query("SELECT * FROM exams WHERE isDeleted = 0 ORDER BY examDate ASC")
    fun getAllExams(): Flow<List<ExamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity): Long

    @Update
    suspend fun updateExam(exam: ExamEntity)

    @Query("UPDATE exams SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDeleteExam(id: Int, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE exams SET isDeleted = 0, deletedAt = 0 WHERE id = :id")
    suspend fun restoreExam(id: Int)

    @Query("DELETE FROM exams WHERE id = :id")
    suspend fun permanentlyDeleteExam(id: Int)

    // --- Student Memories ---
    @Query("SELECT * FROM student_memories WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Update
    suspend fun updateMemory(memory: MemoryEntity)

    @Query("UPDATE student_memories SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDeleteMemory(id: Int, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE student_memories SET isDeleted = 0, deletedAt = 0 WHERE id = :id")
    suspend fun restoreMemory(id: Int)

    @Query("DELETE FROM student_memories WHERE id = :id")
    suspend fun permanentlyDeleteMemory(id: Int)

    // --- Study Sessions (Focus Timer) ---
    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllStudySessions(): Flow<List<StudySessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySession(session: StudySessionEntity): Long

    // --- GPA Records ---
    @Query("SELECT * FROM gpa_records ORDER BY timestamp DESC")
    fun getAllGpaRecords(): Flow<List<GpaRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGpaRecord(record: GpaRecordEntity): Long

    @Query("DELETE FROM gpa_records WHERE id = :id")
    suspend fun deleteGpaRecord(id: Int)

    // ==========================================
    // --- TRASH / RECYCLE BIN QUERIES ---
    // ==========================================
    @Query("SELECT * FROM notes WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM study_documents WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM study_tasks WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedTasks(): Flow<List<StudyTaskEntity>>

    @Query("SELECT * FROM daily_activities WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedActivities(): Flow<List<DailyActivityEntity>>

    @Query("SELECT * FROM student_memories WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM subjects WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM exams WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedExams(): Flow<List<ExamEntity>>

    // RESTORE ALL
    @Query("UPDATE notes SET isDeleted = 0, deletedAt = 0 WHERE isDeleted = 1")
    suspend fun restoreAllNotes()

    @Query("UPDATE study_documents SET isDeleted = 0, deletedAt = 0 WHERE isDeleted = 1")
    suspend fun restoreAllDocuments()

    @Query("UPDATE study_tasks SET isDeleted = 0, deletedAt = 0 WHERE isDeleted = 1")
    suspend fun restoreAllTasks()

    @Query("UPDATE daily_activities SET isDeleted = 0, deletedAt = 0 WHERE isDeleted = 1")
    suspend fun restoreAllActivities()

    @Query("UPDATE student_memories SET isDeleted = 0, deletedAt = 0 WHERE isDeleted = 1")
    suspend fun restoreAllMemories()

    @Query("UPDATE subjects SET isDeleted = 0, deletedAt = 0 WHERE isDeleted = 1")
    suspend fun restoreAllSubjects()

    @Query("UPDATE exams SET isDeleted = 0, deletedAt = 0 WHERE isDeleted = 1")
    suspend fun restoreAllExams()

    // EMPTY TRASH
    @Query("DELETE FROM notes WHERE isDeleted = 1")
    suspend fun emptyNotesTrash()

    @Query("DELETE FROM study_documents WHERE isDeleted = 1")
    suspend fun emptyDocumentsTrash()

    @Query("DELETE FROM study_tasks WHERE isDeleted = 1")
    suspend fun emptyTasksTrash()

    @Query("DELETE FROM daily_activities WHERE isDeleted = 1")
    suspend fun emptyActivitiesTrash()

    @Query("DELETE FROM student_memories WHERE isDeleted = 1")
    suspend fun emptyMemoriesTrash()

    @Query("DELETE FROM subjects WHERE isDeleted = 1")
    suspend fun emptySubjectsTrash()

    @Query("DELETE FROM exams WHERE isDeleted = 1")
    suspend fun emptyExamsTrash()

    // AUTO-PURGE items older than cutoff timestamp (e.g. 30 days)
    @Query("DELETE FROM notes WHERE isDeleted = 1 AND deletedAt < :cutoffTimestamp")
    suspend fun purgeOldDeletedNotes(cutoffTimestamp: Long)

    @Query("DELETE FROM study_documents WHERE isDeleted = 1 AND deletedAt < :cutoffTimestamp")
    suspend fun purgeOldDeletedDocuments(cutoffTimestamp: Long)

    @Query("DELETE FROM study_tasks WHERE isDeleted = 1 AND deletedAt < :cutoffTimestamp")
    suspend fun purgeOldDeletedTasks(cutoffTimestamp: Long)

    @Query("DELETE FROM daily_activities WHERE isDeleted = 1 AND deletedAt < :cutoffTimestamp")
    suspend fun purgeOldDeletedActivities(cutoffTimestamp: Long)

    @Query("DELETE FROM student_memories WHERE isDeleted = 1 AND deletedAt < :cutoffTimestamp")
    suspend fun purgeOldDeletedMemories(cutoffTimestamp: Long)

    @Query("DELETE FROM subjects WHERE isDeleted = 1 AND deletedAt < :cutoffTimestamp")
    suspend fun purgeOldDeletedSubjects(cutoffTimestamp: Long)

    @Query("DELETE FROM exams WHERE isDeleted = 1 AND deletedAt < :cutoffTimestamp")
    suspend fun purgeOldDeletedExams(cutoffTimestamp: Long)
}

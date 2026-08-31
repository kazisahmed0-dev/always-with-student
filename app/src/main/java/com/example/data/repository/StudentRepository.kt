package com.example.data.repository

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
import kotlinx.coroutines.flow.Flow

class StudentRepository(private val dao: AppDao) {

    // User Profile
    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    suspend fun saveUserProfile(profile: UserProfile) = dao.saveUserProfile(profile)

    // Subjects
    val allSubjects: Flow<List<SubjectEntity>> = dao.getAllSubjects()
    suspend fun addSubject(subject: SubjectEntity) = dao.insertSubject(subject)
    suspend fun updateSubject(subject: SubjectEntity) = dao.updateSubject(subject)
    suspend fun softDeleteSubject(id: Int) = dao.softDeleteSubject(id)
    suspend fun restoreSubject(id: Int) = dao.restoreSubject(id)
    suspend fun permanentlyDeleteSubject(id: Int) = dao.permanentlyDeleteSubject(id)

    // Notes
    val allNotes: Flow<List<NoteEntity>> = dao.getAllNotes()
    val favoriteNotes: Flow<List<NoteEntity>> = dao.getFavoriteNotes()
    fun getNotesBySubject(subject: String): Flow<List<NoteEntity>> = dao.getNotesBySubject(subject)
    suspend fun addNote(note: NoteEntity) = dao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = dao.updateNote(note)
    suspend fun softDeleteNote(id: Int) = dao.softDeleteNote(id)
    suspend fun restoreNote(id: Int) = dao.restoreNote(id)
    suspend fun permanentlyDeleteNote(id: Int) = dao.permanentlyDeleteNote(id)

    // Documents / PDFs
    val allDocuments: Flow<List<DocumentEntity>> = dao.getAllDocuments()
    suspend fun addDocument(doc: DocumentEntity) = dao.insertDocument(doc)
    suspend fun updateDocument(doc: DocumentEntity) = dao.updateDocument(doc)
    suspend fun softDeleteDocument(id: Int) = dao.softDeleteDocument(id)
    suspend fun restoreDocument(id: Int) = dao.restoreDocument(id)
    suspend fun permanentlyDeleteDocument(id: Int) = dao.permanentlyDeleteDocument(id)

    // Study Tasks
    val allTasks: Flow<List<StudyTaskEntity>> = dao.getAllTasks()
    suspend fun addTask(task: StudyTaskEntity) = dao.insertTask(task)
    suspend fun updateTask(task: StudyTaskEntity) = dao.updateTask(task)
    suspend fun softDeleteTask(id: Int) = dao.softDeleteTask(id)
    suspend fun restoreTask(id: Int) = dao.restoreTask(id)
    suspend fun permanentlyDeleteTask(id: Int) = dao.permanentlyDeleteTask(id)

    // Daily Activities / Habits
    val allDailyActivities: Flow<List<DailyActivityEntity>> = dao.getAllDailyActivities()
    suspend fun addDailyActivity(activity: DailyActivityEntity) = dao.insertDailyActivity(activity)
    suspend fun updateDailyActivity(activity: DailyActivityEntity) = dao.updateDailyActivity(activity)
    suspend fun softDeleteDailyActivity(id: Int) = dao.softDeleteDailyActivity(id)
    suspend fun restoreDailyActivity(id: Int) = dao.restoreDailyActivity(id)
    suspend fun permanentlyDeleteDailyActivity(id: Int) = dao.permanentlyDeleteDailyActivity(id)

    // Exams
    val allExams: Flow<List<ExamEntity>> = dao.getAllExams()
    suspend fun addExam(exam: ExamEntity) = dao.insertExam(exam)
    suspend fun updateExam(exam: ExamEntity) = dao.updateExam(exam)
    suspend fun softDeleteExam(id: Int) = dao.softDeleteExam(id)
    suspend fun restoreExam(id: Int) = dao.restoreExam(id)
    suspend fun permanentlyDeleteExam(id: Int) = dao.permanentlyDeleteExam(id)

    // Student Memories
    val allMemories: Flow<List<MemoryEntity>> = dao.getAllMemories()
    suspend fun addMemory(memory: MemoryEntity) = dao.insertMemory(memory)
    suspend fun updateMemory(memory: MemoryEntity) = dao.updateMemory(memory)
    suspend fun softDeleteMemory(id: Int) = dao.softDeleteMemory(id)
    suspend fun restoreMemory(id: Int) = dao.restoreMemory(id)
    suspend fun permanentlyDeleteMemory(id: Int) = dao.permanentlyDeleteMemory(id)

    // Study Sessions
    val allStudySessions: Flow<List<StudySessionEntity>> = dao.getAllStudySessions()
    suspend fun addStudySession(session: StudySessionEntity) = dao.insertStudySession(session)

    // GPA Records
    val allGpaRecords: Flow<List<GpaRecordEntity>> = dao.getAllGpaRecords()
    suspend fun saveGpaRecord(record: GpaRecordEntity) = dao.insertGpaRecord(record)
    suspend fun deleteGpaRecord(id: Int) = dao.deleteGpaRecord(id)

    // Trash Flows
    val deletedNotes: Flow<List<NoteEntity>> = dao.getDeletedNotes()
    val deletedDocuments: Flow<List<DocumentEntity>> = dao.getDeletedDocuments()
    val deletedTasks: Flow<List<StudyTaskEntity>> = dao.getDeletedTasks()
    val deletedActivities: Flow<List<DailyActivityEntity>> = dao.getDeletedActivities()
    val deletedMemories: Flow<List<MemoryEntity>> = dao.getDeletedMemories()
    val deletedSubjects: Flow<List<SubjectEntity>> = dao.getDeletedSubjects()
    val deletedExams: Flow<List<ExamEntity>> = dao.getDeletedExams()

    // Restore All Actions
    suspend fun restoreAllTrash() {
        dao.restoreAllNotes()
        dao.restoreAllDocuments()
        dao.restoreAllTasks()
        dao.restoreAllActivities()
        dao.restoreAllMemories()
        dao.restoreAllSubjects()
        dao.restoreAllExams()
    }

    // Empty Trash Actions
    suspend fun emptyAllTrash() {
        dao.emptyNotesTrash()
        dao.emptyDocumentsTrash()
        dao.emptyTasksTrash()
        dao.emptyActivitiesTrash()
        dao.emptyMemoriesTrash()
        dao.emptySubjectsTrash()
        dao.emptyExamsTrash()
    }

    // 30 Days Auto-Purge
    suspend fun autoPurgeOldTrash() {
        val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        dao.purgeOldDeletedNotes(thirtyDaysAgo)
        dao.purgeOldDeletedDocuments(thirtyDaysAgo)
        dao.purgeOldDeletedTasks(thirtyDaysAgo)
        dao.purgeOldDeletedActivities(thirtyDaysAgo)
        dao.purgeOldDeletedMemories(thirtyDaysAgo)
        dao.purgeOldDeletedSubjects(thirtyDaysAgo)
        dao.purgeOldDeletedExams(thirtyDaysAgo)
    }
}

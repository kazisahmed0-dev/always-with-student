package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.DailyActivityEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.ExamEntity
import com.example.data.model.GpaRecordEntity
import com.example.data.model.MemoryEntity
import com.example.data.model.NoteEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.StudyTaskEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.SyncStatus
import com.example.data.model.UserProfile
import com.example.data.repository.StudentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ScreenTab {
    HOME,
    NOTES_LIBRARY,
    CALCULATORS,
    PLANNER_FOCUS,
    PROFILE_LIFE
}

enum class QuickCaptureType {
    NONE,
    NOTE,
    PHOTO,
    IMAGE_UPLOAD,
    PDF_DOC,
    STUDY_TASK,
    DAILY_ACTIVITY,
    MEMORY,
    EXAM,
    ASSIGNMENT
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = StudentRepository(database.appDao())

    // Active Screen Tab
    private val _currentScreen = MutableStateFlow(ScreenTab.HOME)
    val currentScreen: StateFlow<ScreenTab> = _currentScreen.asStateFlow()

    // Quick Capture Dialog State
    private val _quickCaptureType = MutableStateFlow(QuickCaptureType.NONE)
    val quickCaptureType: StateFlow<QuickCaptureType> = _quickCaptureType.asStateFlow()

    // Search query for Notes and Docs
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Selected Subject filter for Notes / Docs
    private val _selectedSubjectFilter = MutableStateFlow<String?>("All")
    val selectedSubjectFilter: StateFlow<String?> = _selectedSubjectFilter.asStateFlow()

    // Motivational Quotes
    val dailyMotivationalQuotes = listOf(
        "“Small progress every day leads to big success.”",
        "“Study hard in silence, let your success make the noise.”",
        "“The expert in anything was once a beginner.”",
        "“Push yourself, because no one else is going to do it for you.”",
        "“Success doesn’t come from what you do occasionally, it comes from what you do consistently.”",
        "“Your future is created by what you do today, not tomorrow.”"
    )
    val currentQuoteIndex = (System.currentTimeMillis() / (1000 * 60 * 60 * 24) % dailyMotivationalQuotes.size).toInt()

    // Cloud Sync Status
    private val _syncState = MutableStateFlow(SyncStatus.SYNCED)
    val syncState: StateFlow<SyncStatus> = _syncState.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    // -------------------------------------------------------------
    // Observables from Database via Repository
    // -------------------------------------------------------------
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allSubjects: StateFlow<List<SubjectEntity>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteNotes: StateFlow<List<NoteEntity>> = repository.favoriteNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDocuments: StateFlow<List<DocumentEntity>> = repository.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTasks: StateFlow<List<StudyTaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDailyActivities: StateFlow<List<DailyActivityEntity>> = repository.allDailyActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExams: StateFlow<List<ExamEntity>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMemories: StateFlow<List<MemoryEntity>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudySessions: StateFlow<List<StudySessionEntity>> = repository.allStudySessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGpaRecords: StateFlow<List<GpaRecordEntity>> = repository.allGpaRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Trash Flows
    val deletedNotes: StateFlow<List<NoteEntity>> = repository.deletedNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deletedDocuments: StateFlow<List<DocumentEntity>> = repository.deletedDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deletedTasks: StateFlow<List<StudyTaskEntity>> = repository.deletedTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deletedActivities: StateFlow<List<DailyActivityEntity>> = repository.deletedActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deletedMemories: StateFlow<List<MemoryEntity>> = repository.deletedMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deletedSubjects: StateFlow<List<SubjectEntity>> = repository.deletedSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deletedExams: StateFlow<List<ExamEntity>> = repository.deletedExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -------------------------------------------------------------
    // Focus Timer State
    // -------------------------------------------------------------
    private val _focusTimerSeconds = MutableStateFlow(25 * 60)
    val focusTimerSeconds: StateFlow<Int> = _focusTimerSeconds.asStateFlow()

    private val _initialFocusSeconds = MutableStateFlow(25 * 60)
    val initialFocusSeconds: StateFlow<Int> = _initialFocusSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _focusSubject = MutableStateFlow("Mathematics")
    val focusSubject: StateFlow<String> = _focusSubject.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Auto purge items older than 30 days on launch
        viewModelScope.launch {
            repository.autoPurgeOldTrash()
        }
    }

    // Navigation & UI Actions
    fun setScreen(tab: ScreenTab) {
        _currentScreen.value = tab
    }

    fun openQuickCapture(type: QuickCaptureType = QuickCaptureType.NONE) {
        _quickCaptureType.value = type
    }

    fun closeQuickCapture() {
        _quickCaptureType.value = QuickCaptureType.NONE
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedSubjectFilter(subject: String?) {
        _selectedSubjectFilter.value = subject
    }

    fun triggerCloudSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncState.value = SyncStatus.WAITING_SYNC
            delay(1500)
            _syncState.value = SyncStatus.SYNCED
            _isSyncing.value = false
        }
    }

    // Profile Actions
    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
            _syncState.value = SyncStatus.WAITING_SYNC
        }
    }

    // Subject Actions
    fun addSubject(name: String, code: String, iconName: String, colorHex: String) {
        viewModelScope.launch {
            val sub = SubjectEntity(name = name, code = code, iconName = iconName, colorHex = colorHex)
            repository.addSubject(sub)
        }
    }

    fun updateSubject(subject: SubjectEntity) {
        viewModelScope.launch { repository.updateSubject(subject) }
    }

    fun deleteSubject(id: Int) {
        viewModelScope.launch { repository.softDeleteSubject(id) }
    }

    fun restoreSubject(id: Int) {
        viewModelScope.launch { repository.restoreSubject(id) }
    }

    fun permanentlyDeleteSubject(id: Int) {
        viewModelScope.launch { repository.permanentlyDeleteSubject(id) }
    }

    // Note Actions
    fun addNote(title: String, content: String, subjectName: String, isPinned: Boolean, isFavorite: Boolean, imageUri: String? = null) {
        viewModelScope.launch {
            val note = NoteEntity(
                title = title,
                content = content,
                subjectName = subjectName,
                isPinned = isPinned,
                isFavorite = isFavorite,
                imageUri = imageUri
            )
            repository.addNote(note)
            _syncState.value = SyncStatus.WAITING_SYNC
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
            _syncState.value = SyncStatus.WAITING_SYNC
        }
    }

    fun toggleNoteFavorite(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isFavorite = !note.isFavorite))
        }
    }

    fun toggleNotePinned(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned))
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch { repository.softDeleteNote(id) }
    }

    fun restoreNote(id: Int) {
        viewModelScope.launch { repository.restoreNote(id) }
    }

    fun permanentlyDeleteNote(id: Int) {
        viewModelScope.launch { repository.permanentlyDeleteNote(id) }
    }

    // Document / PDF Actions
    fun addDocument(title: String, fileUri: String, fileType: String, subjectName: String, fileSize: String) {
        viewModelScope.launch {
            val doc = DocumentEntity(
                title = title,
                fileUri = fileUri,
                fileType = fileType,
                subjectName = subjectName,
                fileSizeFormatted = fileSize
            )
            repository.addDocument(doc)
            _syncState.value = SyncStatus.WAITING_SYNC
        }
    }

    fun toggleDocumentFavorite(doc: DocumentEntity) {
        viewModelScope.launch {
            repository.updateDocument(doc.copy(isFavorite = !doc.isFavorite))
        }
    }

    fun deleteDocument(id: Int) {
        viewModelScope.launch { repository.softDeleteDocument(id) }
    }

    fun restoreDocument(id: Int) {
        viewModelScope.launch { repository.restoreDocument(id) }
    }

    fun permanentlyDeleteDocument(id: Int) {
        viewModelScope.launch { repository.permanentlyDeleteDocument(id) }
    }

    // Study Task Actions
    fun addTask(title: String, description: String, subjectName: String, dueDate: Long, priority: String, reminder: String) {
        viewModelScope.launch {
            val task = StudyTaskEntity(
                title = title,
                description = description,
                subjectName = subjectName,
                dueDate = dueDate,
                priority = priority,
                reminderTimeFormatted = reminder
            )
            repository.addTask(task)
            _syncState.value = SyncStatus.WAITING_SYNC
        }
    }

    fun toggleTaskCompletion(task: StudyTaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(id: Int) {
        viewModelScope.launch { repository.softDeleteTask(id) }
    }

    fun restoreTask(id: Int) {
        viewModelScope.launch { repository.restoreTask(id) }
    }

    fun permanentlyDeleteTask(id: Int) {
        viewModelScope.launch { repository.permanentlyDeleteTask(id) }
    }

    // Daily Activities / Habits Actions
    fun addDailyActivity(title: String, category: String, targetDays: Int = 7) {
        viewModelScope.launch {
            val activity = DailyActivityEntity(
                title = title,
                category = category,
                targetDaysPerWeek = targetDays,
                streakCount = 0
            )
            repository.addDailyActivity(activity)
            _syncState.value = SyncStatus.WAITING_SYNC
        }
    }

    fun toggleDailyActivityToday(activity: DailyActivityEntity) {
        viewModelScope.launch {
            val newCompleted = !activity.isCompletedToday
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val newStreak = if (newCompleted) activity.streakCount + 1 else maxOf(0, activity.streakCount - 1)
            repository.updateDailyActivity(
                activity.copy(
                    isCompletedToday = newCompleted,
                    streakCount = newStreak,
                    lastCompletedDate = if (newCompleted) todayStr else activity.lastCompletedDate
                )
            )
        }
    }

    fun deleteDailyActivity(id: Int) {
        viewModelScope.launch { repository.softDeleteDailyActivity(id) }
    }

    fun restoreDailyActivity(id: Int) {
        viewModelScope.launch { repository.restoreDailyActivity(id) }
    }

    fun permanentlyDeleteDailyActivity(id: Int) {
        viewModelScope.launch { repository.permanentlyDeleteDailyActivity(id) }
    }

    // Exam Actions
    fun addExam(title: String, subjectName: String, examDate: Long, time: String, room: String, marks: Int, syllabus: String) {
        viewModelScope.launch {
            val exam = ExamEntity(
                title = title,
                subjectName = subjectName,
                examDate = examDate,
                timeFormatted = time,
                roomNumber = room,
                totalMarks = marks,
                syllabusNotes = syllabus
            )
            repository.addExam(exam)
            _syncState.value = SyncStatus.WAITING_SYNC
        }
    }

    fun deleteExam(id: Int) {
        viewModelScope.launch { repository.softDeleteExam(id) }
    }

    fun restoreExam(id: Int) {
        viewModelScope.launch { repository.restoreExam(id) }
    }

    fun permanentlyDeleteExam(id: Int) {
        viewModelScope.launch { repository.permanentlyDeleteExam(id) }
    }

    // Student Memory Actions
    fun addMemory(title: String, description: String, dateFormatted: String, moodTag: String, imageUri: String? = null) {
        viewModelScope.launch {
            val memory = MemoryEntity(
                title = title,
                description = description,
                dateFormatted = dateFormatted.ifEmpty { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()) },
                moodOrTag = moodTag,
                imageUri = imageUri
            )
            repository.addMemory(memory)
            _syncState.value = SyncStatus.WAITING_SYNC
        }
    }

    fun deleteMemory(id: Int) {
        viewModelScope.launch { repository.softDeleteMemory(id) }
    }

    fun restoreMemory(id: Int) {
        viewModelScope.launch { repository.restoreMemory(id) }
    }

    fun permanentlyDeleteMemory(id: Int) {
        viewModelScope.launch { repository.permanentlyDeleteMemory(id) }
    }

    // GPA Records
    fun saveGpaRecord(title: String, scaleType: String, scoreGpa: Double, totalCreditsOrSubjects: String, gradeLetter: String, summary: String) {
        viewModelScope.launch {
            repository.saveGpaRecord(
                GpaRecordEntity(
                    title = title,
                    scaleType = scaleType,
                    scoreGpa = scoreGpa,
                    totalCreditsOrSubjects = totalCreditsOrSubjects,
                    gradeLetter = gradeLetter,
                    detailsSummary = summary
                )
            )
        }
    }

    fun deleteGpaRecord(id: Int) {
        viewModelScope.launch { repository.deleteGpaRecord(id) }
    }

    // Recycle Bin Global Actions
    fun restoreAllTrash() {
        viewModelScope.launch { repository.restoreAllTrash() }
    }

    fun emptyAllTrash() {
        viewModelScope.launch { repository.emptyAllTrash() }
    }

    // Focus Timer Controls
    fun setFocusDuration(minutes: Int) {
        val seconds = minutes * 60
        _initialFocusSeconds.value = seconds
        _focusTimerSeconds.value = seconds
        if (_isTimerRunning.value) {
            pauseFocusTimer()
        }
    }

    fun setFocusSubject(subject: String) {
        _focusSubject.value = subject
    }

    fun startFocusTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_focusTimerSeconds.value > 0 && _isTimerRunning.value) {
                delay(1000)
                _focusTimerSeconds.value -= 1
            }
            if (_focusTimerSeconds.value == 0 && _isTimerRunning.value) {
                _isTimerRunning.value = false
                val minutesSpent = _initialFocusSeconds.value / 60
                repository.addStudySession(
                    StudySessionEntity(
                        subjectName = _focusSubject.value,
                        durationMinutes = minutesSpent,
                        notes = "Completed full focus session"
                    )
                )
            }
        }
    }

    fun pauseFocusTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetFocusTimer() {
        pauseFocusTimer()
        _focusTimerSeconds.value = _initialFocusSeconds.value
    }
}

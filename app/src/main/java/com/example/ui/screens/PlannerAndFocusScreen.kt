package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DailyActivityEntity
import com.example.data.model.ExamEntity
import com.example.data.model.StudyTaskEntity
import com.example.ui.MainViewModel
import com.example.ui.QuickCaptureType
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.PriorityBadge
import com.example.ui.components.SubjectTag
import com.example.ui.theme.DangerRed
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TertiaryAmber
import com.example.ui.theme.WarningOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerAndFocusScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Tasks", "Habits", "Focus Timer", "Exams")

    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val habits by viewModel.allDailyActivities.collectAsStateWithLifecycle()
    val exams by viewModel.allExams.collectAsStateWithLifecycle()
    val focusSeconds by viewModel.focusTimerSeconds.collectAsStateWithLifecycle()
    val initialFocusSeconds by viewModel.initialFocusSeconds.collectAsStateWithLifecycle()
    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()
    val focusSubject by viewModel.focusSubject.collectAsStateWithLifecycle()
    val studySessions by viewModel.allStudySessions.collectAsStateWithLifecycle()
    val subjects by viewModel.allSubjects.collectAsStateWithLifecycle()

    var taskToDelete by remember { mutableStateOf<StudyTaskEntity?>(null) }
    var habitToDelete by remember { mutableStateOf<DailyActivityEntity?>(null) }
    var examToDelete by remember { mutableStateOf<ExamEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Study Planner & Focus",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tasks, daily habits, pomodoro focus & exams",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        when (selectedTab) {
                            0 -> viewModel.openQuickCapture(QuickCaptureType.STUDY_TASK)
                            1 -> viewModel.openQuickCapture(QuickCaptureType.DAILY_ACTIVITY)
                            2 -> viewModel.startFocusTimer()
                            3 -> viewModel.openQuickCapture(QuickCaptureType.EXAM)
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (selectedTab) {
                            0 -> "New Task"
                            1 -> "New Habit"
                            2 -> "Focus Now"
                            else -> "Add Exam"
                        },
                        fontSize = 12.sp
                    )
                }
            }
        }

        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> StudyTasksSection(
                    tasks = tasks,
                    onToggleComplete = { viewModel.toggleTaskCompletion(it) },
                    onDelete = { taskToDelete = it },
                    onAddTask = { viewModel.openQuickCapture(QuickCaptureType.STUDY_TASK) }
                )
                1 -> DailyHabitsSection(
                    habits = habits,
                    onToggleToday = { viewModel.toggleDailyActivityToday(it) },
                    onDelete = { habitToDelete = it },
                    onAddHabit = { viewModel.openQuickCapture(QuickCaptureType.DAILY_ACTIVITY) }
                )
                2 -> FocusTimerSection(
                    focusSeconds = focusSeconds,
                    initialFocusSeconds = initialFocusSeconds,
                    isTimerRunning = isTimerRunning,
                    focusSubject = focusSubject,
                    studySessions = studySessions,
                    subjects = subjects.map { it.name },
                    onSetDuration = { viewModel.setFocusDuration(it) },
                    onSetSubject = { viewModel.setFocusSubject(it) },
                    onStart = { viewModel.startFocusTimer() },
                    onPause = { viewModel.pauseFocusTimer() },
                    onReset = { viewModel.resetFocusTimer() }
                )
                3 -> ExamsScheduleSection(
                    exams = exams,
                    onDelete = { examToDelete = it },
                    onAddExam = { viewModel.openQuickCapture(QuickCaptureType.EXAM) }
                )
            }
        }
    }

    // Delete Confirmations (soft delete to Trash)
    taskToDelete?.let { task ->
        ConfirmDeleteDialog(
            title = "Move Task to Trash?",
            message = "“${task.title}” will be moved to the Recycle Bin.",
            onConfirm = { viewModel.deleteTask(task.id) },
            onDismiss = { taskToDelete = null }
        )
    }

    habitToDelete?.let { habit ->
        ConfirmDeleteDialog(
            title = "Move Habit to Trash?",
            message = "“${habit.title}” will be moved to the Recycle Bin.",
            onConfirm = { viewModel.deleteDailyActivity(habit.id) },
            onDismiss = { habitToDelete = null }
        )
    }

    examToDelete?.let { exam ->
        ConfirmDeleteDialog(
            title = "Move Exam to Trash?",
            message = "“${exam.title}” will be moved to the Recycle Bin.",
            onConfirm = { viewModel.deleteExam(exam.id) },
            onDismiss = { examToDelete = null }
        )
    }
}

@Composable
fun StudyTasksSection(
    tasks: List<StudyTaskEntity>,
    onToggleComplete: (StudyTaskEntity) -> Unit,
    onDelete: (StudyTaskEntity) -> Unit,
    onAddTask: () -> Unit
) {
    var taskFilter by remember { mutableStateOf("ALL") }
    val filteredTasks = when (taskFilter) {
        "PENDING" -> tasks.filter { !it.isCompleted }
        "COMPLETED" -> tasks.filter { it.isCompleted }
        else -> tasks
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = taskFilter == "ALL",
                onClick = { taskFilter = "ALL" },
                label = { Text("All (${tasks.size})") }
            )
            FilterChip(
                selected = taskFilter == "PENDING",
                onClick = { taskFilter = "PENDING" },
                label = { Text("Pending (${tasks.count { !it.isCompleted }})") }
            )
            FilterChip(
                selected = taskFilter == "COMPLETED",
                onClick = { taskFilter = "COMPLETED" },
                label = { Text("Completed (${tasks.count { it.isCompleted }})") }
            )
        }

        if (filteredTasks.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Default.CheckCircle,
                title = "No tasks in this view",
                description = "Stay organized and on schedule by creating your daily study milestones.",
                actionText = "Create Study Task",
                onActionClick = onAddTask
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 90.dp, top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTasks, key = { it.id }) { task ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { onToggleComplete(task) },
                                colors = CheckboxDefaults.colors(checkedColor = SuccessGreen)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                    style = if (task.isCompleted) MaterialTheme.typography.bodyMedium.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough) else MaterialTheme.typography.bodyMedium
                                )
                                if (task.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = task.description,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    SubjectTag(subjectName = task.subjectName)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    PriorityBadge(priority = task.priority)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "⏰ ${task.reminderTimeFormatted}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(onClick = { onDelete(task) }, modifier = Modifier.size(32.dp)) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyHabitsSection(
    habits: List<DailyActivityEntity>,
    onToggleToday: (DailyActivityEntity) -> Unit,
    onDelete: (DailyActivityEntity) -> Unit,
    onAddHabit: () -> Unit
) {
    if (habits.isEmpty()) {
        EmptyStateCard(
            icon = Icons.Default.LocalFireDepartment,
            title = "No daily habits added yet",
            description = "Build consistent student study routines and track your daily streaks.",
            actionText = "Add Daily Habit",
            onActionClick = onAddHabit
        )
    } else {
        val completedCount = habits.count { it.isCompletedToday }
        val habitPercent = if (habits.isNotEmpty()) completedCount.toFloat() / habits.size else 0f

        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 90.dp, top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Overall Daily Streak Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = WarningOrange,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Daily Habit Consistency",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Text(
                                text = "$completedCount of ${habits.size} Done Today",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { habitPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            items(habits, key = { it.id }) { habit ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (habit.isCompletedToday) SuccessGreen.copy(alpha = 0.15f) else WarningOrange.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = if (habit.isCompletedToday) SuccessGreen else WarningOrange,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "${habit.streakCount}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (habit.isCompletedToday) SuccessGreen else WarningOrange
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = habit.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${habit.category} • Target ${habit.targetDaysPerWeek} days/wk",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = { onToggleToday(habit) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (habit.isCompletedToday) SuccessGreen else MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = if (habit.isCompletedToday) Icons.Default.Check else Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (habit.isCompletedToday) "Done" else "Check In", fontSize = 12.sp)
                            }

                            IconButton(onClick = { onDelete(habit) }, modifier = Modifier.size(32.dp)) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FocusTimerSection(
    focusSeconds: Int,
    initialFocusSeconds: Int,
    isTimerRunning: Boolean,
    focusSubject: String,
    studySessions: List<com.example.data.model.StudySessionEntity>,
    subjects: List<String>,
    onSetDuration: (Int) -> Unit,
    onSetSubject: (String) -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit
) {
    val minutes = focusSeconds / 60
    val seconds = focusSeconds % 60
    val timeFormatted = String.format(Locale.US, "%02d:%02d", minutes, seconds)
    val progress = if (initialFocusSeconds > 0) focusSeconds.toFloat() / initialFocusSeconds else 0f

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 90.dp, top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pomodoro Visual Dial Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pomodoro Study Clock",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Focus intensely on $focusSubject without distractions",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Timer Circular Clock Container
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = timeFormatted,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (isTimerRunning) "🔥 Focusing..." else "Paused",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Control Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (isTimerRunning) onPause() else onStart()
                            },
                            modifier = Modifier
                                .width(160.dp)
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTimerRunning) WarningOrange else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isTimerRunning) "Pause" else "Start Focus", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        OutlinedButton(
                            onClick = onReset,
                            modifier = Modifier.height(50.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Duration Presets
                    Text("Session Length Presets", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(15, 25, 45, 60).forEach { mins ->
                            FilterChip(
                                selected = (initialFocusSeconds / 60) == mins,
                                onClick = { onSetDuration(mins) },
                                label = { Text("${mins}m") }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Subject Selector
                    Text("Select Target Subject", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val subList = if (subjects.isNotEmpty()) subjects else listOf("Mathematics", "Physics", "English", "ICT")
                        items(subList) { sub ->
                            FilterChip(
                                selected = focusSubject == sub,
                                onClick = { onSetSubject(sub) },
                                label = { Text(sub) }
                            )
                        }
                    }
                }
            }
        }

        // Focus Session History
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Completed Focus Logs",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (studySessions.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "Complete a focus session above to record your daily focus hours!",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        studySessions.take(5).forEach { session ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(text = session.subjectName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(text = "${session.durationMinutes} Minutes Focused", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Text(
                                        text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(session.timestamp)),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExamsScheduleSection(
    exams: List<ExamEntity>,
    onDelete: (ExamEntity) -> Unit,
    onAddExam: () -> Unit
) {
    if (exams.isEmpty()) {
        EmptyStateCard(
            icon = Icons.Default.School,
            title = "No upcoming exams scheduled",
            description = "Track your quiz dates, midterms, and finals with countdown timers.",
            actionText = "Add Exam",
            onActionClick = onAddExam
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 90.dp, top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(exams, key = { it.id }) { exam ->
                val daysLeft = maxOf(0, ((exam.examDate - System.currentTimeMillis()) / 86400000L).toInt())
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SubjectTag(subjectName = exam.subjectName)

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (daysLeft <= 2) DangerRed.copy(alpha = 0.15f) else InfoBlue.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (daysLeft == 0) "Exam Today!" else "$daysLeft Days Left",
                                    color = if (daysLeft <= 2) DangerRed else InfoBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = exam.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "📍 Hall: ${exam.roomNumber} • ${exam.timeFormatted}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Total Marks: ${exam.totalMarks}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (exam.syllabusNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "📖 Syllabus: ${exam.syllabusNotes}",
                                    modifier = Modifier.padding(10.dp),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = { onDelete(exam) }, modifier = Modifier.size(32.dp)) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed)
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoDelete
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AcademicLevel
import com.example.data.model.MemoryEntity
import com.example.data.model.UserProfile
import com.example.ui.MainViewModel
import com.example.ui.QuickCaptureType
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.SyncStatusBadge
import com.example.ui.theme.DangerRed
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TertiaryAmber
import com.example.ui.theme.WarningOrange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAndLifeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Profile", "Memories", "Recycle Bin", "Data & Sync")

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val memories by viewModel.allMemories.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    // Deleted items for Trash Tab
    val deletedNotes by viewModel.deletedNotes.collectAsStateWithLifecycle()
    val deletedDocs by viewModel.deletedDocuments.collectAsStateWithLifecycle()
    val deletedTasks by viewModel.deletedTasks.collectAsStateWithLifecycle()
    val deletedActivities by viewModel.deletedActivities.collectAsStateWithLifecycle()
    val deletedMemories by viewModel.deletedMemories.collectAsStateWithLifecycle()
    val deletedSubjects by viewModel.deletedSubjects.collectAsStateWithLifecycle()
    val deletedExams by viewModel.deletedExams.collectAsStateWithLifecycle()

    val totalTrashCount = deletedNotes.size + deletedDocs.size + deletedTasks.size +
            deletedActivities.size + deletedMemories.size + deletedSubjects.size + deletedExams.size

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showEmptyTrashDialog by remember { mutableStateOf(false) }
    var itemToPermanentDelete by remember { mutableStateOf<Pair<String, () -> Unit>?>(null) }

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
                        text = "Profile & Student Life",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Academic details, campus memories & trash recovery",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                SyncStatusBadge(
                    syncStatus = syncStatus,
                    isSyncing = isSyncing,
                    onSyncClick = { viewModel.triggerCloudSync() }
                )
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                            if (index == 2 && totalTrashCount > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = DangerRed
                                ) {
                                    Text(
                                        text = "$totalTrashCount",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> AcademicProfileSection(
                    profile = userProfile,
                    onSaveProfile = { updated ->
                        viewModel.updateProfile(updated)
                        scope.launch { snackbarHostState.showSnackbar("Academic profile updated successfully!") }
                    }
                )
                1 -> StudentMemoriesSection(
                    memories = memories,
                    onAddMemory = { viewModel.openQuickCapture(QuickCaptureType.MEMORY) },
                    onDeleteMemory = { viewModel.deleteMemory(it) }
                )
                2 -> RecycleBinSection(
                    deletedNotes = deletedNotes,
                    deletedDocs = deletedDocs,
                    deletedTasks = deletedTasks,
                    deletedActivities = deletedActivities,
                    deletedMemories = deletedMemories,
                    deletedSubjects = deletedSubjects,
                    deletedExams = deletedExams,
                    onRestoreNote = { viewModel.restoreNote(it) },
                    onRestoreDoc = { viewModel.restoreDocument(it) },
                    onRestoreTask = { viewModel.restoreTask(it) },
                    onRestoreActivity = { viewModel.restoreDailyActivity(it) },
                    onRestoreMemory = { viewModel.restoreMemory(it) },
                    onRestoreSubject = { viewModel.restoreSubject(it) },
                    onRestoreExam = { viewModel.restoreExam(it) },
                    onRestoreAll = {
                        viewModel.restoreAllTrash()
                        scope.launch { snackbarHostState.showSnackbar("All items restored successfully!") }
                    },
                    onEmptyTrash = { showEmptyTrashDialog = true },
                    onPermanentDelete = { name, action ->
                        itemToPermanentDelete = name to action
                    }
                )
                3 -> DataAndSyncSection(
                    syncStatus = syncStatus,
                    isSyncing = isSyncing,
                    onTriggerSync = {
                        viewModel.triggerCloudSync()
                        scope.launch { snackbarHostState.showSnackbar("Synchronizing offline data...") }
                    }
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
            )
        }
    }

    // Empty Trash confirmation dialog
    if (showEmptyTrashDialog) {
        ConfirmDeleteDialog(
            title = "Permanently Empty Trash?",
            message = "This will permanently wipe all $totalTrashCount deleted items. This cannot be undone.",
            isPermanent = true,
            onConfirm = {
                viewModel.emptyAllTrash()
                showEmptyTrashDialog = false
            },
            onDismiss = { showEmptyTrashDialog = false }
        )
    }

    // Single item permanent delete dialog
    itemToPermanentDelete?.let { (title, action) ->
        ConfirmDeleteDialog(
            title = "Permanently Delete “$title”?",
            message = "This item will be permanently removed from your device immediately.",
            isPermanent = true,
            onConfirm = {
                action()
                itemToPermanentDelete = null
            },
            onDismiss = { itemToPermanentDelete = null }
        )
    }
}

@Composable
fun AcademicProfileSection(
    profile: UserProfile?,
    onSaveProfile: (UserProfile) -> Unit
) {
    var fullName by remember(profile) { mutableStateOf(profile?.fullName ?: "Rahim Ahmed") }
    var studentId by remember(profile) { mutableStateOf(profile?.studentId ?: "STD-2026-9042") }
    var institution by remember(profile) { mutableStateOf(profile?.institutionName ?: "Dhaka Residential Model College") }
    var classOrSem by remember(profile) { mutableStateOf(profile?.classOrSemester ?: "HSC 2nd Year (Batch 2026)") }
    var department by remember(profile) { mutableStateOf(profile?.departmentOrGroup ?: "Science Group") }
    var academicLevel by remember(profile) { mutableStateOf(profile?.academicLevel ?: AcademicLevel.HSC.name) }
    var targetGpa by remember(profile) { mutableStateOf(profile?.targetGpa?.toString() ?: "5.00") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 90.dp, top = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Profile Card Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        Text(text = "$academicLevel • $institution", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Target GPA / CGPA: $targetGpa", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TertiaryAmber)
                    }
                }
            }
        }

        // Academic Level Selection
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Academic Level", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(AcademicLevel.SCHOOL.name, AcademicLevel.SSC.name, AcademicLevel.HSC.name, AcademicLevel.UNIVERSITY.name, AcademicLevel.DIPLOMA.name).forEach { lvl ->
                            FilterChip(
                                selected = academicLevel == lvl,
                                onClick = { academicLevel = lvl },
                                label = { Text(lvl, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Editable Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Student Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        label = { Text("Student ID / Roll Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = institution,
                        onValueChange = { institution = it },
                        label = { Text("School / College / University Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = { Text("Group / Department / Major") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = classOrSem,
                        onValueChange = { classOrSem = it },
                        label = { Text("Class / Semester / Year") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = targetGpa,
                        onValueChange = { targetGpa = it },
                        label = { Text("Target GPA / CGPA Goal") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            val updated = (profile ?: UserProfile()).copy(
                                fullName = fullName,
                                studentId = studentId,
                                institution = institution,
                                classOrSemester = classOrSem,
                                department = department,
                                academicLevel = academicLevel,
                                targetGpa = targetGpa.toDoubleOrNull() ?: 5.0
                            )
                            onSaveProfile(updated)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Profile Details")
                    }
                }
            }
        }
    }
}

@Composable
fun StudentMemoriesSection(
    memories: List<MemoryEntity>,
    onAddMemory: () -> Unit,
    onDeleteMemory: (Int) -> Unit
) {
    if (memories.isEmpty()) {
        EmptyStateCard(
            icon = Icons.Default.PhotoAlbum,
            title = "No student memories saved yet",
            description = "Save your campus memories, group study photos, achievements, and special college moments.",
            actionText = "Save a Memory",
            onActionClick = onAddMemory
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 90.dp, top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Campus Memories (${memories.size})",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(onClick = onAddMemory, shape = RoundedCornerShape(12.dp)) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Moment", fontSize = 12.sp)
                    }
                }
            }

            items(memories, key = { it.id }) { mem ->
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
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFEC4899).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = mem.moodOrTag,
                                    color = Color(0xFFEC4899),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(text = mem.dateFormatted, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(text = mem.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = mem.description,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = "📸 Photo attached • Stored in device sandbox",
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { onDeleteMemory(mem.id) }, modifier = Modifier.size(32.dp)) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecycleBinSection(
    deletedNotes: List<com.example.data.model.NoteEntity>,
    deletedDocs: List<com.example.data.model.DocumentEntity>,
    deletedTasks: List<com.example.data.model.StudyTaskEntity>,
    deletedActivities: List<com.example.data.model.DailyActivityEntity>,
    deletedMemories: List<com.example.data.model.MemoryEntity>,
    deletedSubjects: List<com.example.data.model.SubjectEntity>,
    deletedExams: List<com.example.data.model.ExamEntity>,
    onRestoreNote: (Int) -> Unit,
    onRestoreDoc: (Int) -> Unit,
    onRestoreTask: (Int) -> Unit,
    onRestoreActivity: (Int) -> Unit,
    onRestoreMemory: (Int) -> Unit,
    onRestoreSubject: (Int) -> Unit,
    onRestoreExam: (Int) -> Unit,
    onRestoreAll: () -> Unit,
    onEmptyTrash: () -> Unit,
    onPermanentDelete: (String, () -> Unit) -> Unit
) {
    val totalCount = deletedNotes.size + deletedDocs.size + deletedTasks.size +
            deletedActivities.size + deletedMemories.size + deletedSubjects.size + deletedExams.size

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 90.dp, top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Data Safety Explanation Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Recycle Bin & Data Protection", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "To protect you from accidental loss, deleted notes, PDFs, tasks and memories are safely moved here. Items remain recoverable and will automatically purge after 30 days.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Global Actions (Restore All, Empty Trash)
        if (totalCount > 0) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onRestoreAll,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Restore All ($totalCount)", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onEmptyTrash,
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Empty Trash", fontSize = 12.sp)
                    }
                }
            }
        } else {
            item {
                EmptyStateCard(
                    icon = Icons.Default.Delete,
                    title = "Recycle Bin is Empty",
                    description = "Deleted study notes or files will appear here for 30 days before permanent purging."
                )
            }
        }

        // Deleted Notes
        if (deletedNotes.isNotEmpty()) {
            item { Text("Deleted Notes (${deletedNotes.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
            items(deletedNotes) { note ->
                TrashItemRow(
                    title = note.title,
                    subtitle = "Note • ${note.subjectName}",
                    onRestore = { onRestoreNote(note.id) },
                    onPermanentDelete = {
                        onPermanentDelete("Note: ${note.title}") {
                            // Handled in dialog
                        }
                    }
                )
            }
        }

        // Deleted Docs
        if (deletedDocs.isNotEmpty()) {
            item { Text("Deleted PDF Documents (${deletedDocs.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
            items(deletedDocs) { doc ->
                TrashItemRow(
                    title = doc.title,
                    subtitle = "PDF • ${doc.fileSizeFormatted}",
                    onRestore = { onRestoreDoc(doc.id) },
                    onPermanentDelete = {
                        onPermanentDelete("Document: ${doc.title}") {}
                    }
                )
            }
        }

        // Deleted Tasks
        if (deletedTasks.isNotEmpty()) {
            item { Text("Deleted Tasks (${deletedTasks.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
            items(deletedTasks) { task ->
                TrashItemRow(
                    title = task.title,
                    subtitle = "Task • ${task.subjectName}",
                    onRestore = { onRestoreTask(task.id) },
                    onPermanentDelete = { onPermanentDelete("Task: ${task.title}") {} }
                )
            }
        }

        // Deleted Memories
        if (deletedMemories.isNotEmpty()) {
            item { Text("Deleted Memories (${deletedMemories.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
            items(deletedMemories) { mem ->
                TrashItemRow(
                    title = mem.title,
                    subtitle = "Memory • ${mem.moodOrTag}",
                    onRestore = { onRestoreMemory(mem.id) },
                    onPermanentDelete = { onPermanentDelete("Memory: ${mem.title}") {} }
                )
            }
        }
    }
}

@Composable
fun TrashItemRow(
    title: String,
    subtitle: String,
    onRestore: () -> Unit,
    onPermanentDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row {
                Button(
                    onClick = onRestore,
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Restore", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun DataAndSyncSection(
    syncStatus: com.example.data.model.SyncStatus,
    isSyncing: Boolean,
    onTriggerSync: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 90.dp, top = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (syncStatus == com.example.data.model.SyncStatus.SYNCED) Icons.Default.CloudDone else Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Cloud Sync & Storage", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(
                                    text = if (isSyncing) "Syncing data with cloud backup..." else "All data is securely saved offline",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onTriggerSync,
                        enabled = !isSyncing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isSyncing) "Synchronizing..." else "Sync Now")
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "Offline-First Guarantee", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Room Local Database stores notes, PDFs, calculations, and daily routines.\n• Zero latency and 100% operational when commuting, on campus, or without cellular data.\n• 30-Day Auto-Purge keeps your local storage optimized and responsive.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "About “Always With Student”", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Version 1.0.0 (Build 2026)\nAn All-in-One Student Companion designed for school, college, and university students.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

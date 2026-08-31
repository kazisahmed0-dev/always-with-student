package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SubjectEntity
import com.example.ui.MainViewModel
import com.example.ui.QuickCaptureType

private data class QuickOption(
    val type: QuickCaptureType,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickCaptureBottomSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val quickType by viewModel.quickCaptureType.collectAsStateWithLifecycle()
    val subjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val options = listOf(
        QuickOption(QuickCaptureType.NOTE, "Create New Note", "Text notes & rich study points", Icons.Default.NoteAlt, Color(0xFF2563EB)),
        QuickOption(QuickCaptureType.PHOTO, "Take a Photo", "Camera scan of board or notebook", Icons.Default.CameraAlt, Color(0xFF059669)),
        QuickOption(QuickCaptureType.IMAGE_UPLOAD, "Upload Image", "Diagrams, handwritten notes", Icons.Default.Image, Color(0xFF7C3AED)),
        QuickOption(QuickCaptureType.PDF_DOC, "Add PDF / Doc", "Lecture slides & syllabus files", Icons.Default.PictureAsPdf, Color(0xFFDC2626)),
        QuickOption(QuickCaptureType.STUDY_TASK, "Create Study Task", "Homework & reading to-do", Icons.Default.TaskAlt, Color(0xFFD97706)),
        QuickOption(QuickCaptureType.DAILY_ACTIVITY, "Add Daily Habit", "Daily routine & streak goal", Icons.Default.CheckCircle, Color(0xFF0D9488)),
        QuickOption(QuickCaptureType.MEMORY, "Save a Memory", "Campus moment or achievement", Icons.Default.Stars, Color(0xFFEC4899)),
        QuickOption(QuickCaptureType.EXAM, "Add Exam", "Upcoming quiz, midterm, or final", Icons.Default.School, Color(0xFF4F46E5)),
        QuickOption(QuickCaptureType.ASSIGNMENT, "Add Assignment", "Coursework & project deadline", Icons.Default.Assignment, Color(0xFF0891B2))
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (quickType == QuickCaptureType.NONE) "⚡ Quick Capture" else "Add New ${quickType.name.replace("_", " ")}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (quickType == QuickCaptureType.NONE) "Capture instant notes, photos, tasks & study materials" else "Fill in details below to save locally",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (quickType) {
                QuickCaptureType.NONE -> {
                    // Menu grid of 9 options
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        items(options) { opt ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { viewModel.openQuickCapture(opt.type) }
                                    .testTag("quick_opt_${opt.type.name.lowercase()}"),
                                color = opt.color.copy(alpha = 0.09f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(opt.color.copy(alpha = 0.18f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = opt.icon,
                                            contentDescription = opt.title,
                                            tint = opt.color,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = opt.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }

                QuickCaptureType.NOTE, QuickCaptureType.PHOTO, QuickCaptureType.IMAGE_UPLOAD -> {
                    QuickNoteForm(
                        isImageOrPhoto = quickType != QuickCaptureType.NOTE,
                        isCameraPhoto = quickType == QuickCaptureType.PHOTO,
                        subjects = subjects,
                        onSave = { title, content, sub, isPinned, imgUri ->
                            viewModel.addNote(
                                title = title,
                                content = content,
                                subjectName = sub,
                                isPinned = isPinned,
                                isFavorite = false,
                                imageUri = imgUri
                            )
                            onDismiss()
                        }
                    )
                }

                QuickCaptureType.PDF_DOC -> {
                    QuickDocForm(
                        subjects = subjects,
                        onSave = { title, uri, sub, size ->
                            viewModel.addDocument(title, uri, "PDF", sub, size)
                            onDismiss()
                        }
                    )
                }

                QuickCaptureType.STUDY_TASK, QuickCaptureType.ASSIGNMENT -> {
                    QuickTaskForm(
                        isAssignment = quickType == QuickCaptureType.ASSIGNMENT,
                        subjects = subjects,
                        onSave = { title, desc, sub, priority, reminder ->
                            viewModel.addTask(title, desc, sub, System.currentTimeMillis() + 86400000L, priority, reminder)
                            onDismiss()
                        }
                    )
                }

                QuickCaptureType.DAILY_ACTIVITY -> {
                    QuickActivityForm(
                        onSave = { title, cat ->
                            viewModel.addDailyActivity(title, cat)
                            onDismiss()
                        }
                    )
                }

                QuickCaptureType.MEMORY -> {
                    QuickMemoryForm(
                        onSave = { title, desc, tag ->
                            viewModel.addMemory(title, desc, "", tag)
                            onDismiss()
                        }
                    )
                }

                QuickCaptureType.EXAM -> {
                    QuickExamForm(
                        subjects = subjects,
                        onSave = { title, sub, room, marks, syllabus ->
                            viewModel.addExam(title, sub, System.currentTimeMillis() + 5 * 86400000L, "10:00 AM", room, marks, syllabus)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickNoteForm(
    isImageOrPhoto: Boolean,
    isCameraPhoto: Boolean,
    subjects: List<SubjectEntity>,
    onSave: (title: String, content: String, subject: String, isPinned: Boolean, imageUri: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf(subjects.firstOrNull()?.name ?: "Mathematics") }
    var isPinned by remember { mutableStateOf(false) }
    var fakePhotoCaptured by remember { mutableStateOf(isImageOrPhoto) }

    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        if (isImageOrPhoto) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isCameraPhoto) Color(0xFF059669).copy(alpha = 0.12f) else Color(0xFF7C3AED).copy(alpha = 0.12f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isCameraPhoto) Icons.Default.CameraAlt else Icons.Default.Image,
                        contentDescription = null,
                        tint = if (isCameraPhoto) Color(0xFF059669) else Color(0xFF7C3AED)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isCameraPhoto) "📷 Photo captured from Camera & ready to attach" else "🖼️ Image selected from device gallery",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Note Title") },
            placeholder = { Text("e.g. Organic Chemistry Reactions") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quick_note_title_input"),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Note Content / Study Summary") },
            placeholder = { Text("Write formulas, lecture points, or notes...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .testTag("quick_note_content_input"),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Select Subject Category", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val displaySubs = if (subjects.isNotEmpty()) subjects.take(4) else listOf(
                SubjectEntity(name = "Mathematics"),
                SubjectEntity(name = "Physics"),
                SubjectEntity(name = "Bangla")
            )
            displaySubs.forEach { sub ->
                FilterChip(
                    selected = selectedSubject == sub.name,
                    onClick = { selectedSubject = sub.name },
                    label = { Text(sub.name, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    val mockImg = if (fakePhotoCaptured) "attachment_sample_image.png" else null
                    onSave(title, content, selectedSubject, isPinned, mockImg)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quick_note_save_btn"),
            enabled = title.isNotBlank()
        ) {
            Text("Save Note to $selectedSubject")
        }
    }
}

@Composable
fun QuickDocForm(
    subjects: List<SubjectEntity>,
    onSave: (title: String, uri: String, subject: String, size: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf(subjects.firstOrNull()?.name ?: "Physics") }
    var fileSize by remember { mutableStateOf("2.4 MB") }

    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("PDF / Document Title") },
            placeholder = { Text("e.g. HSC Physics Chapter 2 Formulas.pdf") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = fileSize,
            onValueChange = { fileSize = it },
            label = { Text("File Size") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (title.isNotBlank()) {
                    onSave(title, "file://docs/$title", selectedSubject, fileSize)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {
            Text("Save PDF to Library")
        }
    }
}

@Composable
fun QuickTaskForm(
    isAssignment: Boolean,
    subjects: List<SubjectEntity>,
    onSave: (title: String, description: String, subject: String, priority: String, reminder: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf(subjects.firstOrNull()?.name ?: "Mathematics") }
    var priority by remember { mutableStateOf("HIGH") }
    var reminderTime by remember { mutableStateOf("08:00 PM") }

    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(if (isAssignment) "Assignment Title" else "Study Task Title") },
            placeholder = { Text(if (isAssignment) "e.g. Submit ICT Lab Report 3" else "e.g. Solve 10 Trigonometry exercises") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Details / Page Numbers") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Priority Level", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("HIGH", "MEDIUM", "LOW").forEach { p ->
                FilterChip(
                    selected = priority == p,
                    onClick = { priority = p },
                    label = { Text(p, fontSize = 12.sp) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (title.isNotBlank()) {
                    onSave(title, desc, selectedSubject, priority, reminderTime)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {
            Text("Save Task to Planner")
        }
    }
}

@Composable
fun QuickActivityForm(
    onSave: (title: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Study") }

    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Daily Habit / Activity Name") },
            placeholder = { Text("e.g. Read 15 mins English vocabulary") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Category", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Study", "Revision", "Practice", "Health", "Reading").forEach { cat ->
                FilterChip(
                    selected = category == cat,
                    onClick = { category = cat },
                    label = { Text(cat, fontSize = 12.sp) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (title.isNotBlank()) {
                    onSave(title, category)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {
            Text("Add to Daily Habits Tracker")
        }
    }
}

@Composable
fun QuickMemoryForm(
    onSave: (title: String, description: String, moodTag: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var moodTag by remember { mutableStateOf("Campus Life") }

    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Memory / Moment Title") },
            placeholder = { Text("e.g. College Annual Fest or Science Fair") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("What made this student memory special?") },
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Category Tag", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Campus Life", "Achievement", "Friends", "Lab Work", "Event").forEach { tag ->
                FilterChip(
                    selected = moodTag == tag,
                    onClick = { moodTag = tag },
                    label = { Text(tag, fontSize = 12.sp) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (title.isNotBlank()) {
                    onSave(title, desc, moodTag)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {
            Text("Save Student Memory")
        }
    }
}

@Composable
fun QuickExamForm(
    subjects: List<SubjectEntity>,
    onSave: (title: String, subject: String, room: String, marks: Int, syllabus: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf(subjects.firstOrNull()?.name ?: "Mathematics") }
    var room by remember { mutableStateOf("Room 204") }
    var marks by remember { mutableStateOf("100") }
    var syllabus by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Exam Name") },
            placeholder = { Text("e.g. Final Board Term Examination") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = room,
                onValueChange = { room = it },
                label = { Text("Room") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = marks,
                onValueChange = { marks = it },
                label = { Text("Total Marks") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = syllabus,
            onValueChange = { syllabus = it },
            label = { Text("Syllabus & Topics") },
            placeholder = { Text("Chapters 1, 2, 3 and Practical notes") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (title.isNotBlank()) {
                    onSave(title, selectedSubject, room, marks.toIntOrNull() ?: 100, syllabus)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {
            Text("Save Exam Schedule")
        }
    }
}

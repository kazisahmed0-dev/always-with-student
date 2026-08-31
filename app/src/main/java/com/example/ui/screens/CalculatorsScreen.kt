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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GpaRecordEntity
import com.example.ui.MainViewModel
import com.example.ui.calculator.GpaCalculatorUtils
import com.example.ui.calculator.SscSubjectItem
import com.example.ui.calculator.UniversityCourseItem
import com.example.ui.calculator.UniversitySemesterItem
import com.example.ui.components.EmptyStateCard
import com.example.ui.theme.DangerRed
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TertiaryAmber
import com.example.ui.theme.WarningOrange
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedCalculatorTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("SSC / HSC GPA (5.0)", "University CGPA (4.0)", "Saved Records")
    val gpaRecords by viewModel.allGpaRecords.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Academic Calculators",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Accurate Grade & CGPA calculations with 4th subject rules",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        PrimaryTabRow(
            selectedTabIndex = selectedCalculatorTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedCalculatorTab == index,
                    onClick = { selectedCalculatorTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedCalculatorTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedCalculatorTab) {
                0 -> SscHscGpaSection(
                    onSaveRecord = { title, gpa, count, letter, summary ->
                        viewModel.saveGpaRecord(title, "SSC/HSC 5.0 Scale", gpa, count, letter, summary)
                        scope.launch { snackbarHostState.showSnackbar("GPA calculation record saved locally!") }
                    }
                )
                1 -> UniversityCgpaSection(
                    onSaveRecord = { title, cgpa, credits, letter, summary ->
                        viewModel.saveGpaRecord(title, "University 4.0 Scale", cgpa, credits, letter, summary)
                        scope.launch { snackbarHostState.showSnackbar("CGPA calculation record saved locally!") }
                    }
                )
                2 -> SavedGpaRecordsSection(
                    records = gpaRecords,
                    onDelete = { viewModel.deleteGpaRecord(it) }
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
}

@Composable
fun SscHscGpaSection(
    onSaveRecord: (title: String, gpa: Double, count: String, letter: String, summary: String) -> Unit
) {
    var subjects by remember {
        mutableStateOf(
            listOf(
                SscSubjectItem("1", "Bangla", "A+", false),
                SscSubjectItem("2", "English", "A+", false),
                SscSubjectItem("3", "Mathematics", "A+", false),
                SscSubjectItem("4", "Physics", "A", false),
                SscSubjectItem("5", "Chemistry", "A", false),
                SscSubjectItem("6", "Biology", "A+", false),
                SscSubjectItem("7", "ICT", "A+", false),
                SscSubjectItem("8", "Higher Math (4th Subject)", "A+", true)
            )
        )
    }

    var recordTitle by remember { mutableStateOf("HSC Model Test GPA Result") }
    val result = GpaCalculatorUtils.calculateSscHscGpa(subjects)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 90.dp, top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Result Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ssc_gpa_result_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (result.hasFailedSubject) DangerRed.copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GPA Score (5.0 Scale)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (result.hasFailedSubject) DangerRed else MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = result.letterGrade,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (result.hasFailedSubject) "0.00" else String.format(Locale.US, "%.2f", result.gpa),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = if (result.hasFailedSubject) DangerRed else MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = result.remark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Formula breakdown pill
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Formula: (Main Points ${result.mainPointsSum} + 4th Sub Bonus ${result.fourthSubjectBonus}) ÷ ${result.totalMainSubjects} Subjects",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Save & Add Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        val newSub = SscSubjectItem(UUID.randomUUID().toString(), "Subject ${subjects.size + 1}", "A+", false)
                        subjects = subjects + newSub
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Subject", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        val summary = "GPA: ${result.gpa} (${result.letterGrade}) across ${subjects.size} subjects"
                        onSaveRecord(recordTitle, result.gpa, "${subjects.size} Subjects", result.letterGrade, summary)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save Result", fontSize = 12.sp)
                }
            }
        }

        // Subject Items List
        items(subjects) { sub ->
            SscSubjectRow(
                item = sub,
                onUpdateName = { newName ->
                    subjects = subjects.map { if (it.id == sub.id) it.copy(name = newName) else it }
                },
                onUpdateGrade = { newGrade ->
                    subjects = subjects.map { if (it.id == sub.id) it.copy(gradeLetter = newGrade) else it }
                },
                onToggleFourthSubject = { isFourth ->
                    subjects = subjects.map {
                        if (it.id == sub.id) it.copy(isOptionalFourthSubject = isFourth)
                        else if (isFourth && it.isOptionalFourthSubject) it.copy(isOptionalFourthSubject = false)
                        else it
                    }
                },
                onDelete = {
                    if (subjects.size > 1) {
                        subjects = subjects.filter { it.id != sub.id }
                    }
                }
            )
        }
    }
}

@Composable
fun SscSubjectRow(
    item: SscSubjectItem,
    onUpdateName: (String) -> Unit,
    onUpdateGrade: (String) -> Unit,
    onToggleFourthSubject: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var isGradeMenuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isOptionalFourthSubject) TertiaryAmber.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = item.name,
                    onValueChange = onUpdateName,
                    label = { Text("Subject") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { isGradeMenuExpanded = true }
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${item.gradeLetter} (${GpaCalculatorUtils.getSscGradePoint(item.gradeLetter)})",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 14.sp
                        )
                    }

                    DropdownMenu(
                        expanded = isGradeMenuExpanded,
                        onDismissRequest = { isGradeMenuExpanded = false }
                    ) {
                        GpaCalculatorUtils.sscGradeOptions.forEach { (letter, gp) ->
                            DropdownMenuItem(
                                text = { Text("$letter (GP $gp)") },
                                onClick = {
                                    onUpdateGrade(letter)
                                    isGradeMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = item.isOptionalFourthSubject,
                    onCheckedChange = { onToggleFourthSubject(it) }
                )
                Text(
                    text = "Optional 4th Subject (Points above 2.0 added as bonus)",
                    fontSize = 11.sp,
                    color = if (item.isOptionalFourthSubject) TertiaryAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (item.isOptionalFourthSubject) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun UniversityCgpaSection(
    onSaveRecord: (title: String, cgpa: Double, credits: String, letter: String, summary: String) -> Unit
) {
    var semesters by remember {
        mutableStateOf(
            listOf(
                UniversitySemesterItem(
                    id = "sem1",
                    semesterName = "1st Semester",
                    courses = listOf(
                        UniversityCourseItem("c1", "CSE 101 Intro to CS", 3.0, "A+", 4.00),
                        UniversityCourseItem("c2", "MATH 101 Calculus I", 3.0, "A", 3.75),
                        UniversityCourseItem("c3", "PHY 101 Physics Lab & Theory", 4.0, "A-", 3.50),
                        UniversityCourseItem("c4", "ENG 101 English Comp", 3.0, "A+", 4.00)
                    )
                ),
                UniversitySemesterItem(
                    id = "sem2",
                    semesterName = "2nd Semester",
                    courses = listOf(
                        UniversityCourseItem("c5", "CSE 102 Data Structures", 3.0, "A+", 4.00),
                        UniversityCourseItem("c6", "MATH 102 Linear Algebra", 3.0, "A", 3.75),
                        UniversityCourseItem("c7", "CSE 103 Discrete Math", 3.0, "A-", 3.50)
                    )
                )
            )
        )
    }

    val result = GpaCalculatorUtils.calculateUniversityCgpa(semesters)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 90.dp, top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // CGPA Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("university_cgpa_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Cumulative CGPA (4.0 Scale)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SecondaryTeal
                        ) {
                            Text(
                                text = "${result.totalCredits} Credits",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = String.format(Locale.US, "%.2f", result.cgpa),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = SecondaryTeal
                    )

                    Text(
                        text = result.letterGrade,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = result.remarks,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        val newSem = UniversitySemesterItem(
                            id = UUID.randomUUID().toString(),
                            semesterName = "Semester ${semesters.size + 1}",
                            courses = listOf(
                                UniversityCourseItem(UUID.randomUUID().toString(), "New Course", 3.0, "A", 3.75)
                            )
                        )
                        semesters = semesters + newSem
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Semester", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        val summary = "CGPA: ${result.cgpa} with ${result.totalCredits} Credits in ${semesters.size} Semesters"
                        onSaveRecord("University Cumulative CGPA", result.cgpa, "${result.totalCredits} Credits", result.letterGrade, summary)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save CGPA", fontSize = 12.sp)
                }
            }
        }

        // Semesters
        items(semesters) { sem ->
            UniversitySemesterCard(
                semester = sem,
                onAddCourse = {
                    val updatedCourses = sem.courses + UniversityCourseItem(UUID.randomUUID().toString(), "Course", 3.0, "A", 3.75)
                    semesters = semesters.map { if (it.id == sem.id) it.copy(courses = updatedCourses) else it }
                },
                onUpdateCourse = { updatedCourse ->
                    val updatedCourses = sem.courses.map { if (it.id == updatedCourse.id) updatedCourse else it }
                    semesters = semesters.map { if (it.id == sem.id) it.copy(courses = updatedCourses) else it }
                },
                onDeleteCourse = { courseId ->
                    val updatedCourses = sem.courses.filter { it.id != courseId }
                    semesters = semesters.map { if (it.id == sem.id) it.copy(courses = updatedCourses) else it }
                },
                onDeleteSemester = {
                    if (semesters.size > 1) {
                        semesters = semesters.filter { it.id != sem.id }
                    }
                }
            )
        }
    }
}

@Composable
fun UniversitySemesterCard(
    semester: UniversitySemesterItem,
    onAddCourse: () -> Unit,
    onUpdateCourse: (UniversityCourseItem) -> Unit,
    onDeleteCourse: (String) -> Unit,
    onDeleteSemester: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = semester.semesterName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Row {
                    TextButton(onClick = onAddCourse) {
                        Text("+ Add Course", fontSize = 12.sp)
                    }
                    IconButton(onClick = onDeleteSemester, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Semester", tint = DangerRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            semester.courses.forEach { course ->
                UniversityCourseRow(
                    course = course,
                    onUpdate = onUpdateCourse,
                    onDelete = { onDeleteCourse(course.id) }
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
fun UniversityCourseRow(
    course: UniversityCourseItem,
    onUpdate: (UniversityCourseItem) -> Unit,
    onDelete: () -> Unit
) {
    var isGradeMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = course.courseCodeOrName,
                onValueChange = { onUpdate(course.copy(courseCodeOrName = it)) },
                label = { Text("Course", fontSize = 10.sp) },
                modifier = Modifier.weight(1.5f),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(6.dp))

            OutlinedTextField(
                value = course.creditHours.toString(),
                onValueChange = {
                    val cr = it.toDoubleOrNull() ?: 3.0
                    onUpdate(course.copy(creditHours = cr))
                },
                label = { Text("Cr", fontSize = 10.sp) },
                modifier = Modifier.weight(0.8f),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(6.dp))

            Box {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isGradeMenuExpanded = true }
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "${course.gradeLetter} (${course.gradePoint})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                DropdownMenu(
                    expanded = isGradeMenuExpanded,
                    onDismissRequest = { isGradeMenuExpanded = false }
                ) {
                    GpaCalculatorUtils.universityGradeOptions.forEach { (letter, gp) ->
                        DropdownMenuItem(
                            text = { Text(letter) },
                            onClick = {
                                onUpdate(course.copy(gradeLetter = letter.split(" ").first(), gradePoint = gp))
                                isGradeMenuExpanded = false
                            }
                        )
                    }
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun SavedGpaRecordsSection(
    records: List<GpaRecordEntity>,
    onDelete: (Int) -> Unit
) {
    if (records.isEmpty()) {
        EmptyStateCard(
            icon = Icons.Default.History,
            title = "No saved GPA calculations",
            description = "Calculate your SSC, HSC, or University CGPA in the tabs above and tap 'Save Result' to keep a permanent record."
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 90.dp, top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(records, key = { it.id }) { rec ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = rec.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "${rec.scaleType} • ${rec.totalCreditsOrSubjects}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = rec.detailsSummary, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(rec.timestamp)), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SuccessGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = String.format(Locale.US, "%.2f", rec.scoreGpa),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            IconButton(onClick = { onDelete(rec.id) }, modifier = Modifier.size(32.dp)) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed)
                            }
                        }
                    }
                }
            }
        }
    }
}

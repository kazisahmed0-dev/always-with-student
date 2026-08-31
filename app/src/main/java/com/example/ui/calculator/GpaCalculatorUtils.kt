package com.example.ui.calculator

data class SscSubjectItem(
    val id: String,
    val name: String,
    val gradeLetter: String = "A+",
    val isOptionalFourthSubject: Boolean = false
)

data class SscGpaResult(
    val gpa: Double,
    val letterGrade: String,
    val totalMainSubjects: Int,
    val mainPointsSum: Double,
    val fourthSubjectBonus: Double,
    val hasFailedSubject: Boolean,
    val remark: String
)

data class UniversityCourseItem(
    val id: String,
    val courseCodeOrName: String,
    val creditHours: Double = 3.0,
    val gradeLetter: String = "A",
    val gradePoint: Double = 3.75
)

data class UniversitySemesterItem(
    val id: String,
    val semesterName: String,
    val courses: List<UniversityCourseItem>
)

data class UniversityCgpaResult(
    val cgpa: Double,
    val totalCredits: Double,
    val totalQualityPoints: Double,
    val letterGrade: String,
    val remarks: String
)

object GpaCalculatorUtils {

    // SSC / HSC Grade mapping (5.0 scale)
    val sscGradeOptions = listOf(
        "A+" to 5.0,
        "A" to 4.0,
        "A-" to 3.5,
        "B" to 3.0,
        "C" to 2.0,
        "D" to 1.0,
        "F" to 0.0
    )

    fun getSscGradePoint(letter: String): Double {
        return sscGradeOptions.find { it.first == letter }?.second ?: 0.0
    }

    fun calculateSscHscGpa(subjects: List<SscSubjectItem>): SscGpaResult {
        val mainSubjects = subjects.filter { !it.isOptionalFourthSubject }
        val fourthSubject = subjects.find { it.isOptionalFourthSubject }

        var mainSum = 0.0
        var hasFail = false

        for (sub in mainSubjects) {
            val gp = getSscGradePoint(sub.gradeLetter)
            if (gp == 0.0) {
                hasFail = true
            }
            mainSum += gp
        }

        var fourthBonus = 0.0
        if (fourthSubject != null) {
            val fourthGp = getSscGradePoint(fourthSubject.gradeLetter)
            if (fourthGp > 2.0) {
                fourthBonus = fourthGp - 2.0
            }
        }

        if (mainSubjects.isEmpty()) {
            return SscGpaResult(0.0, "F", 0, 0.0, 0.0, false, "Please add subjects")
        }

        if (hasFail) {
            return SscGpaResult(
                gpa = 0.0,
                letterGrade = "F",
                totalMainSubjects = mainSubjects.size,
                mainPointsSum = mainSum,
                fourthSubjectBonus = fourthBonus,
                hasFailedSubject = true,
                remark = "Failed in one or more compulsory subjects. Retake required."
            )
        }

        val totalEffectivePoints = mainSum + fourthBonus
        var rawGpa = totalEffectivePoints / mainSubjects.size
        if (rawGpa > 5.0) rawGpa = 5.0
        val finalGpa = Math.round(rawGpa * 100.0) / 100.0

        val letter = when {
            finalGpa >= 5.0 -> "A+ (Golden / Outstanding)"
            finalGpa >= 4.0 -> "A"
            finalGpa >= 3.5 -> "A-"
            finalGpa >= 3.0 -> "B"
            finalGpa >= 2.0 -> "C"
            finalGpa >= 1.0 -> "D"
            else -> "F"
        }

        val remark = when {
            finalGpa >= 5.0 -> "Outstanding Achievement! Golden GPA 5.00"
            finalGpa >= 4.0 -> "Excellent performance! Keep up the brilliant work."
            finalGpa >= 3.5 -> "Very Good! A little push will get you to GPA 5.0."
            finalGpa >= 3.0 -> "Good effort. Focus on weak subjects for higher marks."
            else -> "Pass. Need dedicated revision and practice."
        }

        return SscGpaResult(
            gpa = finalGpa,
            letterGrade = letter,
            totalMainSubjects = mainSubjects.size,
            mainPointsSum = mainSum,
            fourthSubjectBonus = fourthBonus,
            hasFailedSubject = false,
            remark = remark
        )
    }

    // University 4.0 Scale Grade Options (UGC / International Standard)
    val universityGradeOptions = listOf(
        "A+ (4.00)" to 4.00,
        "A (3.75)" to 3.75,
        "A- (3.50)" to 3.50,
        "B+ (3.25)" to 3.25,
        "B (3.00)" to 3.00,
        "B- (2.75)" to 2.75,
        "C+ (2.50)" to 2.50,
        "C (2.25)" to 2.25,
        "D (2.00)" to 2.00,
        "F (0.00)" to 0.00
    )

    fun calculateUniversityCgpa(semesters: List<UniversitySemesterItem>): UniversityCgpaResult {
        var totalQualityPoints = 0.0
        var totalCredits = 0.0

        for (sem in semesters) {
            for (course in sem.courses) {
                totalCredits += course.creditHours
                totalQualityPoints += (course.creditHours * course.gradePoint)
            }
        }

        if (totalCredits == 0.0) {
            return UniversityCgpaResult(0.0, 0.0, 0.0, "N/A", "Add course credits to compute")
        }

        val rawCgpa = totalQualityPoints / totalCredits
        val finalCgpa = Math.round(rawCgpa * 100.0) / 100.0

        val letter = when {
            finalCgpa >= 3.75 -> "First Class / High Distinction (A/A+)"
            finalCgpa >= 3.50 -> "Dean's List Standing (A-)"
            finalCgpa >= 3.00 -> "First Division (B/B+)"
            finalCgpa >= 2.50 -> "Second Division (C+)"
            finalCgpa >= 2.00 -> "Passing Grade (D)"
            else -> "Probation / Fail (F)"
        }

        val remarks = when {
            finalCgpa >= 3.75 -> "Summa / Magna Cum Laude Potential! Outstanding Academic Record."
            finalCgpa >= 3.50 -> "Dean's Honor List standard. Excellent academic consistency."
            finalCgpa >= 3.00 -> "Solid academic standing. Good foundation for graduate studies."
            finalCgpa >= 2.50 -> "Satisfactory standing. Focus on high-credit core courses."
            else -> "Needs immediate academic improvement and advisor consultation."
        }

        return UniversityCgpaResult(
            cgpa = finalCgpa,
            totalCredits = totalCredits,
            totalQualityPoints = Math.round(totalQualityPoints * 100.0) / 100.0,
            letterGrade = letter,
            remarks = remarks
        )
    }
}

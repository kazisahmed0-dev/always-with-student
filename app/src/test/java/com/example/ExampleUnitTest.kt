package com.example

import com.example.ui.calculator.GpaCalculatorUtils
import com.example.ui.calculator.SscSubjectItem
import com.example.ui.calculator.UniversityCourseItem
import com.example.ui.calculator.UniversitySemesterItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testSscHscGpaCalculation_allAPlus() {
        val subjects = listOf(
            SscSubjectItem("1", "Bangla", "A+", false),
            SscSubjectItem("2", "English", "A+", false),
            SscSubjectItem("3", "Mathematics", "A+", false),
            SscSubjectItem("4", "Physics", "A+", false),
            SscSubjectItem("5", "Chemistry", "A+", false),
            SscSubjectItem("6", "Biology", "A+", false),
            SscSubjectItem("7", "Higher Math (4th)", "A+", true)
        )
        val result = GpaCalculatorUtils.calculateSscHscGpa(subjects)
        assertEquals(5.00, result.gpa, 0.001)
        assertEquals("A+", result.letterGrade)
        assertFalse(result.hasFailedSubject)
        assertTrue(result.isGoldenA)
    }

    @Test
    fun testSscHscGpaCalculation_withFourthSubjectBonus() {
        val subjects = listOf(
            SscSubjectItem("1", "Bangla", "A", false), // 4.0
            SscSubjectItem("2", "English", "A", false), // 4.0
            SscSubjectItem("3", "Mathematics", "A", false), // 4.0
            SscSubjectItem("4", "Physics", "A", false), // 4.0
            SscSubjectItem("5", "Chemistry", "A", false), // 4.0
            SscSubjectItem("6", "Higher Math (4th)", "A+", true) // 5.0 -> bonus = 5.0 - 2.0 = 3.0
        )
        // Main points = 20.0. Bonus = 3.0. Sum = 23.0. Divided by 5 = 4.60
        val result = GpaCalculatorUtils.calculateSscHscGpa(subjects)
        assertEquals(4.60, result.gpa, 0.01)
        assertEquals("A", result.letterGrade)
        assertFalse(result.hasFailedSubject)
    }

    @Test
    fun testSscHscGpaCalculation_failedSubjectResultsInZero() {
        val subjects = listOf(
            SscSubjectItem("1", "Bangla", "F", false),
            SscSubjectItem("2", "English", "A+", false)
        )
        val result = GpaCalculatorUtils.calculateSscHscGpa(subjects)
        assertEquals(0.00, result.gpa, 0.001)
        assertEquals("F", result.letterGrade)
        assertTrue(result.hasFailedSubject)
    }

    @Test
    fun testUniversityCgpaCalculation_multipleSemesters() {
        val semesters = listOf(
            UniversitySemesterItem(
                id = "s1",
                semesterName = "1st Semester",
                courses = listOf(
                    UniversityCourseItem("c1", "CSE 101", 3.0, "A+", 4.00),
                    UniversityCourseItem("c2", "MATH 101", 3.0, "A", 3.75)
                )
            ),
            UniversitySemesterItem(
                id = "s2",
                semesterName = "2nd Semester",
                courses = listOf(
                    UniversityCourseItem("c3", "CSE 102", 3.0, "A+", 4.00),
                    UniversityCourseItem("c4", "PHY 101", 3.0, "A", 3.75)
                )
            )
        )
        val result = GpaCalculatorUtils.calculateUniversityCgpa(semesters)
        assertEquals(3.875, result.cgpa, 0.01)
        assertEquals(12.0, result.totalCredits, 0.01)
        assertEquals("A", result.letterGrade)
    }
}

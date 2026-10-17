package com.example

import com.example.data.model.SubjectEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CampusLogicTest {

    @Test
    fun testAttendancePercentageCalculation() {
        val subject = SubjectEntity(
            code = "CS302",
            name = "Design & Analysis of Algorithms",
            faculty = "Dr. Ananya Roy",
            attendedCount = 28,
            totalCount = 32
        )
        val expected = (28f / 32f) * 100f
        assertEquals(expected, subject.attendancePercentage, 0.01f)
    }

    @Test
    fun testSafeBunkCalculation() {
        val subject = SubjectEntity(
            code = "CS304",
            name = "Operating Systems",
            faculty = "Prof. R. K. Verma",
            attendedCount = 30,
            totalCount = 34,
            minAttendancePercent = 75.0f
        )
        // With 30 attended out of 34: 30 / (34 + 6) = 30 / 40 = 75%
        assertEquals(6, subject.safeBunks)
    }

    @Test
    fun testClassesNeededWhenBelowThreshold() {
        val subject = SubjectEntity(
            code = "CS306",
            name = "Computer Networks",
            faculty = "Dr. Meera Nambiar",
            attendedCount = 18,
            totalCount = 28, // 18 / 28 = 64.28%
            minAttendancePercent = 75.0f
        )
        // Let needed = x: (18 + x) / (28 + x) >= 0.75 => 18 + x >= 21 + 0.75x => 0.25x >= 3 => x >= 12
        assertEquals(12, subject.classesNeededFor75)
    }

    @Test
    fun testSgpaFormula() {
        // Grades: (Credits, GradePoint)
        val grades = listOf(
            Pair(4, 10), // 40
            Pair(4, 9),  // 36
            Pair(3, 8),  // 24
            Pair(3, 7),  // 21
            Pair(2, 10)  // 20
        )
        val totalCredits = grades.sumOf { it.first } // 16
        val weightedSum = grades.sumOf { it.first * it.second } // 141
        val sgpa = String.format("%.2f", weightedSum.toFloat() / totalCredits.toFloat()).toFloat()
        assertEquals(8.81f, sgpa, 0.01f)
    }

    @Test
    fun testSubscriptionDefaults() {
        val profile = com.example.data.local.PrepopulatedData.defaultProfile
        assertEquals(false, profile.isAiSubscribed)
        assertEquals("₹49/month", profile.subscriptionPrice)
        assertEquals("Campus AI Pro", profile.subscriptionPlan)
    }

    @Test
    fun testSubscriptionActivation() {
        val profile = com.example.data.local.PrepopulatedData.defaultProfile
        val subscribedProfile = profile.copy(
            isAiSubscribed = true,
            subscriptionExpiryDate = "Valid for 30 Days",
            paymentMethod = "UPI (Google Pay)"
        )
        assertTrue(subscribedProfile.isAiSubscribed)
        assertEquals("₹49/month", subscribedProfile.subscriptionPrice)
        assertEquals("Valid for 30 Days", subscribedProfile.subscriptionExpiryDate)
        assertEquals("UPI (Google Pay)", subscribedProfile.paymentMethod)
    }

    @Test
    fun testCustomPaymentQrConfiguration() {
        val profile = com.example.data.local.PrepopulatedData.defaultProfile
        assertEquals("campusmate.pay@upi", profile.customUpiId)
        val updated = profile.copy(
            customUpiId = "student@okhdfcbank",
            customQrUri = "content://media/external/images/media/42"
        )
        assertEquals("student@okhdfcbank", updated.customUpiId)
        assertEquals("content://media/external/images/media/42", updated.customQrUri)
    }
}

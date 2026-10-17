package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val faculty: String,
    val attendedCount: Int,
    val totalCount: Int,
    val minAttendancePercent: Float = 75.0f,
    val roomNumber: String = "",
    val semester: Int = 5
) {
    val attendancePercentage: Float
        get() = if (totalCount > 0) (attendedCount.toFloat() / totalCount.toFloat()) * 100f else 100f

    val safeBunks: Int
        get() {
            val minRatio = minAttendancePercent / 100f
            var bunks = 0
            while (totalCount + bunks + 1 > 0 &&
                (attendedCount.toFloat() / (totalCount + bunks + 1).toFloat()) >= minRatio
            ) {
                bunks++
            }
            return bunks
        }

    val classesNeededFor75: Int
        get() {
            val minRatio = minAttendancePercent / 100f
            if (attendancePercentage >= minAttendancePercent) return 0
            var needed = 0
            while ((attendedCount + needed).toFloat() / (totalCount + needed).toFloat() < minRatio) {
                needed++
                if (needed > 200) break
            }
            return needed
        }
}

@Entity(tableName = "attendance_records")
data class AttendanceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val subjectName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String, // "attended", "absent", "medical"
    val note: String = ""
)

@Entity(tableName = "class_schedules")
data class ClassScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectCode: String,
    val subjectName: String,
    val dayOfWeek: Int, // 1=Mon, 2=Tue, 3=Wed, 4=Thu, 5=Fri, 6=Sat
    val startTime: String, // e.g., "09:30 AM"
    val endTime: String,   // e.g., "10:45 AM"
    val faculty: String,
    val roomNumber: String,
    val isLab: Boolean = false,
    val labExperiment: String = "",
    val status: String = "Upcoming" // "Attended", "Happening Now", "Upcoming"
)

@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val dueDateText: String,
    val points: Int = 20,
    val priority: String = "HIGH", // "HIGH", "MEDIUM", "LOW"
    val isCompleted: Boolean = false,
    val isOverdue: Boolean = false,
    val description: String = "",
    val groupInfo: String = ""
)

@Entity(tableName = "semester_results")
data class SemesterResultEntity(
    @PrimaryKey val semesterNumber: Int,
    val sgpa: Float,
    val totalCredits: Int,
    val isCompleted: Boolean = true
)

@Entity(tableName = "subject_grades")
data class SubjectGradeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val semesterNumber: Int,
    val subjectName: String,
    val credits: Int,
    val gradeLetter: String,
    val gradePoints: Int
)

@Entity(tableName = "study_notes")
data class StudyNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val readTimeMinutes: Int,
    val formulasCount: Int = 0,
    val confidencePercent: Int = 98,
    val summaryText: String,
    val noteType: String = "SUMMARY",
    val createdDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String,
    val text: String,
    val courseCode: String = "CS302",
    val titleTag: String = "",
    val conceptTag: String = "",
    val comparisonTable: String = "",
    val mentalAnchor: String = "",
    val codeSnippet: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "student_profile",
    val fullName: String = "Aarav Sharma",
    val email: String = "aarav.sharma@campus.edu",
    val college: String = "IIT Bombay",
    val university: String = "Autonomous Institute",
    val course: String = "B.Tech",
    val branch: String = "Computer Science & Engineering",
    val semester: Int = 5,
    val academicYear: String = "Yr 3",
    val streakDays: Int = 14,
    val cgpa: Float = 8.74f,
    val minAttendanceRequired: Float = 75.0f,
    val isLoggedIn: Boolean = true,
    val isAiSubscribed: Boolean = false,
    val subscriptionPrice: String = "₹49/month",
    val subscriptionPeriod: String = "Monthly",
    val subscriptionPlan: String = "Campus AI Pro",
    val subscriptionExpiryDate: String = "",
    val paymentMethod: String = "",
    val customUpiId: String = "campusmate.pay@upi",
    val customQrUri: String = ""
)

package com.example.data.repository

import com.example.data.local.CampusDao
import com.example.data.local.PrepopulatedData
import com.example.data.model.AssignmentEntity
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ClassScheduleEntity
import com.example.data.model.SemesterResultEntity
import com.example.data.model.StudyNoteEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.SubjectGradeEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CampusRepository(private val dao: CampusDao) {

    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val allSubjects: Flow<List<SubjectEntity>> = dao.getAllSubjects()
    val allAttendanceRecords: Flow<List<AttendanceRecordEntity>> = dao.getAllAttendanceRecords()
    val allSchedules: Flow<List<ClassScheduleEntity>> = dao.getAllSchedules()
    val allAssignments: Flow<List<AssignmentEntity>> = dao.getAllAssignments()
    val allSemesterResults: Flow<List<SemesterResultEntity>> = dao.getAllSemesterResults()
    val allStudyNotes: Flow<List<StudyNoteEntity>> = dao.getAllStudyNotes()
    val allChatMessages: Flow<List<ChatMessageEntity>> = dao.getAllChatMessages()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingProfile = dao.getUserProfile().firstOrNull()
        if (existingProfile == null) {
            dao.insertOrUpdateProfile(PrepopulatedData.defaultProfile)
            dao.insertSubjects(PrepopulatedData.defaultSubjects)
            dao.insertSchedules(PrepopulatedData.defaultTodayClasses)
            dao.insertAssignments(PrepopulatedData.defaultAssignments)
            dao.insertSemesterResults(PrepopulatedData.defaultSemesterResults)
            for (grade in PrepopulatedData.defaultSubjectGrades) {
                dao.insertSubjectGrade(grade)
            }
            dao.insertStudyNotes(PrepopulatedData.defaultStudyNotes)
            for (msg in PrepopulatedData.defaultChatMessages) {
                dao.insertChatMessage(msg)
            }
        }
    }

    suspend fun markAttendance(subjectId: Long, isAttended: Boolean, note: String = "") = withContext(Dispatchers.IO) {
        val subject = dao.getSubjectById(subjectId) ?: return@withContext
        val newAttended = if (isAttended) subject.attendedCount + 1 else subject.attendedCount
        val newTotal = subject.totalCount + 1
        dao.updateSubject(subject.copy(attendedCount = newAttended, totalCount = newTotal))
        dao.insertAttendanceRecord(
            AttendanceRecordEntity(
                subjectId = subjectId,
                subjectName = subject.name,
                status = if (isAttended) "attended" else "absent",
                note = note
            )
        )
    }

    suspend fun submitMedicalSlip(subjectName: String, slipNote: String = "Medical / Duty Leave") = withContext(Dispatchers.IO) {
        val subjects = dao.getAllSubjects().firstOrNull() ?: emptyList()
        val subject = subjects.find { it.name.equals(subjectName, ignoreCase = true) } ?: subjects.firstOrNull()
        if (subject != null) {
            dao.insertAttendanceRecord(
                AttendanceRecordEntity(
                    subjectId = subject.id,
                    subjectName = subject.name,
                    status = "medical",
                    note = slipNote
                )
            )
        }
    }

    suspend fun addSubject(name: String, code: String, faculty: String, roomNumber: String = "") = withContext(Dispatchers.IO) {
        dao.insertSubject(
            SubjectEntity(
                code = code,
                name = name,
                faculty = faculty,
                attendedCount = 0,
                totalCount = 0,
                roomNumber = roomNumber
            )
        )
    }

    suspend fun deleteSubject(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteSubject(id)
    }

    suspend fun toggleAssignmentCompleted(assignment: AssignmentEntity) = withContext(Dispatchers.IO) {
        dao.updateAssignment(assignment.copy(isCompleted = !assignment.isCompleted))
    }

    suspend fun addAssignment(title: String, subject: String, dueDateText: String, points: Int, priority: String, description: String) = withContext(Dispatchers.IO) {
        dao.insertAssignment(
            AssignmentEntity(
                title = title,
                subject = subject,
                dueDateText = dueDateText,
                points = points,
                priority = priority,
                description = description
            )
        )
    }

    suspend fun deleteAssignment(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteAssignment(id)
    }

    suspend fun addClassSchedule(schedule: ClassScheduleEntity) = withContext(Dispatchers.IO) {
        dao.insertSchedule(schedule)
    }

    suspend fun deleteClassSchedule(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteSchedule(id)
    }

    suspend fun updateClassStatus(id: Long, newStatus: String) = withContext(Dispatchers.IO) {
        val schedules = dao.getAllSchedules().firstOrNull() ?: return@withContext
        val classItem = schedules.find { it.id == id } ?: return@withContext
        dao.updateSchedule(classItem.copy(status = newStatus))
    }

    fun getGradesForSemester(semester: Int): Flow<List<SubjectGradeEntity>> {
        return dao.getGradesForSemester(semester)
    }

    suspend fun addSubjectGrade(grade: SubjectGradeEntity) = withContext(Dispatchers.IO) {
        dao.insertSubjectGrade(grade)
    }

    suspend fun deleteSubjectGrade(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteSubjectGrade(id)
    }

    suspend fun saveSemesterResult(semesterNumber: Int, sgpa: Float, credits: Int) = withContext(Dispatchers.IO) {
        dao.insertSemesterResult(SemesterResultEntity(semesterNumber, sgpa, credits))
    }

    suspend fun addStudyNote(title: String, subject: String, summaryText: String, noteType: String = "SUMMARY") = withContext(Dispatchers.IO) {
        dao.insertStudyNote(
            StudyNoteEntity(
                title = title,
                subject = subject,
                readTimeMinutes = 5,
                formulasCount = 4,
                confidencePercent = 99,
                summaryText = summaryText,
                noteType = noteType
            )
        )
    }

    suspend fun sendChatMessage(userText: String, courseCode: String = "CS302", aiResponseText: String, titleTag: String = "Study Solution", conceptTag: String = "Concept", comparisonTable: String = "", mentalAnchor: String = "", codeSnippet: String = "") = withContext(Dispatchers.IO) {
        dao.insertChatMessage(ChatMessageEntity(sender = "USER", text = userText, courseCode = courseCode))
        dao.insertChatMessage(
            ChatMessageEntity(
                sender = "AI",
                text = aiResponseText,
                courseCode = courseCode,
                titleTag = titleTag,
                conceptTag = conceptTag,
                comparisonTable = comparisonTable,
                mentalAnchor = mentalAnchor,
                codeSnippet = codeSnippet
            )
        )
    }

    suspend fun updateProfile(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateProfile(profile)
    }

    suspend fun resetData() = withContext(Dispatchers.IO) {
        dao.insertOrUpdateProfile(PrepopulatedData.defaultProfile)
        dao.insertSubjects(PrepopulatedData.defaultSubjects)
        dao.insertSchedules(PrepopulatedData.defaultTodayClasses)
        dao.insertAssignments(PrepopulatedData.defaultAssignments)
        dao.insertSemesterResults(PrepopulatedData.defaultSemesterResults)
    }
}

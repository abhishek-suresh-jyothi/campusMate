package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AssignmentEntity
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ClassScheduleEntity
import com.example.data.model.SemesterResultEntity
import com.example.data.model.StudyNoteEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.SubjectGradeEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CampusDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = :id LIMIT 1")
    fun getUserProfile(id: String = "student_profile"): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    // Subjects
    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: Long): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteSubject(id: Long)

    // Attendance Records
    @Query("SELECT * FROM attendance_records ORDER BY timestamp DESC")
    fun getAllAttendanceRecords(): Flow<List<AttendanceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecord(record: AttendanceRecordEntity): Long

    // Class Schedules
    @Query("SELECT * FROM class_schedules WHERE dayOfWeek = :day ORDER BY id ASC")
    fun getSchedulesForDay(day: Int): Flow<List<ClassScheduleEntity>>

    @Query("SELECT * FROM class_schedules ORDER BY dayOfWeek ASC, id ASC")
    fun getAllSchedules(): Flow<List<ClassScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ClassScheduleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<ClassScheduleEntity>)

    @Update
    suspend fun updateSchedule(schedule: ClassScheduleEntity)

    @Query("DELETE FROM class_schedules WHERE id = :id")
    suspend fun deleteSchedule(id: Long)

    // Assignments
    @Query("SELECT * FROM assignments ORDER BY isCompleted ASC, id DESC")
    fun getAllAssignments(): Flow<List<AssignmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<AssignmentEntity>)

    @Update
    suspend fun updateAssignment(assignment: AssignmentEntity)

    @Query("DELETE FROM assignments WHERE id = :id")
    suspend fun deleteAssignment(id: Long)

    // Semester Results & Grades
    @Query("SELECT * FROM semester_results ORDER BY semesterNumber ASC")
    fun getAllSemesterResults(): Flow<List<SemesterResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemesterResult(result: SemesterResultEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemesterResults(results: List<SemesterResultEntity>)

    @Query("SELECT * FROM subject_grades WHERE semesterNumber = :semester ORDER BY id ASC")
    fun getGradesForSemester(semester: Int): Flow<List<SubjectGradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjectGrade(grade: SubjectGradeEntity)

    @Query("DELETE FROM subject_grades WHERE id = :id")
    suspend fun deleteSubjectGrade(id: Long)

    // Study Notes
    @Query("SELECT * FROM study_notes ORDER BY createdDate DESC")
    fun getAllStudyNotes(): Flow<List<StudyNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyNote(note: StudyNoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyNotes(notes: List<StudyNoteEntity>)

    // Chat Messages
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatMessages()
}

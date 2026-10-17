package com.example.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CampusDatabase
import com.example.data.local.PrepopulatedData
import com.example.data.model.AssignmentEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ClassScheduleEntity
import com.example.data.model.SemesterResultEntity
import com.example.data.model.StudyNoteEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.SubjectGradeEntity
import com.example.data.model.UserProfileEntity
import com.example.data.repository.AiStudyRepository
import com.example.data.repository.CampusRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CampusViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CampusDatabase.getDatabase(application)
    private val repository = CampusRepository(db.campusDao())
    private val aiRepository = AiStudyRepository()

    // Navigation and screen state
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _currentSubScreen = MutableStateFlow<String?>(null)
    val currentSubScreen: StateFlow<String?> = _currentSubScreen.asStateFlow()

    // Course module selection in AI Study screen
    private val _selectedCourseModule = MutableStateFlow("Algorithms (CS302)")
    val selectedCourseModule: StateFlow<String> = _selectedCourseModule.asStateFlow()

    private val _aiStudyMode = MutableStateFlow("chat")
    val aiStudyMode: StateFlow<String> = _aiStudyMode.asStateFlow()

    // Bunk simulator slider value
    private val _bunkSliderValue = MutableStateFlow(6)
    val bunkSliderValue: StateFlow<Int> = _bunkSliderValue.asStateFlow()

    // Explore screen filters & search
    private val _exploreFilter = MutableStateFlow("All")
    val exploreFilter: StateFlow<String> = _exploreFilter.asStateFlow()

    private val _exploreSearchQuery = MutableStateFlow("")
    val exploreSearchQuery: StateFlow<String> = _exploreSearchQuery.asStateFlow()

    private val _resumeScanState = MutableStateFlow("IDLE")
    val resumeScanState: StateFlow<String> = _resumeScanState.asStateFlow()

    // Toast message trigger
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Subscription paywall state (₹49/month for AI features)
    private val _showSubscriptionPaywall = MutableStateFlow(false)
    val showSubscriptionPaywall: StateFlow<Boolean> = _showSubscriptionPaywall.asStateFlow()

    // Database reactive streams
    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isAiSubscribed: StateFlow<Boolean> = repository.userProfile
        .map { it?.isAiSubscribed == true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val subjects: StateFlow<List<SubjectEntity>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val schedules: StateFlow<List<ClassScheduleEntity>> = repository.allSchedules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val assignments: StateFlow<List<AssignmentEntity>> = repository.allAssignments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val semesterResults: StateFlow<List<SemesterResultEntity>> = repository.allSemesterResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyNotes: StateFlow<List<StudyNoteEntity>> = repository.allStudyNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.allChatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(index: Int) {
        _currentTab.value = index
        _currentSubScreen.value = null
    }

    fun navigateTo(subScreen: String) {
        _currentSubScreen.value = subScreen
    }

    fun navigateBack() {
        _currentSubScreen.value = null
    }

    fun setSelectedCourseModule(course: String) {
        _selectedCourseModule.value = course
    }

    fun setAiStudyMode(mode: String) {
        _aiStudyMode.value = mode
    }

    fun setBunkSlider(value: Int) {
        _bunkSliderValue.value = value
    }

    fun setExploreFilter(filter: String) {
        _exploreFilter.value = filter
    }

    fun setExploreSearchQuery(query: String) {
        _exploreSearchQuery.value = query
    }

    fun markAttendance(subjectId: Long, isAttended: Boolean) {
        viewModelScope.launch {
            repository.markAttendance(subjectId, isAttended)
            val subName = subjects.value.find { it.id == subjectId }?.name ?: "Subject"
            showToast("Marked ${if (isAttended) "Attended" else "Absent"} for $subName")
        }
    }

    fun submitMedicalSlip(subjectName: String) {
        viewModelScope.launch {
            repository.submitMedicalSlip(subjectName)
            showToast("Medical slip submitted for $subjectName to Dean Office!")
        }
    }

    fun toggleAssignment(assignment: AssignmentEntity) {
        viewModelScope.launch {
            repository.toggleAssignmentCompleted(assignment)
            showToast(if (!assignment.isCompleted) "Marked '${assignment.title}' as Completed!" else "Marked as Pending")
        }
    }

    fun addAssignment(title: String, subject: String, dueDateText: String, points: Int, priority: String, description: String) {
        viewModelScope.launch {
            repository.addAssignment(title, subject, dueDateText, points, priority, description)
            showToast("Assignment '$title' added!")
        }
    }

    fun deleteAssignment(id: Long) {
        viewModelScope.launch {
            repository.deleteAssignment(id)
            showToast("Assignment removed")
        }
    }

    fun addSubject(name: String, code: String, faculty: String, roomNumber: String) {
        viewModelScope.launch {
            repository.addSubject(name, code, faculty, roomNumber)
            showToast("Subject '$name ($code)' created!")
        }
    }

    fun deleteSubject(id: Long) {
        viewModelScope.launch {
            repository.deleteSubject(id)
            showToast("Subject deleted")
        }
    }

    fun addClassSchedule(schedule: ClassScheduleEntity) {
        viewModelScope.launch {
            repository.addClassSchedule(schedule)
            showToast("Class added to timetable!")
        }
    }

    fun deleteClassSchedule(id: Long) {
        viewModelScope.launch {
            repository.deleteClassSchedule(id)
            showToast("Class removed from schedule")
        }
    }

    fun openSubscriptionPaywall() {
        _showSubscriptionPaywall.value = true
    }

    fun dismissSubscriptionPaywall() {
        _showSubscriptionPaywall.value = false
    }

    fun subscribeToAi(paymentMethod: String = "UPI (Google Pay / PhonePe)") {
        viewModelScope.launch {
            val current = userProfile.value ?: PrepopulatedData.defaultProfile
            repository.updateProfile(
                current.copy(
                    isAiSubscribed = true,
                    subscriptionPlan = "Campus AI Pro",
                    subscriptionPrice = "₹49/month",
                    subscriptionPeriod = "Monthly",
                    subscriptionExpiryDate = "Renews next month",
                    paymentMethod = paymentMethod
                )
            )
            _showSubscriptionPaywall.value = false
            showToast("Subscribed to Campus AI Pro for ₹49/month! All AI features unlocked.")
        }
    }

    fun cancelSubscription() {
        viewModelScope.launch {
            val current = userProfile.value ?: PrepopulatedData.defaultProfile
            repository.updateProfile(
                current.copy(
                    isAiSubscribed = false,
                    subscriptionPlan = "Free Tier",
                    subscriptionExpiryDate = "Cancelled",
                    paymentMethod = ""
                )
            )
            showToast("Campus AI Pro subscription cancelled. Switched to Free tier.")
        }
    }

    fun updateCustomPaymentQr(upiId: String, qrUri: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: PrepopulatedData.defaultProfile
            repository.updateProfile(
                current.copy(
                    customUpiId = upiId.ifBlank { "campusmate.pay@upi" },
                    customQrUri = qrUri
                )
            )
            showToast("Payment QR Code and UPI ID updated!")
        }
    }

    fun checkAiAccess(onAccessGranted: () -> Unit) {
        if (userProfile.value?.isAiSubscribed == true) {
            onAccessGranted()
        } else {
            openSubscriptionPaywall()
            showToast("Campus AI Pro (₹49/month) required for AI features.")
        }
    }

    fun sendAiPrompt(query: String) {
        if (query.isBlank()) return
        if (userProfile.value?.isAiSubscribed != true) {
            openSubscriptionPaywall()
            showToast("Subscribe for ₹49/month to access AI Study Coach.")
            return
        }
        viewModelScope.launch {
            val response = aiRepository.generateStudyResponse(query, _selectedCourseModule.value)
            repository.sendChatMessage(
                userText = query,
                courseCode = _selectedCourseModule.value,
                aiResponseText = response.explanation,
                titleTag = response.titleTag,
                conceptTag = response.conceptTag,
                comparisonTable = response.comparisonTable,
                mentalAnchor = response.mentalAnchor,
                codeSnippet = response.codeSnippet
            )
        }
    }

    fun scanResume() {
        if (userProfile.value?.isAiSubscribed != true) {
            openSubscriptionPaywall()
            showToast("Subscribe for ₹49/month to access AI Resume Scanner.")
            return
        }
        viewModelScope.launch {
            _resumeScanState.value = "SCANNING"
            delay(1200)
            _resumeScanState.value = "UPDATED"
            showToast("Resume scanned! +4 new matching internships discovered.")
            delay(2500)
            _resumeScanState.value = "IDLE"
        }
    }

    fun calculateSgpa(grades: List<Pair<Int, Int>>): Float {
        val totalCredits = grades.sumOf { it.first }
        if (totalCredits == 0) return 0.0f
        val weightedSum = grades.sumOf { it.first * it.second }
        return String.format("%.2f", weightedSum.toFloat() / totalCredits.toFloat()).toFloat()
    }

    fun saveSemesterResult(sem: Int, sgpa: Float, credits: Int) {
        viewModelScope.launch {
            repository.saveSemesterResult(sem, sgpa, credits)
            showToast("Semester $sem SGPA ($sgpa) saved successfully!")
        }
    }

    fun updateProfile(name: String, email: String, college: String, university: String, course: String, branch: String, semester: Int) {
        viewModelScope.launch {
            val current = userProfile.value ?: PrepopulatedData.defaultProfile
            repository.updateProfile(
                current.copy(
                    fullName = name,
                    email = email,
                    college = college,
                    university = university,
                    course = course,
                    branch = branch,
                    semester = semester
                )
            )
            showToast("Profile updated!")
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetData()
            showToast("Data restored to campus demo state")
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    private fun showToast(msg: String) {
        _toastMessage.value = msg
    }
}

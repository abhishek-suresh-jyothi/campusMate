package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.presentation.components.CampusBottomNavigation
import com.example.presentation.components.CampusTopHeader
import com.example.presentation.components.SubscriptionPaywallDialog
import com.example.presentation.screens.AiStudyScreen
import com.example.presentation.screens.AssignmentManagerScreen
import com.example.presentation.screens.AttendanceAnalyticsScreen
import com.example.presentation.screens.ExploreScreen
import com.example.presentation.screens.GradeCalculatorScreen
import com.example.presentation.screens.ProfileSettingsScreen
import com.example.presentation.screens.ScheduleScreen
import com.example.presentation.screens.SignInScreen
import com.example.presentation.screens.StudyNotesScreen
import com.example.presentation.screens.TodayScreen
import com.example.presentation.viewmodel.AuthViewModel
import com.example.presentation.viewmodel.CampusViewModel
import com.example.ui.theme.CampusMateTheme
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.Secondary
import com.example.ui.theme.Tertiary

class MainActivity : ComponentActivity() {

    private val viewModel: CampusViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val systemDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemDark) }
            val currentUser by authViewModel.currentUser.collectAsState()

            CampusMateTheme(darkTheme = isDarkTheme) {
                if (currentUser == null) {
                    val isLoading by authViewModel.isLoading.collectAsState()
                    val errorMessage by authViewModel.errorMessage.collectAsState()
                    SignInScreen(
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        onSignInClick = { activity -> authViewModel.launchGoogleSignIn(activity) },
                        onDismissError = { authViewModel.clearError() }
                    )
                } else {
                    CampusAppRoot(
                        viewModel = viewModel,
                        authViewModel = authViewModel,
                        isDarkTheme = isDarkTheme,
                        onToggleDarkTheme = { isDarkTheme = it }
                    )
                }
            }
        }
    }
}

@Composable
fun CampusAppRoot(
    viewModel: CampusViewModel,
    authViewModel: AuthViewModel,
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()
    val currentSubScreen by viewModel.currentSubScreen.collectAsState()

    val profile by viewModel.userProfile.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val schedules by viewModel.schedules.collectAsState()
    val assignments by viewModel.assignments.collectAsState()
    val semesterResults by viewModel.semesterResults.collectAsState()
    val studyNotes by viewModel.studyNotes.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()

    val bunkSliderValue by viewModel.bunkSliderValue.collectAsState()
    val exploreFilter by viewModel.exploreFilter.collectAsState()
    val exploreSearchQuery by viewModel.exploreSearchQuery.collectAsState()
    val resumeScanState by viewModel.resumeScanState.collectAsState()
    val selectedCourseModule by viewModel.selectedCourseModule.collectAsState()
    val aiStudyMode by viewModel.aiStudyMode.collectAsState()

    val toastMessage by viewModel.toastMessage.collectAsState()
    val showSubscriptionPaywall by viewModel.showSubscriptionPaywall.collectAsState()
    val isAiSubscribed by viewModel.isAiSubscribed.collectAsState()

    var showNotificationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Handle back button for sub screens
    BackHandler(enabled = currentSubScreen != null) {
        viewModel.navigateBack()
    }

    val subtitleText = when (currentSubScreen) {
        "calculator" -> "SGPA & CGPA Calculator"
        "assignments" -> "Assignment Manager"
        "notes" -> "AI Notes & Snap-Study"
        "profile" -> "Student Profile"
        else -> when (currentTab) {
            0 -> "Overview & Today's Schedule"
            1 -> "Weekly Class Timetable"
            2 -> "AI Interactive Tutor"
            3 -> "Smart Attendance & Analytics"
            4 -> "Campus Hub & Opportunities"
            else -> "Academic Companion"
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("campus_scaffold"),
        topBar = {
            CampusTopHeader(
                subtitle = subtitleText,
                profile = profile,
                onNotificationClick = { showNotificationDialog = true },
                onProfileClick = { viewModel.navigateTo("profile") },
                onProBadgeClick = { viewModel.openSubscriptionPaywall() }
            )
        },
        bottomBar = {
            if (currentSubScreen == null) {
                CampusBottomNavigation(
                    selectedTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (currentSubScreen != null) {
                when (currentSubScreen) {
                    "calculator" -> {
                        GradeCalculatorScreen(
                            currentCgpa = profile?.cgpa ?: 8.74f,
                            semesterResults = semesterResults,
                            onSaveSemesterResult = { sem, sgpa, credits ->
                                viewModel.saveSemesterResult(sem, sgpa, credits)
                            },
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                    "assignments" -> {
                        AssignmentManagerScreen(
                            assignments = assignments,
                            onToggleAssignment = { viewModel.toggleAssignment(it) },
                            onAddAssignment = { title, sub, due, pts, pri, desc ->
                                viewModel.addAssignment(title, sub, due, pts, pri, desc)
                            },
                            onDeleteAssignment = { viewModel.deleteAssignment(it) },
                            onOpenAiAssistant = {
                                viewModel.checkAiAccess {
                                    viewModel.selectTab(2) // Jump to AI Study
                                }
                            },
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                    "notes" -> {
                        StudyNotesScreen(
                            notes = studyNotes,
                            onBack = { viewModel.navigateBack() },
                            onOpenAiStudy = { viewModel.selectTab(2) },
                            isAiSubscribed = isAiSubscribed,
                            onOpenPaywall = { viewModel.openSubscriptionPaywall() }
                        )
                    }
                    "profile" -> {
                        ProfileSettingsScreen(
                            profile = profile,
                            isDarkTheme = isDarkTheme,
                            onToggleDarkTheme = onToggleDarkTheme,
                            onUpdateProfile = { name, email, col, uni, crs, brn, sem ->
                                viewModel.updateProfile(name, email, col, uni, crs, brn, sem)
                            },
                            onResetData = { viewModel.resetAllData() },
                            onBack = { viewModel.navigateBack() },
                            onOpenPaywall = { viewModel.openSubscriptionPaywall() },
                            onCancelSubscription = { viewModel.cancelSubscription() },
                            onSignOut = { authViewModel.signOut() }
                        )
                    }
                }
            } else {
                when (currentTab) {
                    0 -> {
                        TodayScreen(
                            profile = profile,
                            subjects = subjects,
                            schedules = schedules,
                            assignments = assignments,
                            onOpenAiStudy = { viewModel.selectTab(2) },
                            onOpenAnalytics = { viewModel.selectTab(3) },
                            onOpenSchedule = { viewModel.selectTab(1) },
                            onOpenAssignments = { viewModel.navigateTo("assignments") },
                            onOpenCalculator = { viewModel.navigateTo("calculator") },
                            onOpenNotes = { viewModel.navigateTo("notes") },
                            onOpenExplore = { viewModel.selectTab(4) }
                        )
                    }
                    1 -> {
                        ScheduleScreen(
                            schedules = schedules,
                            onAddClass = { viewModel.addClassSchedule(it) },
                            onDeleteClass = { viewModel.deleteClassSchedule(it) }
                        )
                    }
                    2 -> {
                        AiStudyScreen(
                            courseModule = selectedCourseModule,
                            onSelectCourse = { viewModel.setSelectedCourseModule(it) },
                            activeMode = aiStudyMode,
                            onSelectMode = { viewModel.setAiStudyMode(it) },
                            chatMessages = chatMessages,
                            onSendPrompt = { viewModel.sendAiPrompt(it) },
                            onOpenLibrary = { viewModel.navigateTo("notes") },
                            isAiSubscribed = isAiSubscribed,
                            onOpenPaywall = { viewModel.openSubscriptionPaywall() }
                        )
                    }
                    3 -> {
                        AttendanceAnalyticsScreen(
                            profile = profile,
                            subjects = subjects,
                            semesterResults = semesterResults,
                            bunkSliderValue = bunkSliderValue,
                            onBunkSliderChange = { viewModel.setBunkSlider(it) },
                            onMarkAttendance = { subId, attended ->
                                viewModel.markAttendance(subId, attended)
                            },
                            onSubmitMedicalSlip = { subName ->
                                viewModel.submitMedicalSlip(subName)
                            }
                        )
                    }
                    4 -> {
                        ExploreScreen(
                            filter = exploreFilter,
                            onFilterChange = { viewModel.setExploreFilter(it) },
                            searchQuery = exploreSearchQuery,
                            onSearchChange = { viewModel.setExploreSearchQuery(it) },
                            scanState = resumeScanState,
                            onScanResume = { viewModel.scanResume() }
                        )
                    }
                }
            }
        }
    }

    if (showNotificationDialog) {
        CampusNotificationDialog(onDismiss = { showNotificationDialog = false })
    }

    if (showSubscriptionPaywall) {
        SubscriptionPaywallDialog(
            profile = profile,
            onDismiss = { viewModel.dismissSubscriptionPaywall() },
            onSubscribeSuccess = { paymentMethod ->
                viewModel.subscribeToAi(paymentMethod)
            },
            onUpdatePaymentQr = { upiId, qrUri ->
                viewModel.updateCustomPaymentQr(upiId, qrUri)
            }
        )
    }
}

@Composable
fun CampusNotificationDialog(onDismiss: () -> Unit) {
    val sampleNotifications = listOf(
        Triple(
            "DBMS Lab in 15 Minutes",
            "Room 402 Wing C • System 42 Assigned. Bring Exp 5 assignment printout.",
            "10:15 AM"
        ),
        Triple(
            "Attendance Warning Resolved",
            "Medical slip for Computer Networks was verified by HoD Office.",
            "09:30 AM"
        ),
        Triple(
            "New AI Match Found",
            "Razorpay Backend Engineering internship matches 96% of your skills.",
            "Yesterday"
        ),
        Triple(
            "Mid-Semester Datesheet Released",
            "Exams commence from Nov 14. Check Examination Portal for hall ticket.",
            "2 days ago"
        )
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Campus Alerts",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                sampleNotifications.forEach { (title, body, time) ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = time,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = body,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier.fillMaxWidth().testTag("dismiss_notifications_button")
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

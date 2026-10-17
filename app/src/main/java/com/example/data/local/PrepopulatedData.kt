package com.example.data.local

import com.example.data.model.AssignmentEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ClassScheduleEntity
import com.example.data.model.SemesterResultEntity
import com.example.data.model.StudyNoteEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.SubjectGradeEntity
import com.example.data.model.UserProfileEntity

object PrepopulatedData {
    val defaultProfile = UserProfileEntity(
        id = "student_profile",
        fullName = "Aarav Sharma",
        email = "aarav.sharma@campus.edu",
        college = "IIT Bombay",
        university = "Autonomous Institute",
        course = "B.Tech",
        branch = "Computer Science & Engineering",
        semester = 5,
        academicYear = "Yr 3",
        streakDays = 14,
        cgpa = 8.74f,
        minAttendanceRequired = 75.0f,
        isLoggedIn = true,
        isAiSubscribed = false,
        subscriptionPrice = "₹49/month",
        subscriptionPeriod = "Monthly",
        subscriptionPlan = "Campus AI Pro",
        subscriptionExpiryDate = "",
        paymentMethod = "",
        customUpiId = "campusmate.pay@upi",
        customQrUri = ""
    )

    val defaultSubjects = listOf(
        SubjectEntity(
            code = "CS501",
            name = "Computer Networks",
            faculty = "Prof. S. Sharma",
            attendedCount = 26,
            totalCount = 28,
            minAttendancePercent = 75.0f,
            roomNumber = "LH 201"
        ),
        SubjectEntity(
            code = "CS502",
            name = "Algorithms (DAA)",
            faculty = "Dr. Aris Thorne",
            attendedCount = 19,
            totalCount = 24,
            minAttendancePercent = 75.0f,
            roomNumber = "LH 302"
        ),
        SubjectEntity(
            code = "CS503",
            name = "Operating Systems",
            faculty = "Prof. Elena Rostova",
            attendedCount = 22,
            totalCount = 25,
            minAttendancePercent = 75.0f,
            roomNumber = "LH 105"
        ),
        SubjectEntity(
            code = "CS504",
            name = "Microprocessors & Int.",
            faculty = "Dr. K. Ramanujan",
            attendedCount = 15,
            totalCount = 21,
            minAttendancePercent = 75.0f,
            roomNumber = "Lab 2"
        )
    )

    val defaultTodayClasses = listOf(
        ClassScheduleEntity(
            subjectCode = "CS501",
            subjectName = "Computer Networks",
            dayOfWeek = 4, // Thursday
            startTime = "09:30 AM",
            endTime = "10:45 AM",
            faculty = "Prof. K. Rao",
            roomNumber = "Lecture Hall 201",
            isLab = false,
            status = "Attended"
        ),
        ClassScheduleEntity(
            subjectCode = "CS505",
            subjectName = "Database Management Systems Lab",
            dayOfWeek = 4,
            startTime = "11:15 AM",
            endTime = "01:00 PM",
            faculty = "Prof. S. Gupta",
            roomNumber = "Computing Lab 4",
            isLab = true,
            labExperiment = "Exp 5: Multi-Level Index Queries",
            status = "Happening Now"
        ),
        ClassScheduleEntity(
            subjectCode = "CS506",
            subjectName = "Compiler Design",
            dayOfWeek = 4,
            startTime = "02:30 PM",
            endTime = "03:45 PM",
            faculty = "Guest Lecture: LLVM Optimization",
            roomNumber = "Auditorium B",
            isLab = false,
            status = "Upcoming"
        ),
        ClassScheduleEntity(
            subjectCode = "CS502",
            subjectName = "Algorithms (DAA)",
            dayOfWeek = 5, // Friday
            startTime = "10:00 AM",
            endTime = "11:15 AM",
            faculty = "Dr. Aris Thorne",
            roomNumber = "Lecture Hall 302",
            status = "Upcoming"
        ),
        ClassScheduleEntity(
            subjectCode = "CS503",
            subjectName = "Operating Systems",
            dayOfWeek = 5,
            startTime = "11:30 AM",
            endTime = "12:45 PM",
            faculty = "Prof. Elena Rostova",
            roomNumber = "Lecture Hall 105",
            status = "Upcoming"
        )
    )

    val defaultAssignments = listOf(
        AssignmentEntity(
            title = "DBMS Assignment 4: B+ Tree Indexing",
            subject = "Database Management Systems",
            dueDateText = "Due Tomorrow • 11:59 PM",
            points = 20,
            priority = "HIGH",
            isCompleted = false,
            isOverdue = false,
            description = "Submit C++ implementation + memory trace PDF"
        ),
        AssignmentEntity(
            title = "Software Engineering SRS Document",
            subject = "Software Engineering",
            dueDateText = "Due in 3 Days • Monday, 5:00 PM",
            points = 50,
            priority = "MEDIUM",
            isCompleted = false,
            isOverdue = false,
            description = "Group 12 Submission • 3/4 Team members approved",
            groupInfo = "Review pending by Rohan"
        ),
        AssignmentEntity(
            title = "OS Lab 3: Page Replacement Algorithms",
            subject = "Operating Systems",
            dueDateText = "Completed Yesterday",
            points = 25,
            priority = "HIGH",
            isCompleted = true,
            isOverdue = false,
            description = "FIFO, LRU, Optimal simulation with page fault analysis"
        )
    )

    val defaultSemesterResults = listOf(
        SemesterResultEntity(semesterNumber = 1, sgpa = 8.20f, totalCredits = 22),
        SemesterResultEntity(semesterNumber = 2, sgpa = 8.40f, totalCredits = 24),
        SemesterResultEntity(semesterNumber = 3, sgpa = 8.50f, totalCredits = 24),
        SemesterResultEntity(semesterNumber = 4, sgpa = 8.74f, totalCredits = 24)
    )

    val defaultSubjectGrades = listOf(
        SubjectGradeEntity(semesterNumber = 5, subjectName = "Computer Networks", credits = 4, gradeLetter = "A+", gradePoints = 9),
        SubjectGradeEntity(semesterNumber = 5, subjectName = "Algorithms (DAA)", credits = 4, gradeLetter = "A", gradePoints = 8),
        SubjectGradeEntity(semesterNumber = 5, subjectName = "Operating Systems", credits = 4, gradeLetter = "A+", gradePoints = 9),
        SubjectGradeEntity(semesterNumber = 5, subjectName = "Microprocessors & Int.", credits = 4, gradeLetter = "B+", gradePoints = 7),
        SubjectGradeEntity(semesterNumber = 5, subjectName = "DBMS Lab", credits = 2, gradeLetter = "O", gradePoints = 10)
    )

    val defaultStudyNotes = listOf(
        StudyNoteEntity(
            title = "OS Mod 3: Virtual Memory & Page Replacement",
            subject = "Operating Systems",
            readTimeMinutes = 14,
            formulasCount = 6,
            confidencePercent = 98,
            summaryText = "Covers paging, multi-level page tables, inverted page tables, and LRU clock algorithm.",
            noteType = "SUMMARY"
        ),
        StudyNoteEntity(
            title = "Compiler Design: LR(1) Parsing Table",
            subject = "Compiler Design",
            readTimeMinutes = 9,
            formulasCount = 18,
            confidencePercent = 95,
            summaryText = "Deterministic shift-reduce parsing, canonical collection of LR(1) items, lookahead sets.",
            noteType = "CHEAT_SHEET"
        )
    )

    val defaultChatMessages = listOf(
        ChatMessageEntity(
            sender = "USER",
            text = "Explain Dijkstra's vs Bellman-Ford algorithm with time complexity and negative edge weights intuition.",
            courseCode = "CS302"
        ),
        ChatMessageEntity(
            sender = "AI",
            text = "Shortest path algorithms solve for minimal cumulative weight. The primary divergence lies in how greedily optimal choices are locked in.",
            courseCode = "CS302",
            titleTag = "Algorithmic Paradigm Contrast",
            conceptTag = "Concept Map",
            comparisonTable = "Dijkstra's (Greedy)|O((V + E) log V)|Fails on negative weights###Bellman-Ford (DP)|O(V × E)|Detects negative cycles",
            mentalAnchor = "Dijkstra assumes paths never get cheaper once closed. A negative edge breaks this contract by creating a retroactive discount!",
            codeSnippet = "# Relax edges |V| - 1 times\nfor _ in range(V - 1):\n    for u, v, w in edges:\n        if dist[u] + w < dist[v]:\n            dist[v] = dist[u] + w"
        )
    )
}

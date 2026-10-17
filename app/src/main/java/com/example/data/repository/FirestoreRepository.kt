package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.AssignmentEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.StudyNoteEntity
import com.example.data.model.UserProfileEntity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG = "FirestoreRepository"

class FirestoreRepository(private val db: FirebaseFirestore) {

    // Secondary constructor to resolve named database ID from string resources
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth get() = Firebase.auth

    private fun requireUserId(userId: String): String {
        val currentUid = auth.currentUser?.uid
            ?: throw IllegalStateException("User is not authenticated with Firebase Auth")
        if (currentUid != userId) {
            throw SecurityException("Security violation: authenticated user $currentUid cannot access $userId")
        }
        return currentUid
    }

    suspend fun saveUserProfile(userId: String, profile: UserProfileEntity): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            requireUserId(userId)
            val data = mapOf(
                "id" to userId,
                "fullName" to profile.fullName.ifBlank { "Student" },
                "email" to (auth.currentUser?.email ?: profile.email),
                "college" to profile.college,
                "university" to profile.university,
                "course" to profile.course,
                "branch" to profile.branch,
                "semester" to profile.semester,
                "streakDays" to profile.streakDays,
                "cgpa" to profile.cgpa.toDouble(),
                "isAiSubscribed" to profile.isAiSubscribed,
                "subscriptionPlan" to profile.subscriptionPlan,
                "subscriptionPrice" to profile.subscriptionPrice,
                "subscriptionPeriod" to profile.subscriptionPeriod,
                "customUpiId" to profile.customUpiId,
                "customQrUri" to profile.customQrUri
            )
            db.collection("users").document(userId).set(data).await()
            Log.d(TAG, "User profile saved successfully to Firestore for $userId")
            Unit
        }.onFailure { e ->
            Log.e(TAG, "Failed to save user profile for $userId", e)
        }
    }

    suspend fun getUserProfile(userId: String): Result<UserProfileEntity?> = withContext(Dispatchers.IO) {
        runCatching {
            requireUserId(userId)
            val doc = db.collection("users").document(userId).get().await()
            if (!doc.exists()) return@runCatching null

            UserProfileEntity(
                id = doc.getString("id") ?: userId,
                fullName = doc.getString("fullName") ?: "Student",
                email = doc.getString("email") ?: (auth.currentUser?.email ?: ""),
                college = doc.getString("college") ?: "IIT Bombay",
                university = doc.getString("university") ?: "Autonomous Institute",
                course = doc.getString("course") ?: "B.Tech",
                branch = doc.getString("branch") ?: "Computer Science & Engineering",
                semester = doc.getLong("semester")?.toInt() ?: 5,
                streakDays = doc.getLong("streakDays")?.toInt() ?: 14,
                cgpa = (doc.getDouble("cgpa") ?: 8.74).toFloat(),
                isAiSubscribed = doc.getBoolean("isAiSubscribed") ?: false,
                subscriptionPlan = doc.getString("subscriptionPlan") ?: "Free Tier",
                subscriptionPrice = doc.getString("subscriptionPrice") ?: "₹49/month",
                subscriptionPeriod = doc.getString("subscriptionPeriod") ?: "Monthly",
                customUpiId = doc.getString("customUpiId") ?: "campusmate.pay@upi",
                customQrUri = doc.getString("customQrUri") ?: ""
            )
        }.onFailure { e ->
            Log.e(TAG, "Failed to get user profile for $userId", e)
        }
    }

    fun observeUserProfile(userId: String): Flow<UserProfileEntity?> = callbackFlow {
        val docRef = db.collection("users").document(userId)
        val registration: ListenerRegistration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Snapshot error for user profile", error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val profile = UserProfileEntity(
                    id = snapshot.getString("id") ?: userId,
                    fullName = snapshot.getString("fullName") ?: "Student",
                    email = snapshot.getString("email") ?: (auth.currentUser?.email ?: ""),
                    college = snapshot.getString("college") ?: "IIT Bombay",
                    university = snapshot.getString("university") ?: "Autonomous Institute",
                    course = snapshot.getString("course") ?: "B.Tech",
                    branch = snapshot.getString("branch") ?: "Computer Science & Engineering",
                    semester = snapshot.getLong("semester")?.toInt() ?: 5,
                    streakDays = snapshot.getLong("streakDays")?.toInt() ?: 14,
                    cgpa = (snapshot.getDouble("cgpa") ?: 8.74).toFloat(),
                    isAiSubscribed = snapshot.getBoolean("isAiSubscribed") ?: false,
                    subscriptionPlan = snapshot.getString("subscriptionPlan") ?: "Free Tier",
                    subscriptionPrice = snapshot.getString("subscriptionPrice") ?: "₹49/month",
                    subscriptionPeriod = snapshot.getString("subscriptionPeriod") ?: "Monthly",
                    customUpiId = snapshot.getString("customUpiId") ?: "campusmate.pay@upi",
                    customQrUri = snapshot.getString("customQrUri") ?: ""
                )
                trySend(profile)
            } else {
                trySend(null)
            }
        }
        awaitClose { registration.remove() }
    }

    suspend fun saveAssignment(userId: String, assignment: AssignmentEntity): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            requireUserId(userId)
            val data = mapOf(
                "id" to assignment.id.toInt(),
                "title" to assignment.title,
                "subject" to assignment.subject,
                "dueDateText" to assignment.dueDateText,
                "points" to assignment.points,
                "priority" to assignment.priority,
                "isCompleted" to assignment.isCompleted,
                "description" to assignment.description
            )
            db.collection("users")
                .document(userId)
                .collection("assignments")
                .document(assignment.id.toString())
                .set(data)
                .await()
            Unit
        }
    }

    suspend fun deleteAssignment(userId: String, assignmentId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            requireUserId(userId)
            db.collection("users")
                .document(userId)
                .collection("assignments")
                .document(assignmentId.toString())
                .delete()
                .await()
            Unit
        }
    }

    suspend fun saveStudyNote(userId: String, note: StudyNoteEntity): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            requireUserId(userId)
            val data = mapOf(
                "id" to note.id.toInt(),
                "title" to note.title,
                "subject" to note.subject,
                "summaryText" to note.summaryText,
                "noteType" to note.noteType,
                "createdDate" to note.createdDate
            )
            db.collection("users")
                .document(userId)
                .collection("notes")
                .document(note.id.toString())
                .set(data)
                .await()
            Unit
        }
    }

    suspend fun saveChatMessage(userId: String, message: ChatMessageEntity): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            requireUserId(userId)
            val data = mapOf(
                "id" to message.id.toInt(),
                "sender" to message.sender,
                "text" to message.text,
                "courseCode" to message.courseCode,
                "titleTag" to message.titleTag,
                "conceptTag" to message.conceptTag,
                "comparisonTable" to message.comparisonTable,
                "mentalAnchor" to message.mentalAnchor,
                "codeSnippet" to message.codeSnippet,
                "timestamp" to message.timestamp
            )
            db.collection("users")
                .document(userId)
                .collection("chat_messages")
                .document(message.id.toString())
                .set(data)
                .await()
            Unit
        }
    }
}

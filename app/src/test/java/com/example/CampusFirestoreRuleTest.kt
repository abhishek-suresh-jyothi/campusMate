package com.example

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.AssignmentEntity
import com.example.data.model.UserProfileEntity
import com.example.data.repository.FirestoreRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CampusFirestoreRuleTest : FirestoreEmulatorTestBase() {

    private val aliceEmail = "alice@campus.edu"
    private val bobEmail = "bob@campus.edu"

    @Test
    fun createAndReadUserProfile_authenticatedOwner_succeeds() = runBlocking {
        val aliceUid = signInTestUser(aliceEmail)
        val repository = FirestoreRepository(firestore)

        val profile = UserProfileEntity(
            id = aliceUid,
            fullName = "Alice Sharma",
            email = aliceEmail,
            college = "IIT Bombay",
            university = "Autonomous Institute",
            course = "B.Tech",
            branch = "Computer Science",
            semester = 5,
            streakDays = 14,
            cgpa = 8.85f,
            isAiSubscribed = false
        )

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveUserProfile(aliceUid, profile)
        }
        assertTrue("Save user profile should succeed", saveResult.isSuccess)

        val readResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getUserProfile(aliceUid)
        }
        assertTrue("Read user profile should succeed", readResult.isSuccess)
        val loaded = readResult.getOrThrow()
        assertNotNull(loaded)
        assertEquals("Alice Sharma", loaded?.fullName)
        assertEquals(aliceEmail, loaded?.email)
    }

    @Test
    fun updateAiSubscription_validPayload_persistsInCloud() = runBlocking {
        val aliceUid = signInTestUser(aliceEmail)
        val repository = FirestoreRepository(firestore)

        val subscribedProfile = UserProfileEntity(
            id = aliceUid,
            fullName = "Alice Sharma",
            email = aliceEmail,
            isAiSubscribed = true,
            subscriptionPlan = "Campus AI Pro",
            subscriptionPrice = "₹49/month",
            subscriptionPeriod = "Monthly",
            customUpiId = "campusmate.pay@upi",
            customQrUri = ""
        )

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveUserProfile(aliceUid, subscribedProfile)
        }
        assertTrue(saveResult.isSuccess)

        val readResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getUserProfile(aliceUid)
        }
        val loaded = readResult.getOrThrow()
        assertNotNull(loaded)
        assertTrue(loaded?.isAiSubscribed == true)
        assertEquals("₹49/month", loaded?.subscriptionPrice)
        assertEquals("Campus AI Pro", loaded?.subscriptionPlan)
    }

    @Test
    fun crossUserAccess_bobCannotAccessAliceProfile_fails() = runBlocking {
        val aliceUid = signInTestUser(aliceEmail)
        val repository = FirestoreRepository(firestore)

        val profile = UserProfileEntity(
            id = aliceUid,
            fullName = "Alice Private",
            email = aliceEmail
        )
        repository.saveUserProfile(aliceUid, profile)

        // Sign in as Bob
        signInTestUser(bobEmail)

        // Bob attempting to read or write Alice's data fails
        val bobAttempt = repository.getUserProfile(aliceUid)
        assertTrue("Bob reading Alice's profile must fail", bobAttempt.isFailure)
    }

    @Test
    fun assignments_aliceCanCreateAndManageAssignments() = runBlocking {
        val aliceUid = signInTestUser(aliceEmail)
        val repository = FirestoreRepository(firestore)

        val assignment = AssignmentEntity(
            id = 42L,
            title = "Distributed Systems Milestone 2",
            subject = "CS401",
            dueDateText = "Friday 11:59 PM",
            points = 25,
            priority = "High",
            isCompleted = false,
            description = "Raft Consensus Algorithm Leader Election in Kotlin"
        )

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveAssignment(aliceUid, assignment)
        }
        assertTrue("Alice creating assignment should succeed", saveResult.isSuccess)

        val deleteResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.deleteAssignment(aliceUid, 42L)
        }
        assertTrue("Alice deleting assignment should succeed", deleteResult.isSuccess)
    }
}

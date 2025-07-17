package com.example.collegeapp.data

import com.example.collegeapp.model.StudentProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.collegeapp.model.TeacherProfile
import com.google.firebase.firestore.ListenerRegistration

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Register student
    suspend fun registerStudent(profile: StudentProfile, password: String): String? {
        return try {
            val result = auth.createUserWithEmailAndPassword(profile.email, password).await()
            val uid = result.user?.uid ?: return "Registration failed"
            val studentWithUid = profile.copy(uid = uid)
            db.collection("students").document(uid).set(studentWithUid).await()
            null
        } catch (e: Exception) {
            e.localizedMessage
        }
    }

    // Login existing student
    suspend fun registerStudentPending(profile: StudentProfile, password: String): String? {
        return try {
            val result = auth.createUserWithEmailAndPassword(profile.email, password).await()
            val uid = result.user?.uid ?: return "Registration failed"
            val studentWithUid = profile.copy(uid = uid)
            db.collection("pending_students").document(uid).set(studentWithUid).await()
            null // success
        } catch (e: Exception) {
            e.message
        }
    }
    suspend fun loginStudent(email: String, password: String): Pair<String?, StudentProfile?> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Pair("Login failed", null)
            val doc = db.collection("students").document(uid).get().await()
            val profile = doc.toObject(StudentProfile::class.java)
            if (profile != null) {
                Pair(null, profile)
            } else {
                Pair("Account not approved yet. Please wait for admin approval.", null)
            }
        } catch (e: Exception) {
            Pair(e.message, null)
        }
    }
    suspend fun getStudentProfile(): StudentProfile? {
        val uid = auth.currentUser?.uid ?: return null
        val doc = db.collection("students").document(uid).get().await()
        return doc.toObject(StudentProfile::class.java)
    }

    // Get teacher approval status and profile
    suspend fun getTeacherApprovalStatus(): Pair<String, TeacherProfile?> {
        val uid = auth.currentUser?.uid ?: return Pair("not_registered", null)
        val doc = db.collection("teachers").document(uid).get().await()
        val profile = doc.toObject(TeacherProfile::class.java)
        return when {
            profile == null -> Pair("not_registered", null)
            profile.status == "approved" -> Pair("approved", profile)
            profile.status == "pending" -> Pair("pending", profile)
            else -> Pair("not_registered", null)
        }
    }

    // Register a new teacher
    suspend fun registerTeacher(profile: TeacherProfile, password: String): String? {
        return try {
            val result = auth.createUserWithEmailAndPassword(profile.email, password).await()
            val uid = result.user?.uid ?: return "Registration failed"
            val teacherWithUid = profile.copy(uid = uid)
            db.collection("teachers").document(uid).set(teacherWithUid).await()
            null
        } catch (e: Exception) {
            e.localizedMessage
        }
    }


    fun listenForApproval(uid: String, onStatusChanged: (String) -> Unit): ListenerRegistration {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("students").document(uid)
        return docRef.addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                onStatusChanged("approved")
            } else {
                onStatusChanged("pending")
            }
        }
    }

}

package com.example.collegeapp.data

import android.widget.Toast
import com.example.collegeapp.model.StudentProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.collegeapp.model.TeacherProfile
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    suspend fun registerStudentPending(profile: StudentProfile, password: String): String? {
        // Get FCM token if needed here
        val data = hashMapOf(
            "name" to profile.name,
            "fatherName" to profile.fatherName,
            "motherName" to profile.motherName,
            "address" to profile.address,
            "pincode" to profile.pincode,
            "aadhaar" to profile.aadhaar,
            "clas" to profile.clas,
            "rollNumber" to profile.rollNumber,
            "dob" to profile.dob,
            "mobile" to profile.mobile,
            "email" to profile.email,
            "password" to password,
            "fcmToken" to profile.fcmToken  // make sure this is set!
        )

        return try {
            val functions = FirebaseFunctions.getInstance()
            val result = functions
                .getHttpsCallable("registerPendingStudent")
                .call(data)
                .await()
            null // Success: no error
        } catch (e: Exception) {

            e.message
        }
    }



    suspend fun loginStudent(email: String, password: String): Pair<String?, StudentProfile?> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: return Pair("Login failed: UID not found", null)
            val doc = db.collection("approved_students").document(uid).get().await()
            if (!doc.exists()) {
                Pair("Profile not approved yet or does not exist.", null)
            } else {
                val profile = doc.toObject(StudentProfile::class.java)
                Pair(null, profile)
            }
        } catch (e: Exception) {
            Pair(e.message ?: "Login failed", null)
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

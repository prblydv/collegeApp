package com.example.collegeapp.ui
import com.example.collegeapp.model.StudentProfile
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun LoginTabScreen() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("student_prefs", Context.MODE_PRIVATE)
//    prefs.edit().clear().apply() // This clears all local student data

    val registeredEmail = prefs.getString("registered_email", null)
    val isLoggedIn = prefs.getBoolean("is_logged_in", false)
    val loggedInUid = prefs.getString("logged_in_uid", null)
    var role by remember { mutableStateOf<String?>(null) }

    // --- PRIORITY: If logged in, show only profile and nothing else!
    if (isLoggedIn && loggedInUid != null) {
        StudentProfileScreen(
            onLogout = {
                prefs.edit().apply {
                    remove("is_logged_in")
                    remove("logged_in_uid")
                    apply()
                }
                role = null // go back to role selection on logout
            }
        )
        return // Prevents any other UI from being shown!
    }

    when {

        // 2. Already logged in
        isLoggedIn && registeredEmail != null -> {
            StudentProfileScreen(
                onLogout = {
                    // Clear login flag and email
                    prefs.edit().apply {
                        remove("is_logged_in")
                        remove("registered_email")
                        apply()
                    }
                    role = null // go back to role selection on logout
                }
            )
        }
        // 3. No role selected yet
        role == null -> {
            RoleSelectionScreen { selectedRole ->
                role = selectedRole
            }
        }
        // 4. Login/Register screens based on role
        role == "student" -> StudentLoginAndRegisterScreen()
        role == "teacher" -> TeacherLoginAndRegisterScreen()
    }
}
@Composable
fun StudentProfileScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var studentProfile by remember { mutableStateOf<StudentProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var refreshCounter by remember { mutableStateOf(0) } // <-- New state for refresh

    // Re-fetch profile when refreshCounter changes
    LaunchedEffect(uid, refreshCounter) {
        Log.e("ProfileDebug", "UID used: $uid")

        isLoading = true
        errorMsg = null
        if (uid != null) {
            try {
                val doc = FirebaseFirestore.getInstance()
                    .collection("approved_students")
                    .document(uid)
                    .get()
                    .await()

                studentProfile = doc.toObject(StudentProfile::class.java)
            } catch (e: Exception) {
                errorMsg = "Error loading profile: ${e.message}"
            }
        }
        isLoading = false
    }

    if (isLoading) {
        CircularProgressIndicator()
        return
    }
    if (errorMsg != null) {
        Text(errorMsg!!, color = MaterialTheme.colors.error)
        return
    }
    if (studentProfile == null) {
        Text("Profile not found.")
        return
    }

    // UI to show profile details
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome, ${studentProfile!!.name}", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Roll Number: ${studentProfile!!.rollNumber}")
        Text("Class: ${studentProfile!!.clas}")
        Text("Father's Name: ${studentProfile!!.fatherName}")
        Text("Mother's Name: ${studentProfile!!.motherName}")
        Text("Mobile: ${studentProfile!!.mobile}")
        Text("Email: ${studentProfile!!.email}")
        Text("Fees Paid (Online): ₹${studentProfile!!.feesPaid}")
        Spacer(modifier = Modifier.height(24.dp))
        // Add Refresh Button
        Button(
            onClick = { refreshCounter++ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Refresh Profile")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Logout")
        }
    }
}

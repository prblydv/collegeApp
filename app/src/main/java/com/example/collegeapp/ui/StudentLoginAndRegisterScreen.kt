package com.example.collegeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.collegeapp.data.UserRepository
import com.example.collegeapp.model.StudentProfile
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
@Composable
fun StudentLoginAndRegisterScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = context.getSharedPreferences("student_prefs", android.content.Context.MODE_PRIVATE)
    val repo = remember { UserRepository() }
    var showChoice by remember { mutableStateOf(true) }
    var isRegistering by remember { mutableStateOf(false) }
    var isLoggingIn by remember { mutableStateOf(false) }
    var profile by remember { mutableStateOf<StudentProfile?>(null) }

    var errorMsg by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val isLoggedIn = prefs.getBoolean("is_logged_in", false)
    val loggedInUid = prefs.getString("logged_in_uid", null)
    var autoLoadProfile by remember { mutableStateOf(false) }

    LaunchedEffect(isLoggedIn, loggedInUid) {
        // Only run if the user is logged in but no profile loaded yet
        if (isLoggedIn && loggedInUid != null && profile == null) {
            autoLoadProfile = true
            try {
                val doc = FirebaseFirestore.getInstance()
                    .collection("approved_students")
                    .document(loggedInUid)
                    .get()
                    .await()
                val loadedProfile = doc.toObject(StudentProfile::class.java)
                if (loadedProfile != null) {
                    profile = loadedProfile
                }
            } catch (e: Exception) {
                // Optionally handle error
            }
            autoLoadProfile = false
        }
    }
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        if ((isLoggedIn && profile != null) || profile != null) {
            ShowStudentProfile(profile!!) {
                // On Logout: clear prefs and state
                prefs.edit().clear().apply()
                profile = null
                showChoice = true
                isLoggingIn = false
                isRegistering = false
                com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            }
            return@Column // Only show profile if logged in
        }
        if (showChoice) {
            Button(onClick = { isRegistering = true; showChoice = false }) { Text("Register") }
            Spacer(Modifier.height(16.dp))
            Button(onClick = { isLoggingIn = true; showChoice = false }) { Text("Login") }
        }

        if (isRegistering) {
            StudentRegistrationForm { form, password ->
                coroutineScope.launch {
                    errorMsg = null
                    val err = repo.registerStudentPending(form, password)
                    if (err == null) {
                        showChoice = true
                        isRegistering = false
                        isLoggingIn = false // Show login form immediately
                        errorMsg = null
                    } else {
                        errorMsg = err
                    }
                }
            }
        }



        if (isLoggingIn) {
            StudentLoginForm { email, password ->
                coroutineScope.launch {
                    errorMsg = null
                    val (err, userProfile) = repo.loginStudent(email, password)
                    if (err == null && userProfile != null) {
                        profile = userProfile
                        isLoggingIn = false
                        // Save login state
                        prefs.edit()
                            .putBoolean("is_logged_in", true)
                            .putString("logged_in_uid", userProfile.uid)
                            .apply()
                    } else {
                        errorMsg = err
                    }
                }
            }
        }





        errorMsg?.let {
            Text(text = it, color = Color.Red)
        }
    }
}

@Composable
fun ShowStudentProfile(profile: StudentProfile, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome, ${profile.name}", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(16.dp))
        Text("Roll Number: ${profile.rollNumber}")
        Text("Class: ${profile.clas}")
        Text("Father's Name: ${profile.fatherName}")
        Text("Mother's Name: ${profile.motherName}")
        Text("Mobile: ${profile.mobile}")
        Text("Email: ${profile.email}")
        Text("Fees Paid: ₹${profile.feesPaid}")
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onLogout() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}

package com.example.collegeapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import com.example.collegeapp.repository.CourseRepository
import com.example.collegeapp.ui.MainScreen
import com.google.firebase.messaging.FirebaseMessaging
import androidx.core.content.edit
import com.example.collegeapp.ui.AnimatedNameSplash
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Optionally show a message or handle result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // -- App Check initialization: must come before using any Firebase APIs!
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        CourseRepository.refreshCourses(applicationContext)
        subscribeToCourseUpdates()

        setContent {
            var showSplash by remember { mutableStateOf(false) }

            if (showSplash) {
                // Show your animated logo splash
                AnimatedNameSplash()

            } else {
                // Your existing app content starts here
                val context = this
                var isDark by remember {
                    mutableStateOf(
                        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            .getBoolean("dark_mode", false)
                    )
                }

                com.example.collegeapp.ui.theme.CollegeAppTheme(darkTheme = isDark) {
                    NotificationPermissionMainScreen(
                        isDarkTheme = isDark,
                        onToggleTheme = {
                            isDark = !isDark
                            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                .edit {
                                    putBoolean("dark_mode", isDark)
                                }
                        }
                    )
                }
            }
        }
        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // You can log or display "Subscribed"
                } else {
                    // Handle the error
                }
            }
    }

    private fun subscribeToCourseUpdates() {
        FirebaseMessaging.getInstance().subscribeToTopic("course_updates")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Subscribed to course_updates topic")
                } else {
                    Log.e("FCM", "Failed to subscribe to course_updates", task.exception)
                }
            }
    }

    // This composable wraps MainScreen and handles the dialog logic
    @Composable
    fun NotificationPermissionMainScreen(
        isDarkTheme: Boolean,
        onToggleTheme: () -> Unit
    ) {
        val context = this@MainActivity
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        var showPermissionDialog by remember {
            mutableStateOf(
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        !prefs.getBoolean("notification_permission_prompted", false)
            )
        }

        if (showPermissionDialog) {
            androidx.compose.material.AlertDialog(
                onDismissRequest = {
                    showPermissionDialog = false
                    prefs.edit().putBoolean("notification_permission_prompted", true).apply()
                },
                title = { androidx.compose.material.Text("Enable Notifications") },
                text = { androidx.compose.material.Text("Turn on notifications to receive important news and updates from your school.") },
                confirmButton = {
                    androidx.compose.material.Button(onClick = {
                        showPermissionDialog = false
                        prefs.edit().putBoolean("notification_permission_prompted", true).apply()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }) {
                        androidx.compose.material.Text("Turn On")
                    }
                },
                dismissButton = {
                    androidx.compose.material.Button(onClick = {
                        showPermissionDialog = false
                        prefs.edit().putBoolean("notification_permission_prompted", true).apply()
                    }) {
                        androidx.compose.material.Text("Maybe Later")
                    }
                }
            )
        }

        // Your original MainScreen
        MainScreen(
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme
        )
    }
}


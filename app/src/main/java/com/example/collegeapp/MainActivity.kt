package com.example.collegeapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import com.example.collegeapp.ui.MainScreen
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.example.collegeapp.repository.CourseRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CourseRepository.refreshCourses(applicationContext)

        subscribeToCourseUpdates()

        setContent {
            MaterialTheme {
                MainScreen()
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
}







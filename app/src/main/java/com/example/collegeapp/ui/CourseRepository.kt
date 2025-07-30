package com.example.collegeapp.repository

import android.content.Context
import android.util.Log
import com.example.collegeapp.model.Course
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

object CourseRepository {
    fun clearRecentlyUpdatedCourseId(context: Context) {
        val prefs = context.getSharedPreferences("courses_data", Context.MODE_PRIVATE)
        prefs.edit().remove("recentlyUpdatedCourseId").apply()
        Log.d("CourseRepository", "Cleared recently updated course ID")
    }

    fun refreshCourses(context: Context, updatedCourseId: String? = null) {
        val db = FirebaseFirestore.getInstance()
        db.collection("courses")
            .get()
            .addOnSuccessListener { result ->
                val courses = result.map { doc ->
                    Course(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        syllabus = doc.getString("syllabus") ?: "",
                        courseFees = (doc.get("courseFees") as? Number)?.toLong() ?: 0L,
                        webRegistrationFees = (doc.get("webRegistrationFees") as? Number)?.toLong() ?: 0L,
                        totalSeats = (doc.get("totalSeats") as? Number)?.toLong() ?: 0L,
                        filledSeats = (doc.get("filledSeats") as? Number)?.toLong() ?: 0L,
                        infoNotes = doc.getString("infoNotes") ?: "",
                        lastUpdated = doc.getTimestamp("lastUpdated")?.toDate()?.time ?: 0L
                    )
                }
                saveCoursesLocally(context, courses)

                if (updatedCourseId != null) {
                    val prefs = context.getSharedPreferences("courses_data", Context.MODE_PRIVATE)
                    prefs.edit().putString("recentlyUpdatedCourseId", updatedCourseId).apply()
                    Log.d("CourseRepository", "Saved recently updated course ID: $updatedCourseId")
                }
            }
    }

    private fun saveCoursesLocally(context: Context, courses: List<Course>) {
        val prefs = context.getSharedPreferences("courses_data", Context.MODE_PRIVATE)
        val json = Gson().toJson(courses)
        prefs.edit().putString("courses_list", json).apply()
    }
}

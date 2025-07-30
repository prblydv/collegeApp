package com.example.collegeapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeapp.model.Course
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.SharedPreferences
import android.util.Log

class CoursesViewModel : ViewModel() {
    private var prefs: SharedPreferences? = null

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses

    private val _highlightCourseId = MutableStateFlow<String?>(null)
    val highlightCourseId: StateFlow<String?> = _highlightCourseId

    fun loadCourses(context: Context) {
        viewModelScope.launch {
            _courses.value = getCoursesFromLocal(context)
            _highlightCourseId.value = getRecentlyUpdatedCourseId(context)
        }
    }

    private fun getCoursesFromLocal(context: Context): List<Course> {
        val prefs = context.getSharedPreferences("courses_data", Context.MODE_PRIVATE)
        val json = prefs.getString("courses_list", null)
        return if (json != null) {
            val type = object : TypeToken<List<Course>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }

    private fun getRecentlyUpdatedCourseId(context: Context): String? {
        val prefs = context.getSharedPreferences("courses_data", Context.MODE_PRIVATE)
        return prefs.getString("recentlyUpdatedCourseId", null)
    }

    private var prefsListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    fun startListeningForStorageChanges(context: Context) {
        val prefs = context.getSharedPreferences("courses_data", Context.MODE_PRIVATE)

        prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "courses_list" || key == "recentlyUpdatedCourseId") {
                Log.d("CoursesViewModel", "Detected change in courses storage")
                loadCourses(context)
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(prefsListener)
    }

    override fun onCleared() {
        super.onCleared()
        prefsListener?.let { listener ->
            prefs?.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

}

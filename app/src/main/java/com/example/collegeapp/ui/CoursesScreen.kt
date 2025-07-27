package com.example.collegeapp.ui

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.collegeapp.model.Course
import com.example.collegeapp.repository.CourseRepository
import com.example.collegeapp.viewmodel.CoursesViewModel

@Composable
fun CoursesScreen() {
    val context = LocalContext.current
    val viewModel: CoursesViewModel = viewModel()
    val courses by viewModel.courses.collectAsState()
    val highlightCourseId by viewModel.highlightCourseId.collectAsState()

    // Initial load
    LaunchedEffect(Unit) {
        viewModel.startListeningForStorageChanges(context)

        viewModel.loadCourses(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Available Courses", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (courses.isEmpty()) {
            Text("No courses available. Please check back later.", fontSize = 16.sp)
        } else {
            LazyColumn {
                items(courses) { course ->
                    CourseItem(
                        course = course,
                        highlight = course.id == highlightCourseId,
                        context = context
                    )
                }
            }
        }
    }
}
@Composable
fun CourseItem(course: Course, highlight: Boolean, context: Context) {
    var isHighlighted by remember { mutableStateOf(highlight) }

    LaunchedEffect(highlight) {
        if (highlight) {
            kotlinx.coroutines.delay(5000)
            isHighlighted = false
            CourseRepository.clearRecentlyUpdatedCourseId(context)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .then(if (isHighlighted) Modifier.border(2.dp, Color.Red) else Modifier),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(course.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Subjects: ${course.syllabus}")
            Text("Fees: ₹${course.courseFees} + ₹${course.webRegistrationFees} Web Registration")
            if (course.name != "BA" && course.name != "BSc"&& course.name != "B.Ed"&&  course.name != "Del.Ed") {
                Text("Seats: ${course.filledSeats}/${course.totalSeats}")
            }
            if (course.infoNotes.isNotBlank()) {
                Text("Note: ${course.infoNotes}", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}


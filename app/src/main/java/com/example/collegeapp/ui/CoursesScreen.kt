package com.example.collegeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.collegeapp.model.Course
import com.example.collegeapp.viewmodel.CoursesViewModel

@Composable
fun CoursesScreen() {
    val context = LocalContext.current
    val viewModel: CoursesViewModel = viewModel()
    val courses by viewModel.courses.collectAsState()

    // Initial load
    LaunchedEffect(Unit) {
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
                    CourseItem(course)
                }
            }
        }
    }
}
@Composable
fun CourseItem(course: Course) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(course.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Syllabus: ${course.syllabus}")
            Text("Fees: ₹${course.courseFees} + ₹${course.webRegistrationFees} Web Registration")

            // Display seats only if the course is NOT BA or BSc
            if (course.name != "BA" && course.name != "BSc") {
                Text("Seats: ${course.filledSeats}/${course.totalSeats}")
            }

            if (course.infoNotes.isNotBlank()) {
                Text("Note: ${course.infoNotes}", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

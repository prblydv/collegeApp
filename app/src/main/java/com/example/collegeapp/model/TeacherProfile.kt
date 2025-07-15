package com.example.collegeapp.model

data class TeacherProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val subject: String = "",
    val teacherId: String = "",
    val status: String = "pending"
)

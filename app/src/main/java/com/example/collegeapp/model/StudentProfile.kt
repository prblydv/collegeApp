package com.example.collegeapp.model

data class StudentProfile(
    val uid: String = "",
    val name: String = "",
    val fatherName: String = "",
    val motherName: String = "",
    val address: String = "",
    val pincode: String = "",
    val aadhaar: String = "",
    val clas: String = "",
    val rollNumber: String = "",
    val dob: String = "",
    val mobile: String = "",
    val email: String = "",
    val status: String = "pending",  // "pending", "approved"
    val fcmToken: String = "",       // <--- NEW: for notifications
    val registrationDate: Long = 0L,  // <--- NEW: for admin/sorting
    val feesPaid: Double = 0.0 // <-- NEW FIELD

)

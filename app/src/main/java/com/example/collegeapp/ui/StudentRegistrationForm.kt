package com.example.collegeapp.ui

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.collegeapp.model.StudentProfile
import java.util.*
import com.google.firebase.messaging.FirebaseMessaging
import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson


fun getFcmToken(onToken: (String) -> Unit) {
    FirebaseMessaging.getInstance().token
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onToken(task.result)
            } else {
                onToken("") // fallback if token fetch fails
            }
        }
}
@Composable
fun StudentRegistrationForm(
    onRegister: (StudentProfile, String) -> Unit
) {
    // State variables
    var name by remember { mutableStateOf("") }
    var fatherName by remember { mutableStateOf("") }
    var motherName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var aadhaar by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isWaiting by remember { mutableStateOf(false) } // <-- Add this
    var selectedClass by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val classOptions = listOf("BA", "BSc", "BEd", "DElEd")
    var rollNumber by remember { mutableStateOf("") }

    // Validation logic
    val isPincodeValid = pincode.length == 6 && pincode.all { it.isDigit() }
    val isAadhaarValid = aadhaar.length == 12 && aadhaar.all { it.isDigit() }
    val isMobileValid = mobile.length == 10 && mobile.all { it.isDigit() }
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordValid = password.length >= 6
    val isDobValid = dob.matches(Regex("""\d{2}/\d{2}/\d{4}""")) // dd/mm/yyyy

    val isValid = name.isNotBlank() && fatherName.isNotBlank() && motherName.isNotBlank() &&
            address.isNotBlank() && isPincodeValid && isAadhaarValid &&
            isDobValid && isMobileValid && isEmailValid && isPasswordValid && selectedClass.isNotBlank() && rollNumber.isNotBlank()

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Date Picker
    fun openDatePicker() {
        val cal = Calendar.getInstance()
        val picker = DatePickerDialog(
            context,
            { _, y, m, d -> dob = "%02d/%02d/%04d".format(d, m + 1, y) },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )
        picker.show()
    }

    if (isWaiting) {
        // ----------------- WAITING SCREEN -----------------
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFe7eaf6), Color.White)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text(
                    "Registration submitted!\nWaiting for admin approval...",
                    color = Color(0xFF333366)
                )
            }
        }
    } else {
        // ----------------- REGISTRATION FORM -----------------
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFe7eaf6), Color.White)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Card(
                elevation = 10.dp,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(horizontal = 18.dp, vertical = 16.dp)
                    .fillMaxWidth(0.98f)
                    .wrapContentHeight()
            ) {
                Column(
                    Modifier
                        .padding(22.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "New Student Registration",
                        style = MaterialTheme.typography.h6,
                        color = Color(0xFF333366),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Spacer(Modifier.height(2.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it.trimStart() },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = fatherName,
                        onValueChange = { fatherName = it.trimStart() },
                        label = { Text("Father's Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = motherName,
                        onValueChange = { motherName = it.trimStart() },
                        label = { Text("Mother's Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it.trimStart() },
                        label = { Text("Address") },
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = pincode,
                        onValueChange = { if (it.length <= 6) pincode = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Pincode (6 digits)") },
                        isError = pincode.isNotEmpty() && !isPincodeValid,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    AnimatedVisibility(visible = pincode.isNotEmpty() && !isPincodeValid) {
                        Text("Enter a valid 6-digit pincode", color = MaterialTheme.colors.error, style = MaterialTheme.typography.caption)
                    }
                    OutlinedTextField(
                        value = aadhaar,
                        onValueChange = { if (it.length <= 12) aadhaar = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Aadhaar Number (12 digits)") },
                        isError = aadhaar.isNotEmpty() && !isAadhaarValid,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    AnimatedVisibility(visible = aadhaar.isNotEmpty() && !isAadhaarValid) {
                        Text("Enter a valid 12-digit Aadhaar", color = MaterialTheme.colors.error, style = MaterialTheme.typography.caption)
                    }
                    OutlinedTextField(
                        value = rollNumber,
                        onValueChange = { rollNumber = it.trimStart() },
                        label = { Text("Roll Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Box(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        OutlinedTextField(
                            value = selectedClass,
                            onValueChange = { },
                            label = { Text("Class") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(Icons.Filled.Visibility, contentDescription = "Select Class")
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            classOptions.forEach { option ->
                                DropdownMenuItem(onClick = {
                                    selectedClass = option
                                    expanded = false
                                }) {
                                    Text(option)
                                }
                            }
                        }
                    }

                    // Date of Birth with picker
                    OutlinedTextField(
                        value = dob,
                        onValueChange = { dob = it.take(10) },
                        label = { Text("Date of Birth (dd/mm/yyyy)") },
                        trailingIcon = {
                            IconButton(onClick = { openDatePicker() }) {
                                Icon(Icons.Filled.Visibility, contentDescription = "Pick Date")
                            }
                        },
                        isError = dob.isNotEmpty() && !isDobValid,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    AnimatedVisibility(visible = dob.isNotEmpty() && !isDobValid) {
                        Text("Use format dd/mm/yyyy", color = MaterialTheme.colors.error, style = MaterialTheme.typography.caption)
                    }
                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { if (it.length <= 10) mobile = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Mobile Number (10 digits)") },
                        isError = mobile.isNotEmpty() && !isMobileValid,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    AnimatedVisibility(visible = mobile.isNotEmpty() && !isMobileValid) {
                        Text("Enter a valid 10-digit mobile number", color = MaterialTheme.colors.error, style = MaterialTheme.typography.caption)
                    }
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it.trimStart() },
                        label = { Text("Email") },
                        isError = email.isNotEmpty() && !isEmailValid,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    AnimatedVisibility(visible = email.isNotEmpty() && !isEmailValid) {
                        Text("Enter a valid email", color = MaterialTheme.colors.error, style = MaterialTheme.typography.caption)
                    }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password (min 6 chars)") },
                        isError = password.isNotEmpty() && !isPasswordValid,
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    AnimatedVisibility(visible = password.isNotEmpty() && !isPasswordValid) {
                        Text("Password must be at least 6 characters", color = MaterialTheme.colors.error, style = MaterialTheme.typography.caption)
                    }
                    Spacer(modifier = Modifier.height(22.dp))
                    Button(
                        onClick = {
                            getFcmToken { token ->
                                val studentprofile = StudentProfile(
                                    name = name,
                                    fatherName = fatherName,
                                    motherName = motherName,
                                    address = address,
                                    pincode = pincode,
                                    aadhaar = aadhaar,
                                    dob = dob,
                                    mobile = mobile,
                                    email = email,
                                    clas = selectedClass,
                                    status = "pending",
                                    fcmToken = token, // <--- Add this!
                                    rollNumber = rollNumber
                                )

                                // Call your registration cloud function
                                onRegister(studentprofile, password)

                                // Save locally for profile screen
                                val prefs = context.getSharedPreferences("student_prefs", Context.MODE_PRIVATE)
                                val studentJson = Gson().toJson(studentprofile)
                                prefs.edit { putString("profile_info", studentJson) }
                                prefs.edit { putString("registered_email", email) }
                                prefs.edit { putBoolean("waiting_for_approval", true) }

                                isWaiting = true
                            }
                        },
                        enabled = isValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF6658D3),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Register", style = MaterialTheme.typography.button)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

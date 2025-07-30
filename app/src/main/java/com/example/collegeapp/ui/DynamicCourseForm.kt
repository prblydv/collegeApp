package com.example.collegeapp.ui
import com.google.firebase.messaging.FirebaseMessaging

import android.app.DatePickerDialog
import android.net.Uri
import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar

@Composable
fun DynamicCourseForm(
    course: String,
    onSubmit: () -> Unit
) {
    val context = LocalContext.current
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Personal info state
    val selectedSubjects = remember { mutableStateListOf<String>() }

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
    var religion by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var isSubmitting by remember { mutableStateOf(false) }
    var submitMessage by remember { mutableStateOf<String?>(null) }
    var caste by remember { mutableStateOf("") }
    var submittedSuccessfully by remember { mutableStateOf(false) }
    var isEmailVerified by remember { mutableStateOf(false) }
    var verifiedEmail by remember { mutableStateOf("") }
//    EmailVerificationBlock(onEmailVerified = {
//        isEmailVerified = true
//        verifiedEmail = it
//    })

    // Date picker
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            dob = "%02d-%02d-%04d".format(dayOfMonth, month + 1, year)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Document URIs
    var highSchoolUri by remember { mutableStateOf<Uri?>(null) }
    var intermediateUri by remember { mutableStateOf<Uri?>(null) }
    var aadhaarUri by remember { mutableStateOf<Uri?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var signUri by remember { mutableStateOf<Uri?>(null) }
    var casteUri by remember { mutableStateOf<Uri?>(null) }
    var domicileUri by remember { mutableStateOf<Uri?>(null) }
    var incomeUri by remember { mutableStateOf<Uri?>(null) }
    var transferCertUri by remember { mutableStateOf<Uri?>(null) }
    var characterCertUri by remember { mutableStateOf<Uri?>(null) }
    var migrationCertUri by remember { mutableStateOf<Uri?>(null) }
    fun getSubjectsList(): List<String> = selectedSubjects.toList()
    var paymentAmount by remember { mutableStateOf("0") }   // <-- ADD HERE
    var paymentProofUri by remember { mutableStateOf<Uri?>(null) }   // NEW

    suspend fun submitAdmission() {
        isSubmitting = true
        submitMessage = null
        try {
            // 1. Collect document URIs
            val documentUris = mapOf(
                "highSchoolMarksheet" to highSchoolUri,
                "intermediateMarksheet" to intermediateUri,
                "aadhaarCard" to aadhaarUri,
                "photo" to photoUri,
                "signature" to signUri,
                "casteCertificate" to casteUri,
                "domicileCertificate" to domicileUri,
                "incomeCertificate" to incomeUri,
                "transferCertificate" to transferCertUri,
                "characterCertificate" to characterCertUri,
                "migrationCertificate" to migrationCertUri,
                "paymentProof"          to paymentProofUri

            )

            // 2. Upload each doc and get download URL
            val docUrls = mutableMapOf<String, String>()
            for ((docName, uri) in documentUris) {
                if (uri != null) {
                    val ext = getFileExtension(context, uri)
                    val url = uploadDocumentViaFunction(context, uri, ext)
                    if (url != null) docUrls[docName] = url
                    else throw Exception("Failed to upload $docName")
                }
            }

            // 3. Get FCM token (see below for how to fetch)
//            val fcmToken = "" // <- Replace with real token if needed
            val fcmToken = FirebaseMessaging.getInstance().token.await()
            val amountPaid = paymentAmount.toDoubleOrNull() ?: 0.0

            // 4. Build all fields
            val fields = hashMapOf(
                "name" to name,
                "fatherName" to fatherName,
                "motherName" to motherName,
                "address" to address,
                "pincode" to pincode,
                "aadhaar" to aadhaar,
                "feesPaid" to amountPaid, // Add this!

                "dob" to dob,
                "mobile" to mobile,
                "email" to verifiedEmail,
                "password" to password,
                "religion" to religion,
                "caste" to caste,
                "clas" to course,
                "subjects" to getSubjectsList(),
                "docUrls" to docUrls,
                "fcmToken" to fcmToken
            )


            // 5. Call your cloud function
            val functions = FirebaseFunctions.getInstance()
            val result = functions
                .getHttpsCallable("registerNewAdmission")
                .call(fields)
                .await()

            submitMessage = "Application submitted! Wait for admin approval."
            submittedSuccessfully = true

        } catch (e: Exception) {
            submitMessage = "Error: ${e.message}"
        }
        isSubmitting = false
    }

    // Subjects
    val subjects = when (course) {
        "BA" -> listOf(
            "Hindi Literature","Hindi General","English Language","English General",
            "Sanskrit","Sociology","Geography","Home Science","Education"
        )
        "BSc" -> listOf("Zoology","Botany","Chemistry","Physics","Maths")
        "B.Ed" -> listOf(
            "English","Hindi","Urdu","Sanskrit","Commerce",
            "Physics+Chemistry","Zoology+Botany","Home Science",
            "Civics+History","Economics+Geography"
        )
        else -> emptyList()
    }
    val maxSubjects = when (course) {
        "BA" -> 4
        "BSc" -> 3
        "B.Ed" -> 2
        else -> 0
    }
    val compulsory = if (course == "B.Ed") listOf(
        "Childhood and Growing Up","Contemporary India and Education",
        "Learning and Teaching","Language Across Curriculum",
        "Understanding Disciplines and School Subjects",
        "Art and Aesthetics","Critical Understanding of ICT"
    ) else emptyList()
    if (!submittedSuccessfully) {
        // --- PLACE ALL YOUR EXISTING FORM UI HERE (current Column and everything in it) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // ... ALL your fields, pickers, uploaders, and button

            Text("Personal Information", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            // Name, Father, Mother, Address
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Name") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = fatherName, onValueChange = { fatherName = it },
                label = { Text("Father's Name") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = motherName, onValueChange = { motherName = it },
                label = { Text("Mother's Name") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = address, onValueChange = { address = it },
                label = { Text("Address") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))

            // Pincode (6 digits)
            OutlinedTextField(
                value = pincode,
                onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) pincode = it },
                label = { Text("Pincode (6 digits)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(4.dp))

            // Aadhaar (12 digits)
            OutlinedTextField(
                value = aadhaar,
                onValueChange = { if (it.length <= 12 && it.all(Char::isDigit)) aadhaar = it },
                label = { Text("Aadhaar (12 digits)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(4.dp))

            // DOB picker
            OutlinedTextField(
                value = dob,
                onValueChange = {},
                readOnly = true,
                label = { Text("Date of Birth") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                    }
                }
            )
            Spacer(Modifier.height(4.dp))

            // Mobile (10 digits)
            OutlinedTextField(
                value = mobile,
                onValueChange = { if (it.length <= 10 && it.all(Char::isDigit)) mobile = it },
                label = { Text("Mobile (10 digits)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(Modifier.height(4.dp))

            // Email validation
            var emailError by remember { mutableStateOf(false) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        if (isEmailVerified && it != verifiedEmail) {
                            isEmailVerified = false
                            verifiedEmail = ""
                        }
                        email = it; emailError = !Patterns.EMAIL_ADDRESS.matcher(it).matches()
                    },
                    label = { Text("Email") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = emailError,
                    enabled = !isEmailVerified,
                    trailingIcon = {
                        if (isEmailVerified) {
                            Icon(
                                imageVector = Icons.Default.Check, // Material Icon Check
                                contentDescription = "Verified",
                                tint = Color(0xFF388E3C)
                            )
                        }
                    }
                )
            }
            if (emailError) Text("Invalid email", color = MaterialTheme.colors.error)
            Spacer(Modifier.height(4.dp))

            if (email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches() && !isEmailVerified) {
                Text("Please verify your email to proceed.", color = Color.Red)
                EmailVerificationBlock(
                    email = email,
                    isEmailVerified = isEmailVerified,
                    onEmailVerified = {
                        isEmailVerified = true
                        verifiedEmail = it
                    }
                )
                Spacer(Modifier.height(8.dp))
            }

            // Password (6 alphanumeric)
            var passError by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = password,
                onValueChange = {
                    if (it.length <= 6) password = it
                    passError =
                        password.length != 6 || password.any { ch -> !ch.isLetterOrDigit() }
                },
                label = { Text("Password (6 chars)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                isError = passError
            )
            if (passError) Text(
                "Password must be 6 alphanumeric chars",
                color = MaterialTheme.colors.error
            )
            Spacer(Modifier.height(4.dp))

            // Religion dropdown
            var religionExpanded by remember { mutableStateOf(false) }
            val religions = listOf("Hindu", "Muslim", "Sikh", "Buddhist", "Christian")
            OutlinedTextField(
                value = religion,
                onValueChange = {},
                readOnly = true,
                label = { Text("Religion") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { religionExpanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                },
                isError = religion.isBlank()
            )
            DropdownMenu(
                expanded = religionExpanded,
                onDismissRequest = { religionExpanded = false }
            ) {
                religions.forEach {
                    DropdownMenuItem(onClick = {
                        religion = it
                        religionExpanded = false
                    }) { Text(it) }
                }
            }
            if (religion.isBlank()) Text("Select religion", color = MaterialTheme.colors.error)
            // Caste dropdown (MANDATORY)
            var casteExpanded by remember { mutableStateOf(false) }
            val casteOptions = listOf("OBC", "General", "Minority")
            LabelWithStar("Caste", true)
            OutlinedTextField(
                value = caste,
                onValueChange = {},
                readOnly = true,
                label = { Text("Caste") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { casteExpanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                },
                isError = caste.isBlank()
            )
            DropdownMenu(
                expanded = casteExpanded,
                onDismissRequest = { casteExpanded = false }
            ) {
                casteOptions.forEach {
                    DropdownMenuItem(onClick = {
                        caste = it
                        casteExpanded = false
                    }) { Text(it) }
                }
            }
            if (caste.isBlank()) Text("Select caste", color = MaterialTheme.colors.error)

            Spacer(Modifier.height(12.dp))
            Text("Upload Documents", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "Maximum allowed file size: 2 MB per document.",
                color = Color.Gray,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Always mandatory
            LabelWithStar("High School Marksheet", true)
            DocumentPicker(
                currentUri = highSchoolUri,
                onUriSelected = { highSchoolUri = it }
            )
            LabelWithStar("Intermediate Marksheet", true)
            DocumentPicker(
                currentUri = intermediateUri,
                onUriSelected = { intermediateUri = it }
            )
            LabelWithStar("Aadhaar Card", true)
            DocumentPicker(
                currentUri = aadhaarUri,
                onUriSelected = { aadhaarUri = it }
            )
            LabelWithStar("Photo", true)
            DocumentPicker(
                currentUri = photoUri,
                onUriSelected = { photoUri = it }
            )
            LabelWithStar("Signature", true)
            DocumentPicker(
                currentUri =signUri,
                onUriSelected = { signUri = it }
            )
            LabelWithStar("Caste Certificate", true)
            DocumentPicker(
                currentUri =casteUri,
                onUriSelected = { casteUri = it }
            )
            // Domicile: mandatory only for B.Ed and D.El.Ed
            LabelWithStar("Domicile Certificate", course == "B.Ed" || course == "D.El.Ed")
            DocumentPicker(
                currentUri =domicileUri,
                onUriSelected = { domicileUri = it }
            )
            // Income Certificate: never mandatory
            LabelWithStar("Income Certificate", false)
            DocumentPicker(
                currentUri =incomeUri,
                onUriSelected = { incomeUri = it }
            )
            // Transfer: not mandatory for BA/BSc
            LabelWithStar("Transfer Certificate", course == "D.El.Ed" || course == "B.Ed")
            DocumentPicker(
                currentUri =transferCertUri,
                onUriSelected = { transferCertUri = it }
            )
            // Character: mandatory for D.El.Ed and B.Ed
            LabelWithStar("Character Certificate", course == "D.El.Ed" || course == "B.Ed")
            DocumentPicker(
                currentUri =characterCertUri,
                onUriSelected = { characterCertUri = it }
            )
            // Migration: never mandatory
            LabelWithStar("Migration Certificate", false)
            DocumentPicker(
                currentUri =migrationCertUri,
                onUriSelected = { migrationCertUri = it }
            )
            Spacer(Modifier.height(12.dp))


            Spacer(Modifier.height(12.dp))
            Text("Select Subjects", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            subjects.forEach { subj ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = selectedSubjects.contains(subj),
                            onValueChange = { checked ->
                                if (checked && selectedSubjects.size < maxSubjects) selectedSubjects.add(
                                    subj
                                )
                                else if (!checked) selectedSubjects.remove(subj)
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedSubjects.contains(subj),
                        onCheckedChange = null
                    )
                    Text(subj)
                }
            }
            if (compulsory.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text("Compulsory Subjects:")
                compulsory.forEach { Text("• $it") }
            }

            Spacer(Modifier.height(12.dp))
            Text("Payment", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = paymentAmount,
                onValueChange = { if (it.all(Char::isDigit)) paymentAmount = it },
                label = { Text("Payment Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(4.dp))
            DocumentPicker(
                currentUri = paymentProofUri,
                onUriSelected = { paymentProofUri = it }          // CHANGED
            )

            Text("UPI ID: 8384843193@boi")

            Spacer(Modifier.height(16.dp))
            val requiredDocsFilled =
                highSchoolUri != null &&
                        intermediateUri != null &&
                        aadhaarUri != null &&
                        photoUri != null &&
                        signUri != null &&
                        casteUri != null &&
                        // Conditionally required:
                        (if (course == "B.Ed" || course == "D.El.Ed") domicileUri != null else true) &&
                        (if (course == "B.Ed" || course == "D.El.Ed") transferCertUri != null else true) &&
                        (if (course == "B.Ed" || course == "D.El.Ed") characterCertUri != null else true)
            Button(
                onClick = { showConfirmDialog = true },
                enabled = !isSubmitting &&
                        isEmailVerified && // <-- Require verification
                        listOf(
                            name,
                            fatherName,
                            motherName,
                            address,
                            pincode,
                            aadhaar,
                            dob,
                            mobile,
                            email,
                            password,
                            religion,
                            caste
                        ).all { it.isNotBlank() } &&
                        !emailError && !passError && pincode.length == 6 && aadhaar.length == 12 && mobile.length == 10 &&
                        isEmailVerified && requiredDocsFilled,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Submitting...")
                } else {
                    Text("Submit Application")
                }
            }
            if (showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    title = { Text("Confirm Submission") },
                    text = { Text("Please verify that all your details are correct before submitting your application.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showConfirmDialog = false
                                // Only now do the submit!
                                coroutineScope.launch { submitAdmission() }
                            }
                        ) { Text("Confirm") }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showConfirmDialog = false }
                        ) { Text("Review Again") }
                    }
                )
            }

            if (submitMessage != null) {
                Text(
                    submitMessage!!,
                    color = if (submitMessage!!.startsWith("Error")) Color.Red else Color.Green
                )
            }
        }
    }
    else {
        // --- SHOW CONFIRMATION PAGE HERE ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Thank you for your application!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50) // green
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "You will receive updates on this device about your application.\n\nPlease turn on your notifications to avoid missing any important messages.",
                fontSize = 16.sp
            )
        }


    }
}

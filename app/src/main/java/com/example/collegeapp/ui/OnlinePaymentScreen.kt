package com.example.collegeapp.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payment
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.collegeapp.model.PaymentModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
// --------- FIRESTORE SUBMISSION FUNCTION ----------
import com.google.firebase.functions.ktx.functions
import androidx.compose.runtime.key
import com.example.collegeapp.model.StudentProfile
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


import android.content.Context
import android.util.Base64
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import java.io.InputStream

fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        if (bytes != null) {
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } else null
    } catch (e: Exception) {
        null
    }
}



@Composable
fun OnlinePaymentScreen() {
    var studentProfile by remember { mutableStateOf<StudentProfile?>(null) }

    val currentUser = FirebaseAuth.getInstance().currentUser
    val isLoggedIn = currentUser != null
    val coroutineScope = rememberCoroutineScope()
    var showSuccess by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    LaunchedEffect(isLoggedIn, currentUser?.uid) {
        if (isLoggedIn && currentUser != null) {
            try {
                val doc = FirebaseFirestore.getInstance()
                    .collection("approved_students")
                    .document(currentUser.uid)
                    .get()
                    .await()
                studentProfile = doc.toObject(StudentProfile::class.java)
            } catch (e: Exception) {
                // Handle error or show a message
                studentProfile = null
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, top = 24.dp, start = 8.dp, end = 8.dp) // nice bottom pad for button
        ) {
            Icon(
                Icons.Filled.Payment,
                contentDescription = "Payment",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(50.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Pay your fees online easily and securely.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(24.dp))

            if (showSuccess) {
                Card(
                    backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Column(
                        Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = "Success",
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Payment submitted!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colors.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "We will notify you after verification.",
                            color = MaterialTheme.colors.onSurface
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { showSuccess = false }
                        ) {
                            Text("Make another payment")
                        }
                    }
                }
                return@Column
            }

            errorMsg?.let {
                Text(it, color = MaterialTheme.colors.error)
                Spacer(Modifier.height(12.dp))
            }

            if (isLoggedIn) {
                key(showSuccess) { // This will re-create the form when showSuccess changes!
                    LoggedInPaymentForm(
                        studentProfile = studentProfile,
                        isSubmitting = isSubmitting,
                        errorMsg = errorMsg,
                        onSubmit = { amount, proofUrl ->
                            coroutineScope.launch {
                                isSubmitting = true
                                errorMsg = null
                                val payment = PaymentModel(
                                    uid = currentUser?.uid,
                                    email = currentUser?.email,
                                    amountPaid = amount,
                                    paymentProofUrl = proofUrl,
                                    name = studentProfile?.name,
                                    clas = studentProfile?.clas,
                                    rollNumber = studentProfile?.rollNumber
                                    // add more fields from studentProfile if needed
                                )
                                val result = submitPaymentViaCloudFunction(payment)
                                if (result == null) {
                                    showSuccess = true
                                } else {
                                    errorMsg = result
                                }
                                isSubmitting = false
                            }
                        }
                    )
                }
            } else {
                key(showSuccess) {
                    NotLoggedInPaymentForm(
                        isSubmitting = isSubmitting,
                        errorMsg = errorMsg,
                        onSubmit = { name, clas, roll, email, amount, proofUrl ->
                            coroutineScope.launch {
                                isSubmitting = true
                                errorMsg = null
                                val payment = PaymentModel(
                                    name = name,
                                    clas = clas,
                                    rollNumber = roll,
                                    email = email,
                                    amountPaid = amount,
                                    paymentProofUrl = proofUrl,
                                )
                                val result = submitPaymentViaCloudFunction(payment)
                                if (result == null) {
                                    showSuccess = true
                                } else {
                                    errorMsg = result
                                }
                                isSubmitting = false
                            }
                        }
                    )
                }
            }
        }
    }
}

// --------- LOGGED-IN FORM ----------
@Composable
fun LoggedInPaymentForm(
    studentProfile: StudentProfile?,
    isSubmitting: Boolean,
    errorMsg: String?,
    onSubmit: (Double, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var proofUrl by remember { mutableStateOf("") }

    val isFormValid = amount.isNotBlank() && proofUrl.isNotBlank()
    studentProfile?.let {
        Text("Name: ${it.name}")
        Text("Class: ${it.clas}")
        Text("Roll Number: ${it.rollNumber}")
        Spacer(Modifier.height(16.dp))
    }
    Text("Send payment to:")
    Text("UPI ID: 8384843193@boi", fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(12.dp))

    Spacer(Modifier.height(24.dp))
    Text("Enter Amount Paid:")
    OutlinedTextField(
        value = amount,
        onValueChange = { amount = it },
        label = { Text("Amount (₹)") },
        enabled = !isSubmitting
    )
    Spacer(Modifier.height(16.dp))

    // Screenshot picker
    ScreenshotPicker(enabled = !isSubmitting) { url -> proofUrl = url }

    errorMsg?.let {
        Text(it, color = MaterialTheme.colors.error)
        Spacer(Modifier.height(8.dp))
    }

    if (isSubmitting) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
    }

    Spacer(Modifier.height(24.dp))
    Button(
        enabled = isFormValid && !isSubmitting,
        onClick = {
            onSubmit(amount.toDoubleOrNull() ?: 0.0, proofUrl)
        }
    ) {
        Text(if (isSubmitting) "Submitting..." else "Submit Payment")
    }
}


// --------- NOT LOGGED-IN FORM ----------

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotLoggedInPaymentForm(
    isSubmitting: Boolean,
    errorMsg: String?,
    onSubmit: (String, String, String, String, Double, String) -> Unit
) {
    val classOptions = listOf("BA", "BSc", "BEd", "DElEd")

    var name by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf("") }
    var roll by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var proofUrl by remember { mutableStateOf("") }

    // Error states
    var nameError by remember { mutableStateOf<String?>(null) }
    var classError by remember { mutableStateOf<String?>(null) }
    var rollError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }

    // Dropdown state
    var expanded by remember { mutableStateOf(false) }

    fun isValidName(name: String): Boolean =
        name.trim().matches(Regex("^[A-Za-z ]+$"))

    fun isValidRollNumber(roll: String): Boolean =
        roll.trim().matches(Regex("^\\d+$"))

    fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    fun isValidAmount(amount: String): Boolean =
        amount.trim().toDoubleOrNull()?.let { it > 0 } == true

    val isFormValid = name.isNotBlank() && isValidName(name) &&
            selectedClass.isNotBlank() &&
            roll.isNotBlank() && isValidRollNumber(roll) &&
            email.isNotBlank() && isValidEmail(email) &&
            amount.isNotBlank() && isValidAmount(amount) &&
            proofUrl.isNotBlank()

    Text("Enter your details to submit payment:")

    OutlinedTextField(
        value = name,
        onValueChange = {
            name = it.trimStart()
            nameError = if (name.isBlank() || isValidName(name)) null else "Only letters and spaces allowed"
        },
        label = { Text("Name") },
        enabled = !isSubmitting,
        isError = nameError != null,
        singleLine = true
    )
    nameError?.let { Text(it, color = MaterialTheme.colors.error, fontSize = 12.sp) }

    // CLASS DROPDOWN
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedClass,
            onValueChange = {},
            readOnly = true,
            label = { Text("Class") },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            enabled = !isSubmitting,
            isError = classError != null
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            classOptions.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        selectedClass = option
                        expanded = false
                        classError = null
                    }
                ) {
                    Text(option)
                }
            }
        }
    }
    classError?.let { Text(it, color = MaterialTheme.colors.error, fontSize = 12.sp) }

    OutlinedTextField(
        value = roll,
        onValueChange = {
            roll = it.trimStart()
            rollError = if (roll.isBlank() || isValidRollNumber(roll)) null else "Numbers only"
        },
        label = { Text("Roll Number") },
        enabled = !isSubmitting,
        isError = rollError != null,
        singleLine = true,
    )
    rollError?.let { Text(it, color = MaterialTheme.colors.error, fontSize = 12.sp) }

    OutlinedTextField(
        value = email,
        onValueChange = {
            email = it.trim()
            emailError = if (email.isBlank() || isValidEmail(email)) null else "Invalid email"
        },
        label = { Text("Email") },
        enabled = !isSubmitting,
        isError = emailError != null,
        singleLine = true,
    )
    emailError?.let { Text(it, color = MaterialTheme.colors.error, fontSize = 12.sp) }

    OutlinedTextField(
        value = amount,
        onValueChange = {
            amount = it.trim()
            amountError = if (amount.isBlank() || isValidAmount(amount)) null else "Enter a valid amount"
        },
        label = { Text("Amount (₹)") },
        enabled = !isSubmitting,
        isError = amountError != null,
        singleLine = true,
    )
    amountError?.let { Text(it, color = MaterialTheme.colors.error, fontSize = 12.sp) }
    Spacer(Modifier.height(16.dp))
    Text("Send payment to:")
    Text("UPI ID: 8384843193@boi", fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(12.dp))

    ScreenshotPicker(enabled = !isSubmitting) { url -> proofUrl = url }

    errorMsg?.let {
        Text(it, color = MaterialTheme.colors.error)
        Spacer(Modifier.height(8.dp))
    }

    if (isSubmitting) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
    }

    Spacer(Modifier.height(24.dp))
    Button(
        enabled = isFormValid && !isSubmitting,
        onClick = {
            onSubmit(
                name.trim(),
                selectedClass,
                roll.trim(),
                email.trim(),
                amount.trim().toDoubleOrNull() ?: 0.0,
                proofUrl.trim()
            )
        }
    ) {
        Text(if (isSubmitting) "Submitting..." else "Submit Payment")
    }
}


@Composable
fun ScreenshotPicker(
    enabled: Boolean = true,
    onImageUploaded: (String) -> Unit
) {
    var uploadStatus by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            uploadStatus = "Uploading..."
            val base64 = uriToBase64(context, uri)
            if (base64 == null) {
                uploadStatus = "Failed to read image"
                return@rememberLauncherForActivityResult
            }
            val fileType = "jpg" // (or get actual extension if you want)

            coroutineScope.launch {
                try {
                    val result = Firebase.functions
                        .getHttpsCallable("uploadPaymentScreenshot")
                        .call(mapOf("imageData" to base64, "fileType" to fileType))
                        .await()
                    val url = (result.data as Map<*, *>)["url"] as String
                    onImageUploaded(url)
                    uploadStatus = "Upload successful!"
                } catch (e: Exception) {
                    uploadStatus = "Upload failed: ${e.message}"
                }
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { launcher.launch("image/*") }, enabled = enabled) {
            Text("Upload ScreenShot of Payment")
        }
        uploadStatus?.let { Text(it) }
    }
}



suspend fun submitPaymentViaCloudFunction(payment: PaymentModel): String? {
    return try {
        val data = hashMapOf(
            "uid" to payment.uid,
            "name" to payment.name,
            "clas" to payment.clas,
            "rollNumber" to payment.rollNumber,
            "email" to payment.email,
            "amountPaid" to payment.amountPaid,
            "paymentProofUrl" to payment.paymentProofUrl
        )
        Firebase.functions
            .getHttpsCallable("submitPayment")
            .call(data)
            .await()
        null // success!
    } catch (e: Exception) {
        e.message ?: "Unknown error"
    }
}

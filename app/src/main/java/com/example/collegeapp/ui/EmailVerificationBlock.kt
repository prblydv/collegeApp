package com.example.collegeapp.ui

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
@Composable
fun EmailVerificationBlock(
    email: String,
    isEmailVerified: Boolean,
    onEmailVerified: (String) -> Unit
) {
    var verificationDocId by remember { mutableStateOf<String?>(null) }
    var localEmailVerified by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // --- Reset localEmailVerified if email or isEmailVerified changes ---
    LaunchedEffect(email, isEmailVerified) {
        localEmailVerified = false
        verificationDocId = null
    }

    // Show the Verify button only if not yet verified
    if (!isEmailVerified && !localEmailVerified) {
        /* ---------- VERIFY-EMAIL BUTTON WITH 60-SECOND COOL-DOWN ---------- */

        var coolDownLeft by remember { mutableStateOf(0) }     // seconds left
        val buttonEnabled = email.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                !isLoading && coolDownLeft == 0

        /* countdown every second */
        LaunchedEffect(coolDownLeft) {
            if (coolDownLeft > 0) {
                kotlinx.coroutines.delay(1_000)
                coolDownLeft--
            }
        }

        Button(
            onClick = {
                isLoading = true
                errorMsg  = null
                coroutineScope.launch {
                    try {
                        val result = FirebaseFunctions.getInstance()
                            .getHttpsCallable("sendEmailVerification")
                            .call(hashMapOf<String, Any>("email" to email))
                            .await()

                        /* start a 60-second cool-down after a successful send */
                        coolDownLeft = 30

                        @Suppress("UNCHECKED_CAST")
                        verificationDocId =
                            (result.data as Map<String, Any>)["docId"] as? String
                    } catch (e: Exception) {
                        errorMsg = "Failed to send verification: ${e.message}"
                    }
                    isLoading = false
                }
            },
            enabled = buttonEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            when {
                isLoading        -> CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                coolDownLeft > 0 -> Text("Resend in ${coolDownLeft}s")
                else             -> Text("Verify E-mail")
            }
        }

    }


    if (verificationDocId != null && !isEmailVerified && !localEmailVerified) {
        Text(
            "Verification email sent! Please check your inbox and click the link.",
            color = MaterialTheme.colors.primary
        )
        // Poll Firestore for 'verified: true'
        LaunchedEffect(verificationDocId) {
            val db = FirebaseFirestore.getInstance()
            while (!localEmailVerified) {
                try {
                    val snap = db.collection("email_verifications")
                        .document(verificationDocId!!)
                        .get()
                        .await()

                    if (snap.getBoolean("verified") == true) {
                        localEmailVerified = true
                        onEmailVerified(email)
                    }
                } catch (e: Exception) {                // 👈 swallow permission errors
                    errorMsg = "Could not check verification status: ${e.message}"
                    break                               // exit the loop gracefully
                }
                kotlinx.coroutines.delay(3000)
            }
        }
    }
    if (isEmailVerified || localEmailVerified) {
        Text(
            "Email verified! You can now continue registration.",
            color = Color(0xFF388E3C),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
    if (errorMsg != null) {
        Text(errorMsg!!, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
    }
}

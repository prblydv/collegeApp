
package com.example.collegeapp.ui

import android.app.DatePickerDialog
import android.net.Uri
import android.content.Context
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await



import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun uriTBase64(context: Context, uri: Uri): String {
    return withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: ByteArray(0)
        inputStream?.close()
        android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
    }
}

suspend fun uploadDocumentViaFunction(context: Context, uri: Uri, fileType: String): String? {
    val base64 = uriTBase64(context, uri)
    val functions = FirebaseFunctions.getInstance()
    val data = hashMapOf(
        "imageData" to base64,
        "fileType" to fileType
    )
    val result = functions
        .getHttpsCallable("uploadPaymentScreenshot")
        .call(data)
        .await()
    return (result.data as? Map<*, *>)?.get("url") as? String
}
fun getFileExtension(context: Context, uri: Uri): String {
    val contentResolver = context.contentResolver
    val type = contentResolver.getType(uri)
    return when {
        type?.contains("pdf") == true -> "pdf"
        type?.contains("png") == true -> "png"
        type?.contains("jpeg") == true -> "jpeg"
        type?.contains("jpg") == true -> "jpg"
        else -> "jpg"
    }
}

@Composable
fun LabelWithStar(text: String, required: Boolean) {
    Text(
        buildAnnotatedString {
            append(text)
            if (required) {
                withStyle(style = SpanStyle(Color.Red)) { append(" *") }
            }
        }
    )
}

@Composable
fun NewAdmissionScreen(
    onSubmit: () -> Unit = {}
) {
    var selectedCourse by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("New Admission", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(12.dp))

        Text("Select Course:")
        DropdownMenuSelector(
            options = listOf("BA", "BSc", "B.Ed", "D.El.Ed"),
            selectedOption = selectedCourse,
            onOptionSelected = { selectedCourse = it }
        )

        if (selectedCourse.isNotBlank()) {
            Spacer(Modifier.height(16.dp))
            DynamicCourseForm(
                course = selectedCourse,
                onSubmit = onSubmit
            )
        }
    }
}

@Composable
fun DropdownMenuSelector(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(opt)
                    expanded = false
                }) {
                    Text(opt)
                }
            }
        }
    }
}



@Composable
fun DocumentPicker(
    currentUri: Uri?,
    onUriSelected: (Uri?) -> Unit,
    maxFileSizeBytes: Long = 2 * 1024 * 1024,
    fileError: String? = null
) {
    val context = LocalContext.current
    var localFileError by remember { mutableStateOf<String?>(fileError) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            // Check file size before accepting
            val size = context.contentResolver.openFileDescriptor(uri, "r")?.statSize ?: -1
            if (size > maxFileSizeBytes) {
                localFileError = "File too large. Max allowed size is 2 MB."
                onUriSelected(null)
            } else {
                localFileError = null
                onUriSelected(uri)
            }
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.weight(1f))
        Button(onClick = { launcher.launch("*/*") }) {
            Text(if (currentUri == null) "Upload" else "Change")
        }
    }
    // Show max file size as helper text
    Text("Max file size: 2 MB", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
    // Show error message if any
    if (localFileError != null) {
        Text(localFileError!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp))
    }
}




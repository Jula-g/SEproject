package com.example.physioconsult.physiotherapist

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EnterCodeForm(onBack: () -> Unit, physiotherapistId: String) {
    val context = LocalContext.current
    var digitalCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showAssessmentPreview by remember { mutableStateOf(false) }
    var fetchedAngles by remember { mutableStateOf<Map<String, Map<String, String>>>(emptyMap()) }
    var fetchedLengths by remember { mutableStateOf<Map<String, Map<String, String>>>(emptyMap()) }
    var fetchedImages: MutableList<Bitmap?> by remember { mutableStateOf(mutableListOf()) }
    var isSubmitting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter Code",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = digitalCode,
            onValueChange = {
                digitalCode = it
                errorMessage = ""
            },
            label = { Text("Digital Code") },
            placeholder = { Text("Enter the digital code") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (digitalCode.isBlank()) {
                    errorMessage = "Code cannot be empty."
                } else {
                    isSubmitting = true
                    checkCode(digitalCode) { success, patientId, document, error ->
                        if (success && document != null && patientId != null) {
                            savePatientToPhysiotherapist(physiotherapistId, patientId, document) { saveSuccess, saveError ->
                                if (saveSuccess) {
                                    fetchAssessment(
                                        userId = patientId,
                                        documentId = document,
                                        context = context
                                    ) { fetchSuccess, frontMeasurements, backMeasurements, sideMeasurements, photos, fetchError ->
                                        isSubmitting = false
                                        if (fetchSuccess) {
                                            fetchedAngles = frontMeasurements
                                            fetchedLengths = backMeasurements + sideMeasurements
                                            fetchedImages = photos.toMutableList()
                                            showAssessmentPreview = true
                                        } else {
                                            errorMessage = fetchError ?: "Failed to fetch assessment."
                                        }
                                    }
                                } else {
                                    isSubmitting = false
                                    errorMessage = saveError ?: "Failed to save the patient."
                                }
                            }
                        } else {
                            isSubmitting = false
                            errorMessage = error ?: "Invalid code. Please try again."
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting
        ) {
            Text("Submit")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Back")
        }
    }

    if (showAssessmentPreview) {
        AssessmentPreview(
            onNextClick = { /* Handle next image */ },
            onPreviousClick = { /* Handle previous image */ },
            onCloseClick = {
                showAssessmentPreview = false
            },
            list = fetchedImages,
            angleResults = fetchedAngles,
            lengthResults = fetchedLengths,
            index = 0
        )
    }
}


private fun checkCode(
    code: String,
    onResult: (Boolean, String?, String?, String?) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val codeRef = db.collection("Codes").document(code)

    codeRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val patientId = document.getString("collection")
                val assessment = document.getString("document")
                if (patientId != null) {
                    onResult(true, patientId, assessment, null)
                } else {
                    onResult(false, null, null, "Invalid code data.")
                }
            } else {
                onResult(false, null, null, "Code does not exist.")
            }
        }
        .addOnFailureListener { exception ->
            Log.e("CheckCode", "Error retrieving code document: ${exception.message}")
            onResult(false, null, null, "Failed to validate the code.")
        }
}

private fun savePatientToPhysiotherapist(
    physiotherapistId: String,
    patientId: String,
    newAssessment: String,
    onResult: (Boolean, String?) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val patientListRef = db.collection("patient_list").document(physiotherapistId)

    patientListRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val patientsMap = document.get("patients") as? MutableMap<String, MutableList<String>> ?: mutableMapOf()

                if (patientsMap.containsKey(patientId)) {
                    // Check if the assessment already exists
                    if (patientsMap[patientId]?.contains(newAssessment) == true) {
                        Log.d("SavePatient", "Assessment already exists for patient $patientId.")
                        onResult(false, "Assessment already exists.")
                        return@addOnSuccessListener
                    } else {
                        patientsMap[patientId]?.add(newAssessment)
                    }
                } else {
                    patientsMap[patientId] = mutableListOf(newAssessment)
                }

                patientListRef.update("patients", patientsMap)
                    .addOnSuccessListener {
                        Log.d("SavePatient", "Updated patient $patientId successfully.")
                        onResult(true, null)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("SavePatient", "Error updating patient: ${exception.message}")
                        onResult(false, "Failed to update the patient list.")
                    }
            } else {
                val initialPatientsMap = mapOf(
                    patientId to mutableListOf(newAssessment)
                )

                patientListRef.set(mapOf("patients" to initialPatientsMap))
                    .addOnSuccessListener {
                        Log.d("SavePatient", "Created new patient list with $patientId.")
                        onResult(true, null)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("SavePatient", "Error creating patient list: ${exception.message}")
                        onResult(false, "Failed to create the patient list.")
                    }
            }
        }
        .addOnFailureListener { exception ->
            Log.e("SavePatient", "Error retrieving patient list: ${exception.message}")
            onResult(false, "Failed to retrieve the patient list.")
        }
}

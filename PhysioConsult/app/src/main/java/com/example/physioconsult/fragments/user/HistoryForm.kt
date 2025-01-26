package com.example.physioconsult.fragments.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HistoryForm(assessments: List<String>, isLoading: Boolean, errorMessage: String) {
    val color = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Assessment History",
            fontSize = 24.sp,
            color = color.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else if (assessments.isEmpty()) {
            Text("No assessments found.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(assessments.size) { index ->
                    AssessmentItem(assessments[index])
                }
            }
        }
    }
}

@Composable
fun AssessmentItem(assessmentName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Assessment: $assessmentName",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

/**
 * Fetches the assessment history for the given user ID.
 *
 * @param userId The ID of the user to fetch the assessment history for.
 * @param callback The callback to invoke with the list of assessment names and an error message if
 *                 an error occurred.
 */
fun fetchAssessmentHistory(userId: String, callback: (List<String>, String?) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection(userId)
        .get()
        .addOnSuccessListener { snapshot ->
            if (snapshot != null && !snapshot.isEmpty) {
                val assessments = snapshot.documents.map { document ->
                    document.id
                }
                callback(assessments, null)
            } else {
                callback(emptyList(), "No assessments found for user.")
            }
        }
        .addOnFailureListener { exception ->
            callback(emptyList(), "Failed to load assessment history: ${exception.message}")
        }
}


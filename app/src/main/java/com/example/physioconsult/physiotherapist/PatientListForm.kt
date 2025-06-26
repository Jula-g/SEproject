package com.example.physioconsult.physiotherapist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.physioconsult.user.fetchPatients

@Composable
fun PatientListForm(physiotherapistId: String, onBack: () -> Unit) {
    var patientsMap by remember { mutableStateOf<Map<String, Pair<String, List<String>>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        fetchPatients(physiotherapistId) { fetchedMap, error ->
            isLoading = false
            if (error != null) {
                errorMessage = error
            } else {
                patientsMap = fetchedMap
            }
        }
    }

    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Patient List",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(color = colors.primary)
        } else if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = colors.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else if (patientsMap.isEmpty()) {
            Text(
                text = "No patients found.",
                color = colors.onSurface
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                patientsMap.forEach { (patientId, nameAndAssessments) ->
                    item {
                        val (name, assessments) = nameAndAssessments
                        PatientItem(name, assessments)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = colors.secondary)
        ) {
            Text("Back", color = colors.onSecondary)
        }
    }
}

@Composable
fun PatientItem(name: String, assessments: List<String>) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Patient: $name",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            assessments.forEach { assessment ->
                Text(
                    text = "• $assessment",
                    style = TextStyle(fontSize = 16.sp, color = colors.onSurface),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

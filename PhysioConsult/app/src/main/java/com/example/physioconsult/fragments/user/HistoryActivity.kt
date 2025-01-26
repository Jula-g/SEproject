package com.example.physioconsult.fragments.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.physioconsult.ui.theme.PhysioConsultTheme
import com.google.firebase.auth.FirebaseAuth

class HistoryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PhysioConsultTheme {
                val auth = FirebaseAuth.getInstance()
                val userId = auth.currentUser?.uid
                val userAssessments = remember { mutableStateOf<List<String>>(emptyList()) }
                val isLoading = remember { mutableStateOf(true) }
                val errorMessage = remember { mutableStateOf("") }

                if (userId != null) {
                    LaunchedEffect(Unit) {
                        fetchAssessmentHistory(userId) { assessments, error ->
                            isLoading.value = false
                            if (error != null) {
                                errorMessage.value = error
                            } else {
                                userAssessments.value = assessments
                            }
                        }
                    }
                }

                HistoryForm(
                    assessments = userAssessments.value,
                    isLoading = isLoading.value,
                    errorMessage = errorMessage.value
                )
            }
        }
    }
}

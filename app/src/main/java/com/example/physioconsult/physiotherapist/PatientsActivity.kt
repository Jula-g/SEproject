package com.example.physioconsult.physiotherapist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.physioconsult.user.getUserRole
import com.example.physioconsult.ui.theme.PhysioConsultTheme
import com.google.firebase.auth.FirebaseAuth

/**
 * Activity that displays the list of patients for the physiotherapist.
 */

class PatientsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PhysioConsultTheme {
                val auth = FirebaseAuth.getInstance()
                val userId = auth.currentUser?.uid
                val currentScreen = remember { mutableStateOf("Loading") }
                val userRole = remember { mutableStateOf<String?>(null) }

                if (userId != null) {
                    LaunchedEffect(Unit) {
                        userRole.value = getUserRole(userId)
                        currentScreen.value = "PatientListForm"
                        }
                    }
            }
        }
    }
}

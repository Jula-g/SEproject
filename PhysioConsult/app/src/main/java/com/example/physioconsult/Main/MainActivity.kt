package com.example.physioconsult.Main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.physioconsult.getUserRole
import com.example.physioconsult.physiotherapist.PhysiotherapistForm
import com.example.physioconsult.ui.theme.PhysioConsultTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        setContent {
            PhysioConsultTheme {
                if (userId != null) {
                    val userRole = remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(Unit) {
                        getUserRole(userId) { role ->
                            userRole.value = role
                        }
                    }

                    when (userRole.value) {
                        "patient" -> MainActivityForm()
                        "physiotherapist" -> PhysiotherapistForm()
                        else -> {
                            // Handle case where user has no role
                        }
                    }
                } else {
                    // Handle case where user is not logged in
                    // Optionally navigate to a login screen
                }
            }
        }
    }
}

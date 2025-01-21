package com.example.physioconsult.Main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.physioconsult.physiotherapist.EnterCodeForm
import com.example.physioconsult.user.getUserRole
import com.example.physioconsult.physiotherapist.PhysiotherapistForm
import com.example.physioconsult.ui.theme.PhysioConsultTheme
import com.google.firebase.auth.FirebaseAuth

/**
 * The main activity of the application.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. This is where most initialization should go.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */

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
                        currentScreen.value = when (userRole.value) {
                            "patient" -> "MainActivityForm"
                            "physiotherapist" -> "PhysiotherapistForm"
                            else -> "MainActivityForm"
                        }
                    }

                    when (currentScreen.value) {
                        "MainActivityForm" -> MainActivityForm()
                        "PhysiotherapistForm" -> PhysiotherapistForm(
                            onNavigateToEnterCode = { currentScreen.value = "EnterCodeForm" }
                        )
                        "EnterCodeForm" -> EnterCodeForm(
                            onBack = { currentScreen.value = "PhysiotherapistForm" }
                        )
                    }
                } else {
                    MainActivityForm()
                }
            }
        }
    }
}

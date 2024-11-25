package com.example.physioconsult.login.LogIn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.physioconsult.ui.theme.PhysioConsultTheme

/**
 * Activity class representing the login screen.
 */
class LoginActivity : ComponentActivity() {

    /**
     * Called when the activity is starting. This is where most initialization should go.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     * this contains the data it most recently supplied in onSaveInstanceState. Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhysioConsultTheme{
               LogInForm()
        }
        }
    }
}
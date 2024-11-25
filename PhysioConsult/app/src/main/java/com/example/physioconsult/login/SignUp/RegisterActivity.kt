package com.example.cognitiveapp.Register

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.physioconsult.login.SignUp.RegisterForm
import com.example.physioconsult.ui.theme.PhysioConsultTheme
import com.google.firebase.FirebaseApp

class RegisterActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Set the content of the activity
        setContent {
            PhysioConsultTheme{
                RegisterForm()
            }
        }
    }
}
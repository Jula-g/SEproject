package com.example.physioconsult.physiotherapist

import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.PeopleOutline
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.physioconsult.fragments.therapist.ViewImage


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomNavPhysiotherapist() {
    val context = LocalContext.current
    Surface(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = Color(0xFF84ACD8)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxWidth()
        ) {
            NavigationBarItem(
                icon = { NavigationBarIcon(Icons.Default.Home, "Home Icon") },
                label = { Text("Home") },
                selected = true,
                onClick = { /* home */ }
            )
            NavigationBarItem(
                icon = { NavigationBarIcon(Icons.Default.Schedule, "Appointments") },
                label = { Text("Appointments") },
                selected = false,
                onClick = { /* schedule */ }
            )
            NavigationBarItem(
                icon = { NavigationBarIcon(Icons.Default.ContentPaste, "Assessments") },
                label = { Text("Assessments") },
                selected = false,
                onClick = { /* assessments */ }
            )
            NavigationBarItem(
                icon = { NavigationBarIcon(Icons.Default.PeopleOutline, "Patients") },
                label = { Text("Patients") },
                selected = false,
                onClick = { }
            )
        }
    }
}

@Composable
fun NavigationBarIcon(
    icon: ImageVector,
    contentDescription: String
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = Color.Black
    )
}

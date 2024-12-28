package com.example.physioconsult.Main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
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
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomNavigationBar() {
    Surface(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = Color(0xFF84ACD8)
    ) {
        NavigationBar(
            containerColor = Color.Transparent, // Set to transparent to inherit Surface color
            modifier = Modifier.fillMaxWidth()
        ) {
            NavigationBarItem(
                icon = { NavigationBarIcon(Icons.Default.Home, "Home Icon") },
                label = { Text("Home") },
                selected = true,
                onClick = { /* home */ }
            )
            NavigationBarItem(
                icon = { NavigationBarIcon(Icons.Default.Schedule, "Schedule Icon") },
                label = { Text("Schedule") },
                selected = false,
                onClick = { /* schedule */ }
            )
            NavigationBarItem(
                icon = { NavigationBarIcon(Icons.Default.Add, "Add Icon") },
                label = { Text("Add") },
                selected = false,
                onClick = { /* assessments */ }
            )
            NavigationBarItem(
                icon = { NavigationBarIcon(Icons.Default.History, "History Icon") },
                label = { Text("History") },
                selected = false,
                onClick = { /* history */ }
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
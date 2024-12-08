package com.example.physioconsult

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*

@Composable
fun MainActivityForm() {
    Scaffold(
        bottomBar = {
            BottomNavigationBar()
        }
    ) { innerPadding ->
        Content(Modifier.padding(innerPadding))
    }
}
@Composable
fun Content(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            //The patient here can be changed after problem with DB is sorted out
            Column {
                Text(
                    text = "Welcome",
                    fontSize = 28.sp,
                    color = Color.Black
                )
                Text(
                    text = "Patient",
                    fontSize = 34.sp,
                    color = Color.Black
                )
            }

            // Icon on the right side of the Row
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Patient Icon",
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = "Take Image:",
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { /* Take image action */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // SIZE
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Camera Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Capture Image")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Make Appointment:",
            fontSize = 18.sp

        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { /* Make appointment action */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Calendar Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Appointments")
            }
        }
    }
}


@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary
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
            onClick = { /* schedule if we actually manage (its additional) */ }
        )
        NavigationBarItem(
            icon = { NavigationBarIcon(Icons.Default.Add, "Add Icon") },
            label = { Text("Add") },
            selected = false,
            onClick = { /* For the assesments*/ }
        )
        NavigationBarItem(
            icon = { NavigationBarIcon(Icons.Default.History, "History Icon") },
            label = { Text("History") },
            selected = false,
            onClick = { /* history either assesments or everything I'm a little confused I guess */ }
        )
    }
}

@Composable
fun NavigationBarIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = Color.Black
    )
}

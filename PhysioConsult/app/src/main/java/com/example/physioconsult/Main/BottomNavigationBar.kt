package com.example.physioconsult.Main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Composable function that displays the bottom navigation bar.
 * It includes navigation items for Home, Schedule, Add, and History.
 *
 * The navigation bar has a rounded top shape and a custom background color.
 * Each navigation item has an icon and a label.
 *
 * @param navController The navigation controller to navigate between screens.
 */

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry)
    val selectedItem = currentRoute.value?.destination?.route ?: "home"

    Surface(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = Color(0xFF84ACD8)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxWidth()
        ) {
            NavigationBarItem(
                icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home Icon") },
                label = { Text("Home") },
                selected = selectedItem == "home",
                onClick = { navController.navigate("home") }
            )

            NavigationBarItem(
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add") },
                label = { Text("Add") },
                selected = selectedItem == "add",
                onClick = { navController.navigate("add") }
            )

            NavigationBarItem(
                icon = { Icon(imageVector = Icons.Default.History, contentDescription = "History") },
                label = { Text("History") },
                selected = selectedItem == "history",
                onClick = { navController.navigate("history") }
            )
        }
    }
}

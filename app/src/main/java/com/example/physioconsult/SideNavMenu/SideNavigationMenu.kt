package com.example.physioconsult.SideNavMenu

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.physioconsult.Main.MainActivity
import com.example.physioconsult.Main.navigateHistory
import com.example.physioconsult.Main.navigatePhoto
import com.example.physioconsult.fragments.user.HistoryActivity
import com.example.physioconsult.login.LogIn.LoginActivity
import com.example.physioconsult.user.fetchUserData
import com.google.firebase.auth.FirebaseAuth


/**
 * SideNavigationMenu composable function.
 * Displays a side navigation menu with user information and menu items.
 */

@Composable
fun SideNavigationMenu(onCloseDrawer: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val context = LocalContext.current
    val name = remember { mutableStateOf("Name") }
    val surname = remember { mutableStateOf("Surname") }

    LaunchedEffect(userId) {
        if (userId != null) {
            val userData = fetchUserData(userId)
            name.value = userData["name"] ?: "Name"
            surname.value = userData["surname"] ?: "Surname"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(240.dp)
            .background(Color(0xFF84ACD8)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // User info section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User Icon",
                tint = Color.White,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Gray, shape = RoundedCornerShape(24.dp))
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${name.value} ${surname.value}",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menu items
        MenuItem(
            icon = Icons.Default.Home,
            label = "Home",
            onClick = {
                context.startActivity(Intent(context, MainActivity::class.java))
                onCloseDrawer()
            }
        )
        MenuItem(
            icon = Icons.Default.Add,
            label = "Add Assessment",
            onClick = {
                navigatePhoto(context)
                onCloseDrawer()
            }
        )
        MenuItem(
            icon = Icons.Default.History,
            label = "History",
            onClick = {
                val intent = Intent(context, HistoryActivity::class.java)
                context.startActivity(intent)
                onCloseDrawer()
            }
        )
        MenuItem(
            icon = Icons.Default.Settings,
            label = "Settings",
            onClick = {
                onCloseDrawer()
            }
        )
        MenuItem(
            icon = Icons.Default.Person,
            label = "Log out",
            onClick = {
                auth.signOut()
                context.startActivity(Intent(context, LoginActivity::class.java))
                onCloseDrawer()
            }
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}


/**
 * MenuItem composable function.
 * Displays a menu item with an icon and label.
 *
 * @param icon The icon to display.
 * @param label The label to display.
 * @param onClick The action to perform when the item is clicked.
 */

@Composable
fun MenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

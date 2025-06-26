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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.Person
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
import androidx.navigation.NavHostController
import com.example.physioconsult.Main.MainActivity
import com.example.physioconsult.login.LogIn.LoginActivity
import com.example.physioconsult.user.fetchUserData
import com.google.firebase.auth.FirebaseAuth

/**
 * Composable function that displays the side navigation menu for the physiotherapist.
 * It includes user information and navigation items.
 */
@Composable
fun SideNavPhysiotherapist(navController: NavHostController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
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

        MenuPhysioItem(
            icon = Icons.Default.Home,
            label = "Home",
            onClick = {
                navController.navigate("home")
            }
        )

        MenuPhysioItem(
            icon = Icons.Default.PeopleOutline,
            label = "Patients",
            onClick = { navController.navigate("patients") }
        )
        MenuPhysioItem(
            icon = Icons.Default.Settings,
            label = "Settings",
            onClick = { navController.navigate("settings") }
        )
        MenuPhysioItem(
            icon = Icons.Default.Person,
            label = "Logout",
            onClick = {
                auth.signOut()
                context.startActivity(Intent(context, LoginActivity::class.java))
            }
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

/**
 * Composable function that represents a menu item in the side navigation menu.
 *
 * @param icon The icon to display for the menu item.
 * @param label The label to display for the menu item.
 * @param onClick The action to perform when the menu item is clicked.
 */

@Composable
fun MenuPhysioItem(
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

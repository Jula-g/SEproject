package com.example.physioconsult.Main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.physioconsult.R
import com.example.physioconsult.SideNavMenu.SideNavigationMenu
import com.example.physioconsult.fragments.user.add.Add
import com.example.physioconsult.login.LogIn.Credentials
import com.example.physioconsult.ui.theme.PhysioConsultTheme
import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityForm() {
    // State for the drawer to open or close it
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope() // Required to control drawer state

    // ModalNavigationDrawer wraps the content and provides a side navigation bar
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { SideNavigationMenu() } // Add the side navigation content here
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(75.dp)
                        ) {
                            Text("PhysioConsult", color = Color(0xFF84ACD8))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu Icon",
                                tint = Color(0xFF84ACD8)
                            )
                        }
                    },
                )
            },
            bottomBar = {
                BottomNavigationBar()
            }
        )
        { innerPadding ->
            Content(Modifier.padding(innerPadding)) // Your main content
        }
    }
}


@Composable
fun Content(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with greeting and avatar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Hello!",
                    fontSize = 28.sp,
                    color = Color.Black
                )
                Text(
                    text = "User (not done)",
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(72.dp)
                    .background(Color.LightGray, RoundedCornerShape(36.dp))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Introduction/Description Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = "Welcome!",
                fontSize = 24.sp,
                color = Color.Black,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Physio Consult is an app that speeds up process of physical body measurements, and assessments of it measurement.",
                fontSize = 14.sp,
                color = Color.Black,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

        }
        val imageCamera: Painter =
            painterResource(id = R.drawable.focus)

        // Take Image Section
        CardButtonCamera(
            text = "Take Assessment",
            title = "Assessment",
            description = "By creating a new Assessment you can measure your posture...",
            icon = imageCamera,
            backgroundColor = Color(0xFF84ACD8), // Light Blue
            onClick = { navigatePhoto(context) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        val imageChart: Painter =
            painterResource(id = R.drawable.medical)
        // Appointments Section
        CardButtonAppointments(
            text = "Last Assessment",
            title = "Appointments",
            description = "View your upcoming consultations.",
            icon = imageChart,
            backgroundColor = Color(0xFF84ACD8) // Light Green
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LogInFormPreviewDark() {
    PhysioConsultTheme {
        MainActivityForm()
    }
}

fun navigatePhoto(context: Context) {
    context.startActivity(Intent(context, Add::class.java))
}
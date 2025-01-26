package com.example.physioconsult.physiotherapist

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.physioconsult.SideNavMenu.SideNavPhysiotherapist
import com.example.physioconsult.login.LogIn.LoginActivity
import com.example.physioconsult.user.fetchUserData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/**
 * Composable function that displays the form for the physiotherapist.
 * It includes a top app bar, a bottom navigation bar, and a scrollable content area
 * with user information and action buttons.
 */

// PhysiotherapistForm.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhysiotherapistForm(onNavigateToEnterCode: () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val name = remember { mutableStateOf("Name") }
    val surname = remember { mutableStateOf("Surname") }
    val context = LocalContext.current
    val navController = rememberNavController()

    LaunchedEffect(userId) {
        if (userId != null) {
            val userData = fetchUserData(userId)
            name.value = userData["name"] ?: "Name"
            surname.value = userData["surname"] ?: "Surname"
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideNavPhysiotherapist(
                navController = navController
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(75.dp)
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
                BottomNavPhysiotherapist()
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                                    color = colors.onSurface
                                )
                                Text(
                                    text = "Dr ${name.value} ${surname.value}",
                                    fontSize = 20.sp,
                                    color = colors.onSurface
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

                        Text(
                            text = "Welcome!",
                            fontSize = 24.sp,
                            color = colors.onSurface,
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Text(
                            text = "Physio Consult is an app that speeds up the process of physical body measurements, and assessments of it measurement.",
                            fontSize = 14.sp,
                            color = colors.onSurface,
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        CardButton(
                            text = "Retrieve Assessments",
                            title = "Assessments",
                            description = "Get an assessment from the patient",
                            icon = Icons.Default.ContentPaste,
                            backgroundColor = Color(0xFF84ACD8),
                            iconTint = Color.White,
                            onClick = { onNavigateToEnterCode() }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CardButton(
                            text = "View Patients",
                            title = "Patients",
                            description = "View the list of all your patients along with their assessments",
                            icon = Icons.Default.PeopleOutline,
                            backgroundColor = Color(0xFF84ACD8),
                            iconTint = Color.White,
                            onClick = { navController.navigate("patients") }
                        )
                    }
                }
                composable("patients") {
                    PatientListForm(physiotherapistId = userId ?: "", onBack = { navController.popBackStack() })
                }
                composable("settings") { /* Settings screen */ }
                composable("logout") {
                    auth.signOut()
                    context.startActivity(Intent(context, LoginActivity::class.java))
                }
            }
        }
    }
}
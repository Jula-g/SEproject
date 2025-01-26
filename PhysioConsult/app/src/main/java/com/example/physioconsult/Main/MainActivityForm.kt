package com.example.physioconsult.Main

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.physioconsult.R
import com.example.physioconsult.SideNavMenu.SideNavigationMenu
import com.example.physioconsult.fragments.user.HistoryActivity
import com.example.physioconsult.user.fetchUserData
import com.example.physioconsult.fragments.user.add.Add
import com.example.physioconsult.ui.theme.PhysioConsultTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/**
 * Main activity form composable function.
 * Displays the main UI for the activity, including a top app bar, bottom navigation bar, and content.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityForm() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideNavigationMenu(onCloseDrawer = { coroutineScope.launch { drawerState.close() } })
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("PhysioConsult", color = Color(0xFF84ACD8)) },
                    navigationIcon = {
                        if (currentRoute != "add") {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu Icon",
                                    tint = Color(0xFF84ACD8)
                                )
                            }
                        }
                    },
                )
            },
            bottomBar = {
                if (currentRoute != "add") {
                    BottomNavigationBar(navController = navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") { Content(navController = navController) }
                composable("add") { Add() }
                composable("history") { HistoryActivity() }
            }
        }
    }
}

/**
 * Content composable function.
 * Displays the main content of the activity, including user information and action buttons.
 *
 * @param modifier Modifier to be applied to the content layout.
 */
@Composable
fun Content(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
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
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
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
                    text = "${name.value} ${surname.value}",
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = "Welcome!",
                fontSize = 24.sp,
                color = colors.onSurface,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Physio Consult is an app that speeds up the process of physical body measurements, and assessments of it measurement.",
                fontSize = 14.sp,
                color = colors.onSurface,
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

        CardButtonCamera(
            text = "Take Assessment",
            title = "Assessment",
            description = "By creating a new Assessment you can measure your posture...",
            icon = imageCamera,
            backgroundColor = Color(0xFF84ACD8),
            onClick = { navigatePhoto(context) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        val imageChart: Painter =
            painterResource(id = R.drawable.medical)
        CardButtonAssessments(
            text = "Last Assessment",
            title = "Assessments",
            description = "View your previous assessments.",
            icon = imageChart,
            backgroundColor = Color(0xFF84ACD8),
            onClick = { navigateHistory(context) }
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

/**
 * Navigates to the photo activity.
 *
 * @param context The context from which the navigation is initiated.
 */
fun navigatePhoto(context: Context) {
    context.startActivity(Intent(context, Add::class.java))
}

/**
 * Navigates to the history activity.
 *
 * @param context The context from which the navigation is initiated.
 */

fun navigateHistory(context: Context) {
    context.startActivity(Intent(context, HistoryActivity::class.java))
}

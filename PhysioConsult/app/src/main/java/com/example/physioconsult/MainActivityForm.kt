package com.example.physioconsult

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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.physioconsult.ui.theme.PhysioConsultTheme
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
                    title = { Text("", color = Color.White) },
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
        ) { innerPadding ->
            Content(Modifier.padding(innerPadding)) // Your main content
        }
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
                    text = "User Smith",
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
                text = "Ipsum Lorem",
                fontSize = 24.sp,
                color = Color.Black,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
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
            backgroundColor = Color(0xFF84ACD8) // Light Blue
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

@Composable
fun CardButtonCamera(
    text: String,
    title: String,
    description: String,
    icon: Painter,
    backgroundColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) { // Wrap everything in a Column
        // "Take Assessment" text aligned to the left
        Text(
            text = text,
            fontSize = 24.sp,
            color = Color.Black,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth() // Makes the text span full width
                .padding(start = 8.dp, bottom = 8.dp) // Adds some padding to the text
        )

        // The Card below the text
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp // Shadow size
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Image(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(85.dp)
                        .padding(bottom = 5.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(text = title, fontSize = 18.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = description, fontSize = 14.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CardButtonAppointments(
    text: String,
    title: String,
    description: String,
    icon: Painter,
    backgroundColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) { // Wrap everything in a Column
        // "Take Assessment" text aligned to the left
        Text(
            text = text,
            fontSize = 24.sp,
            color = Color.Black,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth() // Makes the text span full width
                .padding(start = 8.dp, bottom = 8.dp) // Adds some padding to the text
        )

        // The Card below the text with shadow
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp // Shadow size
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Image(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(85.dp)
                        .padding(bottom = 5.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(text = title, fontSize = 18.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = description, fontSize = 14.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = Color.Black
    )
}

@Composable
fun SideNavigationMenu() {
    // Side Navigation Drawer Content
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(240.dp) // Width of the drawer
            .background(Color(0xFF84ACD8)), // Background color of the drawer
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Header Section
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
                text = "User Smith",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menu Items
        MenuItem(icon = Icons.Default.Home, label = "Home", onClick = { /* Home action */ })
        MenuItem(
            icon = Icons.Default.Schedule,
            label = "Schedule",
            onClick = { /* Schedule action */ })
        MenuItem(icon = Icons.Default.Add, label = "Add", onClick = { /* Add action */ })
        MenuItem(
            icon = Icons.Default.History,
            label = "History",
            onClick = { /* History action */ })

        Spacer(modifier = Modifier.weight(1f)) // Push remaining content to the bottom

        // Footer (optional)
        Text(
            text = "Version 1.0",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun MenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LogInFormPreviewDark() {
    PhysioConsultTheme {
        MainActivityForm()
    }
}

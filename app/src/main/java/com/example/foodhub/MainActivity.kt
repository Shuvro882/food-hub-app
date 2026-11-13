package com.example.foodhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodhub.data.FoodRepository
import com.example.foodhub.ui.activity.MyActivityScreen
import com.example.foodhub.ui.auth.AuthScreen
import com.example.foodhub.ui.donate.DonateFoodDialog
import com.example.foodhub.ui.home.HomeScreen
import com.example.foodhub.ui.profile.ProfileScreen
import com.example.foodhub.ui.theme.FoodHubTheme
import com.example.foodhub.ui.theme.GreenPrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FoodRepository.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            FoodHubTheme {
                MainAppScreen()
            }
        }
    }
}

sealed class BottomNavItem(
    val route: String,
    val titleBn: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "খাবার খুঁজুন", Icons.Default.RestaurantMenu)
    object Activity : BottomNavItem("activity", "আমার ইতিহাস", Icons.Default.VolunteerActivism)
    object Profile : BottomNavItem("profile", "প্রোফাইল", Icons.Default.Person)
}

@Composable
fun MainAppScreen() {
    var isAuthenticated by remember { mutableStateOf(FoodRepository.isLoggedIn) }
    var currentTab by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }
    var showDonateDialog by remember { mutableStateOf(false) }

    if (!isAuthenticated) {
        AuthScreen(
            onAuthSuccess = {
                isAuthenticated = true
            }
        )
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val navItems = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Activity,
                        BottomNavItem.Profile
                    )

                    navItems.forEach { item ->
                        val selected = currentTab == item
                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentTab = item },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.titleBn,
                                    tint = if (selected) GreenPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            },
                            label = {
                                Text(
                                    text = item.titleBn,
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) GreenPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    BottomNavItem.Home -> HomeScreen(
                        onOpenDonateDialog = { showDonateDialog = true }
                    )
                    BottomNavItem.Activity -> MyActivityScreen(
                        onOpenDonateDialog = { showDonateDialog = true }
                    )
                    BottomNavItem.Profile -> ProfileScreen(
                        onLogout = {
                            isAuthenticated = false
                        }
                    )
                }
            }

            if (showDonateDialog) {
                DonateFoodDialog(
                    onDismiss = { showDonateDialog = false },
                    onSuccess = {
                        showDonateDialog = false
                        currentTab = BottomNavItem.Home
                    }
                )
            }
        }
    }
}

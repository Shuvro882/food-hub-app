package com.example.foodhub.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodhub.data.FoodRepository
import com.example.foodhub.data.UserRole
import com.example.foodhub.ui.theme.GreenContainer
import com.example.foodhub.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {
    val user = FoodRepository.currentUser
    var selectedRole by remember { mutableStateOf(user?.role ?: UserRole.BOTH) }

    val myDonationCount = FoodRepository.foodList.count { it.donorId == user?.id }
    val myClaimedCount = FoodRepository.foodList.count { it.claimedByUserId == user?.id }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("প্রোফাইল (Profile)", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = GreenPrimary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Avatar Banner
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(GreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (user?.name?.take(1) ?: "U").uppercase(),
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = user?.name ?: "ব্যবহারকারী",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = user?.email ?: "ইমেইল দেওয়া হয়নি",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Impact Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$myDonationCount",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                        Text(
                            text = "মোট ডোনেশন",
                            fontSize = 12.sp,
                            color = GreenPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$myClaimedCount",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "কালেক্ট করা খাবার",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Account Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("ব্যবহারকারীর তথ্য:", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "ফোন: ${user?.phone ?: "N/A"}", fontSize = 14.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "ঠিকানা: ${user?.address ?: "N/A"}", fontSize = 14.sp)
                    }

                    HorizontalDivider()

                    Text("আপনার বর্তমান ভূমিকা (Role):", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UserRole.entries.forEach { role ->
                            FilterChip(
                                selected = selectedRole == role,
                                onClick = {
                                    selectedRole = role
                                    FoodRepository.currentUser = user?.copy(role = role)
                                },
                                label = {
                                    Text(
                                        text = when (role) {
                                            UserRole.DONOR -> "খাদ্য দাতা"
                                            UserRole.RECEIVER -> "খাদ্য গ্রহীতা"
                                            UserRole.BOTH -> "উভয়"
                                        },
                                        fontSize = 12.sp
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Mission Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GreenContainer.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "FoodHub মিশন",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = GreenPrimary
                        )
                        Text(
                            text = "জিরো ওয়েস্ট (Zero Food Waste) ও ক্ষুধার্ত মুক্ত সমাজ গড়ে তোলার লক্ষ্যে আমাদের পথচলা।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Logout Button
            OutlinedButton(
                onClick = {
                    FoodRepository.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("লগআউট (Logout)", fontWeight = FontWeight.Bold)
            }
        }
    }
}

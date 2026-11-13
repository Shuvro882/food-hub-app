package com.example.foodhub.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodhub.data.FoodItem
import com.example.foodhub.data.FoodRepository
import com.example.foodhub.data.FoodStatus
import com.example.foodhub.ui.details.FoodDetailDialog
import com.example.foodhub.ui.home.FoodCard
import com.example.foodhub.ui.theme.GreenContainer
import com.example.foodhub.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyActivityScreen(
    onOpenDonateDialog: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var selectedFoodForDetail by remember { mutableStateOf<FoodItem?>(null) }

    val currentUser = FoodRepository.currentUser

    // My donations
    val myDonations = FoodRepository.foodList.filter { it.donorId == currentUser?.id }

    // My claimed items
    val myClaims = FoodRepository.foodList.filter { it.claimedByUserId == currentUser?.id }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("আমার অ্যাক্টিভিটি (My Activity)", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = GreenContainer,
                contentColor = GreenPrimary
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            "আমার ডোনেশন (${myDonations.size})",
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )

                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            "আমার অনুরোধ (${myClaims.size})",
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            val currentItems = if (selectedTabIndex == 0) myDonations else myClaims

            if (currentItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.HourglassEmpty,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedTabIndex == 0) "আপনি এখনও কোনো খাবার ডোনেট করেননি" else "আপনি কোনো খাবার অনুরোধ করেননি",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedTabIndex == 0) {
                            Button(
                                onClick = onOpenDonateDialog,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                            ) {
                                Icon(Icons.Default.VolunteerActivism, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("এখনই ডোনেট করুন")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(currentItems, key = { it.id }) { item ->
                        FoodCard(
                            food = item,
                            onClick = { selectedFoodForDetail = item },
                            onQuickClaim = {
                                if (selectedTabIndex == 0) {
                                    FoodRepository.markAsCollected(item.id)
                                } else {
                                    FoodRepository.claimFood(item.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    selectedFoodForDetail?.let { food ->
        FoodDetailDialog(
            foodItem = food,
            onDismiss = { selectedFoodForDetail = null },
            onStatusChange = {
                selectedFoodForDetail = FoodRepository.foodList.find { it.id == food.id }
            }
        )
    }
}

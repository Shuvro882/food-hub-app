package com.example.foodhub.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.foodhub.data.FoodItem
import com.example.foodhub.data.FoodRepository
import com.example.foodhub.data.FoodStatus
import com.example.foodhub.ui.common.FoodImageDisplay
import com.example.foodhub.ui.details.FoodDetailDialog
import com.example.foodhub.ui.theme.AvailableGreen
import com.example.foodhub.ui.theme.ClaimedOrange
import com.example.foodhub.ui.theme.CollectedGray
import com.example.foodhub.ui.theme.GreenContainer
import com.example.foodhub.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenDonateDialog: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("সব") }
    var selectedFoodForDetail by remember { mutableStateOf<FoodItem?>(null) }

    val categories = listOf("সব", "রান্না করা খাবার", "শুকনা খাবার", "বেকারি ও স্ন্যাক্স", "ফলমূল ও সবজি")

    // Filter food list
    val filteredList = FoodRepository.foodList.filter { item ->
        val matchesSearch = item.title.contains(searchQuery, ignoreCase = true) ||
                item.location.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "সব" || item.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onOpenDonateDialog,
                icon = { Icon(Icons.Default.VolunteerActivism, contentDescription = "ডোনেট করুন") },
                text = { Text("খাবার ডোনেট করুন", fontWeight = FontWeight.Bold) },
                containerColor = GreenPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(28.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top App Bar Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GreenPrimary)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "FoodHub",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "স্বাগতম, ${FoodRepository.currentUser?.name ?: "গেস্ট"}!",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${FoodRepository.foodList.size} ডোনেশন",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Search Bar & Filter Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("খাবারের নাম বা ঠিকানা দিয়ে খুঁজুন...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = GreenPrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "মুছুন")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    singleLine = true
                )

                // Category Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GreenPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Food Feed List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Fastfood,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "কোন খাবার পাওয়া যায়নি",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "অন্য অনুসন্ধান চেষ্টা করুন অথবা আপনার উদ্ধৃত্ত খাবার ডোনেট করুন",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList, key = { it.id }) { food ->
                        FoodCard(
                            food = food,
                            onClick = { selectedFoodForDetail = food },
                            onQuickClaim = {
                                FoodRepository.claimFood(food.id)
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // Extra space for FAB
                    }
                }
            }
        }
    }

    // Food Detail Dialog
    selectedFoodForDetail?.let { food ->
        FoodDetailDialog(
            foodItem = food,
            onDismiss = { selectedFoodForDetail = null },
            onStatusChange = {
                // Refresh list item detail reference
                selectedFoodForDetail = FoodRepository.foodList.find { it.id == food.id }
            }
        )
    }
}

@Composable
fun FoodCard(
    food: FoodItem,
    onClick: () -> Unit,
    onQuickClaim: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Category Icon, Title, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FoodImageDisplay(
                        imageUriStr = food.imageUri,
                        category = food.category,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = food.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${food.category} • ${food.createdAt}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Status Badge
                Surface(
                    color = when (food.status) {
                        FoodStatus.AVAILABLE -> AvailableGreen.copy(alpha = 0.15f)
                        FoodStatus.CLAIMED -> ClaimedOrange.copy(alpha = 0.15f)
                        FoodStatus.COLLECTED -> CollectedGray.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = when (food.status) {
                            FoodStatus.AVAILABLE -> "প্রাপ্য"
                            FoodStatus.CLAIMED -> "বুকড"
                            FoodStatus.COLLECTED -> "সংগৃহীত"
                        },
                        color = when (food.status) {
                            FoodStatus.AVAILABLE -> AvailableGreen
                            FoodStatus.CLAIMED -> ClaimedOrange
                            FoodStatus.COLLECTED -> CollectedGray
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Details chips: Servings & Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = GreenContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${food.servings} জন খেতে পারবে",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GreenPrimary
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = food.location,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "দাতা: ${food.donorName}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onClick) {
                        Text("বিস্তারিত", fontSize = 13.sp)
                    }

                    if (food.status == FoodStatus.AVAILABLE) {
                        Button(
                            onClick = onQuickClaim,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("কালেক্ট করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

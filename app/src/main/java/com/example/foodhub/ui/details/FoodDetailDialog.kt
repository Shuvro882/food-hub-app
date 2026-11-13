package com.example.foodhub.ui.details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.foodhub.data.FoodItem
import com.example.foodhub.data.FoodRepository
import com.example.foodhub.data.FoodStatus
import com.example.foodhub.ui.common.FoodImageDisplay
import com.example.foodhub.ui.theme.AvailableGreen
import com.example.foodhub.ui.theme.ClaimedOrange
import com.example.foodhub.ui.theme.CollectedGray
import com.example.foodhub.ui.theme.GreenContainer
import com.example.foodhub.ui.theme.GreenPrimary

@Composable
fun FoodDetailDialog(
    foodItem: FoodItem,
    onDismiss: () -> Unit,
    onStatusChange: () -> Unit
) {
    val context = LocalContext.current
    val currentUser = FoodRepository.currentUser
    val isDonorOfThisItem = currentUser?.id == foodItem.donorId
    val isClaimerOfThisItem = currentUser?.id == foodItem.claimedByUserId

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status Badge
                    Surface(
                        color = when (foodItem.status) {
                            FoodStatus.AVAILABLE -> AvailableGreen.copy(alpha = 0.15f)
                            FoodStatus.CLAIMED -> ClaimedOrange.copy(alpha = 0.15f)
                            FoodStatus.COLLECTED -> CollectedGray.copy(alpha = 0.15f)
                        },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = when (foodItem.status) {
                                FoodStatus.AVAILABLE -> "🟢 প্রাপ্য (Available)"
                                FoodStatus.CLAIMED -> "🟧 বুক করা হয়েছে (Claimed)"
                                FoodStatus.COLLECTED -> "✔ সংগৃহীত (Collected)"
                            },
                            color = when (foodItem.status) {
                                FoodStatus.AVAILABLE -> AvailableGreen
                                FoodStatus.CLAIMED -> ClaimedOrange
                                FoodStatus.COLLECTED -> CollectedGray
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "বন্ধ করুন")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Image Banner
                FoodImageDisplay(
                    imageUriStr = foodItem.imageUri,
                    category = foodItem.category,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = foodItem.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Key Info Chips (Servings, Location, Time)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "খাবার পরিমাণ: ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${foodItem.servings} জন খেতে পারবে",
                            color = GreenPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "পিকআপ স্থান: ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = foodItem.location,
                            fontSize = 14.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "পোস্ট করার সময়: ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = foodItem.createdAt,
                            fontSize = 14.sp
                        )
                    }
                }

                if (foodItem.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = "খাবারের বিবরণ:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = foodItem.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Donor Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenContainer.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(GreenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = foodItem.donorName.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = foodItem.donorName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "খাদ্য দাতা (Donor)",
                                    fontSize = 12.sp,
                                    color = GreenPrimary
                                )
                            }
                        }

                        if (foodItem.donorPhone.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${foodItem.donorPhone}")
                                    }
                                    context.startActivity(intent)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "কল করুন",
                                    tint = GreenPrimary
                                )
                            }
                        }
                    }
                }

                // If claimed, display claimer details
                if (foodItem.status == FoodStatus.CLAIMED && foodItem.claimedByName != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ClaimedOrange.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = ClaimedOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("অনুরোধ করেছেন: ${foodItem.claimedByName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                if (foodItem.claimedByPhone != null) {
                                    Text("ফোন: ${foodItem.claimedByPhone}", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons based on status & user
                when {
                    foodItem.status == FoodStatus.AVAILABLE -> {
                        Button(
                            onClick = {
                                FoodRepository.claimFood(foodItem.id)
                                onStatusChange()
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                        ) {
                            Icon(Icons.Default.TouchApp, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("খাবার অনুরোধ / কালেক্ট করুন", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    foodItem.status == FoodStatus.CLAIMED && (isDonorOfThisItem || isClaimerOfThisItem) -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    FoodRepository.markAsCollected(foodItem.id)
                                    onStatusChange()
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                            ) {
                                Text("সংগৃহীত হয়েছে", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    FoodRepository.cancelClaim(foodItem.id)
                                    onStatusChange()
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("অনুরোধ বাতিল", fontSize = 13.sp)
                            }
                        }
                    }

                    foodItem.status == FoodStatus.COLLECTED -> {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            enabled = false,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("এই খাবার বিতরণ সম্পন্ন হয়েছে")
                        }
                    }
                }
            }
        }
    }
}

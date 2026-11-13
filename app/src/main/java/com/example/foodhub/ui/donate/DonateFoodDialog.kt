package com.example.foodhub.ui.donate

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.foodhub.data.FoodRepository
import com.example.foodhub.ui.common.ImageUtils
import com.example.foodhub.ui.theme.GreenContainer
import com.example.foodhub.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateFoodDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var servingsText by remember { mutableStateOf("5") }
    var category by remember { mutableStateOf("রান্না করা খাবার") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf(FoodRepository.currentUser?.address ?: "") }
    var phone by remember { mutableStateOf(FoodRepository.currentUser?.phone ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPresetImage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("রান্না করা খাবার", "শুকনা খাবার", "বেকারি ও স্ন্যাক্স", "ফলমূল ও সবজি", "অন্যান্য")

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            selectedPresetImage = null
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "খাবার ডোনেট করুন",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "বন্ধ করুন")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Form Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    errorMessage?.let { error ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(12.dp),
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Food Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("খাবারের নাম * (যেমন: বিরিয়ানি, খিচুড়ি, রুটি)") },
                        leadingIcon = { Icon(Icons.Default.Fastfood, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Quantity / Servings (কতজন খেতে পারবে)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = servingsText,
                            onValueChange = { servingsText = it },
                            label = { Text("কতজন খেতে পারবে? *") },
                            leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Quick increment/decrement buttons
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            FilledIconButton(
                                onClick = {
                                    val current = servingsText.toIntOrNull() ?: 1
                                    if (current > 1) servingsText = (current - 1).toString()
                                },
                                shape = CircleShape,
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = GreenContainer)
                            ) {
                                Text("-", fontWeight = FontWeight.Bold, color = GreenPrimary, fontSize = 18.sp)
                            }

                            FilledIconButton(
                                onClick = {
                                    val current = servingsText.toIntOrNull() ?: 1
                                    servingsText = (current + 1).toString()
                                },
                                shape = CircleShape,
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = GreenContainer)
                            ) {
                                Text("+", fontWeight = FontWeight.Bold, color = GreenPrimary, fontSize = 18.sp)
                            }
                        }
                    }

                    // Category selection
                    Text("খাবারের ক্যাটাগরি:", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.take(3).forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 12.sp) }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.drop(3).forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 12.sp) }
                            )
                        }
                    }

                    // Image Section (ছবি সংযুক্ত করার স্থান)
                    Text("খাবারের ছবি (Image):", fontWeight = FontWeight.Medium, fontSize = 14.sp)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(GreenContainer)
                            .border(1.dp, GreenPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("ছবি যুক্ত করা হয়েছে!", fontWeight = FontWeight.Bold, color = GreenPrimary)
                                Text("পরিবর্তন করতে চাপুন", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "ছবি সিলেক্ট করুন",
                                    tint = GreenPrimary,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("গ্যালারি থেকে ছবি আপলোড করতে ট্যাপ করুন", fontWeight = FontWeight.Medium, color = GreenPrimary, fontSize = 13.sp)
                            }
                        }
                    }

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("খাবারের বিবরণ (প্যাকিং অবস্থা, তৈরির সময়, ইত্যাদি)") },
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2,
                        maxLines = 4
                    )

                    // Location (পিকআপ লোকেশন)
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("পিকআপের স্থান/ঠিকানা * (যেমন: ধানমন্ডি ২৭, ঢাকা)") },
                        leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Phone Number
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("যোগাযোগের নম্বর *") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            errorMessage = "দয়া করে খাবারের নাম লিখুন"
                            return@Button
                        }
                        val servings = servingsText.toIntOrNull() ?: 1
                        if (servings <= 0) {
                            errorMessage = "কতজন খেতে পারবে সঠিকভাবে লিখুন"
                            return@Button
                        }
                        if (location.isBlank()) {
                            errorMessage = "পিকআপের স্থান উল্লেখ করুন"
                            return@Button
                        }
                        if (phone.isBlank()) {
                            errorMessage = "যোগাযোগের ফোন নম্বর লিখুন"
                            return@Button
                        }

                        val savedImagePath = selectedImageUri?.let { uri ->
                            ImageUtils.saveImageToInternalStorage(context, uri)
                        }

                        FoodRepository.addFoodItem(
                            title = title,
                            category = category,
                            servings = servings,
                            description = description,
                            location = location,
                            phone = phone,
                            imageUri = savedImagePath ?: selectedImageUri?.toString()
                        )
                        onSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Icon(Icons.Default.VolunteerActivism, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("খাবার ডোনেট পোস্ট করুন", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

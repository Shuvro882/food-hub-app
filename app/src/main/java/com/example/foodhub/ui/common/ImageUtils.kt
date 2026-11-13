package com.example.foodhub.ui.common

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.RiceBowl
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodhub.ui.theme.GreenContainer
import com.example.foodhub.ui.theme.GreenPrimary
import java.io.File

object ImageUtils {
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val imagesDir = File(context.filesDir, "food_images").apply {
                if (!exists()) mkdirs()
            }
            val imageFile = File(imagesDir, "food_${System.currentTimeMillis()}.jpg")
            imageFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            imageFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@Composable
fun FoodImageDisplay(
    imageUriStr: String?,
    category: String,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(imageUriStr) {
        if (!imageUriStr.isNullOrEmpty()) {
            try {
                val file = File(imageUriStr)
                if (file.exists()) {
                    BitmapFactory.decodeFile(file.absolutePath)
                } else null
            } catch (e: Exception) {
                null
            }
        } else null
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "খাবারের ছবি",
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(GreenContainer),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = when (category) {
                        "বেকারি ও স্ন্যাক্স" -> Icons.Default.BakeryDining
                        "ফলমূল ও সবজি" -> Icons.Default.RiceBowl
                        else -> Icons.Default.DinnerDining
                    },
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category,
                    color = GreenPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

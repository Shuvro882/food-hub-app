package com.example.foodhub.data

enum class FoodStatus(val labelBn: String) {
    AVAILABLE("প্রাপ্য (Available)"),
    CLAIMED("অনুরোধ করা হয়েছে (Claimed)"),
    COLLECTED("সংগৃহীত (Collected)")
}

data class FoodItem(
    val id: String = "",
    val title: String = "",
    val category: String = "রান্না করা খাবার",
    val servings: Int = 1,
    val description: String = "",
    val location: String = "",
    val donorId: String = "",
    val donorName: String = "",
    val donorPhone: String = "",
    val imageUri: String? = null,
    val status: FoodStatus = FoodStatus.AVAILABLE,
    val createdAt: String = "এখনই",
    val claimedByUserId: String? = null,
    val claimedByName: String? = null,
    val claimedByPhone: String? = null
)

package com.example.foodhub.data

enum class UserRole(val labelBn: String) {
    DONOR("খাদ্য দাতা (Donor)"),
    RECEIVER("খাদ্য গ্রহীতা (Receiver)"),
    BOTH("উভয় (Donor & Receiver)")
}

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val address: String = "",
    val role: UserRole = UserRole.BOTH
)

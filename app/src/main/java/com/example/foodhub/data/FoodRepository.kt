package com.example.foodhub.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

object FoodRepository {
    private const val PREFS_NAME = "foodhub_database_prefs"
    private const val KEY_USERS = "key_users_json"
    private const val KEY_FOODS = "key_foods_json"
    private const val KEY_CURRENT_USER_ID = "key_current_user_id"

    private var sharedPreferences: SharedPreferences? = null

    // Currently active logged-in user
    var currentUser by mutableStateOf<User?>(null)

    // Auth state helper
    val isLoggedIn: Boolean
        get() = currentUser != null

    // Registered users list
    val userList = mutableStateListOf<User>()

    // Food items list
    val foodList = mutableStateListOf<FoodItem>()

    fun init(context: Context) {
        if (sharedPreferences != null) return
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences = prefs

        loadUsers(prefs)
        loadFoods(prefs)
        loadCurrentSession(prefs)
    }

    // --- Persistence helper methods ---

    private fun loadUsers(prefs: SharedPreferences) {
        userList.clear()
        val usersJsonStr = prefs.getString(KEY_USERS, null)
        if (usersJsonStr.isNullOrEmpty()) {
            // Seed initial demo users
            val defaultUsers = listOf(
                User(
                    id = "user_demo_1",
                    name = "রফিকুল ইসলাম",
                    email = "rafiq@example.com",
                    password = "123",
                    phone = "01712345678",
                    address = "ধানমন্ডি, ঢাকা",
                    role = UserRole.BOTH
                ),
                User(
                    id = "user_demo_2",
                    name = "আরিফ আহমেদ",
                    email = "arif@example.com",
                    password = "123",
                    phone = "01811223344",
                    address = "ধানমন্ডি ২৭, ঢাকা",
                    role = UserRole.DONOR
                ),
                User(
                    id = "user_demo_3",
                    name = "মোছাঃ ফাতেমা বেগম",
                    email = "fatema@example.com",
                    password = "123",
                    phone = "01999887766",
                    address = "মিরপুর ১০, ঢাকা",
                    role = UserRole.DONOR
                )
            )
            userList.addAll(defaultUsers)
            saveUsers()
        } else {
            try {
                val jsonArray = JSONArray(usersJsonStr)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    userList.add(
                        User(
                            id = obj.optString("id"),
                            name = obj.optString("name"),
                            email = obj.optString("email"),
                            password = obj.optString("password"),
                            phone = obj.optString("phone"),
                            address = obj.optString("address"),
                            role = try { UserRole.valueOf(obj.optString("role", "BOTH")) } catch (e: Exception) { UserRole.BOTH }
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveUsers() {
        val prefs = sharedPreferences ?: return
        val jsonArray = JSONArray()
        for (user in userList) {
            val obj = JSONObject().apply {
                put("id", user.id)
                put("name", user.name)
                put("email", user.email)
                put("password", user.password)
                put("phone", user.phone)
                put("address", user.address)
                put("role", user.role.name)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_USERS, jsonArray.toString()).apply()
    }

    private fun loadFoods(prefs: SharedPreferences) {
        foodList.clear()
        val foodsJsonStr = prefs.getString(KEY_FOODS, null)
        if (foodsJsonStr.isNullOrEmpty()) {
            // Seed initial sample food items
            val defaultFoods = listOf(
                FoodItem(
                    id = "food_1",
                    title = "চিকেন বিরিয়ানি ও রায়তা",
                    category = "রান্না করা খাবার",
                    servings = 8,
                    description = "অনুষ্ঠানের উদ্বৃত্ত ফ্রেশ বিরিয়ানি। পরিষ্কার ড্রাই কন্টেইনারে প্যাক করা আছে।",
                    location = "ধানমন্ডি ২৭, ঢাকা",
                    donorId = "user_demo_2",
                    donorName = "আরিফ আহমেদ",
                    donorPhone = "01811223344",
                    imageUri = null,
                    status = FoodStatus.AVAILABLE,
                    createdAt = "১৫ মিনিট আগে"
                ),
                FoodItem(
                    id = "food_2",
                    title = "ভুনা খিচুড়ি ও ডিম",
                    category = "রান্না করা খাবার",
                    servings = 6,
                    description = "গরম গরম ভুনা খিচুড়ি ও সিদ্ধ ডিম। পরিষ্কার কন্টেইনারে প্রস্তুত করা আছে।",
                    location = "ধানমন্ডি ১৫, ঢাকা",
                    donorId = "user_demo_2",
                    donorName = "আরিফ আহমেদ",
                    donorPhone = "01811223344",
                    imageUri = null,
                    status = FoodStatus.AVAILABLE,
                    createdAt = "২০ মিনিট আগে"
                ),
                FoodItem(
                    id = "food_3",
                    title = "রুটি ও মিক্সড সবজি",
                    category = "রান্না করা খাবার",
                    servings = 5,
                    description = "বাসায় তৈরি করা গরম রুটি ও তাজা সবজি। সম্পূর্ণ স্বাস্থ্যকর।",
                    location = "মিরপুর ১০, ঢাকা",
                    donorId = "user_demo_3",
                    donorName = "মোছাঃ ফাতেমা বেগম",
                    donorPhone = "01999887766",
                    imageUri = null,
                    status = FoodStatus.AVAILABLE,
                    createdAt = "৩০ মিনিট আগে"
                ),
                FoodItem(
                    id = "food_4",
                    title = "বেকারি পাউরুটি ও বিস্কুট প্যাকেট",
                    category = "বেকারি ও স্ন্যাক্স",
                    servings = 10,
                    description = "বেকারির আনওপেনড পাউরুটি ও টোস্ট বিস্কুট। মেয়াদের সময় ৩ দিন বাকী।",
                    location = "উত্তরা সেক্টর ৭, ঢাকা",
                    donorId = "user_demo_1",
                    donorName = "রফিকুল ইসলাম",
                    donorPhone = "01712345678",
                    imageUri = null,
                    status = FoodStatus.AVAILABLE,
                    createdAt = "১ ঘণ্টা আগে"
                ),
                FoodItem(
                    id = "food_5",
                    title = "তাজা আপেল ও কলা",
                    category = "ফলমূল ও সবজি",
                    servings = 4,
                    description = "১ কেজি তাজা আপেল এবং ডজন কলা। ফ্রেশ ও খাওয়া উপযোগী।",
                    location = "ধানমন্ডি, ঢাকা",
                    donorId = "user_demo_1",
                    donorName = "রফিকুল ইসলাম",
                    donorPhone = "01712345678",
                    imageUri = null,
                    status = FoodStatus.AVAILABLE,
                    createdAt = "২ ঘণ্টা আগে"
                )
            )
            foodList.addAll(defaultFoods)
            saveFoods()
        } else {
            try {
                val jsonArray = JSONArray(foodsJsonStr)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    foodList.add(
                        FoodItem(
                            id = obj.optString("id"),
                            title = obj.optString("title"),
                            category = obj.optString("category"),
                            servings = obj.optInt("servings", 1),
                            description = obj.optString("description"),
                            location = obj.optString("location"),
                            donorId = obj.optString("donorId"),
                            donorName = obj.optString("donorName"),
                            donorPhone = obj.optString("donorPhone"),
                            imageUri = if (obj.has("imageUri") && !obj.isNull("imageUri")) obj.optString("imageUri") else null,
                            status = try { FoodStatus.valueOf(obj.optString("status", "AVAILABLE")) } catch (e: Exception) { FoodStatus.AVAILABLE },
                            createdAt = obj.optString("createdAt", "এখনই"),
                            claimedByUserId = if (obj.has("claimedByUserId") && !obj.isNull("claimedByUserId")) obj.optString("claimedByUserId") else null,
                            claimedByName = if (obj.has("claimedByName") && !obj.isNull("claimedByName")) obj.optString("claimedByName") else null,
                            claimedByPhone = if (obj.has("claimedByPhone") && !obj.isNull("claimedByPhone")) obj.optString("claimedByPhone") else null
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveFoods() {
        val prefs = sharedPreferences ?: return
        val jsonArray = JSONArray()
        for (food in foodList) {
            val obj = JSONObject().apply {
                put("id", food.id)
                put("title", food.title)
                put("category", food.category)
                put("servings", food.servings)
                put("description", food.description)
                put("location", food.location)
                put("donorId", food.donorId)
                put("donorName", food.donorName)
                put("donorPhone", food.donorPhone)
                put("imageUri", food.imageUri)
                put("status", food.status.name)
                put("createdAt", food.createdAt)
                put("claimedByUserId", food.claimedByUserId)
                put("claimedByName", food.claimedByName)
                put("claimedByPhone", food.claimedByPhone)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_FOODS, jsonArray.toString()).apply()
    }

    private fun loadCurrentSession(prefs: SharedPreferences) {
        val currentUserId = prefs.getString(KEY_CURRENT_USER_ID, null)
        if (!currentUserId.isNullOrEmpty()) {
            val user = userList.find { it.id == currentUserId }
            if (user != null) {
                currentUser = user
            } else {
                currentUser = userList.firstOrNull()
            }
        } else {
            // Default logged in demo user for smooth start if desired, or null
            currentUser = userList.firstOrNull()
        }
    }

    private fun saveCurrentSession(userId: String?) {
        val prefs = sharedPreferences ?: return
        prefs.edit().putString(KEY_CURRENT_USER_ID, userId).apply()
    }

    // --- Authentication Operations ---

    fun login(emailInput: String, passwordInput: String): AuthResult {
        val cleanEmail = emailInput.trim()
        val cleanPassword = passwordInput.trim()

        if (cleanEmail.isBlank()) {
            return AuthResult.Error("দয়া করে ইমেইল বা ফোন নম্বর দিন")
        }
        if (cleanPassword.isBlank()) {
            return AuthResult.Error("দয়া করে পাসওয়ার্ড দিন")
        }

        // Match user by email or phone
        val user = userList.find {
            it.email.equals(cleanEmail, ignoreCase = true) || it.phone == cleanEmail
        }

        if (user == null) {
            return AuthResult.Error("এই ইমেইল/ফোন নম্বরে কোনো একাউন্ট পাওয়া যায়নি। দয়া করে আগে রেজিস্ট্রেশন করুন।")
        }

        if (user.password != cleanPassword) {
            return AuthResult.Error("পাসওয়ার্ড ভুল হয়েছে! সঠিক পাসওয়ার্ড দিয়ে আবার চেষ্টা করুন।")
        }

        currentUser = user
        saveCurrentSession(user.id)
        return AuthResult.Success
    }

    fun signup(
        name: String,
        email: String,
        password: String,
        phone: String,
        address: String,
        role: UserRole
    ): AuthResult {
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()
        val cleanName = name.trim()

        if (cleanName.isBlank()) {
            return AuthResult.Error("দয়া করে আপনার নাম দিন")
        }
        if (cleanEmail.isBlank()) {
            return AuthResult.Error("দয়া করে ইমেইল বা ফোন নম্বর দিন")
        }
        if (cleanPassword.isBlank()) {
            return AuthResult.Error("দয়া করে পাসওয়ার্ড দিন")
        }

        // Check duplicate email
        val existingUser = userList.find { it.email.equals(cleanEmail, ignoreCase = true) }
        if (existingUser != null) {
            return AuthResult.Error("এই ইমেইল দিয়ে ইতোমধ্যেই একটি একাউন্ট রয়েছে! সরাসরি লগইন করুন।")
        }

        val newUser = User(
            id = UUID.randomUUID().toString(),
            name = cleanName,
            email = cleanEmail,
            password = cleanPassword,
            phone = phone.ifBlank { "01700000000" },
            address = address.ifBlank { "ঢাকা, বাংলাদেশ" },
            role = role
        )

        userList.add(newUser)
        saveUsers()

        currentUser = newUser
        saveCurrentSession(newUser.id)
        return AuthResult.Success
    }

    fun loginAsDemo(): AuthResult {
        val demoUser = userList.firstOrNull() ?: User(
            id = "user_demo_1",
            name = "রফিকুল ইসলাম",
            email = "rafiq@example.com",
            password = "123",
            phone = "01712345678",
            address = "ধানমন্ডি, ঢাকা",
            role = UserRole.BOTH
        )
        if (!userList.contains(demoUser)) {
            userList.add(demoUser)
            saveUsers()
        }
        currentUser = demoUser
        saveCurrentSession(demoUser.id)
        return AuthResult.Success
    }

    fun logout() {
        currentUser = null
        saveCurrentSession(null)
    }

    // --- Food Operations ---

    fun addFoodItem(
        title: String,
        category: String,
        servings: Int,
        description: String,
        location: String,
        phone: String,
        imageUri: String?
    ) {
        val user = currentUser ?: return
        val newItem = FoodItem(
            id = UUID.randomUUID().toString(),
            title = title,
            category = category,
            servings = servings,
            description = description,
            location = location,
            donorId = user.id,
            donorName = user.name,
            donorPhone = phone.ifBlank { user.phone },
            imageUri = imageUri,
            status = FoodStatus.AVAILABLE,
            createdAt = "এখনই"
        )
        foodList.add(0, newItem)
        saveFoods()
    }

    fun claimFood(foodId: String): Boolean {
        val user = currentUser ?: return false
        val index = foodList.indexOfFirst { it.id == foodId }
        if (index != -1 && foodList[index].status == FoodStatus.AVAILABLE) {
            val item = foodList[index]
            foodList[index] = item.copy(
                status = FoodStatus.CLAIMED,
                claimedByUserId = user.id,
                claimedByName = user.name,
                claimedByPhone = user.phone
            )
            saveFoods()
            return true
        }
        return false
    }

    fun markAsCollected(foodId: String): Boolean {
        val index = foodList.indexOfFirst { it.id == foodId }
        if (index != -1) {
            val item = foodList[index]
            foodList[index] = item.copy(status = FoodStatus.COLLECTED)
            saveFoods()
            return true
        }
        return false
    }

    fun cancelClaim(foodId: String): Boolean {
        val index = foodList.indexOfFirst { it.id == foodId }
        if (index != -1) {
            val item = foodList[index]
            foodList[index] = item.copy(
                status = FoodStatus.AVAILABLE,
                claimedByUserId = null,
                claimedByName = null,
                claimedByPhone = null
            )
            saveFoods()
            return true
        }
        return false
    }

    fun deleteFood(foodId: String) {
        foodList.removeAll { it.id == foodId }
        saveFoods()
    }
}

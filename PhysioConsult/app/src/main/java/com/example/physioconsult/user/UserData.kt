package com.example.physioconsult.user

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

suspend fun fetchUserData(userId: String): Map<String, String> {
    return try {
        val firestore = FirebaseFirestore.getInstance()
        val documentSnapshot = firestore.collection("users").document(userId).get().await()
        mapOf(
            "name" to (documentSnapshot.getString("name") ?: "User"),
            "surname" to (documentSnapshot.getString("surname") ?: "Surname")
        )
    } catch (e: Exception) {
        Log.e("fetchUserData", "Error fetching user data", e)
        emptyMap()
    }
}
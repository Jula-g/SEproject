package com.example.physioconsult.user

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Fetches user data from Firestore for a given user ID.
 *
 * @param userId The ID of the user whose data is to be fetched.
 * @return A map containing the user's name and surname.
 */

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
/**
 * Fetches the role of a user from Firestore for a given user ID.
 *
 * @param userId The ID of the user whose role is to be fetched.
 * @return The role of the user as a String, or null if the role is not found or an error occurs.
 */

suspend fun getUserRole(userId: String): String? {
    val firestore = FirebaseFirestore.getInstance()
    return try {
        val document = firestore.collection("users").document(userId).get().await()
        if (document.exists()) {
            document.getString("role")
        } else {
            null
        }
    } catch (e: Exception) {
        Log.e("getUserData", "Error fetching user role", e)
    }.toString()
}

suspend fun fetchPatients(
    physiotherapistId: String,
    onResult: (Map<String, Pair<String, List<String>>>, String?) -> Unit
) {
    try {
        val db = FirebaseFirestore.getInstance()
        val patientListRef = db.collection("patient_list").document(physiotherapistId)

        val document = patientListRef.get().await()
        if (document.exists()) {
            val patientsMap = document.get("patients") as? Map<String, List<String>> ?: emptyMap()
            val patientDataMap = mutableMapOf<String, Pair<String, List<String>>>()

            patientsMap.forEach { (patientId, assessments) ->
                val userData = fetchUserData(patientId)
                val name = userData["name"] ?: "Unknown"
                val surname = userData["surname"] ?: "Unknown"

                patientDataMap[patientId] = Pair(name + " " + surname, assessments)
            }

            onResult(patientDataMap, null)
        } else {
            onResult(emptyMap(), "No patient list found.")
        }
    } catch (e: Exception) {
        Log.e("FetchPatients", "Error fetching patients: ${e.message}")
        onResult(emptyMap(), "Failed to fetch the patient list.")
    }
}

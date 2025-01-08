package com.example.physioconsult.user

import com.google.firebase.firestore.FirebaseFirestore


fun getUserRole(userId: String, onResult: (String?) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("users").document(userId)
        .get()
        .addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val role = document.getString("role")
                onResult(role)
            } else {
                onResult(null)
            }
        }
        .addOnFailureListener {
            onResult(null)
        }
}

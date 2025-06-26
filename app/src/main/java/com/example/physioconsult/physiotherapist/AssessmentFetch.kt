package com.example.physioconsult.physiotherapist

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Context
import android.graphics.Bitmap
import com.example.physioconsult.fragments.ImageUtils

fun fetchAssessment(
    userId: String,
    documentId: String,
    context: Context,
    onResult: (
        Boolean,
        Map<String, Map<String, String>>,
        Map<String, Map<String, String>>,
        Map<String, Map<String, String>>,
        MutableList<Bitmap?>,
        String?
    ) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val assessmentRef = db.collection(userId).document(documentId)

    assessmentRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val backMeasurements = document.get("back") as? Map<String, Any> ?: emptyMap()
                val sideMeasurements = document.get("side") as? Map<String, Any> ?: emptyMap()
                val frontMeasurements = document.get("front") as? Map<String, Any> ?: emptyMap()

                val backMeasurementsString = backMeasurements.mapValues { it.value.toString() }
                val sideMeasurementsString = sideMeasurements.mapValues { it.value.toString() }
                val frontMeasurementsString = frontMeasurements.mapValues { it.value.toString() }

                val imageFields = listOf("Front", "Back", "Side")
                val imageUtils = ImageUtils()

                imageUtils.retrieveImageFromFirestore(context, documentId, userId, imageFields) { bitmaps ->
                    val validBitmaps: MutableList<Bitmap?> = bitmaps.filterNotNull().toMutableList()
                    if (validBitmaps.size == imageFields.size) {
                        onResult(
                            true,
                            mapOf("front" to frontMeasurementsString),
                            mapOf("back" to backMeasurementsString),
                            mapOf("side" to sideMeasurementsString),
                            validBitmaps,
                            null
                        )
                    } else {
                        onResult(false, emptyMap(), emptyMap(), emptyMap(), mutableListOf(), "Some images are missing.")
                    }
                }
            } else {
                onResult(false, emptyMap(), emptyMap(), emptyMap(), mutableListOf(), "Assessment not found.")
            }
        }
        .addOnFailureListener { exception ->
            Log.e("FetchAssessment", "Error fetching assessment: ${exception.message}")
            onResult(false, emptyMap(), emptyMap(), emptyMap(), mutableListOf(), "Failed to retrieve assessment.")
        }
}
package com.example.physioconsult.fragments.user.assesment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import com.example.physioconsult.Main.MainActivity
import com.example.physioconsult.fragments.ImageUtils
import com.example.physioconsult.fragments.PDFUtils
import com.example.physioconsult.ui.theme.PhysioConsultTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.pose.Pose
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Assesment : ComponentActivity() {
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val measure = Measure()
    private val pdfManager = PDFUtils()

    private var i = mutableStateOf(0)
    private var uriList = mutableListOf<Uri?>()
    private var angResult = mutableMapOf<String, String>()
    private var lenResult = mutableMapOf<String, String>()

    private lateinit var frontResult: Map<String, Double?>
    private lateinit var backResult: Map<String, Double?>
    private lateinit var sideResult: Map<String, Double?>

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val documentId = intent.getStringExtra("documentId")
        val frontImage = intent.getStringExtra("frontImage")
        val backImage = intent.getStringExtra("backImage")
        val sideImage = intent.getStringExtra("sideImage")

        val frontUri = Uri.parse(frontImage)
        val backUri = Uri.parse(backImage)
        val sideUri = Uri.parse(sideImage)

        Log.d("AssesmentActivity", "front: ${frontUri}\nback: ${backUri}\nside: ${sideUri}")

        uriList.addAll(listOf(frontUri, backUri, sideUri))
        Log.d("AssesmentActivity", "urilist: ${uriList}")

        val poses = mutableListOf<Pose>()
        measure.detectAndSavePoses(uriList, this) { detectedPoses ->
            poses.clear()
            poses.addAll(detectedPoses)

            val results = measure.conductMeasurements(poses)
            frontResult = results.first
            backResult = results.second
            sideResult = results.third
            update(frontResult, backResult, sideResult, i.value)
            if (documentId != null && userId != null) {
                saveResultsToFirebase(documentId, userId, frontResult, backResult, sideResult,
                    onSuccess = {
                        Log.d("Firestore", "Results successfully saved.")
                    },
                    onFailure = { exception ->
                        Log.e("Firestore", "Failed to save results: ${exception.message}")
                    }
                )
            }


            setContent {
                PhysioConsultTheme {
                    AssesmentForm(
                        onNextClick = {
                            if (i.value < uriList.size - 1) {
                                i.value++
                                update(frontResult, backResult, sideResult, i.value)
                            }
                        },
                        onPreviousClick = {
                            if (i.value > 0) {
                                i.value--
                                update(frontResult, backResult, sideResult, i.value)
                            }
                        },
                        onCloseClick = {
                            this.startActivity(
                                Intent(
                                    this,
                                    MainActivity::class.java
                                )
                            )
                        },
                        onSavePDFClick = { pdfManager.generatePDF(uriList,this,frontResult,backResult,sideResult) },
                        onGenerateCode = {/* TODO: implement generate access code functionality*/ },
                        uriList = uriList,
                        angleResults = angResult,
                        lengthResults = lenResult,
                        index = i.value
                    )
                }
            }
        }
    }

    private fun update(frontResults: Map<String, Double?>, backResults: Map<String, Double?>, sideResults: Map<String, Double?>, index: Int) {
        when (index) {
            0 -> { // Front View
                val keys = listOf(
                    "Hips",
                    "Shoulders",
                    "Thigh L",
                    "Thigh R",
                    "Shin L",
                    "Shin R",
                    "Forearm L",
                    "Forearm R",
                    "Arm L",
                    "Arm R"
                )

                val resultsMap = keys.zip(frontResults.values).toMap()

                angResult = mutableMapOf(
                    "Hips" to formatToTwoDecimalPlaces(resultsMap["Hips"]),
                    "Shoulders" to formatToTwoDecimalPlaces(resultsMap["Shoulders"])
                )

                lenResult = keys.drop(2).associateWith { key ->
                    formatToTwoDecimalPlaces(resultsMap[key])
                } as MutableMap<String, String>
            }

            1 -> { // Back View
                val keys = listOf(
                    "Hips",
                    "Shoulders",
                    "Thigh L",
                    "Thigh R",
                    "Shin L",
                    "Shin R",
                    "Forearm L",
                    "Forearm R",
                    "Arm L",
                    "Arm R"
                )

                val resultsMap = keys.zip(backResults.values).toMap()

                angResult = mutableMapOf(
                    "Hips" to formatToTwoDecimalPlaces(resultsMap["Hips"]),
                    "Shoulders" to formatToTwoDecimalPlaces(resultsMap["Shoulders"])
                )

                lenResult = keys.drop(2).associateWith { key ->
                    formatToTwoDecimalPlaces(resultsMap[key])
                } as MutableMap<String, String>
            }

            2 -> { // Side View
                val keys = listOf(
                    "Thigh L",
                    "Shin L",
                    "Forearm L",
                    "Arm L"
                )

                val resultsMap = keys.zip(sideResults.values).toMap()

                // Side view has no angles
                angResult = mutableMapOf(
                    "Hips" to "---",
                    "Shoulders" to "---"
                )

                lenResult = keys.associateWith { key ->
                    formatToTwoDecimalPlaces(resultsMap[key])
                } as MutableMap<String, String>
            }

            else -> {
                Log.w("AssessmentActivity", "Invalid index provided: $index")
            }
        }
    }

    private fun formatToTwoDecimalPlaces(value: Double?): String {
        return value?.let { String.format("%.2f", it) } ?: "err"
    }

    private fun saveResultsToFirebase(
        documentId: String,
        userId: String,
        frontResults: Map<String, Double?>,
        backResults: Map<String, Double?>,
        sideResults: Map<String, Double?>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(userId).document(documentId)

        val data = mapOf(
            "front" to frontResults,
            "back" to backResults,
            "side" to sideResults
        )

        docRef.update(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }


}
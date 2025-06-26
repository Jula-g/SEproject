package com.example.physioconsult.fragments.user.assesment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import com.example.physioconsult.Main.MainActivity
import com.example.physioconsult.fragments.PDFUtils
import com.example.physioconsult.ui.theme.PhysioConsultTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.mlkit.vision.pose.Pose

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
                        onSavePDFClick = {
                            pdfManager.generatePDF(
                                uriList,
                                this,
                                frontResult,
                                backResult,
                                sideResult
                            )
                        },
                        onGenerateCode = {
                            if (userId != null && documentId != null) {
                                addCodeToFirebase(this,userId, documentId,
                                    onSuccess = {
                                        Log.d("Code", "${userId}, ${documentId}")
                                    },
                                    onFailure = { exception ->
                                        Log.e("Code", "Code generation failed: ${exception.message}")
                                    })
                            }
                        },
                        uriList = uriList,
                        angleResults = angResult,
                        lengthResults = lenResult,
                        index = i.value
                    )
                }
            }
        }
    }

    private fun update(
        frontResults: Map<String, Double?>,
        backResults: Map<String, Double?>,
        sideResults: Map<String, Double?>,
        index: Int
    ) {
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
            "side" to sideResults,
            "code" to null
        )

        docRef.update(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }


    private fun addCodeToFirebase(
        context: Context,
        userId: String,
        documentId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        checkIfDocumentHasCode(
            userId,
            documentId,
            onResult = { code ->
                if (code == null) {
                    generateCode(
                        onCodeGenerated = { newCode ->
                            val db = FirebaseFirestore.getInstance()
                            val docRefUser = db.collection(userId).document(documentId)

                            val data = mapOf("code" to newCode)

                            docRefUser.set(data, SetOptions.merge())
                                .addOnSuccessListener {
                                    showPopup(context, newCode)
                                    onSuccess()
                                }
                                .addOnFailureListener { e ->
                                    onFailure(e)
                                }
                            val docRefCodes = db.collection("Codes").document(newCode)

                            val data2 = mapOf(
                                "collection" to userId,
                                "document" to documentId
                                )
                            docRefCodes.set(data2)
                                .addOnSuccessListener {
                                    onSuccess()
                                }
                                .addOnFailureListener { e ->
                                    onFailure(e)
                                }

                        },
                        onError = { exception ->
                            onFailure(exception)
                        }
                    )
                } else {
                    showPopup(context, code)
                    onSuccess()
                }
            },
            onError = { exception ->
                onFailure(exception)
            }
        )
    }



    private fun checkIfDocumentHasCode(
        userId: String,
        documentId: String,
        onResult: (String?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(userId).document(documentId)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val code = document.getString("code")
                    onResult(code)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }



    private fun showPopup(context: Context, code: String) {
        AlertDialog.Builder(context)
            .setTitle("Your Code")
            .setMessage(code)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun generateCode(
        onCodeGenerated: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        fun attemptCodeGeneration() {
            val code = (0..999999).random().toString().padStart(6, '0')
            val docRef = db.collection("Codes").document(code)

            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        attemptCodeGeneration()
                    } else {
                        onCodeGenerated(code)
                    }
                }

                .addOnFailureListener { exception ->
                    onError(exception)
                }
        }

        attemptCodeGeneration()
    }


}
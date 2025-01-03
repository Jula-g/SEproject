package com.example.physioconsult.fragments.therapist

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.physioconsult.ui.theme.PhysioConsultTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import android.util.Base64

import java.util.Date
import java.util.Locale

class ViewImage : ComponentActivity() {
    private var iteration: Int = 1
    private lateinit var cameraResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>

    private val db = Firebase.firestore
    private var field = ""

    private var pictureUri = mutableStateOf<Uri?>(null)
    private val imageUri = mutableStateOf<Uri?>(null)
    private val tempUri = mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (iteration) {
            1 -> {
                pictureUri.value =
                    Uri.parse("android.resource://com.example.physioconsult/drawable/front_view")
                field = "FrontURL"
            }

            2 -> {
                pictureUri.value =
                    Uri.parse("android.resource://com.example.physioconsult/drawable/back_view")
                field = "BackURL"
            }

            3 -> {
                pictureUri.value =
                    Uri.parse("android.resource://com.example.physioconsult/drawable/side_view")
                field = "SideURL"
            }
        }

        if (iteration == 3)
            iteration = 1

        // Call Firestore method to retrieve the image from Firestore


        setContent {
            PhysioConsultTheme {
                AddPhoto(
                    onTakePhotoClick = {
                        retrieveImageFromFirestore()
                    },
                    onChooseFromGalleryClick = {
                        // Code for selecting from gallery (if needed)
                    },
                    onConfirmClick = {
                        iteration++
                        Log.e("CONFIRMBUTTONPRESSED", "1")

                    },
                    imageUri2 = imageUri.value,
                    imageUri1 = pictureUri.value
                )
            }
        }
    }

    // Retrieve image from Firestore (Base64)
    private fun retrieveImageFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("sampleTexts").document("jgcu660xn44LPH1xDTi4")


        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val base64String = documentSnapshot.getString("text") // Assuming your Base64 string is stored in the "text" field

                    // Convert Base64 string to Bitmap
                    if (base64String != null) {
                        val bitmap = convertBase64ToBitmap(base64String)
                        if (bitmap != null) {
                            imageUri.value = getImageUriFromBitmap(bitmap) // Display the decoded Bitmap
                        }
                    }
                } else {
                    Log.e("Firestore", "Document does not exist")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error retrieving document", e)
            }
    }

    private fun convertBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            Log.e("Base64ToBitmap", "Error decoding Base64 string", e)
            null
        }
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Image", null)
        return Uri.parse(path)
    }

    private fun uploadImageToFirebase(string: String) {
        // Upload to Firestore (or any other service)
    }
}

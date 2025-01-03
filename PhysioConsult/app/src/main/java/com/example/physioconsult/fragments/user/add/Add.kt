package com.example.physioconsult.fragments.user.add

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import android.util.Base64

import java.util.Date
import java.util.Locale

class Add : ComponentActivity() {
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

        cameraResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.e("CAMRESLAUNCH", "URI: ${tempUri.value}")
                    tempUri.value?.let {
                        saveImageToGallery(it)
                        imageUri.value = tempUri.value
                        Log.e("URIstatus", "URI 1: ${imageUri}")
                    }
                }
            }

        galleryResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val tempUri = result.data?.data
                    imageUri.value = tempUri
                }
            }

        setContent {
            PhysioConsultTheme {
                AddPhoto(
                    onTakePhotoClick = {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.CAMERA), 1
                            )
                        } else {
                            openCamera()
                        }

                    },
                    onChooseFromGalleryClick = {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
                            )
                        } else {
                            chooseFromGallery()
                        }

                    },
                    onConfirmClick = {
                        iteration++
                        Log.e("CONFIRMBUTTONPRESSED", "1")
//                      uploadImageToFirebase(imageUri.value)
                        var string = convertImageUriToBase64(imageUri.value)
                        if (string != null) {
                            uploadImageToFirebase(string)
                        }
                    },
                    imageUri2 = imageUri.value,
                    imageUri1 = pictureUri.value
                )
            }
        }

    }

    fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val photoFile: File? = try {
            createImageFile()
        } catch (e: IOException) {
            null
        }

        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.example.physioconsult.provider",
                it
            )
            tempUri.value = photoURI
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            cameraResultLauncher.launch(cameraIntent)
        }

    }


    fun chooseFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        galleryResultLauncher.launch(Intent.createChooser(intent, "Select Picture"))
    }

    // Save the captured image to the gallery (can be disabled if we don't want to save the pictures on the phone)
    private fun saveImageToGallery(uri: Uri) {
        val contentResolver = contentResolver
        val imageStream = contentResolver.openInputStream(uri)

        imageStream?.use { inputStream ->
            val values = ContentValues().apply {
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    "CapturedImage_${System.currentTimeMillis()}.jpg"
                )
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/PhysioConsult"
                )
            }

            val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val itemUri = contentResolver.insert(collection, values)

            itemUri?.let { outputUri ->
                val outputStream = contentResolver.openOutputStream(outputUri)
                outputStream?.use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                Log.e("Gallery", "Image saved to: $outputUri")
            }
        }
    }

//    private fun uploadImageToFirebase(imageUri: Uri?) {
//        val storageRef = FirebaseStorage.getInstance().reference
//
//        Log.e("UPLOAD", "1")
//        if (imageUri == null) {
//            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
//            return
//        }else {
//            val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
//            val inputStream = contentResolver.openInputStream(imageUri)
//            val uploadTask = inputStream?.let { imageRef.putStream(it) }
//
//
//            Log.e("URIstatus", "Uri: ${imageUri}")
//            Log.e("URIstatus", "inputstream: ${inputStream}")
//            Log.e("URIstatus", "uploadtask: ${uploadTask}")
//            Log.e("URIstatus", "imageref: ${imageRef}")
//            uploadTask?.addOnSuccessListener { taskSnapshot ->
//                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
//                        // Save the download URL to Firebase Realtime Database or Firestore
//                        saveImageUriToDatabase(downloadUri.toString())
//                    }
//                    Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
//                }
//                ?.addOnFailureListener { e ->
//                    Log.e("FirebaseStorage", "Failed to upload image", e)
//                    Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//        }
//    }

    private fun convertImageUriToBase64(uri: Uri?): String? {
        if (uri != null) {
            try {
                // Open input stream to the image URI
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Compress the bitmap to a lower quality (e.g., 50% quality)
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)  // Reducing the quality to 50%

                val byteArray = byteArrayOutputStream.toByteArray()

                // Encode the byte array to Base64 string
                return Base64.encodeToString(byteArray, Base64.DEFAULT)
            } catch (e: Exception) {
                Log.e("ConvertToBase64", "Error converting image to Base64", e)
            }
        }
        return null
    }

    private fun uploadImageToFirebase(string: String) {
        // Reference to Firestore database
        val db = FirebaseFirestore.getInstance()

        // Sample text data
        val sampleText = string

        // Reference to a Firestore collection (e.g., "sampleTexts")
        val textDataRef = db.collection("sampleTexts").document() // Document will be auto-generated

        // Set the text data to Firestore
        val data = hashMapOf(
            "text" to sampleText
        )

        // Save the data to Firestore
        textDataRef.set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Text data uploaded successfully to Firestore", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Text data uploaded successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to upload text data", e)
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



    private fun saveImageUriToDatabase(imageUri: String) {
        val data = hashMapOf(
            field to imageUri
        )

        db.collection("assesments").add(data).addOnSuccessListener { reference ->
            Log.d("ImageURLsave", "DocumentSnapshot successfully written with ID: ${reference.id}")
        }
            .addOnFailureListener { e ->
                Log.w("ImageURLsave", "Error writing document", e)
            }
    }




    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: filesDir
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }


}

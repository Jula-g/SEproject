package com.example.physioconsult.fragments.user.add

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
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
import com.google.firebase.firestore.firestore
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import com.example.physioconsult.Main.MainActivity
import com.example.physioconsult.fragments.ImageUtils

import java.util.Date
import java.util.Locale

class Add : ComponentActivity() {
    private var iteration: Int = 1
    private lateinit var cameraResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>
    private val currentDate = Date() // Get the current date
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val imageDate = dateFormat.format(currentDate)

    private val db = Firebase.firestore
    private var field = ""
    private val imageManager = ImageUtils()

    private var pictureUri = mutableStateOf<Uri?>(null)
    private val imageUri = mutableStateOf<Uri?>(null)
    private val tempUri = mutableStateOf<Uri?>(null)

    private var frontImage =""
    private var backImage =""
    private var sideImage =""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setInitialPictureAndField("")

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
                        Log.e("OnChooseFromGalleryClick", "detected")
                        // TODO: check user's API level if above 33 -> use READ_EXTERNAL_STORAGE
                        //  if below -> READ_MEDIA_IMAEGS
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED) {
                            Log.e("OnChooseFromGalleryClick", "permissions checked")
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                1
                            )
                            Log.e("OnChooseFromGalleryClick", "asked for permission")
                            Log.e("OnChooseFromGalleryClick", "permissions: ${ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)}")
                        } else {
                            Log.e("OnChooseFromGalleryClick", "works")
                            chooseFromGallery()
                        }


                    },
                    onConfirmClick = {
                        val string = imageManager.convertImageUriToBase64(this, imageUri.value)
                        imageUri.value = null

                        iteration++

                        if (string != null) {
                            setInitialPictureAndField(string)
                        }

                        if (iteration >= 4){
                            iteration = 1
                            imageManager.uploadImageToFirebase(this, imageDate, frontImage, backImage, sideImage)
                            this.startActivity(Intent(this, MainActivity::class.java))
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
        Log.e("OnChooseFromGalleryClick", "FUNCTION")
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

    private fun setInitialPictureAndField(imageString: String) {
        when (iteration) {
            1 -> {
                pictureUri.value = Uri.parse("android.resource://com.example.physioconsult/drawable/front_view")
                field = "FrontURL"

            }
            2 -> {
                pictureUri.value = Uri.parse("android.resource://com.example.physioconsult/drawable/back_view")
                field = "BackURL"
                frontImage = imageString

            }
            3 -> {
                pictureUri.value = Uri.parse("android.resource://com.example.physioconsult/drawable/side_view")
                field = "SideURL"
                backImage = imageString

            }
            4 -> {
                sideImage = imageString
            }
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

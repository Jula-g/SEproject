package com.example.physioconsult.fragments.user.add

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Add : ComponentActivity(){
    private lateinit var cameraResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>

    private val imageUri = mutableStateOf<Uri?>(null)
    private val tempUri = mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.e("CAMRESLAUNCH", "URI: ${tempUri.value}")
                tempUri.value?.let {
                    saveImageToGallery(it)
                    imageUri.value = tempUri.value
                }
            }
        }

        galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
                            != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this,
                                arrayOf(Manifest.permission.CAMERA), 1)
                        } else {
                            openCamera()
                        }

                    },
                    onChooseFromGalleryClick = {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                        } else {
                            chooseFromGallery()
                        }

                    },
                    imageUri = imageUri.value
                )
            }
        }

    }

    fun openCamera(){
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


    fun chooseFromGallery(){
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
                put(MediaStore.Images.Media.DISPLAY_NAME, "CapturedImage_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PhysioConsult")
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

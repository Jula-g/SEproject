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
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
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
import androidx.compose.ui.platform.LocalContext
import com.example.physioconsult.Main.MainActivity

import java.util.Date
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Add : ComponentActivity() {
    private var iteration: Int = 1
    private lateinit var cameraResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>
    private val currentDate = Date() // Get the current date
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val imageDate = dateFormat.format(currentDate)

    private val db = Firebase.firestore
    private var field = ""

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
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.READ_MEDIA_IMAGES
                            ) != PackageManager.PERMISSION_GRANTED) {
                            Log.e("CHECK", "CHECK")
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                                1
                            )
                        } else {
                            chooseFromGallery()
                        }


                    },
                    onConfirmClick = {

                        Log.e("CONFIRMBUTTONPRESSED", "1")
//                      uploadImageToFirebase(imageUri.value)
                        var string = convertImageUriToBase64(imageUri.value)

                        iteration++

                        if (string != null) {
                            setInitialPictureAndField(string)
                        }

                        if (iteration >= 4){
                            uploadImageToFirebase()

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
        Log.e("FUNCTION", "FUNCTION")
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
                // Get rotated bitmap
                val rotatedBitmap = rotateImageIfRequired(this, uri)

                // Compress the rotated bitmap to a lower quality (e.g., 50% quality)
                val byteArrayOutputStream = ByteArrayOutputStream()
                rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)

                val byteArray = byteArrayOutputStream.toByteArray()

                // Encode the byte array to Base64 string
                return Base64.encodeToString(byteArray, Base64.DEFAULT)
            } catch (e: Exception) {
                Log.e("ConvertToBase64", "Error converting image to Base64", e)
            }
        }
        return null
    }


    private fun uploadImageToFirebase() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid



        val db = FirebaseFirestore.getInstance()


        // Reference to a Firestore collection
        val textDataRef =
            uid?.let { db.collection(it).document(imageDate) } // Document will be auto-generated

        val field = arrayOf("Front", "Back", "Side")

        val images = arrayOf(frontImage, backImage, sideImage)


        // Set the text data to the fields in one document
        val data = mutableMapOf<String, String>()
        for (i in field.indices) {
            data[field[i]] = images[i] // Assign the field to its corresponding image
        }

        // Save the data to Firestore
        if (textDataRef != null) {
            textDataRef.set(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show()
                    navigateMain(this)

                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }






    private fun rotateImageIfRequired(context: Context, imageUri: Uri): Bitmap? {
        val inputStream = context.contentResolver.openInputStream(imageUri) ?: return null

        // Read EXIF metadata
        val exif = ExifInterface(inputStream)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri))

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateImage(bitmap: Bitmap, degrees: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
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

    fun navigateMain(context: Context) {
        context.startActivity(Intent(context, MainActivity::class.java))
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

package com.example.physioconsult.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class ImageUtils{

    fun convertImageUriToBase64(context: Context, uri: Uri?): String? {
        if (uri != null) {
            try {
                // Get rotated bitmap
                val rotatedBitmap = rotateImageIfRequired(context, uri)

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

    fun uploadImageToFirebase(context: Context, imageDate: String, frontImage: String, backImage: String, sideImage: String, onDocumentIdRetrieved: (String) -> Unit) {
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
                    val documentId = textDataRef.id
                    Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT).show()
//                    navigateMain(this)

                    onDocumentIdRetrieved(documentId)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    fun rotateImageIfRequired(context: Context, imageUri: Uri): Bitmap? {
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

    fun rotateImage(bitmap: Bitmap, degrees: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    // retrieves all fields from specified document in a collection, returns a list of uri values
    fun retrieveImageFromFirestore(context: Context, documentId: String, userId: String, imageFields: List<String>, imageUris: (List<Uri?>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(userId).document(documentId)

        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val uris = mutableListOf<Uri?>()

                    for (imageField in imageFields) {
                        val base64String = documentSnapshot.getString(imageField)

                        // Convert Base64 string to Bitmap
                        if (base64String != null) {
                            val bitmap = convertBase64ToBitmap(base64String)
                            if (bitmap != null) {
                                val uri = getImageUriFromBitmap(context, bitmap)
                                uris.add(uri)
                            }
                        }
                    }

                    imageUris(uris)
                } else {
                    Log.e("Firestore", "Document does not exist")
                    imageUris(emptyList())
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error retrieving document", e)
                imageUris(emptyList())
            }
    }

    // Retrieve image from Firestore (Base64)
//    fun retrieveImageFromFirestore(context: Context, documentId: String, userId: String, imageField: String, imageUri: MutableState<Uri?>) {
//        val db = FirebaseFirestore.getInstance()
//        val docRef = db.collection(userId).document(documentId)
//
//        docRef.get()
//            .addOnSuccessListener { documentSnapshot ->
//                if (documentSnapshot.exists()) {
//                    val base64String = documentSnapshot.getString(imageField)
//
//                    // Convert Base64 string to Bitmap
//                    if (base64String != null) {
//                        val bitmap = convertBase64ToBitmap(base64String)
//                        if (bitmap != null) {
//                            imageUri.value = getImageUriFromBitmap(context, bitmap) // Display the decoded Bitmap
//                        }
//                    }
//                } else {
//                    Log.e("Firestore", "Document does not exist")
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("Firestore", "Error retrieving document", e)
//            }
//    }

    fun convertBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            Log.e("Base64ToBitmap", "Error decoding Base64 string", e)
            null
        }
    }

    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Image", null)
        return Uri.parse(path)
    }
}
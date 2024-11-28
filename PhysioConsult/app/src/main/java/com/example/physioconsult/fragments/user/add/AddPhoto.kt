package com.example.physioconsult.fragments.user.add

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun AddPhoto(onTakePhotoClick: () -> Unit, onChooseFromGalleryClick: () -> Unit, imageUri: Uri? = null) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Image display if imageUri is not null
            imageUri?.let {
                Image(
                    painter = rememberImagePainter(it),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(750.dp)
                        .padding(32.dp)
                )
            }


            // Column to hold buttons, aligned at the bottom center
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter) // Center the buttons horizontally at the bottom
                    .padding(16.dp)
            ) {
                // Row with buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between buttons
                ) {
                    // "Take Photo" button
                    FilledTonalButton(onClick = { onTakePhotoClick() }, modifier = Modifier.weight(1f)) {
                        Text("Take Photo")
                    }

                    // "Choose from Gallery" button
                    FilledTonalButton(onClick = { onChooseFromGalleryClick() }, modifier = Modifier.weight(1f)) {
                        Text("Choose from Gallery")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPhotoPreview() {
    AddPhoto(
        onTakePhotoClick = { /* No-op for preview */ },
        onChooseFromGalleryClick = { /* No-op for preview */ },
        imageUri = Uri.parse("android.resource://com.example.physioconsult/res/drawable/front_back_view_prev.jpg")
    )
}

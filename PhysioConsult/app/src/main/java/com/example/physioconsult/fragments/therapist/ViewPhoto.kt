package com.example.physioconsult.fragments.therapist

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun AddPhoto(onTakePhotoClick: () -> Unit, onChooseFromGalleryClick: () -> Unit, onConfirmClick: () -> Unit, imageUri1: Uri? = null,imageUri2: Uri? = null) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box{
                imageUri2?.let {
                    Image(
                        painter = rememberImagePainter(it),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .height(400.dp)
                            .width(300.dp)
                            .padding(32.dp)
                    )
                }

                imageUri1?.let {
                    Image(
                        painter = rememberImagePainter(it),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .height(400.dp)
                            .width(300.dp)
                            .padding(32.dp)
                    )
                }
            }

            // Column to hold buttons, aligned at the bottom center
            Column(
                modifier = Modifier
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
                        Text("Retrieve Photo")
                    }

                    // "Choose from Gallery" button
                    FilledTonalButton(onClick = { onChooseFromGalleryClick() }, modifier = Modifier.weight(1f)) {
                        Text("Choose from Gallery")
                    }
                }

                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                val messages = listOf(
                    "1. Take off your shoes",
                    "2. Stand on a flat and hard surface",
                    "3. Dont have anything in your pockets ",
                    "4. Dont have arm/ leg jewellery such as watches",
                    "5. Tie up longer hair ",
                    "6. Wear tight clothing eg sports bra and shorts"
                )

                val textFieldStates = remember { messages.map { mutableStateOf(TextFieldValue(it)) } }
                val checkboxStates = remember { List(messages.size) { mutableStateOf(false) } }


                messages.forEachIndexed() { index, message ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.5f))
                            .weight(1f)
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checkboxStates[index].value,
                            onCheckedChange = { checkboxStates[index].value = it }
                        )

                        // Text field
                        BasicTextField(
                            value = textFieldStates[index].value,
                            onValueChange = { textFieldStates[index].value = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            keyboardOptions = KeyboardOptions.Default
                        )
                    }
                }

                val areAllChecked = checkboxStates.all { it.value }

                // Confirm button
                FilledTonalButton(
                    onClick = { onConfirmClick() },
                    enabled = areAllChecked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Confirm")
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
        onConfirmClick = { /*No-op for preview*/ },
        imageUri1 = Uri.parse("android.resource://com.example.physioconsult/drawable/front_view")
    )
}

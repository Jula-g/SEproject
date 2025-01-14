package com.example.physioconsult.fragments.user.assesment

import android.net.Uri
import android.util.Log
import com.example.physioconsult.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.physioconsult.Main.BottomNavigationBar
import java.net.URI

@Composable
fun AssesmentForm(onNextClick: () -> Unit, onPreviousClick: () -> Unit, onCloseClick: () -> Unit, onSavePDFClick: () -> Unit, onGenerateCode: () -> Unit, uriList: MutableList<Uri?>, angleResults:Map<String, String>, lengthResults: Map<String, String>, index: Int){
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(2.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1.2f)
                ) {
                    val uri = uriList[index]
                    if (uri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Image",
                            modifier = Modifier
                                .background(color = Color.DarkGray)
                                .align(Alignment.Center)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.missing_image),
                            contentDescription = "Example Drawable",
                            modifier = Modifier.size(100.dp)
                        )
                    }


                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        // Previous Button
                        IconButton(
                            onClick = {
                                onPreviousClick()
                                Log.d("AssesmentForm", "previous button clicked")
                                      },
                            enabled = index > 0,
                            modifier = Modifier
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Previous",
                                tint = Color.White
                            )
                        }

                        // Next Button
                        IconButton(
                            onClick = {
                                onNextClick()
                                Log.d("AssesmentForm", "next button clicked")
                                      },
                            enabled = index < uriList.size-1,
                            modifier = Modifier
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = "Next",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .weight(2f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val angles = listOf("Hips", "Shoulders")
                    val lengths = listOf("Thigh L", "Thigh R", "Shin L", "Shin R", "Forearm L", "Forearm R", "Arm L", "Arm R")
                    val lengths2 = listOf("Thigh L", "Shin L", "Forearm L", "Arm L")

                    // Angles Section
                    Text(
                        text = "Angles",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        ),
                        modifier = Modifier.padding(4.dp)
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(2.dp),
                        color = Color.LightGray
                    )

                    Column(modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(vertical = 16.dp)
                        .background(Color.LightGray)
                    ) {
                        angles.forEach { angle ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.LightGray)
                            ) {
                                Text(
                                    text = angle,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp)
                                        .align(Alignment.CenterVertically),
                                    color = Color.Black
                                )

                                Text(
                                    text = angleResults[angle] ?: "No Result",
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp)
                                        .align(Alignment.CenterVertically),
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lengths Section
                    Text(
                        text = "Lengths",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(2.dp),
                        color = Color.LightGray
                    )

                    Column(modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(vertical = 16.dp)
                        .background(Color.LightGray)
                        .verticalScroll(rememberScrollState())
                    ) {
                        val lengthsToUse = if (index == 2) lengths2 else lengths
                        lengthsToUse.forEach { length ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = length,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp)
                                        .align(Alignment.CenterVertically),
                                    color = Color.Black
                                )

                                Text(
                                    text = lengthResults[length] ?: "No Result",
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp)
                                        .align(Alignment.CenterVertically),
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = { onSavePDFClick() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF84ACD8),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Save PDF",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    FilledTonalButton(
                        onClick = { onGenerateCode() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF84ACD8),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Access code",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                FilledTonalButton(
                    onClick = { onCloseClick() },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(48.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF84ACD8),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Close",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AssesmentFormPreview() {
    val sampleImages = mutableListOf(Uri.parse("android.resource://com.example.physioconsult/drawable/front_view"))
    val sampleAngleMap = mapOf("Hips" to "45°", "Shoulders" to "30°")
    val sampleLengthMap = mapOf(
        "Thigh L" to "60 cm", "Thigh R" to "58 cm", "Shin L" to "40 cm", "Shin R" to "42 cm",
        "Forearm L" to "35 cm", "Forearm R" to "34 cm", "Arm L" to "50 cm", "Arm R" to "48 cm"
    )

    AssesmentForm(
        onNextClick = { /* Handle back click */ },
        onPreviousClick = { /* Handle back click */ },
        onCloseClick = { /* Handle back click */ },
        onSavePDFClick = { /* Handle Save PDF click */ },
        onGenerateCode = { /* Handle Generate Code click */ },
        uriList = sampleImages,
        angleResults = sampleAngleMap,
        lengthResults = sampleLengthMap,
        index = 1
    )
}
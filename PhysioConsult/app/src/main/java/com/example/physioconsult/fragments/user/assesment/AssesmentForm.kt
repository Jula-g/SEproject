package com.example.physioconsult.fragments.user.assesment

import android.net.Uri
import com.example.physioconsult.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
fun AssesmentForm(onNextClick: () -> Unit, onPreviousClick: () -> Unit, onBackClick: () -> Unit, onSavePDFClick: () -> Unit, onGenerateCode: () -> Unit, uri: Uri, angleResults: List<String>, lengthResults: List<String>){
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
                            onClick = { onPreviousClick() },
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
                            onClick = { onNextClick() },
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


            // Lower row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .weight(2f),
                contentAlignment = Alignment.Center
            ) {

                // TODO:
                //  display angles: base angle (ankles), hip angle, shoulder angle
                //  display lengths: Thigh, Shin, Forearm, Arm (left and right)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    val angles = listOf(
                        "Hips",
                        "Shoulders",
                    )

                    val lengths = listOf(
                        "Thigh L",
                        "Thigh R",
                        "Shin L",
                        "Shin R",
                        "Forearm L",
                        "Forearm R",
                        "Arm L",
                        "Arm R"
                    )

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
                        angles.forEachIndexed { i, angle ->
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
                                    color = Color.Black,

                                    )

                                Text(
                                    text = angleResults[i],
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
                    ) {
                        lengths.forEachIndexed { i, length ->
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
                                    color = Color.Black,

                                    )

                                Text(
                                    text = lengthResults[i],
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
                            containerColor = Color(0xFF84ACD8),    // Background color
                            contentColor = Color.White      // Text color
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
                            containerColor = Color(0xFF84ACD8),    // Background color
                            contentColor = Color.White      // Text color
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
                    onClick = { onBackClick() },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(48.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF84ACD8),    // Background color
                        contentColor = Color.White      // Text color
                    )
                ) {
                    Text(
                        text = "Back",
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
    val sampleImages = Uri.parse("android.resource://com.example.physioconsult/drawable/front_view")
    val sampleAngleList = List(2) { "Result $it" }
    val sampleLengthList = List(8) { "Result $it" }

    AssesmentForm(
        onNextClick = { /* Handle back click */ },
        onPreviousClick = { /* Handle back click */ },
        onBackClick = { /* Handle back click */ },
        onSavePDFClick = { /* Handle Save PDF click */ },
        onGenerateCode = { /* Handle Generate Code click */ },
        uri = sampleImages,
        angleResults = sampleAngleList,
        lengthResults = sampleLengthList
    )
}
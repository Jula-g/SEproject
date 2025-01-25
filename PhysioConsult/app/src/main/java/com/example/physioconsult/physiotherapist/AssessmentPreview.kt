package com.example.physioconsult.physiotherapist

import android.graphics.Bitmap
import android.util.Log
import com.example.physioconsult.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*

@Composable
fun AssessmentPreview(
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onCloseClick: () -> Unit,
    list: MutableList<Bitmap?>,
    angleResults: Map<String, Map<String, String>>,
    lengthResults: Map<String, Map<String, String>>,
    index: Int
) {
    var currentIndex by remember { mutableStateOf(index) }
    val colors = MaterialTheme.colorScheme

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(2.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1.2f)
                ) {
                    val bitmap = list[currentIndex]
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Image",
                            modifier = Modifier
                                .background(color = Color.DarkGray)
                                .align(Alignment.Center)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.missing_image),
                            contentDescription = "Missing Image",
                            modifier = Modifier.size(100.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        IconButton(
                            onClick = {
                                if (currentIndex > 0) {
                                    currentIndex--
                                    onPreviousClick()
                                }
                            },
                            enabled = currentIndex > 0,
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

                        IconButton(
                            onClick = {
                                if (currentIndex < list.size - 1) {
                                    currentIndex++
                                    onNextClick()
                                }
                            },
                            enabled = currentIndex < list.size - 1,
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

            Column(modifier = Modifier.padding(16.dp)) {
                val currentKey = when (currentIndex) {
                    0 -> "front"
                    1 -> "back"
                    2 -> "side"
                    else -> ""
                }

                if (currentKey == "side") {
                    val expectedSideKeys = listOf("Arm L", "Forearm L", "Shin L", "Thigh L")
                    expectedSideKeys.forEach { key ->
                        val value = lengthResults[currentKey]?.get(key) ?: "N/A"
                        Row {
                            Text(text = key, modifier = Modifier.weight(1f))
                            Text(text = value, modifier = Modifier.weight(1f))
                        }
                    }
                } else {
                    lengthResults[currentKey]
                        ?.forEach { (key, value) ->
                            Row {
                                Text(text = key, modifier = Modifier.weight(1f))
                                Text(text = value, modifier = Modifier.weight(1f))
                            }
                        }

                    angleResults[currentKey]?.forEach { (key, value) ->
                        Row {
                            Text(text = key, modifier = Modifier.weight(1f))
                            Text(text = value, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            FilledTonalButton(
                onClick = onCloseClick,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF84ACD8))
            ) {
                Text(text = "Close", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp))
            }
        }
    }
}

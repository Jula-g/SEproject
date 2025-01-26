package com.example.physioconsult.Main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * Composable function that displays a card button for assessments.
 *
 * @param text The text to display inside the card.
 * @param title The title to display inside the card.
 * @param description The description to display inside the card.
 * @param icon The icon to display inside the card.
 * @param backgroundColor The background color of the card.
 */

@Composable
fun CardButtonAssessments(
    text: String,
    title: String,
    description: String,
    icon: Painter,
    backgroundColor: Color,
    onClick: () -> Unit

) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val colors = MaterialTheme.colorScheme

        Text(
            text = text,
            fontSize = 24.sp,
            color = colors.onSurface,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Image(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(85.dp)
                        .padding(bottom = 5.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(text = title, fontSize = 18.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = description, fontSize = 14.sp, color = Color.White)
                }
            }
        }
    }
}

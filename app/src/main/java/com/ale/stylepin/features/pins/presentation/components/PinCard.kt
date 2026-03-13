package com.ale.stylepin.features.pins.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ale.stylepin.features.pins.domain.entities.Pin
import kotlin.random.Random

@Composable
fun PinCard(
    pin: Pin,
    currentUserId: String? = null,
    onPinClick: (String) -> Unit,
    onLikeClick: (String) -> Unit = {}
) {
    val height = androidx.compose.runtime.remember { Random.nextInt(180, 320).dp }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onPinClick(pin.id) }
    ) {
        Box {
            AsyncImage(
                model = pin.imageUrl,
                contentDescription = pin.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            // Like pequeño en el grid
            IconButton(
                onClick = { onLikeClick(pin.id) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = if (pin.isLikedByMe) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (pin.isLikedByMe) Color.Red else Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = pin.title, style = MaterialTheme.typography.titleSmall, maxLines = 2)
    }
}
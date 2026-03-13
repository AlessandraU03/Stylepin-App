package com.ale.stylepin.features.pins.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ale.stylepin.features.pins.domain.entities.Pin
import kotlin.random.Random

@Composable
fun PinCard(
    pin: Pin,
    onPinClick: (String) -> Unit
) {
    // Altura aleatoria para dar el efecto Pinterest en cascada
    val height = androidx.compose.runtime.remember { Random.nextInt(180, 320).dp }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onPinClick(pin.id) }
    ) {
        AsyncImage(
            model = pin.imageUrl,
            contentDescription = pin.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = pin.title,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2
        )
    }
}
package com.ale.stylepin.features.pins.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ale.stylepin.features.pins.domain.entities.Pin

@Composable
fun PinCard(
    pin: Pin,
    onPinClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPinClick(pin.id) }
    ) {
        AsyncImage(
            model = pin.imageUrl,
            contentDescription = pin.title,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 120.dp)
                .clip(RoundedCornerShape(16.dp)), // Bordes redondeados nativos
            contentScale = ContentScale.FillWidth // Permite el diseño escalonado (Staggered)
        )
        if (pin.title.isNotBlank()) {
            Text(
                text = pin.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 6.dp, start = 4.dp, end = 4.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
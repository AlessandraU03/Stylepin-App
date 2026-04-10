package com.ale.stylepin.features.explore.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ale.stylepin.features.explore.domain.entities.TrendingBoard

@Composable
fun TrendingBoardCard(
    item: TrendingBoard,
    onClick: () -> Unit
) {
    val urls = item.previewUrls

    Column(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Cuadrado perfecto
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            when {
                urls.size >= 3 -> {
                    // Diseño 3 Fotos: 1 izquierda completa, 2 a la derecha divididas horizontalmente
                    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        AsyncImage(
                            model = urls[0], contentDescription = null,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            AsyncImage(
                                model = urls[1], contentDescription = null,
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                            AsyncImage(
                                model = urls[2], contentDescription = null,
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                urls.size == 2 -> {
                    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        AsyncImage(model = urls[0], contentDescription = null, modifier = Modifier.weight(1f).fillMaxHeight(), contentScale = ContentScale.Crop)
                        AsyncImage(model = urls[1], contentDescription = null, modifier = Modifier.weight(1f).fillMaxHeight(), contentScale = ContentScale.Crop)
                    }
                }
                urls.size == 1 -> {
                    AsyncImage(model = urls[0], contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(item.board.name.take(1).uppercase(), style = MaterialTheme.typography.displayMedium, color = Color.Gray)
                    }
                }
            }
        }

        Text(
            text = item.board.name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp, start = 4.dp, end = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${item.board.pinsCount} pins",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
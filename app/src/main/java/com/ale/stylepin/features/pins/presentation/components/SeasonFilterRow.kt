package com.ale.stylepin.features.pins.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SeasonFilterRow(
    selectedSeason: String,
    onSeasonSelected: (String) -> Unit
) {
    // Valores exactos de tu ENUM de Python
    val seasons = listOf("todo_el_ano", "primavera", "verano", "otono", "invierno")

    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(seasons) { season ->
            FilterChip(
                selected = selectedSeason == season,
                onClick = { onSeasonSelected(season) },
                label = {
                    // Ponemos la primera letra en mayúscula solo para la vista
                    Text(season.replace("_", " ").replaceFirstChar { it.uppercase() })
                }
            )
        }
    }
}
package com.ale.stylepin.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val routeName: String // Nombre de la ruta para comparar
)

@Composable
fun StylePinBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    // Usamos el nombre de las clases serializables como referencia
    val items = listOf(
        BottomNavItem("Inicio", Icons.Filled.Home, Icons.Outlined.Home, "com.ale.stylepin.core.navigation.PinsRoute"),
        BottomNavItem("Explorar", Icons.Filled.Search, Icons.Outlined.Search, "com.ale.stylepin.core.navigation.SearchRoute"),
        // Espacio para el FAB central
        BottomNavItem("", Icons.Default.Home, Icons.Default.Home, "dummy"),
        BottomNavItem("Alertas", Icons.Filled.Notifications, Icons.Outlined.Notifications, "com.ale.stylepin.core.navigation.AlertsRoute"),
        BottomNavItem("Perfil", Icons.Filled.Person, Icons.Outlined.Person, "com.ale.stylepin.core.navigation.ProfileRoute")
    )

    BottomAppBar(
        modifier = Modifier.height(70.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                if (index == 2) {
                    // Ítem invisible para dejar hueco al Botón Flotante
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { },
                        enabled = false,
                        colors = NavigationBarItemDefaults.colors(
                            disabledIconColor = Color.Transparent,
                            disabledTextColor = Color.Transparent
                        )
                    )
                } else {
                    val isSelected = currentRoute == item.routeName
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { onNavigate(item.title) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }
    }
}
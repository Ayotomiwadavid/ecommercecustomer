package tech.azurestar.kmp.ecommercecustomer.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavLocations(val route: NavLocations, val title: String, val image: ImageVector) {
    data object Home : BottomNavLocations(NavLocations.HOME, "Home", Icons.Default.Home)
    data object Cart : BottomNavLocations(NavLocations.CART, "Cart", Icons.Default.ShoppingCart)
    data object Orders : BottomNavLocations(NavLocations.ORDERS, "Orders", Icons.AutoMirrored.Filled.ListAlt)
    data object Profile : BottomNavLocations(NavLocations.PROFILE, "Profile", Icons.Default.AccountCircle)
}
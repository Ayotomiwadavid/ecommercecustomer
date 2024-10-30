package tech.azurestar.kmp.ecommercecustomer.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import tech.azurestar.kmp.ecommercecustomer.ui.nav.BottomNavLocations
import tech.azurestar.kmp.ecommercecustomer.ui.nav.NavLocations


@Composable
fun DefaultBottomNavBar(navController: NavController) {

    val list = listOf(BottomNavLocations.Home, BottomNavLocations.Cart, BottomNavLocations.Orders, BottomNavLocations.Profile)

    NavigationBar {
        BottomAppBar {
            list.forEach { location ->
                NavigationBarItem(
                    icon = { Icon(imageVector = location.image, contentDescription = location.title) },
                    label = { Text(text = location.title) },
                    selected = navController.currentDestination?.route == location.route.name,
                    onClick = {
                        navController.navigate(location.route.name) {
                            popUpTo(0) {
                                saveState = true
                            }
                        }
                    }
                )
            }
        }
    }
}
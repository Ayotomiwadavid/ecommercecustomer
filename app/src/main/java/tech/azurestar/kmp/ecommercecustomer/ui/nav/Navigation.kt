package tech.azurestar.kmp.ecommercecustomer.ui.nav

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import org.koin.compose.koinInject
import tech.azurestar.kmp.ecommercecustomer.db.item.CartItem
import tech.azurestar.kmp.ecommercecustomer.db.item.Item
import tech.azurestar.kmp.ecommercecustomer.ui.auth.LoginAuthScreen
import tech.azurestar.kmp.ecommercecustomer.ui.auth.SignUpAuthScreen
import tech.azurestar.kmp.ecommercecustomer.ui.cart.CartScreen
import tech.azurestar.kmp.ecommercecustomer.ui.home.HomeScreen
import tech.azurestar.kmp.ecommercecustomer.ui.home.ItemDescriptionScreen
import tech.azurestar.kmp.ecommercecustomer.ui.order.CheckoutScreen
import tech.azurestar.kmp.ecommercecustomer.ui.order.OrderScreen
import tech.azurestar.kmp.ecommercecustomer.ui.order.SelectAddress
import tech.azurestar.kmp.ecommercecustomer.ui.profile.ProfileScreen
import tech.azurestar.kmp.ecommercecustomer.vm.AuthViewModel
import tech.azurestar.kmp.ecommercecustomer.vm.DBViewModel

@Composable
fun Navigation() {

    val navController = rememberNavController()

    val authViewModel = koinInject<AuthViewModel>()
    val dbViewModel = koinInject<DBViewModel>()

    val isAuthenticated = authViewModel.currentUser.collectAsStateWithLifecycle().value != null
    val hasCustomerAccount = dbViewModel.customerAccount.collectAsStateWithLifecycle().value != null
    val items = dbViewModel.items.collectAsStateWithLifecycle().value

    NavHost(
        navController = navController,
        startDestination = when {
            isAuthenticated && hasCustomerAccount -> NavLocations.HOME.name
            isAuthenticated -> NavLocations.PROFILE.name
            else -> NavLocations.AUTH_LOGIN.name
        }
    ) {
        composable(NavLocations.AUTH_LOGIN.name) {
            LoginAuthScreen(navController)
        }
        composable(NavLocations.AUTH_SIGN_UP.name) {
            SignUpAuthScreen(navController)
        }
        composable(NavLocations.PROFILE.name) {
            ProfileScreen(navController)
        }
        composable(NavLocations.HOME.name) {
            HomeScreen(navController)
        }
        composable("${NavLocations.ITEM_DESCRIPTION.name}/{itemId}", arguments = listOf(navArgument("itemId") { type = NavType.LongType })) {
            ItemDescriptionScreen(it.arguments?.getLong("itemId")!!)
        }
        composable(NavLocations.CART.name) {
             CartScreen(navController)
        }
        composable(NavLocations.ORDERS.name) {
            OrderScreen(navController)
        }
        composable(NavLocations.CHECKOUT.name) {
            CheckoutScreen(navController)
        }
    }
}
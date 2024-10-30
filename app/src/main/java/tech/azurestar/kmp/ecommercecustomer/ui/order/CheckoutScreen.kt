package tech.azurestar.kmp.ecommercecustomer.ui.order

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import org.koin.compose.koinInject
import tech.azurestar.kmp.ecommercecustomer.db.item.CartItem
import tech.azurestar.kmp.ecommercecustomer.db.item.Item
import tech.azurestar.kmp.ecommercecustomer.ui.DefaultScaffold
import tech.azurestar.kmp.ecommercecustomer.vm.CheckoutViewModel
import tech.azurestar.kmp.ecommercecustomer.vm.OrderViewModel

@Composable
fun CheckoutScreen(navController: NavController) {

    val orderViewModel = koinInject<OrderViewModel>()
    val checkoutViewModel = koinInject<CheckoutViewModel>()

    var addressId by remember { mutableStateOf<Long?>(null) }

    if(addressId == null) {
        SelectAddress { addressId = it.id }
    }

    if(addressId != null) {
        DefaultScaffold {
            Button(onClick = { orderViewModel.placeOrder(checkoutViewModel.cartItems, checkoutViewModel.items,
                addressId!!
            ) }) {
                Text(text = "Complete payment")
            }
        }
    }
}
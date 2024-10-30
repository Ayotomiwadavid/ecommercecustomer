package tech.azurestar.kmp.ecommercecustomer.vm

import androidx.lifecycle.ViewModel
import tech.azurestar.kmp.ecommercecustomer.db.item.CartItem
import tech.azurestar.kmp.ecommercecustomer.db.item.Item

class CheckoutViewModel: ViewModel() {
    var cartItems: List<CartItem> = emptyList()
    var items: List<Item> = emptyList()
}
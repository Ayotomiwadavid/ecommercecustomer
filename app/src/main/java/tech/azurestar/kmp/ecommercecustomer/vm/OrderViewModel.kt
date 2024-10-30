package tech.azurestar.kmp.ecommercecustomer.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.azurestar.kmp.ecommercecustomer.db.DatabaseConstants
import tech.azurestar.kmp.ecommercecustomer.db.item.Address
import tech.azurestar.kmp.ecommercecustomer.db.item.CartItem
import tech.azurestar.kmp.ecommercecustomer.db.item.Item
import tech.azurestar.kmp.ecommercecustomer.db.item.Order
import tech.azurestar.kmp.ecommercecustomer.db.item.OrderItem
import java.util.UUID

class OrderViewModel(supabaseClient: SupabaseClient) : ViewModel() {

    val auth = supabaseClient.auth
    val currentUser get() = auth.currentUserOrNull()
    val postgrest = supabaseClient.postgrest

    val addresses = MutableStateFlow<List<Address>>(emptyList())
    val orders = MutableStateFlow<List<Order>>(emptyList())
    val orderItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val items = MutableStateFlow<List<Item>>(emptyList())

    fun initialize() {
        getOrders()
        getOrderItems()
        getAddresses()
    }

    @OptIn(SupabaseExperimental::class)
    fun getOrders() {
        viewModelScope.launch {
            try {
                postgrest.from(DatabaseConstants.TABLE_ORDERS).selectAsFlow(
                    Order::id,
                ).collectLatest {
                    orders.value = it
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    @OptIn(SupabaseExperimental::class)
    fun getOrderItems() {
        viewModelScope.launch {
            try {

                postgrest.from(DatabaseConstants.TABLE_ORDER_ITEMS).selectAsFlow(
                    OrderItem::id
                ).collectLatest {
                    orderItems.value = it
                    items.value = postgrest.from(DatabaseConstants.TABLE_ITEMS).select {
                        filter {
                            it.forEach {
                                or {
                                    Item::id eq it.itemId
                                }
                            }
                            if (it.isEmpty()) {
                                and {
                                    Item::id eq -1
                                }
                            }
                        }
                    }.decodeList()
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    @OptIn(SupabaseExperimental::class)
    fun getAddresses() {
        viewModelScope.launch {
            try {
                postgrest.from(DatabaseConstants.TABLE_ADDRESS).selectAsFlow(
                    Address::id
                ).collectLatest {
                    addresses.value = it
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    fun addAddress(address: Address) {
        viewModelScope.launch {
            postgrest.from(DatabaseConstants.TABLE_ADDRESS).insert(address)
        }
    }

    fun editAddress(address: Address) {
        viewModelScope.launch {
            postgrest.from(DatabaseConstants.TABLE_ADDRESS).update(address) {
                filter {
                    Address::id eq address.id
                }
            }
        }
    }

    fun deleteAddress(address: Address) {
        viewModelScope.launch {
            postgrest.from(DatabaseConstants.TABLE_ADDRESS).delete {
                filter {
                    Address::id eq address.id
                }
            }
        }
    }

    fun placeOrder(cartItems: List<CartItem>, items: List<Item>, addressId: Long) {
        viewModelScope.launch {

            val orderItems = mutableListOf<OrderItem>()

            cartItems.forEach { cartItem ->
                val itemInCart = items.find { it.id == cartItem.itemId }
                val orderItem = OrderItem(
                    userId = UUID.fromString(auth.currentUserOrNull()!!.id),
                    sellerId = itemInCart!!.userId,
                    itemId = cartItem.itemId,
                    quantity = cartItem.quantity,
                    options = cartItem.options,
                    totalPrice = cartItem.quantity * (itemInCart!!.price + cartItem.options.sumOf {
                        (it.values.values.firstOrNull() ?: 0).toDouble()
                    }),
                    delivered = false
                )
                val item = postgrest.from(DatabaseConstants.TABLE_ORDER_ITEMS)
                    .insert(orderItem) { select() }.decodeSingle<OrderItem>()
                orderItems.add(item)
            }

            val order = Order(
                orderItemsIds = orderItems.map { it.id },
                price = orderItems.sumOf { it.totalPrice },
                userId = UUID.fromString(auth.currentUserOrNull()!!.id),
                addressId = addressId
            )

            postgrest.from(DatabaseConstants.TABLE_ORDERS).insert(order)
        }
    }
}
package tech.azurestar.kmp.ecommercecustomer.ui.order

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import org.koin.compose.koinInject
import tech.azurestar.kmp.ecommercecustomer.db.item.Item
import tech.azurestar.kmp.ecommercecustomer.db.item.Order
import tech.azurestar.kmp.ecommercecustomer.db.item.OrderItem
import tech.azurestar.kmp.ecommercecustomer.ui.DefaultScaffold
import tech.azurestar.kmp.ecommercecustomer.vm.OrderViewModel

@Composable
fun OrderScreen(navController: NavController) {
    val orderViewModel = koinInject<OrderViewModel>()

    val orders = orderViewModel.orders.collectAsState().value
    val orderItems = orderViewModel.orderItems.collectAsState().value
    val items = orderViewModel.items.collectAsState().value

    DefaultScaffold {
        LazyColumn {
            items(orders.size) { index ->
                val order = orders[index]
                val orderItems = orderItems.filter { order.orderItemsIds.contains(it.id) }
                val items = items.filter { orderItems.map { orderItem -> orderItem.itemId }.contains(it.id) }
                OrderDetails(order = order, orderItems = orderItems, items = items)
            }
        }
    }
}

@Composable
fun OrderDetails(order: Order, orderItems: List<OrderItem>, items: List<Item>) {
    Column {
        Text(text = "Order ID: ${order.id}")
        Text(text = "Order Price: ${orderItems.sumOf { orderItem -> items.find { it.id == orderItem.itemId }?.price ?: 0.0 }}")
        Text(text = "Order Items: ${orderItems.size}")
        orderItems.forEach {
            Text(text = "Item ID: ${it.id}")
            Text(text = "Item Quantity: ${it.quantity}")
            Text(text = "Item Price: ${items.find { item -> item.id == it.itemId }?.price}")
        }
    }
}
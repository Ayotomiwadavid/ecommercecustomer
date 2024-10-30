package tech.azurestar.kmp.ecommercecustomer.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import io.github.jan.supabase.storage.authenticatedStorageItem
import org.koin.compose.koinInject
import tech.azurestar.kmp.ecommercecustomer.db.item.CartItem
import tech.azurestar.kmp.ecommercecustomer.db.item.Item
import tech.azurestar.kmp.ecommercecustomer.language.TextProvider
import tech.azurestar.kmp.ecommercecustomer.ui.DefaultScaffold
import tech.azurestar.kmp.ecommercecustomer.ui.components.NumberPicker
import tech.azurestar.kmp.ecommercecustomer.ui.components.NumberPickerOrientation
import tech.azurestar.kmp.ecommercecustomer.ui.nav.NavLocations
import tech.azurestar.kmp.ecommercecustomer.vm.CheckoutViewModel
import tech.azurestar.kmp.ecommercecustomer.vm.DBViewModel
import tech.azurestar.kmp.ecommercecustomer.vm.IMAGES
import tech.azurestar.kmp.ecommercecustomer.vm.getCartItems
import tech.azurestar.kmp.ecommercecustomer.vm.getItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController) {

    val dbViewModel = koinInject<DBViewModel>()
    val checkoutViewModel = koinInject<CheckoutViewModel>()

    val cart = dbViewModel.cartItems.collectAsState().value
    val cartItems = cart.getCartItems()
    val items = cart.getItems()
    var selectedItems by remember { mutableStateOf<List<Long>>(emptyList()) }

    DefaultScaffold(navController = navController, topBar = {
        TopAppBar(title = { Text(TextProvider.CART.getText()) }, actions = {
            SelectAllButton(
                cart = cart,
                selectedItems = selectedItems.map { id -> cartItems.find { it.id == id }!! },
                onSelectAll = { selectedItems = cartItems.map { it.id } }, {
                    selectedItems = emptyList()
                })
        })
    }) {
        Column {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(items.size) { id ->
                    val item = items[id]
                    val cartItem = cartItems[id]
                    CartItemView(
                        item = item,
                        cartItem = cartItem,
                        isSelected = selectedItems.any { it == cartItem.id },
                        onSelect = {
                            selectedItems = selectedItems.toMutableList().apply {
                                if (contains(cartItem.id)) remove(cartItem.id) else add(cartItem.id)
                            }
                        },
                        onDelete = {
                            selectedItems = selectedItems.filter { it != cartItem.id }
                            dbViewModel.removeFromCart(cartItem)
                        },
                        onUpdateQuantity = { newQuantity ->
                            dbViewModel.changeQuantity(cartItem, newQuantity)
                        },
                        onClick = { navController.navigate("${NavLocations.ITEM_DESCRIPTION.name}/${item.id}") }
                    )
                }
            }
            OrderSummary(
                items,
                selectedItems.map { id -> cartItems.find { it.id == id }!! }) { cartItems, items ->
                checkoutViewModel.cartItems = cartItems
                checkoutViewModel.items = items
                navController.navigate(NavLocations.CHECKOUT.name)
            }
        }
    }
}

@Composable
fun SelectAllButton(
    cart: List<Pair<CartItem, Item>>,
    selectedItems: List<CartItem>,
    onSelectAll: () -> Unit,
    onUnselectAll: () -> Unit
) {
    val allSelected = selectedItems.size == cart.size && cart.isNotEmpty()

    TextButton(
        onClick = if (allSelected) onUnselectAll else onSelectAll,
    ) {
        Text(if (allSelected) "Unselect All" else "Select All")
    }
}

@Composable
fun OrderSummary(
    items: List<Item>,
    selectedCartItems: List<CartItem>,
    onPlaceOrder: (List<CartItem>, List<Item>) -> Unit
) {
    val itemsInCart =
        items.filter { selectedCartItems.any { cartItem -> cartItem.itemId == it.id } }
    val totalPrice = selectedCartItems.sumOf {
        it.quantity * (items.first { item -> item.id == it.itemId }.price + it.options.sumOf {
            (it.values.values.firstOrNull() ?: 0).toDouble()
        })
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Order Summary",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Selected Items: ${selectedCartItems.size}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Total Price: $${String.format("%.2f", totalPrice)}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Button(
            onClick = {
                onPlaceOrder(
                    selectedCartItems,
                    itemsInCart
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Place Order")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CartItemView(
    item: Item,
    cartItem: CartItem,
    isSelected: Boolean,
    onSelect: (Boolean) -> Unit,
    onUpdateQuantity: (Int) -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 1.dp
        )
    ) {
        Column(Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                IconButton(onClick = { onSelect(!isSelected) }) {
                    Icon(
                        if (isSelected) Icons.Filled.Circle else Icons.Outlined.Circle,
                        contentDescription = if (isSelected) "Deselect" else "Select",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Row(Modifier.weight(3f), verticalAlignment = Alignment.Top) {
                    AsyncImage(
                        model = authenticatedStorageItem(IMAGES, item.images.first()),
                        contentDescription = "Item Image",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(end = 16.dp),
                        contentScale = ContentScale.Crop,
                        onError = { error ->
                            println("Hello it failed due to: ${error.result.throwable.message}")
                        }
                    )
                    Column {
                        Text(text = item.name, style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = "$${
                                String.format(
                                    "%.2f",
                                    item.price + cartItem.options.sumOf { (it.values.values.firstOrNull() ?: 0).toDouble() })
                            }",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                NumberPicker(
                    value = cartItem.quantity,
                    onValueChange = onUpdateQuantity,
                    orientation = NumberPickerOrientation.Vertical
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
            FlowRow {
                cartItem.options.forEach { option ->
                    AssistChip(
                        onClick = { /*TODO*/ },
                        label = { Text(option.name) },
                        modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                    )
                }
            }
        }
    }
}
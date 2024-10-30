package tech.azurestar.kmp.ecommercecustomer.vm

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.TextSearchType
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.azurestar.kmp.ecommercecustomer.db.DatabaseConstants
import tech.azurestar.kmp.ecommercecustomer.db.item.Cart
import tech.azurestar.kmp.ecommercecustomer.db.item.CartItem
import tech.azurestar.kmp.ecommercecustomer.db.item.Category
import tech.azurestar.kmp.ecommercecustomer.db.item.Customer
import tech.azurestar.kmp.ecommercecustomer.db.item.Item
import java.util.UUID

class DBViewModel(supabase: SupabaseClient, private val storageViewModel: StorageViewModel) :
    ViewModel() {

    val postgrest = supabase.postgrest
    val auth = supabase.auth
    val customerAccount = MutableStateFlow<Customer?>(null)
    val categories = MutableStateFlow<List<Category>>(emptyList())
    val selectedCategories = MutableStateFlow<List<Category>>(emptyList())
    val items = MutableStateFlow<List<Item>>(emptyList())
    val initialized = MutableStateFlow(false)
    val searchResults = MutableStateFlow<List<Item>>(emptyList())
    val cart = MutableStateFlow<Cart?>(null)
    val cartItems = MutableStateFlow<List<Pair<CartItem, Item>>>(emptyList())

    fun initialize() {
        getCustomerAccount()
        getCategories()
        getItems()
        getCart()
        createCartIfNotExists()
    }

    fun selectCategory(category: Category) {
        if (selectedCategories.value.contains(category)) {
            selectedCategories.value -= category
        } else {
            selectedCategories.value += category
        }
    }

    private fun getCategories() {
        viewModelScope.launch {
            try {
                categories.value = postgrest.from(DatabaseConstants.TABLE_CATEGORIES).select()
                    .decodeList<Category>()
            } catch (e: Exception) {
                println("yadsa ${e.message}")
            }
        }
    }

    fun searchItems(query: String) {
        viewModelScope.launch {
            try {
                searchResults.value = postgrest.from(DatabaseConstants.TABLE_ITEMS).select {
                    filter {
                        textSearch(
                            DatabaseConstants.COL_ITEMS_NAME,
                            query,
                            config = "english",
                            textSearchType = TextSearchType.WEBSEARCH
                        )
                    }
                    limit(count = 10)
                }.decodeList<Item>()
            } catch (e: Exception) {
                println("yadsa ${e.message}")
            }
        }
    }

    fun getItems(name: String = "") {
        viewModelScope.launch {
            try {
                items.value = postgrest.from(DatabaseConstants.TABLE_ITEMS).select {
                    limit(count = 50)
                    if (name.isNotBlank()) {
                        filter {
                            textSearch(
                                DatabaseConstants.COL_ITEMS_NAME,
                                name,
                                config = "english",
                                textSearchType = TextSearchType.WEBSEARCH
                            )
                        }
                    }
                    if (selectedCategories.value.isNotEmpty()) {
                        filter {
                            or {
                                selectedCategories.value.forEach {
                                    eq(DatabaseConstants.COL_ITEMS_CATEGORY_ID, it.id)
                                }
                            }
                        }
                    }
                }.decodeList<Item>()
                if (customerAccount.value != null) {
                    initialized.value = true
                }
            } catch (e: Exception) {
                println("yadsa ${e.message}")
            }
        }
    }

    fun getItem(itemId: Long, onFinished: (Item) -> Unit) {
        viewModelScope.launch {
            try {
                onFinished(postgrest.from(DatabaseConstants.TABLE_ITEMS).select {
                    filter {
                        Item::id eq itemId
                    }
                }.decodeSingle<Item>())
            } catch (e: Exception) {
                println("yadsa ${e.message}")
            }
        }
    }

    @OptIn(SupabaseExperimental::class)
    fun getCustomerAccount() {
        viewModelScope.launch {
            try {
                postgrest.from(DatabaseConstants.TABLE_CUSTOMERS).selectAsFlow(Customer::id)
                    .collectLatest {
                        customerAccount.value = it.firstOrNull()
                        if (customerAccount.value == null || items.value.isNotEmpty()) {
                            initialized.value = true
                        }
                    }
            } catch (e: Exception) {
                initialized.value = true
                println("yadsa ${e.message}")
            }
        }
    }

    fun makeCustomerAccount(name: String, profileImage: Uri?) {
        if (profileImage != null) {
            storageViewModel.uploadProfileImage(profileImage) {
                viewModelScope.launch {
                    postgrest.from(DatabaseConstants.TABLE_CUSTOMERS).insert(
                        Customer(
                            name = name,
                            userId = UUID.fromString(auth.currentUserOrNull()!!.id),
                            profileImage = it
                        )
                    )
                }
            }
        } else {
            viewModelScope.launch {
                postgrest.from(DatabaseConstants.TABLE_CUSTOMERS).insert(
                    Customer(
                        name = name,
                        userId = UUID.fromString(auth.currentUserOrNull()!!.id),
                        profileImage = null
                    )
                )
            }
        }
    }

    fun updateCustomerAccount(name: String, profileImage: Uri?, onFinished: () -> Unit) {

        customerAccount.value?.profileImage?.let {
            storageViewModel.removeOldImage(it)
        }

        if (profileImage != null) {
            storageViewModel.uploadProfileImage(profileImage) { image ->
                viewModelScope.launch {
                    postgrest.from(DatabaseConstants.TABLE_CUSTOMERS).update(
                        {
                            Customer::name setTo name
                            Customer::profileImage setTo image
                        }
                    ) {
                        filter {
                            Customer::id eq customerAccount.value!!.id
                        }
                    }
                    onFinished()
                }
            }
        } else {
            viewModelScope.launch {
                postgrest.from(DatabaseConstants.TABLE_CUSTOMERS).update(
                    {
                        Customer::name setTo name
                    }
                ) {
                    filter {
                        Customer::id eq customerAccount.value!!.id
                    }
                }
                onFinished()
            }
        }
    }

    private fun createCartIfNotExists() {
        auth.currentUserOrNull()?.let {
            viewModelScope.launch {
                val result = postgrest.from(DatabaseConstants.TABLE_CARTS).select {
                    filter { Cart::userId eq UUID.fromString(it.id) }
                }.decodeSingleOrNull<Cart>()

                if (result == null) {
                    postgrest.from(DatabaseConstants.TABLE_CARTS).insert(
                        Cart(
                            userId = UUID.fromString(it.id),
                            cartItemIds = emptyList()
                        )
                    )
                }
            }
        }
    }

    fun addToCart(item: CartItem) {
        viewModelScope.launch {
            try {

                val cartItem =
                    postgrest.from(DatabaseConstants.TABLE_CART_ITEMS).insert(item) { select() }
                        .decodeSingle<CartItem>()
                postgrest.from(DatabaseConstants.TABLE_CARTS)
                    .update(
                        {
                            Cart::cartItemIds setTo cart.value!!.cartItemIds.toMutableList()
                                .apply { add(cartItem.id) }
                                .map { it }
                        }
                    ) {
                        filter {
                            Cart::userId eq UUID.fromString(auth.currentUserOrNull()!!.id)
                        }
                    }
            } catch (e: Exception) {
                println("yadsa ${e.message}")
            }
        }
    }

    fun removeFromCart(item: CartItem) {
        viewModelScope.launch {
            postgrest.from(DatabaseConstants.TABLE_CARTS)
                .update(
                    {
                        Cart::cartItemIds setTo cart.value!!.cartItemIds.toMutableList()
                            .apply { remove(item.id) }
                    }
                ) {
                    filter {
                        Cart::userId eq UUID.fromString(auth.currentUserOrNull()!!.id)
                    }
                }
            postgrest.from(DatabaseConstants.TABLE_CART_ITEMS).delete {
                filter {
                    CartItem::id eq item.id
                }
            }
        }
    }

    fun changeQuantity(cartItem: CartItem, quantity: Int) {
        viewModelScope.launch {
            cartItems.value = cartItems.value.map {
                if (it.first.id == cartItem.id) {
                    cartItem.copy(quantity = quantity) to it.second
                } else {
                    it
                }
            }
            postgrest.from(DatabaseConstants.TABLE_CART_ITEMS).update(
                {
                    CartItem::quantity setTo quantity
                }
            ) {
                filter {
                    CartItem::id eq cartItem.id
                }
            }
        }
    }

    @OptIn(SupabaseExperimental::class)
    private fun getCart() {
        viewModelScope.launch {
            try {
                postgrest.from(DatabaseConstants.TABLE_CARTS).selectAsFlow(Cart::userId)
                    .collectLatest {
                        it.firstOrNull()?.let {
                            cart.value = it
                            val cartItems =
                                postgrest.from(DatabaseConstants.TABLE_CART_ITEMS).select {
                                    filter {
                                        or {
                                            cart.value!!.cartItemIds.map { CartItem::id eq it }
                                        }
                                    }
                                }.decodeList<CartItem>()
                            val items = postgrest.from(DatabaseConstants.TABLE_ITEMS).select {
                                filter {
                                    or {
                                        cartItems.map { Item::id eq it.itemId }
                                    }
                                }
                            }.decodeList<Item>()

                            this@DBViewModel.cartItems.value = cartItems.map { cartItem ->
                                cartItem to items.first { it.id == cartItem.itemId }
                            }
                        }
                    }
            } catch (e: Exception) {
                initialized.value = true
                println("yadsa ${e.message}")
            }
        }
    }
}


fun List<Pair<CartItem, Item>>.getItems(): List<Item> = map { it.second }
fun List<Pair<CartItem, Item>>.getCartItems(): List<CartItem> = map { it.first }
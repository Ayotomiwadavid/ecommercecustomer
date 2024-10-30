package tech.azurestar.kmp.ecommercecustomer.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.jan.supabase.storage.authenticatedStorageItem
import org.koin.compose.koinInject
import tech.azurestar.kmp.ecommercecustomer.db.item.CartItem
import tech.azurestar.kmp.ecommercecustomer.db.item.Item
import tech.azurestar.kmp.ecommercecustomer.language.TextProvider
import tech.azurestar.kmp.ecommercecustomer.ui.DefaultScaffold
import tech.azurestar.kmp.ecommercecustomer.vm.DBViewModel
import tech.azurestar.kmp.ecommercecustomer.vm.IMAGES
import tech.azurestar.kmp.ecommercecustomer.vm.getItems
import java.util.UUID

@Composable
fun ItemDescriptionScreen(itemId: Long) {

    val dbViewModel = koinInject<DBViewModel>()
    var item by remember {
        mutableStateOf(dbViewModel.items.value.find { it.id == itemId }
            ?: dbViewModel.cartItems.value.getItems().find { it.id == itemId })
    }
    var showDrawer by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = Unit) {
        if (item == null) {
            dbViewModel.getItem(itemId) { item = it }
        }
    }

    item?.let {
        DefaultScaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(onClick = {
                    showDrawer = true
                }) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = TextProvider.ADD_TO_CART.getText()
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = TextProvider.ADD_TO_CART.getText())
                }
            }
        ) {
            if(showDrawer) {
                AddItemDrawer(item = it, { showDrawer = false }) { options, totalPrice, quantity ->
                    dbViewModel.addToCart(
                        CartItem(
                            itemId = it.id,
                            userId = UUID.fromString(dbViewModel.auth.currentUserOrNull()?.id),
                            quantity = quantity,
                            options = options
                        )
                    )
                    showDrawer = false
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Image Carousel
                if (it.images.isNotEmpty()) {
                    val pagerState = rememberPagerState(pageCount = { it.images.size })
                    ElevatedCard(Modifier.padding(16.dp)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(650.dp)
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxWidth()
                            ) { page ->
                                AsyncImage(
                                    model = authenticatedStorageItem(IMAGES, it.images[page]),
                                    contentDescription = "Item image ${page + 1}",
                                    modifier = Modifier
                                        .height(600.dp)
                                        .fillMaxWidth(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            // Pager Indicator
                            Row(
                                Modifier
                                    .padding(top = 8.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                repeat(pagerState.pageCount) { iteration ->
                                    val color = if (pagerState.currentPage == iteration)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    Box(
                                        modifier = Modifier
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(color)
                                            .size(8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Item Details
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = it.name,
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            PriceTag(price = it.price)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        DetailCard(it)
                    }
                }
            }
        }
    }
}

@Composable
fun PriceTag(price: Double) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Text(
            text = "$${String.format("%.2f", price)}",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun DetailCard(item: Item) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            DetailItem("Description", item.description)
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
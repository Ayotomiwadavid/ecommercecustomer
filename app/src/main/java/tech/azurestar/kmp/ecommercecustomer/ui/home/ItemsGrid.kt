package tech.azurestar.kmp.ecommercecustomer.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import io.github.jan.supabase.storage.authenticatedStorageItem
import org.koin.compose.koinInject
import tech.azurestar.kmp.ecommercecustomer.db.item.Item
import tech.azurestar.kmp.ecommercecustomer.ui.nav.NavLocations
import tech.azurestar.kmp.ecommercecustomer.vm.DBViewModel
import tech.azurestar.kmp.ecommercecustomer.vm.IMAGES

@Composable
fun ItemsGrid(navController: NavController) {
    val dbViewModel = koinInject<DBViewModel>()

    val items = dbViewModel.items.collectAsState().value

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
    ) {
        items(items) { item ->
            ItemCard(item) { navController.navigate("${NavLocations.ITEM_DESCRIPTION.name}/${item.id}") }
        }
    }
}

@Composable
fun ItemCard(item: Item, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (item.images.isNotEmpty()) {
                AsyncImage(
                    model = authenticatedStorageItem(IMAGES, item.images.first()),
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 8.dp, end = 8.dp),
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge,
//                    overflow = TextOverflow.Ellipsis,
                    softWrap = true,
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    text = "₮${item.price}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
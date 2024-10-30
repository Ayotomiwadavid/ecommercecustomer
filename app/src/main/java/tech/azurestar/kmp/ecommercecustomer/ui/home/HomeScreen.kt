package tech.azurestar.kmp.ecommercecustomer.ui.home

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavController
import org.koin.compose.koinInject
import tech.azurestar.kmp.ecommercecustomer.ui.DefaultScaffold
import tech.azurestar.kmp.ecommercecustomer.vm.DBViewModel

@Composable
fun HomeScreen(navController: NavController) {

    val dbViewModel = koinInject<DBViewModel>()
    val focusManager = LocalFocusManager.current

    DefaultScaffold(
        Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        },
        navController
    ) {
        Column {
            SearchBar(
                onSearch = { query ->
                    dbViewModel.getItems(query)
                }
            )
            CategoryFilterChips(
                categories = dbViewModel.categories.collectAsState().value,
                selectedCategories = dbViewModel.selectedCategories.collectAsState().value
            ) { category ->
                dbViewModel.selectCategory(category)
                dbViewModel.getItems()
            }
            ItemsGrid(navController)
        }
    }
}
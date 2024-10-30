package tech.azurestar.kmp.ecommercecustomer.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.koin.compose.koinInject
import tech.azurestar.kmp.ecommercecustomer.vm.DBViewModel

@Composable
fun SearchBar(initialText: String = "", onSearch: (String) -> Unit) {

    var text by remember { mutableStateOf(initialText) }
    var isFocused by remember { mutableStateOf(false) }
    val dbViewModel = koinInject<DBViewModel>()
    val searchResults = dbViewModel.searchResults.collectAsState().value

    Column(Modifier.padding(all = 8.dp)) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isFocused = it.hasFocus
                },
            value = text,
            shape = if(isFocused) RectangleShape else RoundedCornerShape(28.dp),
            onValueChange = {
                text = it
                if (text.trim().length > 2) dbViewModel.searchItems(it)
            },
            label = { Text("Search") },
            leadingIcon = {
                IconButton(onClick = { onSearch(text) }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(text) })
        )

        AnimatedVisibility(
            visible = searchResults.isNotEmpty() && isFocused,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                RectangleShape,
                elevation = CardDefaults.cardElevation()
            ) {
                Column {
                    searchResults.forEach {
                        Text(
                            modifier = Modifier
                                .clickable {
                                    text = it.name
                                    onSearch(it.name)
                                }
                                .fillMaxWidth()
                                .padding(8.dp),
                            text = it.name
                        )
                    }
                }
            }
        }
    }
}
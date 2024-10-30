package tech.azurestar.kmp.ecommercecustomer.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tech.azurestar.kmp.ecommercecustomer.db.item.Item
import tech.azurestar.kmp.ecommercecustomer.db.item.Option
import tech.azurestar.kmp.ecommercecustomer.ui.components.NumberPicker

@Composable
fun AddItemDrawer(item: Item, onDismiss: () -> Unit, onConfirm: (List<Option>, Double, Int) -> Unit) {
    Column {
        Text(
            text = item.name,
            Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally), textAlign = TextAlign.Center
        )
        OptionsConfigurationDrawer(
            basePrice = item.price,
            options = item.options,
            onDismiss, onConfirm
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsConfigurationDrawer(
    basePrice: Double,
    options: List<Option>,
    onDismiss: () -> Unit,
    onConfirm: (List<Option>, Double, Int) -> Unit
) {
    var selectedOptions by remember { mutableStateOf(listOf<Option>()) }
    var quantity by remember { mutableStateOf(1) }


    LaunchedEffect(key1 = Unit) {
        selectedOptions = options.map { option ->
            option.copy(values = mapOf(option.values.keys.first() to option.values.values.firstOrNull()))
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {

        val totalPrice = remember(selectedOptions) {
            basePrice + selectedOptions.sumOf { option ->
                option.values.values.firstOrNull() ?: 0.0
            }
        }


        OptionsDrawerContent(
            options = options,
            quantity = quantity,
            selectedOptions = selectedOptions,
            totalPrice = totalPrice,
            basePrice = basePrice,
            onOptionSelected = { options ->
                selectedOptions = selectedOptions.filter { it.name != options.name } + options
            },
            onQuantityChange = { quantity = it },
            onConfirm = { onConfirm(selectedOptions, totalPrice, quantity) },
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun OptionsDrawerContent(
    options: List<Option>,
    quantity: Int,
    selectedOptions: List<Option>,
    totalPrice: Double,
    basePrice: Double,
    onOptionSelected: (Option) -> Unit,
    onQuantityChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            text = "Configure Options",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(options) { option ->
                OptionSelector(
                    option = option,
                    selectedValue = selectedOptions.find { it.name == option.name }?.values?.keys?.firstOrNull()
                        ?: "",
                    onValueSelected = { value ->
                        onOptionSelected(option.copy(values = mapOf(value to option.values[value])))
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }

        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "Quantity",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            NumberPicker(value = quantity, onValueChange = onQuantityChange)
        }

        PriceSummary(
            basePrice = basePrice,
            totalPrice = totalPrice,
            selectedOptions = selectedOptions,
            options = options
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
            Button(onClick = onConfirm) {
                Text("Confirm")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OptionSelector(
    option: Option,
    selectedValue: String?,
    onValueSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = option.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.Start
        ) {
            option.values.forEach { (value, priceIncrease) ->
                InputChip(
                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                    selected = selectedValue == value,
                    label = {
                        Text(text = value)
                        priceIncrease?.let { increase ->
                            if (increase > 0) {
                                Text(
                                    modifier = Modifier.padding(start = 4.dp),
                                    text = "+$${String.format("%.2f", increase)}",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    onClick = { onValueSelected(value) }
                )

            }
        }
    }
}

@Composable
private fun PriceSummary(
    basePrice: Double,
    totalPrice: Double,
    selectedOptions: List<Option>,
    options: List<Option>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Price Summary",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Base Price")
                Text("$${String.format("%.2f", basePrice)}")
            }

            // Show selected options with their price increases
            selectedOptions.forEach { option ->
                val priceIncrease = option.values.values.firstOrNull() ?: 0.0
                if (priceIncrease > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${option.name}: ${option.values.keys.first()}")
                        Text("+$${String.format("%.2f", priceIncrease)}")
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Price",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${String.format("%.2f", totalPrice)}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
package tech.azurestar.kmp.ecommercecustomer.ui.order

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import org.koin.compose.koinInject
import tech.azurestar.kmp.ecommercecustomer.db.item.Address
import tech.azurestar.kmp.ecommercecustomer.ui.DefaultScaffold
import tech.azurestar.kmp.ecommercecustomer.vm.OrderViewModel
import java.util.UUID

@Composable
fun SelectAddress(
    onSelect: (Address) -> Unit
) {

    val orderViewModel = koinInject<OrderViewModel>()

    val addresses by orderViewModel.addresses.collectAsState()

    var selectedAddressId by remember {
        mutableLongStateOf(0L)
    }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<Address?>(null) }

    DefaultScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Select Delivery Address",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                addresses.forEach { address ->
                    AddressCard(
                        address = address,
                        isSelected = address.id == selectedAddressId,
                        onSelect = { selectedAddressId = address.id },
                        onEdit = {
                            editingAddress = address
                            showAddEditDialog = true
                        }
                    )
                }
                AnimatedVisibility(visible = selectedAddressId != 0L) {
                    Button(
                        onClick = {
                            onSelect(addresses.find { it.id == selectedAddressId }!!)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Select Address")
                    }
                }
            }

            Button(
                onClick = {
                    editingAddress = null
                    showAddEditDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Address",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Add New Address")
            }
        }
    }

    if (showAddEditDialog) {
        AddEditAddressDialog(
            address = editingAddress,
            onDismiss = { showAddEditDialog = false },
            onSave = { address ->
                if (editingAddress != null) {
                    orderViewModel.editAddress(address)
                } else {
                    orderViewModel.addAddress(address)
                }
                showAddEditDialog = false
            }
        )
    }
}

@Composable
fun AddressCard(
    address: Address,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    address.line1?.let { Text(it, style = MaterialTheme.typography.bodyLarge) }
                    address.line2?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        address.city?.let { Text(it) }
                        address.pincode?.let { Text("- $it") }
                    }
                }
            }
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Address"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAddressDialog(
    address: Address?,
    onDismiss: () -> Unit,
    onSave: (Address) -> Unit
) {

    val auth = koinInject<SupabaseClient>().auth

    var line1 by remember { mutableStateOf(address?.line1 ?: "") }
    var line2 by remember { mutableStateOf(address?.line2 ?: "") }
    var city by remember { mutableStateOf(address?.city ?: "") }
    var pincode by remember { mutableStateOf(address?.pincode?.toString() ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (address == null) "Add New Address" else "Edit Address",
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = line1,
                    onValueChange = { line1 = it },
                    label = { Text("Address Line 1") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = line2,
                    onValueChange = { line2 = it },
                    label = { Text("Address Line 2") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = pincode,
                    onValueChange = { pincode = it },
                    label = { Text("Pincode") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val newAddress = address?.copy(
                                line1 = line1,
                                line2 = line2,
                                city = city,
                                pincode = pincode.toLongOrNull(),
                                userId = UUID.fromString(auth.currentUserOrNull()!!.id)
                            ) ?: Address(
                                line1 = line1,
                                line2 = line2,
                                city = city,
                                pincode = pincode.toLongOrNull(),
                                userId = UUID.fromString(auth.currentUserOrNull()!!.id)
                            )
                            onSave(newAddress)
                        },
                        enabled = line1.isNotBlank() && city.isNotBlank() && pincode.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
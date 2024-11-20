package tech.azurestar.kmp.ecommercecustomer.ui.order

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import org.koin.compose.koinInject
import tech.azurestar.kmp.ecommercecustomer.ui.DefaultScaffold
import tech.azurestar.kmp.ecommercecustomer.vm.CheckoutViewModel
import tech.azurestar.kmp.ecommercecustomer.vm.InvoiceRequest
import tech.azurestar.kmp.ecommercecustomer.vm.InvoiceViewModel
import tech.azurestar.kmp.ecommercecustomer.vm.OrderViewModel

@Composable
fun CheckoutScreen(navController: NavController) {
    val orderViewModel = koinInject<OrderViewModel>()
    val checkoutViewModel = koinInject<CheckoutViewModel>()
    val paymentViewModel = koinInject<InvoiceViewModel>()

    var addressId by remember { mutableStateOf<Long?>(null) }
    var isInvoiceRequested by remember { mutableStateOf(false) }
    var isPaymentCompleted by remember { mutableStateOf(false) }

    if (addressId == null) {
        SelectAddress { addressId = it.id }
    }

    if (addressId != null) {
        DefaultScaffold {
            Button(onClick = {
                if (!isInvoiceRequested) {
                    // Step 1: Request Invoice
                    val invoiceRequest = InvoiceRequest(
                        senderInvoiceNo = "12345", // Generate dynamically if needed
                        invoiceReceiver = "receiver_code",
                        invoiceDescription = "Order description",
                        invoiceCode = "code_001",
                        amount = checkoutViewModel.items.sumOf { it.price},
                        customerName = "John Doe", // Replace with actual customer data
                        customerEmail = "john.doe@example.com",
                        PhoneNumber = "1234567890"
                    )

                    paymentViewModel.createInvoice(
                        request = invoiceRequest,
                        onSuccess = {
                            isInvoiceRequested = true
                            paymentViewModel.startWebSocket() // Step 2: Listen for payment notification
                        },
                        onError = { error ->
                            // Handle invoice generation error
                            //Text(text = "Error generating invoice: $error")
                        }
                    )
                }
            }) {
                Text(text = if (isInvoiceRequested) "Waiting for payment..." else "Generate Invoice")
            }
        }
    }

    // Step 3: Observe payment status
    val paymentStatus by paymentViewModel.paymentStatus.collectAsState()

    paymentStatus?.let { status ->
        when (status.paymentStatus) {
            "success" -> {
                if (!isPaymentCompleted) {
                    isPaymentCompleted = true
                    orderViewModel.placeOrder(
                        checkoutViewModel.cartItems,
                        checkoutViewModel.items,
                        addressId!!
                    )
                    Text(text = "Payment Success! Order placed for customer ${status.customerId}.")
                }
            }
            "failed" -> {
                Text(text = "Payment Failed for customer ${status.customerId}.")
            }
        }
    }
}

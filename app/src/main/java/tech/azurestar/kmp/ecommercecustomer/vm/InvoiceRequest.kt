package tech.azurestar.kmp.ecommercecustomer.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.InternalAPI
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import tech.azurestar.kmp.ecommercecustomer.PAYMENT_INVOICE_URL

@Serializable
data class InvoiceRequest(
    val senderInvoiceNo: String,
    val invoiceReceiver: String,
    val invoiceDescription: String,
    val invoiceCode: String,
    val amount: Double,
    val customerName: String,
    val customerEmail: String,
    val PhoneNumber: String
)

@Serializable
data class PaymentNotification(
    val customerId: String,
    val paymentStatus: String,
    val message: String
)

class InvoiceViewModel : ViewModel() {

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
        install(WebSockets)
    }

    private val _paymentStatus = MutableStateFlow<PaymentNotification?>(null)
    val paymentStatus: StateFlow<PaymentNotification?> = _paymentStatus


    fun createInvoice(request: InvoiceRequest, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response: HttpResponse = httpClient.post(PAYMENT_INVOICE_URL) {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }

                if (response.status.isSuccess()) {
                    val message = response.bodyAsText()
                    onSuccess(message)
                } else {
                    onError("Failed to create invoice: ${response.status.description}")
                }
            } catch (e: Exception) {
                Napier.e("Error creating invoice", e)
                onError("Invoice creation failed: ${e.message}")
            }
        }
    }

    fun startWebSocket() {
        viewModelScope.launch {
            try {
                httpClient.webSocket(
                    method = HttpMethod.Get,
                    host = "mongolia-payment-method-628287405723.asia-east2.run.app",
                    path = "/ws"
                ) {
                    Napier.d("WebSocket connected")

                    // Listen to messages from the server
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            try {
                                val message = frame.readText()
                                val notification = Json.decodeFromString<PaymentNotification>(message)
                                _paymentStatus.value = notification
                            } catch (e: Exception) {
                                Napier.e("Error parsing WebSocket message", e)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Napier.e("WebSocket connection failed", e)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        httpClient.close()
    }
}

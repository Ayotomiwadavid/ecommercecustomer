package tech.azurestar.kmp.ecommercecustomer.db.item

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderItem(
    val id: Long = 0,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val sellerId: UUID,
    val createdAt: LocalDateTime = LocalDateTime.parse("2021-01-01T00:00:00"),
    val itemId: Long?,
    val quantity: Int,
    val options: List<Option> = listOf(),
    val totalPrice: Double = 0.0,
    val delivered : Boolean,
)
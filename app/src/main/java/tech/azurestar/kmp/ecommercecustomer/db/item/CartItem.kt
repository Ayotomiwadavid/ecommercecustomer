package tech.azurestar.kmp.ecommercecustomer.db.item

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.uuid.Uuid

@Serializable
data class CartItem(
    val id: Long = 0,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID?,
    val createdAt : LocalDateTime = LocalDateTime.parse("2021-01-01T00:00:00"),
    val itemId: Long,
    val options: List<Option> = emptyList(),
    val quantity: Int
)
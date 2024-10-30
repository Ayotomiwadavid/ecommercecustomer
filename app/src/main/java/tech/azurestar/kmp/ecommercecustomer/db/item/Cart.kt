package tech.azurestar.kmp.ecommercecustomer.db.item

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.uuid.Uuid

@Serializable
data class Cart(
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID? = null,
    val createdAt: LocalDateTime = LocalDateTime.parse("2021-01-01T00:00:00"),
    var cartItemIds: List<Long>,
)
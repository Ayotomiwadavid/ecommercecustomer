package tech.azurestar.kmp.ecommercecustomer.db.item

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Item(
    val id: Long = 0,
    val createdAt: LocalDateTime = LocalDateTime.parse("2021-01-01T00:00:00"),
    val name: String,
    val description: String,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    val images: List<String> = emptyList(),
    val categoryId: Long = 0,
    val price: Double = 0.0,
    val options: List<Option> = emptyList()
)
package tech.azurestar.kmp.ecommercecustomer.db.item

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Customer(
    val id: Long = 0,
    val createdAt: LocalDateTime = LocalDateTime.parse("2021-01-01T00:00:00"),
    val name: String?,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID?,
    val profileImage: String?
)
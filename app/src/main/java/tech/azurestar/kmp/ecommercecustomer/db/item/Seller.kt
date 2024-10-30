package tech.azurestar.kmp.ecommercecustomer.db.item

import kotlinx.datetime.LocalDateTime
import java.util.UUID
import kotlinx.serialization.Serializable

@Serializable
data class Seller(
    val id: Long = 0,
    val createdAt: LocalDateTime = LocalDateTime.parse("2021-01-01T00:00:00"),
    val name: String = "",
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID = UUID.randomUUID(),
    val profileImage: String?
)
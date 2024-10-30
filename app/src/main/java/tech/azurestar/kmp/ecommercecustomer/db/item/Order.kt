package tech.azurestar.kmp.ecommercecustomer.db.item

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Order(
    val id: Long = 0,
    val createdAt: LocalDateTime? = LocalDateTime.parse("2021-01-01T00:00:00"),
    val orderItemsIds: List<Long>,
    val price: Double?,
    val percentage: Int = 15,
    val paidToSeller: Boolean = false,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID?,
    val addressId: Long? = null,
)
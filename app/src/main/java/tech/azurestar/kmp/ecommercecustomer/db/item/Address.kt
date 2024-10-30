package tech.azurestar.kmp.ecommercecustomer.db.item

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Address(
    val id: Long = 0,
    val line1: String?,
    val line2: String?,
    val city: String?,
    val pincode: Long?,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID?
) {

}
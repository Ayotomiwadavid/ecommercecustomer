package tech.azurestar.kmp.ecommercecustomer.db.item

import kotlinx.serialization.Serializable

@Serializable
data class Option(
    val name: String = "",
    val values: Map<String, Double?> = mapOf()
)
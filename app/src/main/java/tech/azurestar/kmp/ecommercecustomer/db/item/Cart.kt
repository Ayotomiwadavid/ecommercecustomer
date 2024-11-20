package tech.azurestar.kmp.ecommercecustomer.db.item

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

@Serializable
data class Cart(
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID? = null,
    @Serializable(with = LocalDateTimeWithoutTimeZoneSerializer::class)
    val createdAt: LocalDateTime = LocalDateTime.parse("2021-01-01T00:00:00"),
    var cartItemIds: List<Long>,
)


object LocalDateTimeWithoutTimeZoneSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val rawDate = decoder.decodeString()
        // Remove the timezone part and ensure it's a valid format for LocalDateTime parsing
        val sanitizedDate =
            rawDate.split("+").first().take(26)  // Remove timezone and trim to valid precision
        return LocalDateTime.parse(sanitizedDate)
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toString())
    }
}
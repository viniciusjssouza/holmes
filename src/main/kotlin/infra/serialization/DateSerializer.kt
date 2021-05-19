package infra.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializer(forClass = DateSerializer::class)
object DateSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("DateSerializer", PrimitiveKind.STRING)

    override fun serialize(output: Encoder, obj: LocalDateTime) {
        output.encodeString(obj.format(DateTimeFormatter.ISO_INSTANT))
    }

    override fun deserialize(input: Decoder): LocalDateTime {
        return LocalDateTime.parse(input.decodeString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}
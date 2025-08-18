package com.colman.dailypulse.models.habits

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object DayOfWeekSerializer : KSerializer<DayOfWeek> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DayOfWeek", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): DayOfWeek = DayOfWeek.valueOf(decoder.decodeString())
    override fun serialize(encoder: Encoder, value: DayOfWeek) = encoder.encodeString(value.name)
}

object DayOfWeekSetSerializer : KSerializer<Set<DayOfWeek>> {
    private val delegateSerializer = ListSerializer(DayOfWeekSerializer)
    override val descriptor: SerialDescriptor = delegateSerializer.descriptor

    override fun deserialize(decoder: Decoder): Set<DayOfWeek> {
        return delegateSerializer.deserialize(decoder).toSet()
    }

    override fun serialize(encoder: Encoder, value: Set<DayOfWeek>) {
        delegateSerializer.serialize(encoder, value.toList())
    }
}

@Serializable
data class Habit(
    val id: String? = "",
    val title: String?,
    @Serializable(with = DayOfWeekSetSerializer::class)
    val daysOfWeek: Set<DayOfWeek>?,
    val goal: Int? = 60,
    val totalCount: Int? = 0,
    val createdAt: Instant = Clock.System.now(),
    val lastModified: Instant = Clock.System.now()
)
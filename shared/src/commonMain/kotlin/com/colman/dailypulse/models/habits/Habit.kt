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
    val id: String? = "", // Unique ID
    val title: String?, // Short name of the habit
    @Serializable(with = DayOfWeekSetSerializer::class)
    val daysOfWeek: Set<DayOfWeek>?, // Days when habit should be done
    val frequency: Int? = 1, // How often habit should be done
    val dailyCount: Int? = 0, // How many times habit should be done in a day
    val totalCount: Int? = 0, // Total number of times habit should be done
    val createdAt: Instant = Clock.System.now(), // When habit was created
    val lastModified: Instant = Clock.System.now()
)
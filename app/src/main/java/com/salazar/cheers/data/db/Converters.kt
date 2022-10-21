package com.salazar.cheers.data.db

import androidx.room.TypeConverter
import cheers.chat.v1.Message
import cheers.chat.v1.MessageType
import cheers.chat.v1.RoomStatus
import cheers.chat.v1.RoomType
import cheers.type.UserOuterClass
import com.google.protobuf.Timestamp
import com.salazar.cheers.internal.ActivityType
import com.salazar.cheers.internal.Beverage
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class Converters {
    @TypeConverter
    fun fromMessageStatus(value: Message.Status) = value.name

    @TypeConverter
    fun toMessageStatus(name: String) = Message.Status.values()
        .firstOrNull { it.name.equals(name, ignoreCase = true) }
        ?: Message.Status.UNRECOGNIZED

    @TypeConverter
    fun fromStoryState(value: UserOuterClass.StoryState) = value.name

    @TypeConverter
    fun toStoryState(name: String) = UserOuterClass.StoryState.values()
        .firstOrNull { it.name.equals(name, ignoreCase = true) }
        ?: UserOuterClass.StoryState.UNRECOGNIZED

    @TypeConverter
    fun fromActivityType(value: ActivityType) = value.name

    @TypeConverter
    fun toActivityType(name: String) = ActivityType.values()
            .firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: ActivityType.NONE

    @TypeConverter
    fun fromMessageType(value: MessageType) = value.name

    @TypeConverter
    fun toMessageType(name: String) =
        MessageType.values()
            .firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: MessageType.UNRECOGNIZED

    @TypeConverter
    fun fromRoomType(value: RoomType) = value.name

    @TypeConverter
    fun toRoomType(name: String) =
        RoomType.values()
            .firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: RoomType.UNRECOGNIZED

    @TypeConverter
    fun fromRoomStatus(value: RoomStatus) = value.name

    @TypeConverter
    fun toRoomStatus(name: String) =
        RoomStatus.values()
            .firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: RoomStatus.UNRECOGNIZED

    @TypeConverter
    fun fromBeverage(value: Beverage) = value.name

    @TypeConverter
    fun toBeverage(name: String) = Beverage.fromName(name)

    @TypeConverter
    fun fromTimestamp(value: Timestamp) = value.seconds

    @TypeConverter
    fun toTimestamp(value: Long): Timestamp = Timestamp.newBuilder().setSeconds(value).build()

    @TypeConverter
    fun fromList(value: List<String>) = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<String>>(value)

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

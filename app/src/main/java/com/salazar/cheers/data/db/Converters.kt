package com.salazar.cheers.data.db

import androidx.room.TypeConverter
import cheers.chat.v1.MessageType
import cheers.chat.v1.RoomStatus
import cheers.chat.v1.RoomType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.protobuf.Timestamp
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Date

class Converters {
    @TypeConverter
    fun fromString(value: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(value, mapType)
    }
    @TypeConverter
    fun fromStringMap(map: Map<String, String>): String {
        val gson = Gson()
        return gson.toJson(map)
    }

    @TypeConverter
    fun fromStoryState(value: com.salazar.cheers.core.model.StoryState) = value.name

    @TypeConverter
    fun toStoryState(name: String) = com.salazar.cheers.core.model.StoryState.values()
        .firstOrNull { it.name.equals(name, ignoreCase = true) }
        ?: com.salazar.cheers.core.model.StoryState.UNKNOWN

    @TypeConverter
    fun fromActivityType(value: com.salazar.cheers.data.activity.ActivityType) = value.name

    @TypeConverter
    fun toActivityType(name: String) = com.salazar.cheers.data.activity.ActivityType.values()
        .firstOrNull { it.name.equals(name, ignoreCase = true) }
        ?: com.salazar.cheers.data.activity.ActivityType.NONE

    @TypeConverter
    fun fromMessageType(value: MessageType) = value.name

    @TypeConverter
    fun toMessageType(name: String) =
        MessageType.values()
            .firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: MessageType.TEXT

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

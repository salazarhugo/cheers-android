package com.salazar.cheers.core.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.salazar.cheers.core.model.ActivityType
import com.salazar.cheers.core.model.MessageType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Date

class Converters {

    @TypeConverter
    fun fromStringForIntArray(value: String): List<Int> {
        if (value.isBlank())
            return emptyList()
        return value.split(",").map { it.toInt() }
    }

    @TypeConverter
    fun fromIntList(list: List<Int>): String {
        return list.joinToString(",")
    }
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
            ?: MessageType.TEXT

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

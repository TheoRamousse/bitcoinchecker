package fr.uca.bitcoinchecker.utils.database.converter

import androidx.room.TypeConverter
import java.util.*

class DateTimestampConverter {
    @TypeConverter
    fun fromTimestamp(timestamp: Long?) = timestamp?.let { Date(it) }

    @TypeConverter
    fun toTimestamp(date: Date?) = date?.time
}
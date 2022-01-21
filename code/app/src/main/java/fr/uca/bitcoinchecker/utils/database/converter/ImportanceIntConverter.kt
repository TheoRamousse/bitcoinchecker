package fr.uca.bitcoinchecker.utils.database.converter

import androidx.room.TypeConverter
import fr.iut.bitcoinchecker.model.NotificationItem

fun Int.toImportance() = enumValues<NotificationItem.NotificationImportance>()[this]

class ImportanceIntConverter {

    @TypeConverter
    fun toOrdinal(notificationImportance: NotificationItem.NotificationImportance) = notificationImportance.ordinal
}
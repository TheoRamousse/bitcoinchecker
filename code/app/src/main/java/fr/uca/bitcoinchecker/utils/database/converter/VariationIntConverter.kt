package fr.uca.bitcoinchecker.utils.database.converter

import androidx.room.TypeConverter
import fr.uca.bitcoinchecker.model.NotificationItem

fun Int.toVariation() = enumValues<NotificationItem.Variation>()[this]

class VariationIntConverter {
    @TypeConverter
    fun fromInt(ordinal: Int) = ordinal.toImportance()

    @TypeConverter
    fun toOrdinal(variation: NotificationItem.Variation) = variation.ordinal
}
package fr.iut.bitcoinchecker.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import fr.uca.bitcoinchecker.model.ContainerNotificationItem
import java.util.Date

@Entity(tableName = "notifications", foreignKeys = [ForeignKey(entity = ContainerNotificationItem::class,
                                                                parentColumns = ["id"],
                                                                childColumns = ["containerId"],
                                                                onDelete = CASCADE)])
data class NotificationItem(@PrimaryKey(autoGenerate = true) val id : Long, var value: Int, var importance : NotificationImportance, var variation : Variation, var creationDate : Date?, var containerId : Long) {
    enum class NotificationImportance {
        LOW,
        MEDIUM,
        HIGH
    }

    enum class Variation {
        UP,
        DOWN
    }
}
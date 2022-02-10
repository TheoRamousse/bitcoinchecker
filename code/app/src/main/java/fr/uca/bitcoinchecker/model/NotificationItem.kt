package fr.uca.bitcoinchecker.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import fr.uca.bitcoinchecker.model.ContainerNotificationItem
import java.util.Date

@Entity(
    tableName = "notifications",
    foreignKeys = [ForeignKey(entity = ContainerNotificationItem::class,
                                parentColumns = ["id"],
                                childColumns = ["containerId"],
                                onDelete = CASCADE
                             )]
)
data class NotificationItem(
    var value: Long = 0,
    var isTreated: Boolean = false,
    var importance: NotificationImportance = NotificationImportance.LOW,
    var variation: Variation = Variation.DOWN,
    var creationDate: Date? = Date(),
    var containerId: Long = -42
) {
    @PrimaryKey(autoGenerate = true) var id: Long? = null

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
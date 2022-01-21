package fr.uca.bitcoinchecker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.iut.bitcoinchecker.model.NotificationItem

@Entity(tableName = "containers")
class ContainerNotificationItem(var name : String
                                    ) {
    @PrimaryKey(autoGenerate = true) var id : Long? = null
}
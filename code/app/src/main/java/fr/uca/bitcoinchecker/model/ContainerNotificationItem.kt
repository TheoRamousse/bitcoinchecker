package fr.uca.bitcoinchecker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.iut.bitcoinchecker.model.NotificationItem

@Entity(tableName = "containers")
data class ContainerNotificationItem(@PrimaryKey(autoGenerate = true) val id : Long, var name : String, var listOfNotifications : MutableList<NotificationItem>) {
}
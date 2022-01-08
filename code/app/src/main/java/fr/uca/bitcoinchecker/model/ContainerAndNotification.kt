package fr.uca.bitcoinchecker.model

import androidx.room.Embedded
import androidx.room.Relation
import fr.iut.bitcoinchecker.model.NotificationItem

data class ContainerAndNotification(@Embedded val containerNotificationItem: ContainerNotificationItem, @Relation(parentColumn = "id", entityColumn = "containerId") val notificationItem: NotificationItem) {
}
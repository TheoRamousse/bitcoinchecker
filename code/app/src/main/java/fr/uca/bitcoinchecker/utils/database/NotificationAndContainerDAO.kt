package fr.uca.bitcoinchecker.utils.database

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import fr.iut.bitcoinchecker.model.NotificationItem
import fr.uca.bitcoinchecker.model.ContainerAndNotification
import fr.uca.bitcoinchecker.model.ContainerNotificationItem

@Dao
interface NotificationAndContainerDAO {

    //Common to both
    @Transaction
    @Query("SELECT * FROM containers")
    fun getContainersWithNotifications() : List<ContainerAndNotification>


    //Container
    @Query("SELECT * FROM containers WHERE id=:id")
    fun getContainerById(id : Long)

    @Insert(onConflict = REPLACE)
    fun insertContainer(containerNotificationItem: ContainerNotificationItem)

    @Update
    fun updateContainer(containerNotificationItem: ContainerNotificationItem)

    @Delete
    fun deleteContainer(containerNotificationItem: ContainerNotificationItem)


    //Notification

    @Query("SELECT * FROM notifications WHERE id=:id")
    fun getNotificationsById(id : Long)

    @Insert(onConflict = REPLACE)
    fun insertNotification(notificationItem: NotificationItem)

    @Update
    fun updateNotification(notificationItem: NotificationItem)

    @Delete
    fun deleteNotification(notificationItem: NotificationItem)
}
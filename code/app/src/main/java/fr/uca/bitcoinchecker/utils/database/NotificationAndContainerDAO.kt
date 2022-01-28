package fr.uca.bitcoinchecker.utils.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import fr.iut.bitcoinchecker.model.NotificationItem
import fr.uca.bitcoinchecker.model.ContainerNotificationItem

@Dao
interface NotificationAndContainerDAO {

    //Container
    @Query("SELECT * FROM containers WHERE id=:id")
    fun getContainerById(id : Long) : LiveData<ContainerNotificationItem>

    @Insert(onConflict = REPLACE)
    fun insertContainer(containerNotificationItem: ContainerNotificationItem)

    @Update
    fun updateContainer(containerNotificationItem: ContainerNotificationItem)

    @Delete
    fun deleteContainer(containerNotificationItem: ContainerNotificationItem)

    @Query("SELECT id FROM containers WHERE name=:name")
    fun getContainerIdByName(name: String): LiveData<Long>


    //Notification

    @Query("SELECT * FROM notifications WHERE containerId=:id")
    fun getNotificationsByContainerId(id : Long) : LiveData<List<NotificationItem>>

    @Insert(onConflict = REPLACE)
    fun insertNotification(notificationItem: NotificationItem)

    @Update
    fun updateNotification(notificationItem: NotificationItem)

    @Delete
    fun deleteNotification(notificationItem: NotificationItem)

    @Query("SELECT * FROM notifications WHERE id=:id")
    fun getNotificationById(id: Long) : MutableLiveData<NotificationItem>

}
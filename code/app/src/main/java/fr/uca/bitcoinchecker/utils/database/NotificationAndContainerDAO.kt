package fr.uca.bitcoinchecker.utils.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import fr.uca.bitcoinchecker.model.ContainerNotificationItem
import fr.uca.bitcoinchecker.model.NotificationItem

@Dao
interface NotificationAndContainerDAO {

    //Container
    @Query("SELECT * FROM containers WHERE id=:id")
    fun getContainerById(id : Long) : LiveData<ContainerNotificationItem>

    @Query("SELECT * FROM containers WHERE id=:id")
    fun getContainerByIdNoLiveData(id : Long) : ContainerNotificationItem

    @Insert(onConflict = REPLACE)
    fun insertContainer(containerNotificationItem: ContainerNotificationItem)

    @Update
    fun updateContainer(containerNotificationItem: ContainerNotificationItem)

    @Delete
    fun deleteContainer(containerNotificationItem: ContainerNotificationItem)

    @Query("SELECT id FROM containers WHERE name=:name")
    fun getContainerIdByName(name: String): LiveData<Long>

    @Query("SELECT id FROM containers WHERE name=:name")
    fun getContainerIdByNameNoLiveData(name: String): Long

    @Query("SELECT * FROM containers")
    fun getAllContainers(): LiveData<List<ContainerNotificationItem>>

    @Query("SELECT * FROM containers")
    fun getAllContainersNoLiveData(): List<ContainerNotificationItem>


    //Notification

    @Query("SELECT * FROM notifications WHERE containerId=:id")
    fun getNotificationsByContainerId(id : Long) : LiveData<List<NotificationItem>>

    @Query("SELECT * FROM notifications WHERE containerId=:id")
    fun getNotificationsByContainerIdNoLiveData(id : Long) : List<NotificationItem>

    @Insert(onConflict = REPLACE)
    fun insertNotification(notificationItem: NotificationItem)

    @Update
    fun updateNotification(notificationItem: NotificationItem)

    @Delete
    fun deleteNotification(notificationItem: NotificationItem)

    @Query("SELECT * FROM notifications WHERE id=:id")
    fun getNotificationById(id: Long) : LiveData<NotificationItem>

}
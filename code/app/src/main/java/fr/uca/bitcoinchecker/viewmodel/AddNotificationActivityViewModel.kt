package fr.uca.bitcoinchecker.viewmodel

import androidx.lifecycle.*
import fr.uca.bitcoinchecker.model.ContainerNotificationItem
import fr.uca.bitcoinchecker.model.NotificationItem
import fr.uca.bitcoinchecker.utils.database.NotificationAndContainerDatabase
import fr.uca.bitcoinchecker.view.activity.ViewNotificationActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddNotificationActivityViewModel(var notificationIdInBase: Long, var notificationIdInApi : String ,var containerName: String): ViewModel() {
    private lateinit var observerAddNotif: Observer<Long>
    private val databaseAccessor: NotificationAndContainerDatabase =
        NotificationAndContainerDatabase.getInstance()
    val notification = if(notificationIdInBase == ViewNotificationActivity.NEW_NOTIFICATION_ID)  MutableLiveData(
        NotificationItem()
    ) else databaseAccessor.notificationAndContainerDAO().getNotificationById(notificationIdInBase)
    private var containerIdLiveData = databaseAccessor.notificationAndContainerDAO().getContainerIdByName(containerName)


    fun addNotification() {

        if (containerIdLiveData.value == null) {

            observerAddNotif = Observer<Long>(){
                it?.let {
                    viewModelScope.launch(Dispatchers.IO) {
                        withContext(Dispatchers.Main) {
                            containerIdLiveData.removeObserver(observerAddNotif)
                        }
                        notification.value?.containerId = it
                        databaseAccessor.notificationAndContainerDAO().insertNotification(notificationItem = notification.value!!)
                    }
                }
            }
            containerIdLiveData.observeForever(observerAddNotif)

            viewModelScope.launch(Dispatchers.IO) {
                databaseAccessor.notificationAndContainerDAO()
                    .insertContainer(ContainerNotificationItem(name = containerName, notificationIdInApi))
            }
        }
        else{
            viewModelScope.launch(Dispatchers.IO) {
                databaseAccessor.notificationAndContainerDAO().insertNotification(notificationItem = notification.value!!)
            }
        }
    }

    fun updateNotification(){
        viewModelScope.launch(Dispatchers.IO) {
            databaseAccessor.notificationAndContainerDAO().updateNotification(notificationItem = notification.value!!)
        }
    }
}
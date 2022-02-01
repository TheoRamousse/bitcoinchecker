package fr.uca.bitcoinchecker.viewmodel

import androidx.lifecycle.*
import fr.iut.bitcoinchecker.model.NotificationItem
import fr.uca.bitcoinchecker.model.ContainerNotificationItem
import fr.uca.bitcoinchecker.utils.database.NotificationAndContainerDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class AddNotificationActivityViewModel(var id: Long?, var containerName: String): ViewModel() {
    private lateinit var observerAddNotif: Observer<Long>
    private var databaseAccessor: NotificationAndContainerDatabase =
        NotificationAndContainerDatabase.getInstance()
    lateinit var notification: LiveData<NotificationItem>
    private var containerIdLiveData = databaseAccessor.notificationAndContainerDAO().getContainerIdByName(containerName)

    init {
        if(id != null){
            notification = databaseAccessor.notificationAndContainerDAO().getNotificationById(id!!)
        }
    }


    fun addNotification(tmpNewNotif : NotificationItem) {

        if (containerIdLiveData.value == null) {

            observerAddNotif = Observer<Long>(){
                it?.let {
                    viewModelScope.launch(Dispatchers.IO) {
                        tmpNewNotif.containerId = it
                        databaseAccessor.notificationAndContainerDAO().insertNotification(notificationItem = tmpNewNotif)
                        withContext(Dispatchers.Main) {
                            containerIdLiveData.removeObserver(observerAddNotif)
                        }
                    }
                }
            }
            containerIdLiveData.observeForever(observerAddNotif)

            viewModelScope.launch(Dispatchers.IO) {
                databaseAccessor.notificationAndContainerDAO()
                    .insertContainer(ContainerNotificationItem(name = containerName))
            }
        }
        else{
            viewModelScope.launch(Dispatchers.IO) {
                databaseAccessor.notificationAndContainerDAO().insertNotification(notificationItem = tmpNewNotif)
            }
        }
    }

    fun updateNotification(){
        viewModelScope.launch(Dispatchers.IO) {
            databaseAccessor.notificationAndContainerDAO().updateNotification(notificationItem = notification.value!!)
        }
    }
}
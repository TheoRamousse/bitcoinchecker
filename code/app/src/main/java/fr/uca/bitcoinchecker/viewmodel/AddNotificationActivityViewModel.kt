package fr.uca.bitcoinchecker.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.iut.bitcoinchecker.model.NotificationItem
import fr.uca.bitcoinchecker.model.ContainerNotificationItem
import fr.uca.bitcoinchecker.utils.database.NotificationAndContainerDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class AddNotificationActivityViewModel(var id: Long?, var containerId: Long?, var containerName: String): ViewModel() {
    private lateinit var observerAddNotif: androidx.lifecycle.Observer<Long>
    private var databaseAccessor: NotificationAndContainerDatabase =
        NotificationAndContainerDatabase.getInstance()
    var notification: MutableLiveData<NotificationItem> = MutableLiveData<NotificationItem>()
    private var containerIdLiveData = MutableLiveData<Long>()

    init {
        if(id != null){
            notification = databaseAccessor.notificationAndContainerDAO().getNotificationById(id!!)
        }
        else{
            if(containerId != null) {
                containerIdLiveData.value = containerId!!
                notification.value = NotificationItem(
                    0,
                    NotificationItem.NotificationImportance.LOW,
                    NotificationItem.Variation.DOWN,
                    Date(),
                    containerId!!
                )
            }
            else{
                notification.value = NotificationItem(
                    0,
                    NotificationItem.NotificationImportance.LOW,
                    NotificationItem.Variation.DOWN,
                    Date(),
                    0
                )
            }
        }
    }


    fun addNotification() {
        observerAddNotif = Observer<Long>(){
            it?.let {
                viewModelScope.launch(Dispatchers.IO) {
                    notification.value?.containerId = it
                    databaseAccessor.notificationAndContainerDAO().insertNotification(notificationItem = notification.value!!)
                    withContext(Dispatchers.Main) {
                        containerIdLiveData.removeObserver(observerAddNotif)
                    }
                }
            }
        }
        containerIdLiveData.observeForever(observerAddNotif)

        if (containerIdLiveData.value == null) {
            viewModelScope.launch(Dispatchers.IO) {
                databaseAccessor.notificationAndContainerDAO()
                    .insertContainer(ContainerNotificationItem(name = containerName))
            }
        }
    }
}
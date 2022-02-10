package fr.uca.bitcoinchecker.viewmodel

import androidx.lifecycle.*
import fr.uca.bitcoinchecker.model.ContainerNotificationItem
import fr.uca.bitcoinchecker.model.NotificationItem
import fr.uca.bitcoinchecker.utils.database.NotificationAndContainerDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ListFragmentViewModel(var name: String, var idCrypto: String, private val dataInitializedListener: DataInitializedListener) : ViewModel() {
    interface DataInitializedListener {
        fun onDataInitialized()
    }

    private lateinit var observerAddNotif: Observer<Long>
    private lateinit var observer: Observer<Long>
    private var databaseAccessor: NotificationAndContainerDatabase =
        NotificationAndContainerDatabase.getInstance()
    private var containerId = getContainerIdByName(name)
    lateinit var dataNotification: LiveData<List<NotificationItem>>

    init {
        observer = Observer<Long>(){
            if(it != null){
                dataNotification = Transformations.switchMap(containerId){
                    databaseAccessor.notificationAndContainerDAO().getNotificationsByContainerId(it)
                }
                dataInitializedListener.onDataInitialized()
                containerId.removeObserver(observer)
            }
        }

        containerId.observeForever(observer)

    }

    fun addNotification(newValue: NotificationItem) {
        observerAddNotif = Observer<Long>(){
            it?.let {
                viewModelScope.launch(Dispatchers.IO) {
                    newValue.containerId = it
                    databaseAccessor.notificationAndContainerDAO().insertNotification(newValue)
                    withContext(Dispatchers.Main) {
                        containerId.removeObserver(observerAddNotif)
                    }
                }
            }
        }
        containerId.observeForever(observerAddNotif)

        if (containerId.value == null) {
            containerId.observeForever(observer)
            viewModelScope.launch(Dispatchers.IO) {
                databaseAccessor.notificationAndContainerDAO()
                    .insertContainer(ContainerNotificationItem(name = name, idCrypto))
            }
        }
    }

    fun removeNotification(valueToDelete: NotificationItem) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseAccessor.notificationAndContainerDAO().deleteNotification(valueToDelete)
        }
    }

    fun findNotificationsByContainerId(id: Long): LiveData<List<NotificationItem>> {
        return databaseAccessor.notificationAndContainerDAO().getNotificationsByContainerId(id)
    }



    private fun getContainerIdByName(name: String) =
        databaseAccessor.notificationAndContainerDAO().getContainerIdByName(name)
}
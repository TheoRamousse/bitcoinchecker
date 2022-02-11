package fr.uca.bitcoinchecker.utils.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import androidx.core.app.NotificationCompat
import fr.uca.bitcoinchecker.R
import fr.uca.bitcoinchecker.model.ContainerNotificationItem
import fr.uca.bitcoinchecker.model.Endpoints
import fr.uca.bitcoinchecker.model.NotificationItem
import fr.uca.bitcoinchecker.model.Quote
import fr.uca.bitcoinchecker.utils.api.HttpRequestExecutor
import fr.uca.bitcoinchecker.utils.api.json_converter.JsonToQuotesConverter
import fr.uca.bitcoinchecker.utils.database.NotificationAndContainerDatabase
import kotlinx.coroutines.*
import java.lang.Runnable
import kotlin.coroutines.CoroutineContext

class NotificationSender() : JobService(), HttpRequestExecutor.Callback, CoroutineScope {
    companion object {
        val JOB_ID = 34844
        val NOTIFICATION_CHANNEL_ID = "424344"
        val PERIODICITY_IN_SEC = 3
    }
    private val RESPONSE_CURRENT_QUOTE = "fr.uca.bitcoinchecker.utils.service.notificationsender"
    private val databaseAccessor: NotificationAndContainerDatabase =
    NotificationAndContainerDatabase.getInstance()

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job


    override fun onStartJob(p0: JobParameters?): Boolean {
        Thread(Runnable {
            inspectAllNotifs()
            jobFinished(p0, true)
        }).start()
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }


    private fun inspectAllNotifs() {
            async(Dispatchers.IO) {
                val allContainers: List<ContainerNotificationItem> =
                    databaseAccessor.notificationAndContainerDAO().getAllContainersNoLiveData()


                allContainers.forEach {
                    HttpRequestExecutor.executeUrlResolution(
                        this@NotificationSender,
                        Endpoints.API_ENDPOINT_CURRENT_QUOTE.prefix + it.idCrypto + Endpoints.API_ENDPOINT_CURRENT_QUOTE.suffix,
                        JsonToQuotesConverter,
                        RESPONSE_CURRENT_QUOTE
                    )
                }
            }
    }

    override suspend fun onDataReceived(item: Any, responseKey: String) {
        if(responseKey == RESPONSE_CURRENT_QUOTE){
            with(item as Quote)
            {
                coroutineScope {
                    async(Dispatchers.IO) {
                        val listOfNotifications = databaseAccessor.notificationAndContainerDAO()
                            .getNotificationsByContainerIdNoLiveData(
                                databaseAccessor.notificationAndContainerDAO()
                                    .getContainerIdByNameNoLiveData(item.name)
                            )
                        listOfNotifications.forEach {
                            if (it.isTreated) {
                                if (it.variation == NotificationItem.Variation.UP && it.value <= item.currentPrice) {
                                    it.isTreated = false
                                } else if (it.variation == NotificationItem.Variation.DOWN && it.value >= item.currentPrice) {
                                    it.isTreated = false
                                }
                            } else {
                                if (it.variation == NotificationItem.Variation.UP && it.value < item.currentPrice || it.variation == NotificationItem.Variation.DOWN && it.value > item.currentPrice) {
                                    it.isTreated = true
                                    sendNotif(it, item.currentPrice)
                                }
                            }
                            databaseAccessor.notificationAndContainerDAO().updateNotification(it)
                        }
                    }
                }
            }
        }
    }

    private fun sendNotif(notification: NotificationItem, currentPrice: Double){
        val mNotificationManager =
            baseContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
        var variationAsStringId = R.string.above
        if(notification.variation == NotificationItem.Variation.DOWN)
            variationAsStringId = R.string.under
        mBuilder.setContentTitle(getString(R.string.warning))
        mBuilder.setContentText(String.format("%s %s %s %s %d$ (%f$)", getString(R.string.the), databaseAccessor.notificationAndContainerDAO().getContainerByIdNoLiveData(notification.containerId).name, getString(R.string.is_past), getString(variationAsStringId), notification.value, currentPrice))
        mBuilder.setSmallIcon(R.drawable.ic_baseline_warning_24)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBuilder.color = baseContext.getColor(R.color.black)
        };
        mBuilder.setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var importance = NotificationManager.IMPORTANCE_LOW
            if(notification.importance == NotificationItem.NotificationImportance.HIGH)
                importance = NotificationManager.IMPORTANCE_HIGH
            else if(notification.importance == NotificationItem.NotificationImportance.MEDIUM)
                importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            assert(mNotificationManager != null)
            mNotificationManager!!.createNotificationChannel(notificationChannel)
        }
        assert(mNotificationManager != null)
        mNotificationManager!!.notify(System.currentTimeMillis().toInt(), mBuilder.build())

    }
}
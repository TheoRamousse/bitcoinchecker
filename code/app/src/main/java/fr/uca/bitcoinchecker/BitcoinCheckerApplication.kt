package fr.uca.bitcoinchecker

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import fr.uca.bitcoinchecker.utils.database.NotificationAndContainerDatabase
import fr.uca.bitcoinchecker.utils.service.NotificationSender


class BitcoinCheckerApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        NotificationAndContainerDatabase.initialize(this)

        scheduleJob()
    }

    private fun scheduleJob(){
        val componentName = ComponentName(applicationContext, NotificationSender::class.java)
        val info = JobInfo.Builder(NotificationSender.JOB_ID, componentName)
            .setRequiresCharging(false)
            .setPersisted(true)
            .setMinimumLatency(1)
            .setOverrideDeadline(NotificationSender.PERIODICITY_IN_SEC*1000L)
            .build()
        val scheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.schedule(info)
    }
}
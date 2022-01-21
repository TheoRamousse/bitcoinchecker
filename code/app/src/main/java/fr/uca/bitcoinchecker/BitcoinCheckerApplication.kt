package fr.uca.bitcoinchecker

import android.app.Application
import fr.uca.bitcoinchecker.utils.database.NotificationAndContainerDatabase

class BitcoinCheckerApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        NotificationAndContainerDatabase.initialize(this)
    }
}
package fr.uca.bitcoinchecker.utils.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.iut.bitcoinchecker.model.NotificationItem
import fr.uca.bitcoinchecker.model.ContainerNotificationItem
import fr.uca.bitcoinchecker.utils.database.converter.DateTimestampConverter
import fr.uca.bitcoinchecker.utils.database.converter.ImportanceIntConverter
import fr.uca.bitcoinchecker.utils.database.converter.VariationIntConverter

@Database(entities = [ContainerNotificationItem::class, NotificationItem::class], version = 1)
@TypeConverters(ImportanceIntConverter::class, VariationIntConverter::class, DateTimestampConverter::class)
abstract class NotificationAndContainerDatabase : RoomDatabase() {

    abstract fun notificationAndContainerDAO() : NotificationAndContainerDAO

    companion object {
        private lateinit var application : Context
        const val DB_FILENAME = "BitcoinChecker.db"

        @Volatile
        private var instance: NotificationAndContainerDatabase? = null

        fun getInstance(): NotificationAndContainerDatabase {
            if (::application.isInitialized) {
                if (instance == null)
                    synchronized(this) {
                        if (instance == null)
                            instance = Room.databaseBuilder(
                                application.applicationContext,
                                NotificationAndContainerDatabase::class.java,
                                DB_FILENAME
                            ).build()
                    }
                return instance!!
            } else {
                throw RuntimeException("Database not initialized !!!!!")
            }
        }

        @Synchronized
        fun initialize(app: Context) {
            if (::application.isInitialized) {
                throw RuntimeException("Database is already initialized !!!!!!!!")
            }
            application = app
        }
    }
}
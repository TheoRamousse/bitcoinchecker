package fr.uca.bitcoinchecker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "containers")
class ContainerNotificationItem(var name : String,
                                var idCrypto: String
                                    ) {
    @PrimaryKey(autoGenerate = true) var id : Long = 0
}
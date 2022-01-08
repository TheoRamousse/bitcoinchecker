package fr.uca.bitcoinchecker.utils

import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class HttpRequestExecutor {
    companion object{
        fun<T: Any, U: Any> executeUrlResolution(callback : Callback<T>, apiEndpoint : String, converter: JsonConverter<T,U>){
            val thread = Thread {
                try {
                    val connection = URL(apiEndpoint).openConnection() as HttpURLConnection
                    var receivedText : String
                    try{
                        connection.connect()
                        receivedText = connection.inputStream.use { inputStream ->
                            inputStream.reader().use {
                                it.readText()
                            }
                        }
                    }finally {
                        connection.disconnect()
                    }

                    callback.onDataReceived(converter.convertUniqueItem(receivedText))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            thread.start()

        }
    }

    interface Callback<T: Any>{
        fun onDataReceived(item : T)
    }
}
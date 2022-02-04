package fr.uca.bitcoinchecker.utils.api

import fr.uca.bitcoinchecker.utils.api.json_converter.JsonConverter
import kotlinx.coroutines.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class HttpRequestExecutor {
    companion object{
         suspend fun<T: Any, U: Any> executeUrlResolution(callback : Callback, apiEndpoint : String, converter: JsonConverter<T, U>, responseKey: String) = withContext(Dispatchers.IO){
                try {
                    val connection = URL(apiEndpoint).openConnection() as HttpURLConnection
                    var receivedText: String
                    try {
                        connection.connect()
                        receivedText = connection.inputStream.use { inputStream ->
                            inputStream.reader().use {
                                it.readText()
                            }
                        }
                    } finally {
                        connection.disconnect()
                    }


                    callback.onDataReceived(
                        converter.convertUniqueItem(receivedText),
                        responseKey
                    )

                } catch (e: Exception) {
                    e.printStackTrace()
                }



        }
    }

    interface Callback{
        fun onDataReceived(item : Any, responseKey: String)
    }
}
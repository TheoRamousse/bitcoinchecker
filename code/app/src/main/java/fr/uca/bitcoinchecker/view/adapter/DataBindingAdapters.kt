package fr.uca.bitcoinchecker.view.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import androidx.databinding.InverseMethod
import fr.uca.bitcoinchecker.model.NotificationItem
import fr.uca.bitcoinchecker.model.Quote
import fr.uca.bitcoinchecker.utils.database.converter.toImportance
import fr.uca.bitcoinchecker.utils.database.converter.toVariation
import fr.uca.bitcoinchecker.view.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URL

object DataBindingAdapters {

    @JvmStatic
    @BindingConversion
    fun textCurrentPriceOfCrypto(currentCrypto: Quote?): String{
        if(currentCrypto != null)
        {
            return String.format("1 %s = %.2f $", currentCrypto.name, currentCrypto.currentPrice)
        }
        else{
            return ""
        }
    }

    @JvmStatic
    @InverseMethod("stringToLong")
    fun longToString(value: Long): String{
        if(value != 0L)
        {
            return String.format("%d", value)
        }
        else{
            return ""
        }
    }

    @JvmStatic
    fun stringToLong(value: String): Long {
        return if (value.isBlank()) 0L else value.toLong()
    }


    @JvmStatic
    @InverseMethod("intToVariation")
    fun variationToInt(value: NotificationItem.Variation?) = value?.ordinal ?: 0

    @JvmStatic
    fun intToVariation(value: Int) = value.toVariation()


    @JvmStatic
    @InverseMethod("intToImportance")
    fun importanceToInt(value: NotificationItem.NotificationImportance?) = value?.ordinal ?: 0

    @JvmStatic
    fun intToImportance(value: Int) = value.toImportance()


    @JvmStatic
    @BindingAdapter("app:image")
    fun urlToImage(view: ImageView,currentUrl: String?) {
        if (currentUrl != null) {
            runBlocking(Dispatchers.Default) {
                var image: Bitmap? = null
                val job = launch {
                    image =
                        BitmapFactory.decodeStream(
                            URL(currentUrl).openConnection().getInputStream()
                        )
                }
                job.join()
                (view.context as MainActivity).mainScope.launch {
                    view.setImageBitmap(image)
                }
            }
        }
    }

}
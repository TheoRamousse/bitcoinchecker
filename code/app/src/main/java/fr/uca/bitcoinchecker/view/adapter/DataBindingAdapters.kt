package fr.uca.bitcoinchecker.view.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import fr.uca.bitcoinchecker.model.Quote
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
    @BindingAdapter("app:image")
    fun urlToImage(view: ImageView,currentUrl: String?){
        if(currentUrl != null)
        {
            runBlocking(Dispatchers.Default) {
                var image: Bitmap?=null
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
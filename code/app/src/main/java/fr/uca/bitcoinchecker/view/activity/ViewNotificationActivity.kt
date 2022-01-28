package fr.uca.bitcoinchecker.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import fr.uca.bitcoinchecker.R

class ViewNotificationActivity: AppCompatActivity() {


    companion object{
        fun getIntent(context: Context)=Intent(context, ViewNotificationActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_notification_activity)

    }
}
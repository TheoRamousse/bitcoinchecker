package fr.uca.bitcoinchecker.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import fr.uca.bitcoinchecker.R
import fr.uca.bitcoinchecker.databinding.AddNotificationActivityBinding
import fr.uca.bitcoinchecker.viewmodel.AddNotificationActivityViewModel
import fr.uca.bitcoinchecker.viewmodel.factory.ViewModelFactory

class ViewNotificationActivity: AppCompatActivity() {


    companion object{
        val NEW_NOTIFICATION_ID = -42L
        val NOTIFICATION_ID = "fr.uca.viewNotificationActivity.id"
        val CRYPTO_ID_IN_API = "fr.uca.viewNotificationActivity.id_in_api"
        val CONTAINER_NAME = "fr.uca.viewNotificationActivity.containerName"
        fun getIntent(context: Context, id: Long, idInApi: String,containerName: String)=Intent(context, ViewNotificationActivity::class.java).putExtra(NOTIFICATION_ID, id).putExtra(
            CONTAINER_NAME, containerName).putExtra(CRYPTO_ID_IN_API, idInApi)
    }


    private lateinit var binding: AddNotificationActivityBinding
    private lateinit var viewModel: AddNotificationActivityViewModel
    private var containerName: String? = null
    private var notificationId: Long = NEW_NOTIFICATION_ID
    private var cryptoIdInApi: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtras()
        viewModel = ViewModelProvider(this@ViewNotificationActivity, ViewModelFactory.createViewModel { AddNotificationActivityViewModel(notificationId, cryptoIdInApi!! ,containerName!!) }).get(AddNotificationActivityViewModel::class.java)

        binding = DataBindingUtil.setContentView<AddNotificationActivityBinding>(this,R.layout.add_notification_activity)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        if(notificationId != NEW_NOTIFICATION_ID)
            binding.buttonAddOrUpdate.text = getText(R.string.updateNotification)

    }

    private fun setupExtras() {
        cryptoIdInApi = intent.getStringExtra(CRYPTO_ID_IN_API)
        notificationId = intent.getLongExtra(NOTIFICATION_ID,-42L)
        containerName = intent.getStringExtra(CONTAINER_NAME)
        if(containerName == ""){
            containerName = null
        }
    }

    fun addOrUpdateNotif(view: View){
        viewModel.addNotification()
        finish()
    }
}
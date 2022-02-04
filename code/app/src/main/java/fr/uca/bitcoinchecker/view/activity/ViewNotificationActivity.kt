package fr.uca.bitcoinchecker.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textview.MaterialTextView
import fr.iut.bitcoinchecker.model.NotificationItem
import fr.uca.bitcoinchecker.R
import fr.uca.bitcoinchecker.databinding.ActivityMainBinding
import fr.uca.bitcoinchecker.databinding.AddNotificationActivityBinding
import fr.uca.bitcoinchecker.viewmodel.AddNotificationActivityViewModel
import fr.uca.bitcoinchecker.viewmodel.MainActivityViewModel
import fr.uca.bitcoinchecker.viewmodel.factory.ViewModelFactory
import java.util.*

class ViewNotificationActivity: AppCompatActivity() {


    companion object{
        val NEW_NOTIFICATION_ID = -42L
        val NOTIFICATION_ID = "fr.uca.viewNotificationActivity.id"
        val CONTAINER_NAME = "fr.uca.viewNotificationActivity.containerName"
        fun getIntent(context: Context, id: Long, containerName: String)=Intent(context, ViewNotificationActivity::class.java).putExtra(NOTIFICATION_ID, id).putExtra(
            CONTAINER_NAME, containerName)
    }


    private lateinit var binding: AddNotificationActivityBinding
    private lateinit var viewModel: AddNotificationActivityViewModel
    private var containerName: String? = null
    private var notificationId: Long = NEW_NOTIFICATION_ID

    private lateinit var spinnerChoiceUpDown: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtras()
        viewModel = ViewModelProvider(this@ViewNotificationActivity, ViewModelFactory.createViewModel { AddNotificationActivityViewModel(notificationId, containerName!!) }).get(AddNotificationActivityViewModel::class.java)

        binding = DataBindingUtil.setContentView<AddNotificationActivityBinding>(this,R.layout.add_notification_activity)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        spinnerChoiceUpDown = binding.spinnerChoiceUpDown

        setupSpinner()

    }

    private fun setupExtras() {
        notificationId = intent.getLongExtra(NOTIFICATION_ID,-42L)
        containerName = intent.getStringExtra(CONTAINER_NAME)
        if(containerName == ""){
            containerName = null
        }
    }

    private fun setupSpinner() {
        spinnerChoiceUpDown.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            NotificationItem.Variation.values()
        )
        spinnerChoiceUpDown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("TOTO", id.toString());
            }

        }
    }

    fun addNotif(view: View){
        viewModel.addNotification()
    }
}
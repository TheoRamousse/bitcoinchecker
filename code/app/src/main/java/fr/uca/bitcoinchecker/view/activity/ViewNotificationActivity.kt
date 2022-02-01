package fr.uca.bitcoinchecker.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.util.AttributeSet
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textview.MaterialTextView
import fr.iut.bitcoinchecker.model.NotificationItem
import fr.uca.bitcoinchecker.R
import fr.uca.bitcoinchecker.viewmodel.AddNotificationActivityViewModel
import fr.uca.bitcoinchecker.viewmodel.MainActivityViewModel
import fr.uca.bitcoinchecker.viewmodel.factory.ViewModelFactory
import java.util.*

class ViewNotificationActivity: AppCompatActivity() {


    companion object{
        val NOTIFICATION_ID = "fr.uca.viewNotificationActivity.id"
        val CONTAINER_NAME = "fr.uca.viewNotificationActivity.containerName"
        fun getIntent(context: Context, containerName: String)=Intent(context, ViewNotificationActivity::class.java).putExtra(
            CONTAINER_NAME, containerName)
        fun getIntent(context: Context, id: Long, containerName: String)=Intent(context, ViewNotificationActivity::class.java).putExtra(NOTIFICATION_ID, id).putExtra(
            CONTAINER_NAME, containerName)
    }


    private lateinit var viewModel: AddNotificationActivityViewModel
    private var containerName: String? = null
    private var notificationId: Long? = null

    private lateinit var editCryptoName: EditText
    private lateinit var spinnerChoiceUpDown: Spinner
    private lateinit var editValueDollar: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_notification_activity)

        findElementsById()

        setupExtras()

        setupSpinner()

        if(containerName != null){
            editCryptoName.text = Editable.Factory.getInstance().newEditable(containerName!!)
            viewModel = ViewModelProvider(this@ViewNotificationActivity, ViewModelFactory.createViewModel { AddNotificationActivityViewModel(notificationId, containerName!!) }).get(
                AddNotificationActivityViewModel::class.java)


            if(notificationId != null){
                populateElementsWithLiveData()
            }

        }
    }

    private fun setupExtras() {
        notificationId = intent.getLongExtra(NOTIFICATION_ID,-1L)
        if(notificationId == -1L){
            notificationId = null
        }
        containerName = intent.getStringExtra(CONTAINER_NAME)
        if(containerName == ""){
            containerName = null
        }
    }

    private fun setupSpinner() {
        spinnerChoiceUpDown.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, NotificationItem.Variation.values())
    }

    private fun populateElementsWithLiveData() {
        editValueDollar.text = Editable.Factory.getInstance().newEditable(viewModel.notification.value?.value.toString())
        spinnerChoiceUpDown.setSelection(viewModel.notification.value?.variation?.ordinal!!)
    }

    private fun findElementsById() {
        editCryptoName = findViewById(R.id.edit_crypto_name)
        spinnerChoiceUpDown = findViewById(R.id.spinner_choice_up_down)
        editValueDollar = findViewById(R.id.edit_value_dollar)
    }

    fun addNotif(view: View){
        viewModel.addNotification(NotificationItem(editValueDollar.text.toString().toInt(), NotificationItem.NotificationImportance.MEDIUM,
            spinnerChoiceUpDown.selectedItem as NotificationItem.Variation, Date(), 0
        ))
    }
}
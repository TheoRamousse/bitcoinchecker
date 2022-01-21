package fr.uca.bitcoinchecker.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.iut.bitcoinchecker.model.NotificationItem
import fr.uca.bitcoinchecker.R
import fr.uca.bitcoinchecker.view.adapter.NotificationRecyclerViewAdapter
import fr.uca.bitcoinchecker.viewmodel.MainActivityViewModel
import fr.uca.bitcoinchecker.viewmodel.factory.ViewModelFactory
import java.util.*
import kotlin.math.log

class NotificationListFragment : Fragment(), LifecycleOwner, MainActivityViewModel.DataInitializedListener {
    companion object {
        private const val EXTRA_CONTAINER_NAME = "fr.uca.bitcoinchecker.extra_container_id"

        fun newInstance(containerName: String) = NotificationListFragment().apply {
            arguments = bundleOf(EXTRA_CONTAINER_NAME to containerName)
        }
    }

    private lateinit var containerName: String
    private lateinit var viewModel : MainActivityViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterList: NotificationRecyclerViewAdapter
    private lateinit var floatingButton: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        containerName = savedInstanceState?.getString(EXTRA_CONTAINER_NAME) ?: arguments?.getString(
            EXTRA_CONTAINER_NAME)!!

        viewModel = ViewModelProvider(this, ViewModelFactory.createViewModel { MainActivityViewModel(containerName, this) }).get(MainActivityViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.notification_list_fragment, container, false)
        recyclerView = view.findViewById(R.id.listNotifications)
        adapterList = NotificationRecyclerViewAdapter(view.context)
        recyclerView.adapter = adapterList

        floatingButton = view.findViewById(R.id.floatingButton)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        floatingButton.setOnClickListener {
            viewModel.addNotification(NotificationItem( 42, NotificationItem.NotificationImportance.HIGH, NotificationItem.Variation.UP,
                Date(),-1))
        }
    }

    override fun onDataInitialized() {
        viewModel.dataNotification.observe(this) {
            adapterList.updateList(it!!)
        }
    }


}
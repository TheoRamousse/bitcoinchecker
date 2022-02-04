package fr.uca.bitcoinchecker.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.iut.bitcoinchecker.model.NotificationItem
import fr.uca.bitcoinchecker.R
import fr.uca.bitcoinchecker.view.activity.ViewNotificationActivity
import fr.uca.bitcoinchecker.view.adapter.NotificationRecyclerViewAdapter
import fr.uca.bitcoinchecker.viewmodel.ListFragmentViewModel
import fr.uca.bitcoinchecker.viewmodel.factory.ViewModelFactory
import java.util.*

class NotificationListFragment : Fragment(), LifecycleOwner, ListFragmentViewModel.DataInitializedListener {
    companion object {
        private const val EXTRA_CONTAINER_NAME = "fr.uca.bitcoinchecker.extra_container_id"

        fun newInstance(containerName: String) = NotificationListFragment().apply {
            arguments = bundleOf(EXTRA_CONTAINER_NAME to containerName)
        }
    }

    private lateinit var containerName: String
    private lateinit var viewModel : ListFragmentViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterList: NotificationRecyclerViewAdapter
    private lateinit var floatingButton: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        containerName = savedInstanceState?.getString(EXTRA_CONTAINER_NAME) ?: arguments?.getString(
            EXTRA_CONTAINER_NAME)!!

        viewModel = ViewModelProvider(this, ViewModelFactory.createViewModel { ListFragmentViewModel(containerName, this) }).get(ListFragmentViewModel::class.java)

        if(savedInstanceState?.getString(EXTRA_CONTAINER_NAME) != null)
            onDataInitialized()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_CONTAINER_NAME, containerName)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.notification_list_fragment, container, false)
        recyclerView = view.findViewById(R.id.listNotifications)
        adapterList = NotificationRecyclerViewAdapter(view.context, containerName)
        recyclerView.adapter = adapterList

        floatingButton = view.findViewById(R.id.floatingButton)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        floatingButton.setOnClickListener {
            startActivity(ViewNotificationActivity.getIntent(requireContext(), -42L, containerName))
        }
    }

    override fun onDataInitialized() {
        viewModel.dataNotification.observe(this) {
            adapterList.updateList(it!!)
        }
    }


}
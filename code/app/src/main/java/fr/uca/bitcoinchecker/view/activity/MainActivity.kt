package fr.uca.bitcoinchecker.view.activity

import android.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.uca.bitcoinchecker.R
import fr.uca.bitcoinchecker.view.fragment.NotificationListFragment
import fr.uca.bitcoinchecker.viewmodel.MainActivityViewModel

class MainActivity : SimpleFragmentActivity() {
    private lateinit var viewModel: MainActivityViewModel
    private var selectedContainerName: String = "bitcoin"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun createFragment() = NotificationListFragment.newInstance(selectedContainerName)

    override fun getLayoutResId() = R.layout.activity_main
}
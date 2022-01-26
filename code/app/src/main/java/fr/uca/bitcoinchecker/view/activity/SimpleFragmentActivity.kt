package fr.uca.bitcoinchecker.view.activity

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import fr.uca.bitcoinchecker.R


abstract class SimpleFragmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(getLayoutResId())


        if (!isFragmentLateinitialized()){
            startFragment()
        }
    }

    protected fun startFragment() {
        if(supportFragmentManager.findFragmentById(R.id.container_fragment) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container_fragment, createFragment())
                .commit()
        }
    }

    protected fun startFragmentOrReplace() {
        if(supportFragmentManager.findFragmentById(R.id.container_fragment) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container_fragment, createFragment())
                .commit()
        }
        else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_fragment, createFragment())
                .commit()
        }
    }


    protected abstract fun isFragmentLateinitialized(): Boolean

    protected abstract fun createFragment(): Fragment


    @LayoutRes
    protected abstract fun getLayoutResId(): Int
}

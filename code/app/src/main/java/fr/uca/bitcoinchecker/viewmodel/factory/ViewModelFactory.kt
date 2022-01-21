package fr.uca.bitcoinchecker.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory {
    companion object{
        fun <VM : ViewModel> createViewModel(f: () -> VM): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = f() as T
            }
        }
    }
}
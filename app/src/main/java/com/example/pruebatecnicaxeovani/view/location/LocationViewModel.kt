package com.example.pruebatecnicaxeovani.view.location

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebatecnicaxeovani.repository.RepositoryPerson
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {
    val locationLiveData = MutableLiveData<HashMap<String,String>>()
    val networkConnection = MutableLiveData<Boolean>()
    fun getLocation(context: Context, requireActivity: FragmentActivity){
        viewModelScope.launch {
            RepositoryPerson.getCurrentLocation(context,this@LocationViewModel, requireActivity)
        }
    }
}
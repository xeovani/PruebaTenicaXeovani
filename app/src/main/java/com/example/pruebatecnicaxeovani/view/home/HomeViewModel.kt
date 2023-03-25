package com.example.pruebatecnicaxeovani.view.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebatecnicaxeovani.repository.RepositoryPerson
import com.example.pruebatecnicaxeovani.responses2.ResponsePerson
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

      val personLiveData = MutableLiveData<com.example.pruebatecnicaxeovani.responses2.Result> ()
      fun getPerson(context: Context, page: Int = 1){
          viewModelScope.launch {
              RepositoryPerson.getPopularPerson(context,page, this@HomeViewModel)
          }
      }

}
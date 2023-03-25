package com.example.pruebatecnicaxeovani.view.dashboard

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebatecnicaxeovani.repository.RepositoryPerson
import com.example.pruebatecnicaxeovani.responses2.movies.ResponseMovies
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    val popularMoviesLiveData =  MutableLiveData<ResponseMovies>()
      fun getPopularMovies(context: Context,page: Int = 1)  {
          viewModelScope.launch {
              RepositoryPerson.getMoviesPopular(context,page, this@DashboardViewModel)
          }
      }

}
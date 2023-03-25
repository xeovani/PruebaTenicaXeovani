package com.example.pruebatecnicaxeovani.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.pruebatecnicaxeovani.connection.Services
import com.example.pruebatecnicaxeovani.connection.ServicesPopularMovies
import com.example.pruebatecnicaxeovani.connection.config
import com.example.pruebatecnicaxeovani.dataModel.entitiesLocation.Location
import com.example.pruebatecnicaxeovani.responses2.KnownFor
import com.example.pruebatecnicaxeovani.responses2.Result
import com.example.pruebatecnicaxeovani.responses2.movies.ResponseMovies
import com.example.pruebatecnicaxeovani.room.config.DatabaseLocation
import com.example.pruebatecnicaxeovani.room.config.DatabasePopularMovies
import com.example.pruebatecnicaxeovani.room.config.DatabasePopularPerson
import com.example.pruebatecnicaxeovani.view.dashboard.DashboardViewModel
import com.example.pruebatecnicaxeovani.view.home.HomeViewModel
import com.example.pruebatecnicaxeovani.view.location.LocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RepositoryPerson {
    companion object{
        var db : DatabasePopularPerson? = null
        var dbPopularMovies : DatabasePopularMovies? = null
        var dbLastLocation: DatabaseLocation? = null
        var dbFirebase:DatabaseReference ? = null
        private lateinit var fusedLocationClient: FusedLocationProviderClient

        private fun initializeDB(context: Context): DatabasePopularPerson{
            return DatabasePopularPerson.getDatabase(context)
        }
        private fun initilizeMoviesDB(context: Context):DatabasePopularMovies{
            return DatabasePopularMovies.getDatabasePopularMovies(context)
        }
        private fun initilizeLastLocationDB(context: Context):DatabaseLocation{
            return DatabaseLocation.getDatabaseLastLocation(context)
        }

        //Obtiene las reseñas de peliculas del usuario más popular
        @SuppressLint("SuspiciousIndentation")
        suspend fun getPopularPerson(context:Context, page:Int, viewModel: HomeViewModel){
            if (db == null){
                db = initializeDB(context)
            }

            //Consumo de servicio
            val retrofit = Retrofit.Builder()
                .baseUrl(config.urlBase)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(Services::class.java)

            //Inserta en la base de datos
            try{
                val call = retrofit.showPerson(page = 1)
                if (call.code() == 200){
                    val body = call.body()
                    if(body?.results!!.isNotEmpty()){
                        //Insertamos los datos
                        var listDataBase: MutableList<KnownFor> = mutableListOf()
                        //Recorres la lista del servicio para buscar la persona mas popular
                        var listPopular : MutableList<Int> = mutableListOf()
                        body.results.forEachIndexed { index, result ->

                            listPopular.add(result!!.popularity!!.toInt())
                            var mostPopularity = listPopular.maxOrNull()
                            if (result!!.popularity?.toInt() == mostPopularity) {

                                body.results[index]?.knownFor?.forEach { knownFor ->
                                    if (knownFor != null) {
                                        listDataBase.add(knownFor)
                                    }
                                }
                            }
                        }


/*                        listPopularity.forEach {

                            Log.e("result--->", it.toString() + "\n")
                        }*/
                            //Log.e("nombre--->",body.results[3]?.popularity.toString())/*
/*                        body.results[3]?.knownFor?.forEach { knownFor->
                            if (knownFor != null) {
                                listDataBase.add(knownFor)
                            }

                        }*/

                        if (listDataBase.isNotEmpty()) {
                            db!!.daoPopularPerson().insertPerson(listDataBase)
                        }

                    }else{
                        //viewModel.personLiveData.value = ResponsePerson(1, emptyList(), 0,1)
                        viewModel.personLiveData.value = Result(false,0,0,emptyList(),null,null,null,null)
                    }
                }else{
                    //viewModel.personLiveData.value = ResponsePerson(1, emptyList(), 0,1)
                    viewModel.personLiveData.value = Result(false,0,0,emptyList(),null,null,null,null)
                }
            }catch (e: java.lang.Exception){
                e.printStackTrace()
                //Si no hay internet hace una busqueda local
                val listPerson = db!!.daoPopularPerson().getPopularPerson()


                    if (listPerson.isNotEmpty()){
                        viewModel.personLiveData.value = Result(false,0,0,listPerson,null,null,null,null)
                    }else{
                        //viewModel.personLiveData.value = ResponsePerson(1, emptyList(),0,1)
                        viewModel.personLiveData.value = Result(false,0,0,emptyList(),null,null,null,null)
                    }

            }

        }

        //obtiene las peliculas mas populares
        suspend fun getMoviesPopular(context: Context, page: Int, viemodel:DashboardViewModel){
            if (dbPopularMovies == null){
                dbPopularMovies = initilizeMoviesDB(context)
            }

            var retrofit = Retrofit
                .Builder()
                .baseUrl(config.urlBase)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ServicesPopularMovies::class.java)

            try {
                val call = retrofit.getMoviesPopular(page = 1)
                if (call.code() == 200){
                    val body = call.body()
                    if (body?.results!!.isNotEmpty()){
                        /**Insertamos los datos nuevos*/
                        var listDataBaseMovies: MutableList<com.example.pruebatecnicaxeovani.responses2.movies.Result> = mutableListOf()
                        body.results.forEach { results ->
                            if (results != null) {
                                listDataBaseMovies.add(results)
                            }

                        }

                        if (listDataBaseMovies.isNotEmpty()){
                            dbPopularMovies!!.daoPopularMovies().insertPopularMovies(listDataBaseMovies)
                        }

                    }else{
                        viemodel.popularMoviesLiveData.value = ResponseMovies(page, emptyList(),1,0)
                    }
                }else{
                        viemodel.popularMoviesLiveData.value = ResponseMovies(page, emptyList(),1,0)
                }
            }catch (e: java.lang.Exception){
                e.printStackTrace()
                /**Si no hay internet se hace una busqueda local*/
                var lisPopularMovies = dbPopularMovies!!.daoPopularMovies().getPopularMovies()
                if (lisPopularMovies.isNotEmpty()){
                    viemodel.popularMoviesLiveData.value = ResponseMovies(1,lisPopularMovies,1,lisPopularMovies.size)

                }else{
                    viemodel.popularMoviesLiveData.value = ResponseMovies(1, emptyList(),1,0)
                }

            }
        }

        suspend fun getCurrentLocation(context: Context, viewModel: LocationViewModel){
                if (dbLastLocation == null){
                    dbLastLocation = initilizeLastLocationDB(context)
                }
            try {
/*                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true*/
                val cm : ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val bulider: NetworkRequest.Builder = NetworkRequest.Builder()
                cm.registerNetworkCallback(
                    bulider.build(),
                    object :ConnectivityManager.NetworkCallback(){
                        //Si tenemos conexion a internet
                        override fun onAvailable(network: Network) {
                            Log.e("FragmentLocation", "OnAvalaible")

                            val isConnected:Boolean = cm.getNetworkCapabilities(network)!!.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            if (isConnected){
                                if (ActivityCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    return
                                }
                                fusedLocationClient.lastLocation
                                    .addOnSuccessListener { location : android.location.Location? ->
                                        Log.e("latitud", "${location?.latitude} " + "${location?.longitude}")

                                        //Acediendo a firebase
                                        dbFirebase = FirebaseDatabase.getInstance().reference
                                        dbFirebase!!.child("Location").push().child("latitude").setValue(location?.latitude)

                                    }


                                val listLocation: MutableList<Location> = mutableListOf()
                            }

                        }
                        //Si No tenemos conexion a internet
                        override fun onLost(network: Network) {

                        }
                    }
                )


            }catch (e: java.lang.Exception){
                e.printStackTrace()
            }
        }
    }
}
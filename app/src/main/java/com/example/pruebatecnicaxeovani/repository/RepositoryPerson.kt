package com.example.pruebatecnicaxeovani.repository

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.example.pruebatecnicaxeovani.R
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
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class RepositoryPerson {
    companion object{
        private var db : DatabasePopularPerson? = null
        private var dbPopularMovies : DatabasePopularMovies? = null
        private var dbLastLocation: DatabaseLocation? = null
        private var networkConnection:Boolean? = null
        @SuppressLint("StaticFieldLeak")
        private val dbFirebase = Firebase.firestore
        private var TAG = "Result---->"
        private const val CHANNEL_ID = "MY_CHANNEL_ID"
        private var timeSavedLocation:String?= null
        private var getTimeFirebase : String? = null

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

       @SuppressLint("MissingPermission")
       suspend fun getCurrentLocation(
           context: Context,
           viewModel: LocationViewModel,
           requireActivity: FragmentActivity
       ){
                if (dbLastLocation == null){
                    dbLastLocation = initilizeLastLocationDB(context)
                }
           fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            try {

                val cm : ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val bulider: NetworkRequest.Builder = NetworkRequest.Builder()
                cm.registerNetworkCallback(
                    bulider.build(),
                    object :ConnectivityManager.NetworkCallback(){
                        //Si tenemos conexion a internet
                        @RequiresApi(Build.VERSION_CODES.O)
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
                                //Obteniendo la fecha
                                val datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy, hh:mm:ss a"))
                                Log.e("conection--->", "internet")
                                fusedLocationClient.lastLocation
                                    .addOnSuccessListener { location : android.location.Location? ->

                                        networkConnection = true
                                        viewModel.networkConnection.postValue(networkConnection)
                                        //Acediendo a firebase
                                Log.e("time-->",datetime)
                                        val userLastLocation : HashMap<String, Any>? = HashMap()
                                        userLastLocation?.set("location", "${location?.latitude}")
                                        userLastLocation?.set("longitude", "${location?.longitude}")
                                        userLastLocation?.set("time", datetime)

                                        //Guarda los datos en firabase
                                                dbFirebase
                                                    .collection("user")
                                                    .add(userLastLocation!!)
                                                    .addOnSuccessListener {
                                                        Log.e(TAG,"DocumentSnapshot successfully written!")
                                                    }
                                                    .addOnFailureListener {
                                                            e->Log.e(TAG,"Error writing document",e)
                                                    }



                                        //Obtener los datos de firabase
                                        dbFirebase.collection("user")
                                            .get().addOnSuccessListener { result->
                                                //obtenemos los valores del documento
                                                result.forEachIndexed {
                                                    indice, valor->
                                                    if (indice == 0){
                                                        var lat = valor.data.getValue("location").toString()
                                                        var lon = valor.data.getValue("longitude").toString()

                                                        getTimeFirebase = valor.data.getValue("time").toString()
                                                        var listLocation = HashMap<String,String>()
                                                        listLocation["location"] = lat
                                                        listLocation["longitude"] = lon


                                                        viewModel.locationLiveData.postValue(listLocation)

                                              //Mostramos la notificacionComponent que avisa que se guardamos la ubicacion cada 5 minutos
                                                        createNotificationChannel(CHANNEL_ID, "Test channel notification",requireActivity)
                                                        createNotification(getTimeFirebase!!, requireActivity)

                                             //Guardamos la ultima ubicacion en room
                                                        val mLocation = Location(null,lat,lon)

                                                        val locationMutableList = mutableListOf(mLocation)
                                                        viewModel.viewModelScope.launch {
                                                            dbLastLocation!!.daoLocation().insertLastLocation(locationMutableList)
                                                        }

                                                    }

                                                }
                                            }

                                    }



                            }





                        }
                        //Si No tenemos conexion a internet
                        override fun onLost(network: Network) {
                            Log.e("conection--x", "no internet")

                            networkConnection = false
                            viewModel.networkConnection.postValue(networkConnection)
                            viewModel.viewModelScope.launch {
                               var listLocation =  dbLastLocation!!.daoLocation().getLastLocation()
                                listLocation.forEach {
                                    var hashMapLocation = HashMap<String,String>()
                                    hashMapLocation.put("location",it.latitude)
                                    hashMapLocation.put("longitude",it.longitude)
                                    viewModel.locationLiveData.postValue(hashMapLocation)

                                }


                            }

                        }
                    }
                )


            }catch (e: java.lang.Exception){
                e.printStackTrace()
            }
        }


        private fun createNotificationChannel(channelID: String, channelName: String,requireActivity: FragmentActivity) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name: String = requireActivity.getString(R.string.channel_name)
                val descriptionText = requireActivity.getString(R.string.title_location)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    requireActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun createNotification(time:String, requireActivity: FragmentActivity) {
            val notificationID = 101
            val notificationManager = requireActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var builder = NotificationCompat.Builder(requireActivity, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_add_location_24)
                .setContentTitle(requireActivity.getString(R.string.title_notifications_Location))
                .setContentText(requireActivity.getString(R.string.title_body_notifications_location))
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(time))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId(CHANNEL_ID)
                .build()
            notificationManager.notify(notificationID, builder);

        }



    }
}
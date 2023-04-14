package com.example.pruebatecnicaxeovani.view.location

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pruebatecnicaxeovani.R
import com.example.pruebatecnicaxeovani.databinding.FragmentLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class LocationFragment : Fragment(), OnMapReadyCallback,GoogleMap.OnMarkerClickListener{
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    private  var lat: String? = null
    private var long: String? = null

    private lateinit var mMap: GoogleMap


    private lateinit var viewModel: LocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LocationViewModel::class.java]

        //viewModel.getLocation(this.requireContext().applicationContext)


        val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
//agendamos la tarea utilizando una expresion lambda , pasandole el metodo que se debe ejecutar , luego debemos indicar el tiempo que tomara la primera accion ,seguido indicamos el
//intervalo de tiempo que tomara para repetir la accion,finalmente indicamos la unidad de tiempo.
        executor.scheduleAtFixedRate({
            viewModel.getLocation(this.requireContext().applicationContext, requireActivity())
                                     }, 0, 60, TimeUnit.SECONDS)

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationBinding.inflate(inflater,container,false)
        val root:View = binding.root


        viewModel.locationLiveData.observe(viewLifecycleOwner, Observer {
            lat = it["location"]
            long = it["longitude"]
                Log.e("LocationFragment--->", it["location"]!! + it["longitude"])
            val location = LatLng(it["location"]!!.toDouble(),it["longitude"]!!.toDouble())
            val mapFragment = childFragmentManager.findFragmentById(binding.googleMap.id) as SupportMapFragment
                mapFragment.getMapAsync(this)

        })

        viewModel.networkConnection.observe(viewLifecycleOwner, Observer {
            if (!it){
                val dialogBuilder = AlertDialog.Builder(requireActivity())
                            dialogBuilder.setMessage(it.toString())
                                // if the dialog is cancelable
                                .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener{
                                    dialog, which ->
                                    dialog.dismiss()
                                })
                                .setPositiveButton(R.string.ok, DialogInterface.OnClickListener {
                                        dialog, id ->
                                    val intent =
                                        Intent(Settings.ACTION_WIFI_IP_SETTINGS, null)
                                    startActivity( intent)
                                    dialog.dismiss()

                                })

                val alert = dialogBuilder.create()
                            alert.setTitle(R.string.message_alert_noInternet)
                            alert.show()
            }
        })

        return root
    }

       override fun onMapReady(googleMap: GoogleMap) {
           mMap = googleMap
           val myLocation = LatLng(lat!!.toDouble(),long!!.toDouble())
           mMap.addMarker(MarkerOptions().position(myLocation).title("Marker in current location"))
           mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
           mMap.uiSettings.isZoomControlsEnabled = true
           mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,18f),4000,null)
       }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMarkerClick(p0: Marker)= false

}
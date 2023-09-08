package com.example.pruebatecnicaxeovani.view.upload

import android.R.attr
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.pruebatecnicaxeovani.databinding.FragmentUploadBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.FileNotFoundException
import java.io.IOException


class UploadFragment: Fragment() {

    //binding del fragment
    private var _binding: FragmentUploadBinding? = null
    private val binding get()= _binding!!
    private var firebaseStorage: FirebaseStorage? = null
    private var uriImg:List<Uri>?= null
    private val REQUEST_IMAGE_CAPTURE = 1
    private val maxNumImages = 5
/*    private var requestFileIntent = Intent(Intent.ACTION_PICK).apply {
        type = "image/jpg"
    }*/
    private val REQUEST_PHOTO_PICKER_SINGLE_SELECT = 1
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    //private var intent: Intent? = Intent(MediaStore.ACTION_PICK_IMAGES)
    private var intent: Intent? = null
    //Abre la galeria de imagenes para seleccionar una o varias
/*    val pickMultipleMedia = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(
            5
        )
    ){uris ->
    // Callback is invoked after the user selects media items or closes the
    // photo picker.
    if (uris.isNotEmpty()) {
        uris.forEach {
            //val selectedImageUri:Uri = it.
            //binding.imgAttached.setImageBitmap()
            uriImg = uris
            Log.e("uri--->", it.toString())
            Log.e("uriAbsolute--->", it.isAbsolute.toString())
            Log.e("uriRelative--->", it.isRelative.toString())
            Log.e("uriRelative--->", it.lastPathSegment.toString())
        }
        Log.d("PhotoPicker", "Number of items selected: ${uris.size}")
    } else {
        Log.d("PhotoPicker", "No media selected")
    }}*/

    private lateinit var viewModel: UploadViewModel
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewModel = ViewModelProvider(this).get(UploadViewModel::class.java)
        //intent?.type = "image/*"
        //intent?.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX,maxNumPhotos)
        //intent?.action = Intent.ACTION_GET_CONTENT

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)

        binding.icOpenCamera.setOnClickListener {
            dispatchTakePictureIntent(this.requireContext())

        }

        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.icAttachImg.setOnClickListener {

            //startActivityForResult(Intent.createChooser(intent,"Select Pcture"), PHOTO_GALERY)
            //startActivityForResult(intent, -1)
            //selectImage()
            //pickMultipleMedia?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            //dispatchTakePictureIntent(this.requireContext())
            selectImages()
            //Toast.makeText(context,"Error--> in open camera",Toast.LENGTH_SHORT ).show()
        }
        //Inicializa firebaseStorage


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    //Abre la camara para capturar foto
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun dispatchTakePictureIntent(context: Context) {
        intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {

            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            Toast.makeText(context,"Error in open camera-->",Toast.LENGTH_SHORT ).show()
            Log.e("error", e.toString())

        }

    }
    //Selecciona una sola imagen
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun selectImages(){
        intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        intent?.type = "image/*"
        //Con este metodo le decimos el maximo numero de imagenes que puede subir
        intent?.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX,maxNumImages)
        startActivityForResult(intent, REQUEST_PHOTO_PICKER_SINGLE_SELECT )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("result-->", requestCode.toString())
        if (resultCode != RESULT_CANCELED ){
            if (requestCode == REQUEST_PHOTO_PICKER_SINGLE_SELECT && data != null){

                var pathImages:Int = data!!.clipData!!.itemCount -1
                for (i in 0.. pathImages){
                    firebaseStorage = Firebase.storage
                    val storageREf = firebaseStorage!!.reference

                    Log.e("uriLastPath-->","${data!!.clipData?.getItemAt(i)?.uri?.lastPathSegment}")

                    data!!.clipData?.getItemAt(i)?.uri?.let {
                        //Almacenamos en cloud Firestore
                        storageREf.child("images").child(data!!.clipData?.getItemAt(i)?.uri?.lastPathSegment!!).putFile(it).addOnSuccessListener { documentReference  ->
                            Log.e("SUCCESS", "DocumentSnapshot added with ID: ${documentReference.storage}")
                            Toast.makeText(context,"Se subio exitosamente la foto", Toast.LENGTH_SHORT).show()

                        }
                            .addOnFailureListener { e ->
                                Log.e("FAIL", "Error adding document", e)

                            }
                    }

                }

            }
        }

    }



}
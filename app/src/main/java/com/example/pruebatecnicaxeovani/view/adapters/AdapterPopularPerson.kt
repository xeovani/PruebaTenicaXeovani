package com.example.pruebatecnicaxeovani.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pruebatecnicaxeovani.R
import com.example.pruebatecnicaxeovani.connection.config
import com.example.pruebatecnicaxeovani.responses2.KnownFor

class AdapterPopularPerson(private val dataSet: List<KnownFor?>?): RecyclerView.Adapter<AdapterPopularPerson.MovieViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.content_item,viewGroup,false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val url = config.posterPath + dataSet!![position]!!.posterPath

        holder.textName.text = dataSet[position]!!.name
        holder.textOverView.text = dataSet[position]!!.overview
        Glide.with(holder.ImgView.context)
            .load(url)
            .placeholder(R.drawable.ic_person_48)
            .into(holder.ImgView)
    }

    override fun getItemCount() = dataSet!!.size




    class MovieViewHolder(view: View):RecyclerView.ViewHolder(view){
        val textName: TextView
        val textOverView: TextView
        val ImgView: ImageView
        init {
            textName = view.findViewById(R.id.textName)
            textOverView = view.findViewById(R.id.textOverView)
            ImgView = view.findViewById(R.id.thumbnailMovie)
        }

    }

}
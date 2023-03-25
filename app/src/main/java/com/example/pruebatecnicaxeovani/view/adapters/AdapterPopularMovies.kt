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

class AdapterPopularMovies(private val dataSet: List<com.example.pruebatecnicaxeovani.responses2.movies.Result?>?):RecyclerView.Adapter<AdapterPopularMovies.MoviePupularViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MoviePupularViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.content_item_movies,viewGroup,false)
        return MoviePupularViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoviePupularViewHolder, position: Int) {
        val url = config.posterPath + dataSet?.get(position)!!.posterPath

        holder.textName.text = dataSet[position]!!.title
        holder.textOverView.text = dataSet[position]!!.overview
        Glide.with(holder.ImgView.context)
            .load(url)
            .placeholder(R.drawable.ic_person_48)
            .into(holder.ImgView)
    }

    override fun getItemCount() = dataSet!!.size

    class MoviePupularViewHolder(view: View):RecyclerView.ViewHolder(view) {
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
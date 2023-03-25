package com.example.pruebatecnicaxeovani.connection

import com.example.pruebatecnicaxeovani.responses2.movies.ResponseMovies
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ServicesPopularMovies {
    @GET("movie/popular")
    suspend fun getMoviesPopular(
        @Query("api_key") api_key:String= "6c9d5b4ecfe1a2a7740cafcecc8372b6",
        @Query("page") page:Int
    ):Response<ResponseMovies>

}
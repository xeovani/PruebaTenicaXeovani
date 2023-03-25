package com.example.pruebatecnicaxeovani.connection


import com.example.pruebatecnicaxeovani.responses2.ResponsePerson
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Services {
    @GET("person/popular")
    suspend fun showPerson(
      @Query("api_key") api_key: String = "6c9d5b4ecfe1a2a7740cafcecc8372b6",
      @Query("language") language: String = "en-US",
      @Query("page")page:Int
    ):retrofit2.Response<ResponsePerson>

}
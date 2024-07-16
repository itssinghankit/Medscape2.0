package com.example.medscape20.data.remote

import com.example.medscape20.data.remote.dto.user.articles.NewsResDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface MedscapeNewsApi {

//    @GET("everything?q=waste-management&apiKey=6161697ad1174baca526057bbddd80a3")
//    suspend fun getNewsArticles():NewsResDto

    @GET("{path}")
    suspend fun getNewsArticles(
       @Path("path") path: String,
       @QueryMap queryParams: Map<String, String>
    ): NewsResDto


}
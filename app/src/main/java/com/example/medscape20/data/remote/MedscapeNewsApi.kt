package com.example.medscape20.data.remote

import com.example.medscape20.data.remote.dto.user.home.articles.NewsResDto
import retrofit2.http.GET
import retrofit2.http.POST

interface MedscapeNewsApi {

    @GET("everything?q=waste-management&apiKey=6161697ad1174baca526057bbddd80a3")
    suspend fun getNewsArticles():NewsResDto

}
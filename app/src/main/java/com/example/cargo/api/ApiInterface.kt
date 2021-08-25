package com.example.cargo.api

import com.example.cargo.data.GalleryData
import com.example.cargo.utils.FileUtils
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET(FileUtils.get)
    suspend fun galleryApi(
        @Query("method") method: String = FileUtils.method,
        @Query("per_page") per_page: Int = FileUtils.per_page,
        @Query("page") page: Int,
        @Query("api_key") api: String = FileUtils.api_key,
        @Query("format") format: String = FileUtils.format,
        @Query("extras") extras: String = FileUtils.extras,
        @Query("nojsoncallback") noJson: Int = FileUtils.noJsonCallback
    ): GalleryData
}
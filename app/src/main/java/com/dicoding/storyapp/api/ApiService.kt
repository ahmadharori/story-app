package com.dicoding.storyapp.api

import com.dicoding.storyapp.api.response.FileUploadResponse
import com.dicoding.storyapp.api.response.LoginResponse
import com.dicoding.storyapp.api.response.RegisterResponse
import com.dicoding.storyapp.api.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<LoginResponse>

    @GET("stories")
    suspend fun stories(
        @Header("Authorization") authHeader : String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ) : StoriesResponse

    @GET("stories")
    fun storiesWithLocation(
        @Header("Authorization") authHeader : String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ) : Call<StoriesResponse>

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Header("Authorization") authHeader: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<FileUploadResponse>
}
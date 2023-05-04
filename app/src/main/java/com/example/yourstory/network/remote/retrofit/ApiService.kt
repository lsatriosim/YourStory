package com.example.yourstory.network.remote.retrofit

import com.example.yourstory.network.remote.responses.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("stories")
    fun getStories(): Call<StoriesResponse>

    @GET("stories")
    suspend fun getStoriesWithPaging(
        @Query("page") page:Int,
        @Query("size") size:Int
    ): List<Story>

    @GET("stories")
    fun getStoriesWithLocation(
        @Query("location") location:Int
    ): Call<StoriesResponse>

    @GET("/stories/{id}")
    fun getStory(
        @Path("id") id:String
    ): Call<StoryResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name:String,
        @Field("email") email:String,
        @Field("password") password:String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email:String,
        @Field("password") password:String
    ): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<RegisterResponse>
}
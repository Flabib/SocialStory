package id.my.fahdilabib.socialstory.data.remote

import id.my.fahdilabib.socialstory.data.remote.request.LoginRequest
import id.my.fahdilabib.socialstory.data.remote.request.RegisterRequest
import id.my.fahdilabib.socialstory.data.remote.responses.BaseResponse
import id.my.fahdilabib.socialstory.data.remote.responses.LoginResponse
import id.my.fahdilabib.socialstory.data.remote.responses.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
    ): BaseResponse

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): BaseResponse

    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("stories")
    suspend fun stories(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 1,
        @Query("location") location: Int = 0
    ): StoriesResponse
}
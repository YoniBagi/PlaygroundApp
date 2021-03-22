package com.example.trainingapp.costumViews

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class NetworkManager {
    companion object {
        private const val BASE_URL = "http://192.168.20.17/bha-api/"

        val instanceServiceApi: ServiceApi by lazy {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client =
                OkHttpClient.Builder()
                    .addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(client)
                .build()
            retrofit.create(ServiceApi::class.java)
        }
    }
}

interface ServiceApi {

    //const val PATH_AUDIO_TRANSMIT = "http://192.168.1.187/bha-api/audio-transmit.cgi?http-user=ghfpgs0001&http-password=m6VDJxHZdH"
    @Multipart
/*    @Headers( "Content-Type: audio/basic",
            "Content-Length: 9999999",
            "Connection: Keep-Alive",
            "Cache-Control: no-cache")*/
    @POST("audio-transmit.cgi?http-user=ghfpgs0001&http-password=m6VDJxHZdH")
    //fun fetchRaw(): Call<ResponseBody>
    fun upload(@Part part: MultipartBody.Part): Call<ResponseBody>
}

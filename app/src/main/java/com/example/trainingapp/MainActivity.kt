package com.example.trainingapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.trainingapp.costumViews.NetworkManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL


const val YEHONATAN_DOORBIRD_IP = "192.168.1.187"
const val OFFICE_DOORBIRD_IP = "192.168.20.17"
const val PATH_SESSION =
    "http://192.168.20.17/bha-api/video.cgi?sessionid=Zlv9KCtyYUh9ncDSe2h1hsoUh6noXZm7aT2YYRwvMoxLeed0cWtS8cSE9tvhN"
const val PATH_HTTP_CRED = "http://ghfpgs0001:m6VDJxHZdH@$YEHONATAN_DOORBIRD_IP/bha-api/video.cgi"
const val PATH_RTSP_CRED = "rtsp://ghfpgs0001:m6VDJxHZdH@$YEHONATAN_DOORBIRD_IP/mpeg/media.amp"
const val AUDIO_PATH =
    "http://192.168.1.187/bha-api/audio-receive.cgi?http-user=ghfpgs0001&http-password=m6VDJxHZdH"

const val TAG = "MainActivityVideo"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //InputStream inputStream = getResources().openRawResource(R.raw.piano12);
        val thread = Thread { this.playUrl() }
        thread.start()
        //val inStr = assets.open("doorbird_record")

    }

    private fun playUrl() {
        val inStr = URL(AUDIO_PATH).openStream()
        val buffer = ByteArray(1000)
        var i = 0
        //val file = File("//android_asset/doorbird_record")

        //while (inStr.read(buffer).also { i = it } != -1) {


        Handler(Looper.getMainLooper()).postDelayed({
            //inStr.close()
            inStr.read(buffer)
            Log.d("mymain", inStr.toString())
            val part = MultipartBody.Part.createFormData(
                "sd", "dsff", buffer.toRequestBody(
                    ("audio/basic").toMediaType()
                )
            )
            //val rb = file.asRequestBody(("audio/*").toMediaType())
            val call = NetworkManager.instanceServiceApi.upload(part)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val i = response.body()
                    Log.d("success", i.toString())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("fail", t.message.toString())
                }
            })

        }, 3000)

    }


    //}
}
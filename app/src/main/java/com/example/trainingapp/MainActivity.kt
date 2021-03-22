package com.example.trainingapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trainingapp.costumViews.NetworkManager
import com.example.trainingapp.managers.G711UCodec
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.*
import java.util.concurrent.TimeUnit


const val YEHONATAN_DOORBIRD_IP = "192.168.1.187"
const val OFFICE_DOORBIRD_IP = "192.168.20.17"
const val PATH_SESSION =
    "http://192.168.20.17/bha-api/video.cgi?sessionid=Zlv9KCtyYUh9ncDSe2h1hsoUh6noXZm7aT2YYRwvMoxLeed0cWtS8cSE9tvhN"
const val PATH_HTTP_CRED = "http://ghfpgs0001:m6VDJxHZdH@$YEHONATAN_DOORBIRD_IP/bha-api/video.cgi"
const val PATH_RTSP_CRED = "rtsp://ghfpgs0001:m6VDJxHZdH@$YEHONATAN_DOORBIRD_IP/mpeg/media.amp"
const val AUDIO_PATH =
    "http://192.168.1.187/bha-api/audio-receive.cgi?http-user=ghfpgs0001&http-password=m6VDJxHZdH"

const val AUDIO_TRANSMIT =
    "http://$YEHONATAN_DOORBIRD_IP/bha-api/audio-transmit.cgi?http-user=ghfpgs0001&http-password=m6VDJxHZdH"
const val AUDIO_TRANSMIT_SESSION =
    "http://$YEHONATAN_DOORBIRD_IP/bha-api/audio-transmit.cgi?sessionid=mpM0h8re90dtnMCwRYhdTlReY2h1jhw9XnCvRJESZbpFBK2xY6uDlSf3iZseR"

const val NEW_DOORBIRD_AUDIO_TRANSMIT =
    "http://192.168.20.105/bha-api/audio-transmit.cgi?http-user=ghhccp0001&http-password=sEekrd6DfT"

const val TAG = "MainActivityVideo"

const val headerByteArray =
    "POST /bha-api/audio-transmit.cgi?http-user=ghfpgs0001&http-password=m6VDJxHZdH HTTP/1.0\\r\\n" +
            "Content-Type: audio/basic\\r\\n" + "Content-Length: 9999999\\r\\n" + "Connection: Keep-Alive\\r\\n" +
            "Cache-Control: no-cache\\r\\n" + "\\r\\n"

class MainActivity : AppCompatActivity() {
    var os: FileOutputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate")
        //askPermission()


        //sendLocalAudio()
        /*GlobalScope.launch {
            sendDataOverTCP()
        }*/
        //val inStr = assets.open("doorbird_record")

        GlobalScope.launch { sendWithOutputStream() }

        //sendWithRetrofit()

    }

    private fun sendWithRetrofit() {
        val assetFile = assets.open("audio-recieved")
        val outBuffer = ByteArray(9999)
        assetFile.read(outBuffer)
        val part = MultipartBody.Part.createFormData("sd", "dsff", outBuffer.toRequestBody())
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
    }

    private fun recordAndOutPutStream() {
        GlobalScope.launch {
            val sampleRate = 44100 //8000

            val channelConfig: Int = AudioFormat.CHANNEL_IN_MONO
            val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
            var minBufSize: Int = AudioRecord.getMinBufferSize(
                sampleRate,
                channelConfig,
                audioFormat
            )
            //var minBufSize: Int = 9999999
            val recorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                minBufSize * 10
            );

            recorder.startRecording();

            /////Encoding:
            //Encoder encoder = new G711UCodec();
            val outBuffer = ByteArray(minBufSize)
            val buffer = ShortArray(minBufSize)

            //while (true) {
            var i = 0


            Log.d("mymain", "sendHttp()")

            //val buffer = ByteArray(i)
            //val instream = assets.open("doorbird_record")
            //val str = instream.read(buffer)
            val urlString: String = AUDIO_TRANSMIT// URL to call

            try {
                URL(urlString).openStream()
                val url = URL(urlString)
                val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "POST";
                urlConnection.setRequestProperty("Content-Type", "audio/basic");
                urlConnection.setRequestProperty("Content-Length", "99999999");
                urlConnection.setRequestProperty("Connection", "Keep-Alive")
                urlConnection.setRequestProperty("Cache-Control", "no-cache")
                urlConnection.useCaches = false;
                urlConnection.doOutput = true;


                val writer = urlConnection.outputStream
                while (recorder.read(buffer, 0, buffer.size).also { i = it } != -1) {
                    Log.d("TAG RECPRD", "record:  $i")
                    //Encoding:
                    G711UCodec.encode(buffer, minBufSize, outBuffer, 0)
                    writer.write(outBuffer)
                    writer.flush()
                }
                writer.close()
                urlConnection.connect()
                //out.close()
                Log.d(
                    "masas",
                    "${urlConnection.responseMessage} CODE: ${urlConnection.responseCode}"
                )
                urlConnection.connect()
            } catch (e: Exception) {
                val url = URL(urlString)
                val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "POST";
                urlConnection.setRequestProperty("Content-Type", "audio/basic");
                urlConnection.setRequestProperty("Content-Length", "99999999");
                urlConnection.setRequestProperty("Connection", "Keep-Alive")
                urlConnection.setRequestProperty("Cache-Control", "no-cache")
                urlConnection.useCaches = false;
                urlConnection.doOutput = true;


                val writer = urlConnection.outputStream
                urlConnection.connect()
                while (recorder.read(buffer, 0, buffer.size).also { i = it } != -1) {
                    Log.d("TAG RECPRD", "record:  $i")
                    //Encoding:
                    G711UCodec.encode(buffer, minBufSize, outBuffer, 0)
                    writer.write(outBuffer)
                    writer.flush()
                }
                writer.close()
                println(e.message)
            }

        }
    }

    private fun getBaseConnection(): HttpURLConnection {
        val urlConnection =
            URL(AUDIO_TRANSMIT).openConnection() as HttpURLConnection
        //urlConnection.doInput = true
        //urlConnection.connectTimeout = 100000
        //urlConnection.readTimeout = 100000

        return urlConnection
    }

    private fun sendWithOutputStream() {
        val buffer = ByteArray(8000)
        val instream = assets.open("audio-recieved")
        val urlConnection = getBaseConnection()
        urlConnection.requestMethod = "POST"
        val outStream = DataOutputStream(urlConnection.outputStream)
        urlConnection.connect()
        while (instream.read(buffer) != -1) {
            outStream.write(buffer)
            outStream.flush()
        }
        outStream.close()
        try {
            //    Log.d(TAG, urlConnection.responseMessage.toString())
        } catch (e: ProtocolException) {
            Log.d(TAG, e.message.toString())
        } finally {
            urlConnection.disconnect()
        }


        /*val headerByteArrray = headerByteArray.encodeToByteArray()
        Log.d(TAG, "headerByteArrray: ${headerByteArrray.contentToString()}")
        Log.d(TAG, "headerByteArrray.size:  ${headerByteArrray.size}")*/
        /*var outputStreamWriter =
            DataOutputStream(urlConnection.outputStream)*/
        //val buffer = ByteArray(headerByteArrray.size)
        //val instream = assets.open("audio-recieved")
        var i = 0
        //while (instream.read(buffer).also { i = it } != -1) {
        Log.d("BUFFER", "read $i")
        /*outputStreamWriter.write(headerByteArrray)
        outputStreamWriter.flush()*/
        //}
        //outputStreamWriter.close()


//        Log.d(TAG, urlConnection.responseCode.toString())
/*        val inStr = BufferedReader(InputStreamReader(urlConnection.inputStream))
        var c: Char
        var str = ""
        while (inStr.read().also { c = it as Char } != -1){
            str = "$str$c"
        }*/
    }

    private suspend fun sendDataOverTCP() {
        val socket = Socket("192.168.1.187", 80)
        val outStream = socket.getOutputStream()

        val dataOutputStream = DataOutputStream(outStream)


        val printWriter = PrintWriter(outStream)
        printWriter.apply {
            println("GET /bha-api/audio-transmit.cgi?http-user=ghfpgs0001&http-password=m6VDJxHZdH HTTP/1.0\\r\\n")
            println("Content-Type: audio/basic\\r\\n")
            println("Content-Length: 9999999\\r\\n")
            println("Connection: Keep-Alive\\r\\n")
            println("Cache-Control: no-cache\\r\\n")
            println("\\r\\n")
        }
        printWriter.flush()
        val br = BufferedReader(InputStreamReader(socket.getInputStream()))
        var t: String?
        while (br.readLine().also { t = it } != null) println(t)
        br.close()
        printWriter.close()
        socket.close()
        /*dataOutputStream.writeBytes(headerByteArray)
        dataOutputStream.flush()
        dataOutputStream.close()*/

        /*val buffer = ByteArray(900)
        val instream = assets.open("audio-recieved")

        while (instream.read().also { printWriter.write(it) } != -1)
            printWriter.flush()
        printWriter.close()*/

    }

    private fun askPermission() {
        Log.d(TAG, "askPermission")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1234
            )
        } else {
            val thread = Thread { this.playUrl() }
            thread.start()
        }
    }

    private fun recordAndDatagraSocket() {
        val datagramSocket = DatagramSocket()
        val buffer = ByteArray(5000)

        val sampleRate = 44100 //8000

        val channelConfig: Int = AudioFormat.CHANNEL_IN_MONO
        val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
        var minBufSize: Int = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        //var minBufSize: Int = 9999999
        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            minBufSize * 10
        );

        recorder.startRecording();
        val headerByteArray =
            ("GET /bha-api/audio-transmit.cgi?http-user=ghfpgs0001&http-password=m6VDJxHZdH HTTP/1.1\\r\\n" +
                    "Content-Type: audio/basic\\r\\n" + "Content-Length: 9999999\\r\\n" + "Connection: Keep-Alive\\r\\n" +
                    "Cache-Control: no-cache\\r\\n" + "\\r\\n").toByteArray()

        val IPAddress = InetAddress.getByName("192.168.1.187")

        val sendData = ByteArray(1024)
        val receiveData = ByteArray(1024)

        datagramSocket.send(DatagramPacket(headerByteArray, headerByteArray.size, IPAddress, 80))
        while (recorder.read(buffer, 0, buffer.size) != -1) {
            val sendPacket = DatagramPacket(buffer, buffer.size, IPAddress, 80)
            datagramSocket.send(sendPacket)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1234 -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    val thread = Thread { this.playUrl() }
                    thread.start()
                } else {
                    Log.d("TAG", "permission denied by user")
                }
                return
            }
        }
    }

    private fun sendLocalAudio(/*outBuffer: ByteArray*/) {
        val sampleRate = 44100 //8000
        val channelConfig: Int = AudioFormat.CHANNEL_IN_MONO
        val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
        var minBufSize: Int = AudioRecord.getMinBufferSize(
            sampleRate,
            channelConfig,
            audioFormat
        )
        val thread = Thread {
            try {
                val buffer = ByteArray(minBufSize)
                val instream = assets.open("sixteenBit.raw")
                instream.read(buffer)
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                var client = OkHttpClient().newBuilder().addInterceptor(interceptor)
                    .connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
                    .build()

                var request: Request = Request.Builder()
                    .url(AUDIO_TRANSMIT)
                    .addHeader("Content-Type", "audio/basic")
                    .addHeader("Content-Length", "$minBufSize")
                    .addHeader("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2")
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Cache-Control", "no-cache")
                    .method("POST", buffer.toRequestBody())
                    .build()

                /*val soc = client.socketFactory.createSocket("192.168.1.187", 80)
                soc.keepAlive = true
                val outStrwam = soc.getOutputStream()
                var i: Int
                while (instream.read(buffer).also { i = it } != -1) {
                    Log.d("BUFFER", "read $i")
                    outStrwam.write(buffer)
                    outStrwam.flush()
                }*/

                var response: okhttp3.Response = client.newCall(request).execute()

                Log.d(TAG, response.body.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()
    }

    private fun playUrl() {

        val filePath =
            Environment.getExternalStorageDirectory().absolutePath + "/sd8k16bitMono4.wav"
        try {
            os = FileOutputStream(filePath)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val sampleRate = 44100 //8000

        val channelConfig: Int = AudioFormat.CHANNEL_IN_MONO
        val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
        var minBufSize: Int = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        //var minBufSize: Int = 9999999
        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            minBufSize * 10
        );

        recorder.startRecording();

        /////Encoding:
        //Encoder encoder = new G711UCodec();
        val outBuffer = ByteArray(minBufSize)
        val buffer = ShortArray(minBufSize)

        //while (true) {
        var i = 0
        while (recorder.read(outBuffer, 0, outBuffer.size).also { i = it } != -1) {
            //reading data from MIC into buffer
            //Encoding:
            //G711UCodec.encode(buffer, minBufSize, outBuffer, 0)
            //sendHttp(outBuffer, i)

            writeToFile(outBuffer)
        }

        //sendLocalAudio(/*outBuffer*/)


        //val inStr = URL(AUDIO_PATH).openStream()
        //val buffer = ByteArray(9999999)

        //val file = File("//android_asset/doorbird_record")

        //while (inStr.read(buffer).also { i = it } != -1) {


        //Handler(Looper.getMainLooper()).postDelayed({
        //inStr.close()
        //inStr.read(buffer)
        /*
            Log.d("mymain", outBuffer.toString())
            val part = MultipartBody.Part.createFormData("sd", "dsff", outBuffer.toRequestBody())
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

            */
         */


//        }, 3000)

        //}
    }

    private fun sendHttp(inStr: ByteArray, i: Int) {
        GlobalScope.launch {
            Log.d("mymain", inStr.toString())

            val buffer = ByteArray(i)
            //val instream = assets.open("doorbird_record")
            //val str = instream.read(buffer)
            val urlString: String = AUDIO_TRANSMIT// URL to call

            val out: OutputStream?
            try {
                val url = URL(urlString)
                val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "POST";
                urlConnection.setRequestProperty("Content-Type", "audio/basic\\r\\n");
                urlConnection.setRequestProperty("Content-Length", "$i\\r\\n");
                urlConnection.setRequestProperty("Connection", "Keep-Alive\\r\\n")
                urlConnection.setRequestProperty("Cache-Control", "no-cache\\r\\n")
                urlConnection.useCaches = false;
                urlConnection.doOutput = true;


                //out = BufferedOutputStream(urlConnection.outputStream)
                val writer = urlConnection.outputStream
                urlConnection.connect()
                writer.write(inStr)
                writer.flush()
                writer.close()
                //out.close()
                /*     Log.d(
                         "masas",
                         "${urlConnection.responseMessage} CODE: ${urlConnection.responseCode}"
                     )*/
                urlConnection.disconnect()
            } catch (e: Exception) {
                e.message?.let { Log.d("globla", it) }
            }

        }
    }

    @Throws(IOException::class)
    private fun writeToFile(inSrr: ByteArray) {
        // Write the output audio in byte
        //val sData = ShortArray(1024)
        val bData: ByteArray = inSrr
        //println("Short wirting to file$sData")
        try {
            os?.write(bData, 0, 2048)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            os?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
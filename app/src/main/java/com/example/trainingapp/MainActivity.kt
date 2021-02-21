package com.example.trainingapp

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.IOException


const val YEHONATAN_DOORBIRD_IP = "192.168.1.187"
const val OFFICE_DOORBIRD_IP = "192.168.20.17"
const val PATH_SESSION =
    "http://192.168.20.17/bha-api/video.cgi?sessionid=Zlv9KCtyYUh9ncDSe2h1hsoUh6noXZm7aT2YYRwvMoxLeed0cWtS8cSE9tvhN"
const val PATH_HTTP_CRED = "http://ghfpgs0001:m6VDJxHZdH@$YEHONATAN_DOORBIRD_IP/bha-api/video.cgi"
const val PATH_RTSP_CRED = "rtsp://ghfpgs0001:m6VDJxHZdH@$YEHONATAN_DOORBIRD_IP/mpeg/media.amp"
const val TAG = "MainActivityVideo"

class MainActivity : AppCompatActivity() {

    // creating a variable for
    // button and media
    // button and media player
    var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // setting on click listener for our play and pause buttons.
        playBtn?.setOnClickListener(View.OnClickListener { // calling method to play audio.
            playAudio()
        })
        pauseBtn?.setOnClickListener(View.OnClickListener {
            // checking the media player
            // if the audio is playing or not.
            if (mediaPlayer!!.isPlaying) {
                // pausing the media player if media player
                // is playing we are calling below line to
                // stop our media player.
                mediaPlayer!!.stop()
                mediaPlayer!!.reset()
                mediaPlayer!!.release()

                // below line is to display a message
                // when media player is paused.
                Toast.makeText(this@MainActivity, "Audio has been paused", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // this method is called when media
                // player is not playing.
                Toast.makeText(this@MainActivity, "Audio has not played", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun playAudio() {
        val audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        val streamUrl = "https://glzicylv01.bynetcdn.com/glglz_mp3"

        // initializing media player
        mediaPlayer = MediaPlayer()

        // below line is use to set the audio
        // stream type for our media player.
        mediaPlayer!!.setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())

        // below line is use to set our
        // url to our media player.
        try {
            mediaPlayer!!.setDataSource(streamUrl)
            // below line is use to prepare
            // and start our media player.
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // below line is use to display a toast message.
        Toast.makeText(this, "Audio started playing..", Toast.LENGTH_SHORT).show()
    }
}
package com.example.trainingapp

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


const val YEHONATAN_DOORBIRD_IP = "192.168.1.187"
const val OFFICE_DOORBIRD_IP = "192.168.20.17"
const val PATH_SESSION =
    "http://192.168.20.17/bha-api/video.cgi?sessionid=Zlv9KCtyYUh9ncDSe2h1hsoUh6noXZm7aT2YYRwvMoxLeed0cWtS8cSE9tvhN"
const val PATH_HTTP_CRED = "http://ghfpgs0001:m6VDJxHZdH@$YEHONATAN_DOORBIRD_IP/bha-api/video.cgi"
const val PATH_RTSP_CRED = "rtsp://ghfpgs0001:m6VDJxHZdH@$YEHONATAN_DOORBIRD_IP/mpeg/media.amp"
const val TAG = "MainActivityVideo"

class MainActivity : AppCompatActivity() {

    private var mSimpleExoPlayer: SimpleExoPlayer? = null

    private var mLibVLC: LibVLC? = null
    private var mMediaPlayer: org.videolan.libvlc.MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setVideoWithHeader()
        //setVideoInlineCred()
        //setVideoIntent()
        //initPlay()
        initwebView()
        //initVideoView()
        //initVlcPlayer()
        //initVlcHTTPPlayer()
    }

    private fun initVlcHTTPPlayer() {
        val args = ArrayList<String>()
        args.add("-vvv")
        mLibVLC = LibVLC(this, args)
        mMediaPlayer = org.videolan.libvlc.MediaPlayer(mLibVLC)
/*
        mMediaPlayer!!.attachViews(
            videoLayoutHttp,
            null,
            false,
            true
        )*/

        try {
            val media =
                Media(mLibVLC, Uri.parse(PATH_HTTP_CRED))
            mMediaPlayer!!.media = media
            media.release()
        } catch (e: IOException) {
            throw RuntimeException("Invalid asset folder")
        }
        mMediaPlayer!!.play()

    }

    private fun initVlcPlayer() {
        val args = ArrayList<String>()
        args.add("-vvv")
        mLibVLC = LibVLC(this, args)
        mMediaPlayer = org.videolan.libvlc.MediaPlayer(mLibVLC)

        mMediaPlayer!!.attachViews(
            videoLayout,
            null,
            false,
            true
        )

        try {
            val media =
                Media(mLibVLC, Uri.parse(PATH_RTSP_CRED))
            mMediaPlayer!!.media = media
            media.release()
        } catch (e: IOException) {
            throw RuntimeException("Invalid asset folder")
        }
        mMediaPlayer!!.play()

        mMediaPlayer!!.setEventListener { event ->
            when (event.type) {
                /*org.videolan.libvlc.MediaPlayer.Event.Buffering ->
                    Log.d(TAG, "Buffering");

                org.videolan.libvlc.MediaPlayer.Event.EncounteredError ->
                    Log.d(TAG, "EncounteredError");

                org.videolan.libvlc.MediaPlayer.Event.EndReached ->
                    Log.d(TAG, "EndReached");

                org.videolan.libvlc.MediaPlayer.Event.ESAdded ->
                    Log.d(TAG, "ESAdded");

                org.videolan.libvlc.MediaPlayer.Event.ESDeleted ->
                    Log.d(TAG, "ESDeleted");

                org.videolan.libvlc.MediaPlayer.Event.ESSelected ->
                    Log.d(TAG, "ESSelected");

                org.videolan.libvlc.MediaPlayer.Event.LengthChanged ->
                    Log.d(TAG, "LengthChanged");

                org.videolan.libvlc.MediaPlayer.Event.MediaChanged ->
                    Log.d(TAG, "MediaChanged");

                org.videolan.libvlc.MediaPlayer.Event.PausableChanged ->
                    Log.d(TAG, "PausableChanged");

                org.videolan.libvlc.MediaPlayer.Event.PositionChanged ->
                    Log.d(TAG, "PositionChanged");

                org.videolan.libvlc.MediaPlayer.Event.RecordChanged ->
                    Log.d(TAG, "RecordChanged");

                org.videolan.libvlc.MediaPlayer.Event.SeekableChanged ->
                    Log.d(TAG, "SeekableChanged");

                org.videolan.libvlc.MediaPlayer.Event.TimeChanged ->
                    Log.d(TAG, "TimeChanged");

*/

                org.videolan.libvlc.MediaPlayer.Event.Opening ->
                    Log.d(TAG, "Opening");

                org.videolan.libvlc.MediaPlayer.Event.Paused ->
                    Log.d(TAG, "Paused");

                org.videolan.libvlc.MediaPlayer.Event.Playing ->
                    Log.d(TAG, "Playing");


                org.videolan.libvlc.MediaPlayer.Event.Stopped -> {
                    Log.d(TAG, "Stopped");
                    mMediaPlayer!!.stop()
                    mMediaPlayer!!.detachViews()
                    mMediaPlayer!!.release();
                    mLibVLC!!.release();
                    initVlcPlayer()
                }

            }
        }
    }

    private fun initVideoView() {
        videoView.setVideoPath(PATH_RTSP_CRED)
        videoView.start()
    }

    private fun initwebView() {
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.loadUrl(PATH_HTTP_CRED)
    }

    private fun setVideoInlineCred() {
        videoView.setVideoURI(Uri.parse(PATH_SESSION))
        //videoView.start()
        videoView.setOnPreparedListener {
            Log.d("prepare", "prepare")
            videoView.start()
        }
    }

    //http://ghfpgs0001:m6VDJxHZdH@192.168.1.187/bha-api/video.cgi
    //rtsp://ghfpgs0001:m6VDJxHZdH@192.168.1.187:8557/mpeg/media.amp


    private fun initPlay() {
        val uri = Uri.parse(PATH_HTTP_CRED)
        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
            this,
            DefaultTrackSelector(),
            DefaultLoadControl()
        )
        mPlayerView.player = mSimpleExoPlayer
        mSimpleExoPlayer?.playWhenReady = true

        val defaultHttpDataSourceFactory = DefaultHttpDataSourceFactory(
            Util.getUserAgent(
                this,
                "exoplayerapp"
            )
        )

        val hlsMediaSource: HlsMediaSource = HlsMediaSource.Factory(defaultHttpDataSourceFactory)
            .createMediaSource(uri)

        mSimpleExoPlayer?.prepare(hlsMediaSource)
    }

    private fun setVideoIntent() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PATH_HTTP_CRED))
        startActivity(intent)
    }

    private fun setVideoWithHeader() {
        val header: MutableMap<String, String> = HashMap(1)
        val cred = "ghfpgs0001" + ":" + "m6VDJxHZdH"
        //Base64.encodeToString(toEncrypt, Base64.DEFAULT);
        val auth = "Basic " + Base64.encodeToString(cred.toByteArray(), Base64.DEFAULT)
        header["Authorization"] = auth
        videoView.setVideoURI(Uri.parse(PATH_RTSP_CRED), header)
        videoView.setOnPreparedListener(MediaPlayer.OnPreparedListener { mp ->
            Log.d("prepare", "prepare")
            mp.prepareAsync()
            videoView.start()
        })
    }
}
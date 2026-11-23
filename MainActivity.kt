package com.sda.videoplayer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.VideoView
import android.widget.MediaController

class MainActivity : Activity() {
    private lateinit var video: VideoView
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 28, 20, 20)
            setBackgroundColor(0xFF101820.toInt())
        }

        val title = TextView(this).apply {
            text = "SDA Video Player"
            textSize = 24f
            setTextColor(0xFFFFFFFF.toInt())
            gravity = Gravity.CENTER
        }
        status = TextView(this).apply {
            text = "Choose a video stored on your device"
            textSize = 14f
            setTextColor(0xFFB8C7D1.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 12)
        }
        video = VideoView(this).apply {
            setBackgroundColor(0xFF000000.toInt())
        }

        val controls = MediaController(this)
        video.setMediaController(controls)
        controls.setAnchorView(video)

        val open = Button(this).apply {
            text = "OPEN LOCAL VIDEO"
            setOnClickListener { chooseVideo() }
        }

        root.addView(title, LinearLayout.LayoutParams(-1, -2))
        root.addView(status, LinearLayout.LayoutParams(-1, -2))
        root.addView(video, LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(open, LinearLayout.LayoutParams(-1, -2))
        setContentView(root)
    }

    private fun chooseVideo() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "video/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        @Suppress("DEPRECATION")
        startActivityForResult(intent, REQUEST_VIDEO)
    }

    @Deprecated("Uses the document picker for broad Android compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VIDEO && resultCode == RESULT_OK) {
            val uri: Uri = data?.data ?: return
            try {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) { }
            status.text = "Playing selected local video"
            video.setVideoURI(uri)
            video.setOnPreparedListener { it.start() }
            video.setOnErrorListener { _, _, _ ->
                status.text = "Cannot play this video format"
                true
            }
        }
    }

    companion object {
        private const val REQUEST_VIDEO = 1001
    }
}

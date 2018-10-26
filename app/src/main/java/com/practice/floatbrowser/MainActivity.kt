package com.practice.floatbrowser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_WINDOW_OVERLAY = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openWindowBtn.setOnClickListener {
            openWindow()
        }
        closeWindowBtn.setOnClickListener {
            closeWindow()
        }
    }

    private fun closeWindow() {
        stopService(Intent(this@MainActivity, MainService::class.java))
        finish()
    }

    private fun openWindow() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT).show();
            startActivityForResult(
                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())),
                    REQUEST_WINDOW_OVERLAY
            );
        } else {
            startService(Intent(this@MainActivity, MainService::class.java))
        }
    }
}

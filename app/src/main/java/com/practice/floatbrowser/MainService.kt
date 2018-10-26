package com.practice.floatbrowser

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView

class MainService : Service() {
    var focusable: Boolean = false;
    var expanded: Boolean = true;
    var x: Int = 0;
    var y: Int = 0;
    var floatView: View? = null;
    var homeBtn: ImageView? = null;
    var actionBtn: ImageView? = null;
    var logoBtn: ImageView? = null;
    var webview: WebView? = null;
    var backBtn: ImageView? = null;
    var toggleFocusableButton: ImageView? = null;
    var windowManager: WindowManager? = null;
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager;
        initView()
        showFloatWindow()
    }

    fun initView() {
        floatView = LayoutInflater.from(this).inflate(R.layout.float_layout, null);
        toggleFocusableButton = floatView?.findViewById(R.id.toggleFocusableBtn)
        backBtn = floatView?.findViewById(R.id.backBtn)
        homeBtn = floatView?.findViewById(R.id.homeBtn)
        actionBtn = floatView?.findViewById(R.id.actionBtn)
        webview = floatView?.findViewById(R.id.webview)
        logoBtn = floatView?.findViewById(R.id.logoBtn)
        webview?.webChromeClient = WebChromeClient();
        webview?.webViewClient = WebViewClient()
        backBtn?.setOnClickListener {
            webview?.goBack()
        }
        homeBtn?.setOnClickListener {
            webview?.loadUrl("https://www.baidu.com")
        }
        actionBtn?.setOnClickListener {
            expanded = !expanded;
            updateView()
        }
        toggleFocusableButton?.setOnClickListener {
            focusable = !focusable;
            updateView()
        }
        logoBtn?.isClickable = true
        logoBtn?.setOnTouchListener { v: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = event.rawX.toInt();
                    y = event.rawY.toInt();
                }
                MotionEvent.ACTION_MOVE -> {
                    var nowX = event.rawX.toInt()
                    var nowY = event.rawY.toInt()
                    var movedX = nowX - x;
                    var movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    var layoutParams = floatView?.layoutParams as WindowManager.LayoutParams;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager?.updateViewLayout(floatView, layoutParams)
                }
                else -> {

                }
            }
            false
        }


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        windowManager?.removeView(floatView)
        super.onDestroy()
    }

    fun updateView() {
        val layoutParams = floatView?.layoutParams as WindowManager.LayoutParams;
        if (expanded) {
            toggleFocusableButton?.visibility = View.VISIBLE
            backBtn?.visibility = View.VISIBLE
            homeBtn?.visibility = View.VISIBLE
            actionBtn?.setImageResource(R.drawable.ic_collapse)
            layoutParams.width = getExpandWidth()
            layoutParams.height = getExpandHeight()
        } else {
            toggleFocusableButton?.visibility = View.GONE
            backBtn?.visibility = View.GONE
            homeBtn?.visibility = View.GONE
            actionBtn?.setImageResource(R.drawable.ic_expand)
            layoutParams.width = dp2px(60f);
            layoutParams.height = dp2px(30f);
        }
        toggleFocusableButton?.setImageResource(if (focusable) {
            R.drawable.ic_focus
        } else {
            R.drawable.ic_not_focus
        })
        layoutParams.flags = getFlags()
        windowManager?.updateViewLayout(floatView, layoutParams)
    }


    private fun showFloatWindow() {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.flags = getFlags()
        layoutParams.format = PixelFormat.RGBA_8888
        layoutParams.width = getExpandWidth()
        layoutParams.height = getExpandHeight()
        layoutParams.x = 0
        layoutParams.y = 0
        windowManager?.addView(floatView, layoutParams)
    }

    fun getExpandWidth(): Int =
            if (windowManager?.defaultDisplay?.rotation == Surface.ROTATION_0 || windowManager?.defaultDisplay?.rotation == Surface.ROTATION_180) {
                Resources.getSystem().displayMetrics.widthPixels
            } else {
                Resources.getSystem().displayMetrics.widthPixels / 2
            }


    fun getExpandHeight(): Int =
            if (windowManager?.defaultDisplay?.rotation == Surface.ROTATION_0 || windowManager?.defaultDisplay?.rotation == Surface.ROTATION_180) {
                Resources.getSystem().displayMetrics.heightPixels / 2
            } else {
                Resources.getSystem().displayMetrics.heightPixels
            }

    fun getFlags(): Int =
            if (focusable) {
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            } else {
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
            }


}

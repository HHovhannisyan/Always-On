package com.example.alwayson.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alwayson.R
import com.example.alwayson.databinding.ActivityWebBinding
import com.example.alwayson.databinding.CustomDialogFragmentBinding
import com.example.alwayson.utils.NetworkHelper
import android.net.http.SslError
import android.view.*

import android.webkit.SslErrorHandler

import android.webkit.WebView


class WebActivity : AppCompatActivity()  {

    lateinit var customView: View
    private var customDialog1: AlertDialog? = null
    val networkHelper = NetworkHelper(this)
    lateinit var activityWebBinding: ActivityWebBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityWebBinding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(activityWebBinding.root)
        // activityWebBinding.webContainer.setBackgroundColor(Color.TRANSPARENT)

        activityWebBinding.swipeRefresh.setOnRefreshListener {
            activityWebBinding.swipeRefresh.setColorSchemeColors(
                Color.BLUE,
                Color.RED,
                Color.GREEN,
                Color.MAGENTA
            )

            activityWebBinding.webView.loadUrl(activityWebBinding.webView.url!!)
        }

        val currentUrl = intent.getStringExtra("currentUrl")
        if (currentUrl != null) {
            intent.getStringExtra("currentUrl")?.let { loadPage(it) }
        }

        val dialogBinding: CustomDialogFragmentBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.custom_dialog_fragment,
                null,
                false
            )

        customDialog1 = AlertDialog.Builder(this, 0).create()

        customDialog1?.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            setView(dialogBinding.root)
            setCancelable(false)

            Log.d("onReceivedError", "dismiss()!!")

            dialogBinding.retryBtn.setOnClickListener {
                if (networkHelper.isNetworkConnected() && customDialog1 != null && customDialog1!!.isShowing) {
                    customDialog1!!.dismiss()
                }
            }
        }
        customDialog1!!.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetJavaScriptEnabled")
    fun loadPage(url: String) {
        activityWebBinding.webView.visibility = View.INVISIBLE

        webViewSettings(url)
        activityWebBinding.apply {

            webView.webViewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, url: String) {
                    super.onPageFinished(view, url)
                    Log.d("onPageFinished", "onPageFinished")
                    activityWebBinding.webView.visibility = View.VISIBLE
                    swipeRefresh.isRefreshing = false
                }


                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    Log.d("onReceivedError", "onReceivedError")
                    try {
                        webView.stopLoading()
                    } catch (e: Exception) {
                    }
                    if (!networkHelper.isNetworkConnected()) {

                        customDialog1!!.show()
                    }
                }
                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler,
                    error: SslError?
                ) {
                    handler.proceed()
                }



                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    //activityWebBinding.webView.visibility = View.VISIBLE

                    Log.d("onPageStarted", "onPageStarted")
                }

/*
                override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                    activityWebBinding.webView.loadUrl(url!!)
                    return true
                }*/

            }



            webView.webChromeClient = object : WebChromeClient() {

                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {

                    if (view is FrameLayout) {
                        customView = view

                        fullScreen.addView(customView)
                        fullScreen.visibility = View.VISIBLE
                        mainContainer.visibility = View.GONE

                    }
                }


                override fun onHideCustomView() {

                    fullScreen.removeView(activityWebBinding.fullScreen)
                    fullScreen.visibility = View.GONE
                    mainContainer.visibility = View.VISIBLE
                }
            }
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun webViewSettings(url: String) {

        activityWebBinding.webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            builtInZoomControls = true
            displayZoomControls = false
            javaScriptCanOpenWindowsAutomatically = true
            cacheMode = WebSettings.LOAD_DEFAULT
            setAppCachePath(applicationContext.cacheDir.absolutePath)

            defaultZoom = WebSettings.ZoomDensity.FAR
            loadWithOverviewMode = true
            useWideViewPort = true
            allowFileAccess = true
            allowContentAccess = true
            setSupportZoom(true)
            setText(100)
            //  webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

            setRenderPriority(WebSettings.RenderPriority.HIGH)
           // mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            setSupportMultipleWindows(true)
        }

        activityWebBinding.webView.apply {
            if (Build.VERSION.SDK_INT >= 22) {
                setLayerType(View.LAYER_TYPE_HARDWARE, null)
            } else {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            }
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            isScrollbarFadingEnabled = true
            setBackgroundColor(Color.TRANSPARENT)
           // setInitialScale(1)
            val cookieManager = CookieManager.getInstance()
            cookieManager.acceptCookie()
            cookieManager.setAcceptThirdPartyCookies(this, true)
            loadUrl(url)
        }
    }

/*
    override fun onBackPressed() {
        if (activityWebBinding.webView.canGoBack()) {
            activityWebBinding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }*/


    override fun onPause() {
        super.onPause()
        activityWebBinding.webView.apply {
            onPause()
            pauseTimers()
        }
    }


    override fun onResume() {
        super.onResume()
        activityWebBinding.webView.apply {
            onResume()
            resumeTimers()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        activityWebBinding.webView.saveState(outState)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && activityWebBinding.webView.canGoBack()) {
            activityWebBinding.webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }



    override fun onDestroy() {
        super.onDestroy()
        //Utils.deleteCache(this)

        activityWebBinding.webView.apply {
            val parent: ViewParent = parent
            (parent as ViewGroup).removeView(this)
            stopLoading()
// Call this method when exiting to remove the bound service, otherwise some specific systems will report an error
            settings.javaScriptEnabled = false
            clearHistory()
            destroyDrawingCache()
            removeAllViews()
            destroy()
        }
    }

}
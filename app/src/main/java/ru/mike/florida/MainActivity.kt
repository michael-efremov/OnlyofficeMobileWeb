package ru.mike.florida

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import java.net.URL

/*
Example of webview integration. Minimum set of functions and callbacks
 */
class MainActivity : AppCompatActivity() {

    companion object {

        val TAG: String = MainActivity::class.java.simpleName

        private const val OPEN_URl = "URL"
        private const val TOKEN = "TOKEN"
    }

    // url document is passed in arguments
    private var url: String? = null

    // token is passed in arguments
    private var token: String? = null

    private var webView: WebView? = null

    private val headers: Map<String, String>
        get() {
            return mapOf(
                "Authorization" to "Bearer $token",
                "Accept" to "application/json"
            )
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView = findViewById(R.id.webView)
        getArgs()
        setSettings()
        clearHistory()
        load()
    }

    // Clear history and cookie
    override fun onDestroy() {
        super.onDestroy()
    }

    // Example. Сan use a fragment and pass it to arguments or take it from another place
    private fun getArgs() {
        intent.extras?.let { bundle ->
            token = bundle.getString(TOKEN)
            url = bundle.getString(OPEN_URl)
        }
    }

    private fun clearHistory() {
        webView?.apply {
            clearCache(true)
            clearHistory()
            clearFormData()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setSettings() {
        webView?.settings?.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            setAppCacheEnabled(false)
            domStorageEnabled = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        }
        webView?.setDownloadListener(WebViewDownload())
        webView?.webViewClient = WebViewCallbacks()
        webView?.webChromeClient = WebViewChromeClient()
    }

    /*
    The token must be registered in the cookie. After the work is completed, the cookies are cleared.
     */
    private fun load() {
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setCookie(URL(url).protocol + "://" + URL(OPEN_URl).host, "asc_auth_key=$token")
            setAcceptThirdPartyCookies(webView, true)
        }
        webView?.loadUrl(url ?: "", headers)
    }

    private inner class WebViewDownload : DownloadListener {
        override fun onDownloadStart(
            url: String,
            userAgent: String,
            contentDisposition: String,
            mimetype: String,
            contentLength: Long,
        ) {
            // Get file name and open chooser and download
        }
    }

    // Need methods
    private inner class WebViewCallbacks : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        // Show progress
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        // Hide progress
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
        }

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {}

        @TargetApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            super.onReceivedError(view, request, error)
            onReceivedError(view, error.errorCode, error.description.toString(), request.url.toString())
        }

        override fun onReceivedHttpError(
            view: WebView,
            request: WebResourceRequest,
            errorResponse: WebResourceResponse,
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
        }

        @SuppressLint("WebViewClientOnReceivedSslError")
        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            super.onReceivedSslError(view, handler, error)
        }
    }

    /*
     * WebView file chooser
     * */
    private inner class WebViewChromeClient : WebChromeClient() {

        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            // Alert dialog
            return super.onJsAlert(view, url, message, result)
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            Log.d(TAG, "onConsoleMessage(): " + consoleMessage.message())
            return super.onConsoleMessage(consoleMessage)
        }

        override fun onShowFileChooser(
            webView: WebView,
            uploadMsg: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams,
        ): Boolean {
            return true
        }
    }

}
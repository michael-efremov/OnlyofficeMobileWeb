package ru.mike.florida

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController


class MainFragment : Fragment(R.layout.fragment_main) {

    private var webView: WebView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        webView = view.findViewById(R.id.main_web_view)
        setSettings()
        webView?.loadUrl("https://kim.teamlab.info/example/")
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setSettings() {
        webView?.settings?.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            domStorageEnabled = true
        }
        webView?.webViewClient = MainWebViewClient(findNavController())
    }

}

private class MainWebViewClient(private val navController: NavController): WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url
        if (url != null) {
            val path = url.path
            if (path?.contains("editor") == true) {
                navController.navigate(R.id.action_mainFragment_to_editorFragment, Bundle(1).apply {
                    putString("document_url", url.toString())
                })
                return true
            }
            return false
        }
        return super.shouldOverrideUrlLoading(view, request)
    }

}